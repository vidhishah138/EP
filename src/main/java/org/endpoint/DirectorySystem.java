package org.endpoint;
import java.util.*;

public class DirectorySystem {

    // Represents each folder in the file system
    static class TrieNode {
        String name;
        TreeMap<String, TrieNode> children;

        TrieNode(String name) {
            this.name = name;
            this.children = new TreeMap<>();
        }
    }

    private final TrieNode root = new TrieNode("");

    public void create(String path) {
        String[] parts = path.split("/");
        TrieNode current = root;

        for (String part:parts) {
            current.children.putIfAbsent(part, new TrieNode(part));
            current=current.children.get(part);
        }
        System.out.println("CREATE " + path);
    }
    
    public void move(String source, String target) {
        TrieNode[] src = findParentAndChild(source);
        TrieNode[] dest = findParentAndChild(target);

        if (src == null || dest == null) {
            System.out.println("Cannot move " + source + " - source or target does not exist");
            return;
        }

        String[] srcArr = source.split("/");
        String folderName = srcArr[srcArr.length - 1];

        // Remove from source's parent and add to destination
        TrieNode movedNode = src[0].children.remove(folderName);
        dest[1].children.put(folderName, movedNode);

        System.out.println("MOVE " + source + " " + target);
    }
    
    public void delete(String path) {
        TrieNode[] node = findParentAndChild(path);
        if (node == null) {
            System.out.println("Cannot delete " + path);
            return;
        }

        String[] parts = path.split("/");
        String name = parts[parts.length - 1];
        node[0].children.remove(name);

        System.out.println("DELETE " + path);
    }

    // Handles LIST command
    public void list() {
        System.out.println("LIST");
        printTree(root, 0);
    }

    // Helper: Recursively prints the directory structure
    private void printTree(TrieNode node, int level) {
        for (String name : node.children.keySet()) {
            for (int i = 0; i < level; i++) System.out.print("  ");
            System.out.println(name);
            printTree(node.children.get(name), level + 1);
        }
    }

    // Helper: Finds parent and target node
    private TrieNode[] findParentAndChild(String path) {
        String[] parts = path.split("/");
        TrieNode current = root;
        TrieNode parent = null;

        for (String part:parts) {
            parent = current;
            current = current.children.get(part);
            if (current == null) return null;
        }

        return new TrieNode[]{parent, current};
    }

    public static void main(String[] args) {
        DirectorySystem system = new DirectorySystem();

        List<String> commands = List.of(
                "CREATE fruits",
                "CREATE vegetables",
                "CREATE grains",
                "CREATE fruits/apples",
                "CREATE fruits/apples/fuji",
                "LIST",
                "CREATE grains/squash",
                "MOVE grains/squash vegetables",
                "CREATE foods",
                "MOVE grains foods",
                "MOVE fruits foods",
                "MOVE vegetables foods",
                "LIST",
                "DELETE fruits/apples",
                "DELETE foods/fruits/apples",
                "LIST"
        );

        for (String cmd : commands) {
            if (cmd.startsWith("CREATE ")) {
                system.create(cmd.substring(7));
            } else if (cmd.startsWith("MOVE ")) {
                String[] parts = cmd.substring(5).split(" ");
                system.move(parts[0], parts[1]);
            } else if (cmd.startsWith("DELETE ")) {
                system.delete(cmd.substring(7));
            } else if (cmd.equals("LIST")) {
                system.list();
            }
        }
    }
}