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
    private static final int MAX_DISTANCE = 10; // give up after this many steps

    public static final void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Syntax: WordLadder <startword> <endword> <dictionaryfile>");
            return;
        }
        WordLadder wl = new WordLadder(loadFile(args[2]));
        List<String> path = wl.computePath(args[0], args[1]);
        if (path == null || path.isEmpty()) {
            System.out.println("Can't get from " + args[0] + " to " + args[1] + " in less than " + MAX_DISTANCE);
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
        Map<Integer, Set<Node>> nodesByLength = new HashMap<Integer, Set<Node>>();
        for (String s : corpus) {
            Node n = graph.get(s);
            if (n == null) {
                n = new Node(s);
            }

            graph.put(s, n);

            // evaluate adding n as a neighbor to existing nodes of the same length
            Set<Node> nodesForLength = nodesByLength.get(s.length());
            if (nodesForLength == null) {
                nodesForLength = new HashSet<Node>();
                nodesByLength.put(s.length(), nodesForLength);
            }
            for (Node candidate : nodesForLength) {
                if (isAdjacent(s, candidate.getWord())) {
                    candidate.addNeighbor(n);
                    n.addNeighbor(candidate);
                }
            }
            
            nodesForLength.add(n);
        }

        for (Node n : graph.values()) {
            n.freezeNeighbors();
        }
    }

    boolean isAdjacent(String s1, String s2) {
        return isOneChange(s1, s2) || isAnagram(s1, s2) /* ||
                isOneShorter(s1, s2) ||
                isOneShorter(s2, s1) */ ;
    }

    boolean isAnagram(String s1, String s2) {
        return /* s.length() == word.length() && */
                sort(s1).equals(sort(s2));
    }

    private List<Character> sort(String s) {
        List<Character> chars = new ArrayList(s.length());
        for (Character c : s.toCharArray()) {
            chars.add(c);
        }
        Collections.sort(chars);
        return chars;
    }

    boolean isOneChange(String s1, String s2) {
        final boolean firstCharSame = s1.charAt(0) == s2.charAt(0);
        final String s1tail = s1.substring(1);
        final String s2tail = s2.substring(1);
        return firstCharSame ? isOneChange(s1tail, s2tail) : s1tail.equals(s2tail);
    }
    
    String graphToString() {
        StringBuilder sb = new StringBuilder();
        for (Node n : graph.values()) {
            sb.append(n.getWord()).append('\t').append(n.getDistance()).append('\t');
            for (Node neighbor : n.getNeighbors()) {
                sb.append(neighbor.getWord()).append(',');
            }
            sb.append('\n');
        }
        return sb.toString();
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

        // reset distances
        for (Node n : graph.values()) {
            n.resetDistance();
        }

        // traverse graph from startNode setting distances
        Deque<Node> nodesToSearch = new LinkedList<Node>();
        startNode.setDistance(0);
        nodesToSearch.addLast(startNode);

        int distance = 0;
        int answerDistance = MAX_DISTANCE;
        while (!nodesToSearch.isEmpty() && distance < answerDistance) {
            final Node node = nodesToSearch.removeFirst();
            distance = node.getDistance();
            for (Node neighbor : node.getNeighbors()) {
                if (neighbor.getDistance() == null) {
                    neighbor.setDistance(distance + 1);
                    nodesToSearch.addLast(neighbor);
                    if (neighbor == endNode) {
                        answerDistance = distance + 1;
                        // if !findAllSolutions
                        return pathTo(endNode);
                    }
                }
            }
        }

        // can't get there from here within MAX_DISTANCE
        return Collections.emptyList();
    }

    /**
     * Builds a path from the given node using decreasing distances.
     * This method assumes a solution exists in the graph.
     * @param endNode final node (maximum distance)
     * @return nodes from a node with zero distance to endNode
     */
    List<String> pathTo(Node endNode) {
        String[] result = new String[endNode.getDistance()+1];
        Node n = endNode;
        for (int i = result.length; i >= 0; i--) {
            result[n.getDistance()] = n.getWord();
            for (Node neighbor : n.getNeighbors()) {
                final Integer d = neighbor.getDistance();
                if (d != null && d == i-1) {
                    n = neighbor;
                    break;
                }
            }
        }
        return Arrays.asList(result);
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
