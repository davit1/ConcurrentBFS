import java.util.ArrayList;
import java.util.List;

public class Node {

    int id;
    List<Node> neighbors = new ArrayList<>();

    Node(int id) {
        this.id = id;
    }

    void addNeighbor(Node neighbor) {
        neighbors.add(neighbor);
    }

    List<Node> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
