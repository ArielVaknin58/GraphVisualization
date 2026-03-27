package Unused;

import Algorithms.*;
import Services.GraphData;
import com.fasterxml.jackson.annotation.*;

public class GeminiResponse {
    public String type;
    public String message;

    // 1. decide the class using 'action', but fill it with 'parameters' data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "action"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BFS.class, name = "run_bfs"),
            @JsonSubTypes.Type(value = DFS.class, name = "run_dfs"),
            @JsonSubTypes.Type(value = BiPartite.class, name = "run_bipartite"),
            @JsonSubTypes.Type(value = EulerCircuit.class, name = "run_euler_circuit"),
            @JsonSubTypes.Type(value = TopologicalSort.class, name = "run_topological"),
            @JsonSubTypes.Type(value = PrimAlgorithm.class, name = "run_prim"),
            @JsonSubTypes.Type(value = BellmanFordAlgorithm.class, name = "run_bellman_ford"),
            @JsonSubTypes.Type(value = ConnectivityComponents.class, name = "run_connectivity"),
            @JsonSubTypes.Type(value = EulerPath.class, name = "run_euler_path"),
            @JsonSubTypes.Type(value = FloydWarshallAlgorithm.class, name = "run_floyd_warshall"),
            @JsonSubTypes.Type(value = FordFelkersonAlgorithm.class, name = "run_ford_felkerson"),
            @JsonSubTypes.Type(value = HamiltonianPath.class, name = "run_hamilton_path"),
            @JsonSubTypes.Type(value = KosarajuSharirAlgorithm.class, name = "run_kosaraju"),
            @JsonSubTypes.Type(value = MinCutAlgorithm.class, name = "run_mincut"),
            @JsonSubTypes.Type(value = ShortestPathsTree.class, name = "run_shortest_paths_tree"),
            @JsonSubTypes.Type(value = SuperGraph.class, name = "run_super"),
            @JsonSubTypes.Type(value = Clique.class, name = "run_clique"),
            @JsonSubTypes.Type(value = IndependentSetAlgorithm.class, name = "run_independent_set"),
            @JsonSubTypes.Type(value = kColors.class, name = "run_k_colors"),
            @JsonSubTypes.Type(value = VertexCover.class, name = "run_vertex_cover"),


    })
    @JsonProperty("parameters")
    public Algorithm algorithm;

    @JsonProperty("graphData")
    public GraphData graphData;
}