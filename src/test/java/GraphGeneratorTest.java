import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GraphGeneratorTest {

    @Test
    public void testGraphConnectivity() {
        int n = 200;
        double density = 0.1;
        List<Node> graph = GraphGenerator.generateGraph(n, density);
        Set<Integer> visited = SequentialBFS.traverse(graph.getFirst());
        assertEquals(graph.size(), visited.size(), "Graph should be connected");
    }

    @Test
    public void testBigGraphConnectivity() {
        int n = 5000;
        double density = 0.2;
        List<Node> graph = GraphGenerator.generateGraph(n, density);
        Set<Integer> visited = SequentialBFS.traverse(graph.getFirst());
        assertEquals(graph.size(), visited.size(), "Graph should be connected");
    }

    @Test
    public void testNoSelfLoops() {
        List<Node> graph = GraphGenerator.generateGraph(100, 0.05);
        for (Node node : graph) {
            for (Node neighbor : node.getNeighbors()) {
                assertNotSame(node, neighbor, "Self-loop detected at node " + node.id);
            }
        }
    }

    @Test
    public void testUndirectedEdges() {
        List<Node> graph = GraphGenerator.generateGraph(100, 0.05);
        for (Node node : graph) {
            for (Node neighbor : node.getNeighbors()) {
                assertTrue(neighbor.getNeighbors().contains(node),
                        "Edge is not undirected between " + node.id + " and " + neighbor.id);
            }
        }
    }

    @Test
    public void testNoDuplicateEdges() {
        List<Node> graph = GraphGenerator.generateGraph(100, 0.05);
        for (Node node : graph) {
            Set<Node> seen = new HashSet<>();
            for (Node neighbor : node.getNeighbors()) {
                assertTrue(seen.add(neighbor), "Duplicate edge found at node " + node.id + " with neighbor " + neighbor.id);
            }
        }
    }

    @Test
    public void testCorrectNodeCount() {
        int n = 150;
        List<Node> graph = GraphGenerator.generateGraph(n, 0.05);
        assertEquals(n, graph.size(), "Graph should contain " + n + " nodes");
    }

    @Test
    public void testEdgeCountRoughlyMatchesDensity() {
        int n = 200;
        double density = 0.1;
        List<Node> graph = GraphGenerator.generateGraph(n, density);

        int actualEdges = graph.stream().mapToInt(n1 -> n1.getNeighbors().size()).sum() / 2;

        int minEdges = n - 1;
        int maxEdges = (n * (n - 1)) / 2;
        int expectedEdges = minEdges + (int)((maxEdges - minEdges) * density);

        // Allow a margin of error due to randomness
        int lowerBound = (int)(expectedEdges * 0.8);
        int upperBound = (int)(expectedEdges * 1.2);

        assertTrue(actualEdges >= lowerBound && actualEdges <= upperBound,
                "Edge count " + actualEdges + " not within expected bounds (" + lowerBound + " - " + upperBound + ")");
    }

    // Simple BFS for connectivity check
    private Set<Integer> bfs(Node start) {
        Set<Integer> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        visited.add(start.id);
        queue.offer(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Node neighbor : current.getNeighbors()) {
                if (visited.add(neighbor.id)) {
                    queue.offer(neighbor);
                }
            }
        }
        return visited;
    }
}
