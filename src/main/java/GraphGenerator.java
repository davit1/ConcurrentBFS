import java.util.*;

public class GraphGenerator {
    private GraphGenerator() { }

    public static List<Node> generateGraph(int n, double density) {
        if (n <= 0) return new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(new Node(i));
        }
        if (n == 1) return nodes;

        Random rand = new Random();
        Set<Long> existingEdges = new HashSet<>();

        // Create spanning tree to ensure connectivity
        for (int i = 1; i < n; i++) {
            int parentIndex = rand.nextInt(i);
            connectNodes(nodes.get(i), nodes.get(parentIndex), existingEdges);
        }

        // Calculate non-tree edges and target extra edges
        long totalNonTreeEdges = ((long)(n-1) * (n-2)) / 2;
        int amountOfExtraEdges = (int) Math.min(Integer.MAX_VALUE, Math.min(totalNonTreeEdges, density * totalNonTreeEdges));

        // Add extra edges based on density
        if (n <= 1000) {
            addEdgesViaList(nodes, existingEdges, amountOfExtraEdges, rand);
        } else {
            addEdgesViaSampling(nodes, existingEdges, amountOfExtraEdges, n, rand);
        }

        return nodes;
    }

    private static void connectNodes(Node a, Node b, Set<Long> edgeSet) {
        a.addNeighbor(b);
        b.addNeighbor(a);
        int min = Math.min(a.id, b.id);
        int max = Math.max(a.id, b.id);
        edgeSet.add(((long)min << 32) | max);
    }

    private static void addEdgesViaList(List<Node> nodes, Set<Long> existingEdges,
                                        int k, Random rand) {
        List<Long> candidateEdges = new ArrayList<>();
        int n = nodes.size();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                long key = ((long)i << 32) | j;
                if (!existingEdges.contains(key)) {
                    candidateEdges.add(key);
                }
            }
        }

        Collections.shuffle(candidateEdges, rand);
        for (int i = 0; i < k && i < candidateEdges.size(); i++) {
            long edge = candidateEdges.get(i);
            int nodeA = (int)(edge >>> 32);
            int nodeB = (int)edge;
            connectNodes(nodes.get(nodeA), nodes.get(nodeB), existingEdges);
        }
    }

    private static void addEdgesViaSampling(List<Node> nodes, Set<Long> existingEdges,
                                            int k, int n, Random rand) {
        Set<Long> addedEdges = new HashSet<>(k);
        while (addedEdges.size() < k) {
            int a = rand.nextInt(n);
            int b = rand.nextInt(n);
            if (a == b) continue;

            int min = Math.min(a, b);
            int max = Math.max(a, b);
            long key = ((long)min << 32) | max;

            if (!existingEdges.contains(key) && !addedEdges.contains(key)) {
                addedEdges.add(key);
                connectNodes(nodes.get(min), nodes.get(max), existingEdges);
            }
        }
    }
}