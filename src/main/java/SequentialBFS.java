import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SequentialBFS {

    public static Set<Integer> traverse(Node start) {
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
