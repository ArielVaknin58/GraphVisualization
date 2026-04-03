package Algorithms;

import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import java.util.Map;


public class AlgorithmFactory {
    public static Command getAlgorithm(String name, Graph graph, Map<String,String> params) {
        return switch (name.toLowerCase()) {
            case "bfs" -> new BFS(graph, graph.VerticeIndexer.get(params.get(AppSettings.API_MODE_INPUT_NODE_STRING)));
            case "bellmanford" -> new BellmanFordAlgorithm(graph, graph.VerticeIndexer.get(params.get(AppSettings.API_MODE_INPUT_NODE_STRING)));
            case "bipartite" -> new BiPartite(graph);
            case "clique" -> new Clique(graph,Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)),Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)));
            case "connectivitycomponents" -> new ConnectivityComponents(graph);
            //case "dfs" -> new DFS(graph, graph.V.getFirst());
            case "eulercircuit" -> new EulerCircuit(graph);
            case "eulerpath" -> new EulerPath(graph);
            case "floydwarshall" -> new FloydWarshallAlgorithm(graph);
            case "fordfelkerson" -> new FordFelkersonAlgorithm(graph,graph.VerticeIndexer.get(params.get(AppSettings.API_MODE_S_STRING)),graph.VerticeIndexer.get(params.get(AppSettings.API_MODE_T_STRING)));
            case "hamiltonpath" -> new HamiltonianPath(graph);
            case "independentset" -> new IndependentSetAlgorithm(graph,Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)),Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)));
            case "kcolors" -> new kColors(graph,Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)),Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)));
            case "kosarajusharir" -> new KosarajuSharirAlgorithm(graph);
            case "maxcut" -> new MaxCut(graph,Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)),Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)));
            case "mincut" -> new MinCutAlgorithm(graph);
            case "prim" -> new PrimAlgorithm(graph);
            case "shortestpathstree" -> new ShortestPathsTree(graph,graph.VerticeIndexer.get(params.get(AppSettings.API_MODE_INPUT_NODE_STRING)));
            case "supergraph" -> new SuperGraph(graph);
            case "topologicalsort" -> new TopologicalSort(graph);
            case "vertexcover" -> new VertexCover(graph,Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)),Integer.parseInt(params.get(AppSettings.API_MODE_ITERATIONS_STRING)));
            default -> null;
        };
    }
}