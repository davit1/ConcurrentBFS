import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class ParallelTraversalDemo {

    public static void main(String[] args) throws IOException, InterruptedException {

        int totalNodes = 7000;
        double density = 0.02;

        System.out.println("Started generating");
        // 1. Generate graph
        List<Node> graph = GraphGenerator.generateGraph(totalNodes, density);
        System.out.println("finished generating");

        // Parallel BFS time measurement
        // 2. Run BFS from node 0
        BlockingQueue<Node> blockingQueue = new LinkedBlockingQueue<>();
        Set<Integer> concurrentSet = ConcurrentHashMap.newKeySet();
        ParallelBFS parallelBFS = new ParallelBFS(blockingQueue, concurrentSet, 16);


        Instant start = Instant.now();
        parallelBFS.traverseAndWait(graph.getFirst());
        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);
        System.out.print("Time to traverse the graph: ");
        System.out.println(elapsed.toMillis());

        if (parallelBFS.getSeenSet().size() == totalNodes) {
            System.out.println("bfs visited all of the nodes");
        } else {
            System.out.println("bfs not working properly, not all nodes have been visited");
            System.out.println(parallelBFS.getSeenSet().size());
            System.out.println(totalNodes);
        }

    }

}
