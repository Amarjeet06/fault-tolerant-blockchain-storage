import java.util.*;

public class Node {
    public int id;
    public List<byte[]> fragments = new ArrayList<>();

    public Node(int id) {
        this.id = id;
    }
}