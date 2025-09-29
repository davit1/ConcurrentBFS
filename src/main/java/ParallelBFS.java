import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelBFS {

    private static final Node POISON = new Node(-1000);
    private final AtomicLong pendingWorkCount = new AtomicLong(0);
    private final int threadCount;
    private final ExecutorService exec;
    private final BlockingQueue<Node> workQueue;
    private final Set<Integer> seenSet; // must be a thread safe implementation of the Set interface

    private class BfsTraverserTask implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Node node = workQueue.poll(50, TimeUnit.MILLISECONDS);
                    if (node != null) {
                        simulateLoad(node);
                        for (Node neighbor: node.getNeighbors()) {
                            if (seenSet.add(neighbor.id)) {
                                pendingWorkCount.incrementAndGet();
                                workQueue.add(neighbor);
                            }
                        }
                        pendingWorkCount.decrementAndGet();
                    } else if (pendingWorkCount.get() == 0) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("caught the interrupted exception");
                Thread.currentThread().interrupt();
            }
        }

        private void simulateLoad(Node node) {
            try {
                // Vary sleep based on node properties for realism
                int delay = 1 + (node.id % 4);  // 10-50ms range
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public ParallelBFS(BlockingQueue<Node> blockingQueue, Set<Integer> concurrentSet, int threadCount) {
        this.threadCount = threadCount;
        exec  = Executors.newFixedThreadPool(threadCount);
        workQueue = blockingQueue;
        seenSet = concurrentSet;
    }

    public Set<Integer> getSeenSet() {
        return this.seenSet;
    }

    private void start(Node start) {
        if (start == null) {
            throw new IllegalArgumentException("Start node cannot be null");
        }
        seenSet.add(start.id);
        workQueue.add(start);
        pendingWorkCount.set(1);

        for (int i = 0; i < threadCount; i++) {
            exec.submit(new BfsTraverserTask());
        }
    }

    // An alternative, blocking start method
    public void traverseAndWait(Node start) throws InterruptedException {
        this.start(start); // Start the process

        // Shutdown and wait for termination
        exec.shutdown();
        exec.awaitTermination(15000, TimeUnit.MILLISECONDS);
    }

}
