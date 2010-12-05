import java.io.*;
import java.util.*;

/**
 * Calculates a shortest path from start word to end word.
 * Each step in the path is a dictionary entry
 * and each step is one of the following transformations:<ul>
 * <li>change one letter</li>
 * <!-- TODO: add/remove one letter -->
 * <!-- TODO: reorder letters (anagram) -->
 * </ul>
 * <dl>
 * <dt>Input</dt><dd>a dictionary (word per line), start word, end word</dd>
 * <dt>Output</dt><dd>shortest path (word per line) connecting the start and end words</dd>
 * </dl>
 * User: Cheng Leong
 * Date: Dec 4, 2010
 * Time: 6:28:12 PM
 */
public class WordLadder {

    public static final void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Syntax: WordLadder <startword> <endword> <dictionaryfile>");
            return;
        }
        WordLadder wl = new WordLadder(loadFile(args[2]));
        List<String> path = wl.computePath(args[0], args[1]);
        if (path == null || path.isEmpty()) {
            System.out.println("Can't get from " + args[0] + " to " + args[1]);
        } else {
            for (String step : path) {
                System.out.println(step);
            }
        }
    }

    /**
     * returns Set of trimmed lines from the named file
     * @param filename name of input file containing one word per line
     * @return words Set of lines in the file
     * @throws IOException if the file cannot be loaded
     */
    private static Set<String> loadFile(String filename) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(filename));
        Set<String> corpus = new HashSet<String>();
        for (String s; (s = reader.readLine()) != null; ) {
            corpus.add(s.trim());
        }
        return Collections.unmodifiableSet(corpus);
    }

    Map<String, Node> graph;
    public WordLadder(Set<String> corpus) {
        graph = new HashMap<String, Node>(corpus.size());
        for (String s : corpus) {
            Node n = graph.get(s);
            if (n == null) {
                n = new Node(s);
            }
            graph.put(s, n);
            // TODO: add n as a neighbor to existing nodes
        }

        for (Node n : graph.values()) {
            n.freezeNeighbors();
        }
    }

    public List<String> computePath(String startWord, String endWord) {
        Node startNode = graph.get(startWord);
        if (startNode == null) {
            return Collections.emptyList(); // unknown start
        }
        Node endNode = graph.get(endWord);
        if (endNode == null) {
            return Collections.emptyList(); // unknown end
        }
        if (startNode == endNode) {
            return Collections.singletonList(endWord); // nothing to do
        }
        List<String> path = new LinkedList<String>();

        // TODO: traverse graph from startNode or endNode setting distances

        // can't get there from here
        return Collections.emptyList();
    }

    // graph node contains a word and adjacent neighbors
    public class Node {
        private String word;
        private Set<Node> neighbors = new HashSet<Node>();
        private Integer distance = null; // unknown

        public Node(String word) {
            this.word = word;
        }
        public String getWord() { return word; }
        public void addNeighbor(Node neighbor) {
            neighbors.add(neighbor);
        }
        /** makes neighbors permanently immutable */
        public void freezeNeighbors() {
            neighbors = Collections.unmodifiableSet(neighbors);
        }
        public Set<Node> getNeighbors() {
            return neighbors;
        }

        public void resetDistance() {
            distance = null;
        }
        public Integer getDistance() {
            return distance;
        }
        public void setDistance(int i) {
            distance = Integer.valueOf(i);
        }
    }
}
