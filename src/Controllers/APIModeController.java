package Controllers;

import Algorithms.Algorithm;
import Algorithms.AlgorithmFactory;
import Algorithms.Command;
import GraphVisualizer.AppSettings;
import Services.GraphData;
import Services.GraphTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.javalin.Javalin;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class APIModeController extends Controller{

    private boolean APIMode = false;
    private boolean isGraphLoaded = false;
    private static APIModeController instance = null;
    private int port = AppSettings.API_MODE_PORT;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private GraphTools tool = new GraphTools();


    private APIModeController(boolean apimode)
    {
        APIMode = apimode;

    }

    public Gson getGsonParser()
    {
        return gson;
    }

    public static APIModeController getInstance()
    {
        return instance;
    }

    public int getPort()
    {
        return port;
    }

    public static APIModeController CreateInstance(boolean apimode)
    {
        if(instance == null)
        {
            instance = new APIModeController(apimode);
        }

        return instance;
    }

    public boolean isAPIMode() {
        return APIMode;
    }

    public void setAPIMode(boolean APIMode) {
        this.APIMode = APIMode;
    }

    public void initAPIMode()
    {
        if(isAPIMode())
        {
            var app = Javalin.create().start(ControllerManager.getApiModeController().getPort());
            app.before(ctx -> {
                System.out.println("[GraphVisualizer] ["+ctx.method().name()+"] INFO "+ ctx.path());
            });
            app.get("/", ctx ->
            {

                ctx.result("""
                        ~~ Welcome to Graph Visualizer API ! ~~
                           \s
                            Please load a graph in .json format using the following example structure :
                           \s
                            {
                                   "isDirected": false,
                                   "nodes": ["1", "2", "3"],
                                   "edges": [
                                       {
                                           "from": "1",
                                           "to": "2",
                                           "weight": 1                                \s
                                       },
                                       {
                                           "from": "2",
                                           "to": "3",
                                           "weight": 1
                                       }
                                   ]
                            }
                           \s
                            # the values for the "from" and "to" keys are strings- the labels of the number.
                            # an optional field for an edge is capacity, for algorithms that calculate flows.
                            # the value types for capacity and weight are ints- not floats.

                       \s""");
            });
            app.post("/echo", ctx ->
            {
                ctx.result("Received Information : \n" + ctx.body());
            });
            app.post("/algorithm/{algoToken}", ctx ->
            {
                String algo = ctx.pathParam("algoToken");
                if(isGraphLoaded)
                {
                    String jsonBody = ctx.body();
                    Map<String, String> params = gson.fromJson(jsonBody, new TypeToken<Map<String, String>>(){}.getType());
                    Command command = AlgorithmFactory.getAlgorithm(algo, ControllerManager.getGraphInputController().getGraph(),params);
                    if(command != null)
                    {
                        ctx.result(command.executeCommand(params));
                    }
                    else
                    {
                        ctx.status(404).result("Error in parameters or algorithm token. Check URL and parameters.");
                    }
                }else
                {
                    ctx.status(404).result("No graph loaded.");
                }

            });
            app.post("/load_graph", ctx ->
            {
                try{
                    String rawData = ctx.body();
                    GraphData info =  APIModeController.getInstance().getGsonParser().fromJson(rawData, GraphData.class);
                    if(verifyGraphDataObject(info))
                    {
                        GraphInputController.CreateGraphStatic(info);

                        isGraphLoaded = true;
                        ctx.result("Graph loaded successfully !");
                    }
                    else
                    {
                        ctx.status(400).result("GraphData object has invalid values.");
                    }


                } catch (JsonSyntaxException e){
                    ctx.status(400).result("JSON Syntax Error: " + e.getMessage());
                } catch (Exception e){
                    ctx.status(500).result("Internal Error: " + e.getMessage());
                }

            });
            app.get("/endpoints", ctx ->
            {
               ctx.result("""
            ## AVAILABLE ENDPOINTS (case insensitive): ##
            - Graphs only - no other parameters required :
            /BiPartite : for Bi-Partite algorithm in an undirected graph
            /ConnectivityComponents : for ConnectivityComponents algorithm in an undirected graph
            /EulerCircuit : for the Euler Circuit algorithm
            /EulerPath : for Euler Path algorithm in an undirected graph
            /FloydWarshall : for Floyd-Warshall algorithm
            /HamiltonPath : for Hamilton Path algorithm 
            /KosarajuSharir : for Kosaraju-Sharir algorithm 
            /MinCut : for MinCut algorithm 
            /Prim : for Prim algorithm
            /SuperGraph : for SuperGraph algorithm in a directed graph
            /TopologicalSort : for Topological Sort algorithm in a directed graph
           
            - %s variable required :
            /BFS : for BFS algorithm
            /BellmanFord : for Bellman-Ford algorithm
            /ShortestPathsTree : for Shortest-Path algorithm in an undirected graph
            
            - Non-Deterministic Algorithms : %s and %s parameters required :
            /Clique : for Clique algorithm
            /IndependentSet : for Independent Set algorithm
            /kColors : for k colors algorithm
            /MaxCut : for MaxCut algorithm
            /VertexCover : for VertexCover algorithm
             
            - Flow algorithms : %s and %s parameters required :
             /FordFelkerson : for Ford-Felkerson algorithm 
                        
          """.formatted(AppSettings.API_MODE_INPUT_NODE_STRING,AppSettings.API_MODE_ITERATIONS_STRING,AppSettings.API_MODE_K_STRING,
                       AppSettings.API_MODE_S_STRING,AppSettings.API_MODE_T_STRING));
            });
        }
    }

    private boolean verifyGraphDataObject(GraphData obj)
    {
        return obj != null && obj.nodes != null && obj.edges != null;
    }
}
