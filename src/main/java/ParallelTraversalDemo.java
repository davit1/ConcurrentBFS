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

//      # Parallel BFS time measurement
        // 2. Run BFS from node 0
        System.out.println("finished generating");
        BlockingQueue<Node> blockingQueue = new LinkedBlockingQueue<>();
        ConcurrentMap<Integer, Boolean> concurrentMap = new ConcurrentHashMap<>();
        ParallelBFS parallelBFS = new ParallelBFS(blockingQueue, concurrentMap, 16);
        Instant start = Instant.now();
        parallelBFS.start(graph.getFirst());
        parallelBFS.stop(15000);
        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);
        System.out.print("Time to traverse the graph: ");
        System.out.println(elapsed.toMillis());

        if (parallelBFS.getSeenMap().size() == totalNodes) {
            System.out.println("bfs visited all of the nodes");
        } else {
            System.out.println("bfs not working properly, not all nodes have been visited");
            System.out.println(parallelBFS.getSeenMap().size());
            System.out.println(totalNodes);
        }

    }

}
