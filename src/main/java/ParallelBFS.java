import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelBFS {

    private final AtomicLong pendingWorkCount = new AtomicLong(0);
    private final int threadCount;
    private final ExecutorService exec;
    private final BlockingQueue<Node> workQueue;
    private final ConcurrentMap<Integer, Boolean> seenMap;

    private class BfsTraverserTask implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Node node = workQueue.poll(50, TimeUnit.MILLISECONDS);
                    if (node != null) {
                        simulateLoad(node);
                        List<Node> batch = null;
                        for (Node neighbor: node.getNeighbors()) {
                            if (seenMap.putIfAbsent(neighbor.id, true) == null) {
                                if (batch == null) {
                                    batch = new ArrayList<>(32);
                                }
                                batch.add(neighbor);
                            }
                        }
                        if (batch != null) {
                            pendingWorkCount.addAndGet(batch.size());
                            workQueue.addAll(batch);
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

    public ParallelBFS(BlockingQueue<Node> blockingQueue, ConcurrentMap<Integer, Boolean> concurrentMap, int threadCount) {
        this.threadCount = threadCount;
        exec  = Executors.newFixedThreadPool(threadCount);
        workQueue = blockingQueue;
        seenMap = concurrentMap;
    }

    public ConcurrentMap<Integer, Boolean> getSeenMap() {
        return this.seenMap;
    }

    public void start(Node start) {
        if (start == null) {
            throw new IllegalArgumentException("Start node cannot be null");
        }
        seenMap.put(start.id, true);
        workQueue.add(start);
        pendingWorkCount.set(1);

        for (int i = 0; i < threadCount; i++) {
            exec.submit(new BfsTraverserTask());
        }
    }

    public void stop(int timeout) {
        exec.shutdown();
        try {
            if (!exec.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                exec.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("didn't terminate in given time");
            exec.shutdownNow();
        }
    }

}
