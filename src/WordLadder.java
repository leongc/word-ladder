import java.io.*;
import java.util.*;

/**
 * Calculates a shortest path from start word to end word.
 * Each step in the path is a dictionary entry
 * and each step is one of the following transformations:<ul>
 * <li>change one letter</li>
 * <!-- TODO: add/remove one letter -->
 * <li>reorder letters (anagram)</li>
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

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Syntax: WordLadder <startword> <endword> <dictionaryfile>");
            return;
        }
        WordLadder wl = new WordLadder(loadFile(args[2]));
        List<String> path = wl.computePath(args[0], args[1]);
        if (path == null || path.isEmpty()) {
            System.out.println("Can't get from " + args[0] + " to " + args[1] + " in " + MAX_DISTANCE + " steps");
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

    private long adjacentCount = 0;
    private long anagramCount = 0;
    Map<String, GraphNode> graph;

    public WordLadder(Set<String> corpus) {
        graph = new HashMap<String, GraphNode>(corpus.size());
        Map<Integer, Set<GraphNode>> nodesByLength = new HashMap<Integer, Set<GraphNode>>();
        for (String s : corpus) {
            GraphNode n = graph.get(s);
            if (n == null) {
                n = new GraphNode(s);
            }
            graph.put(s, n);

            // evaluate adding n as a neighbor to existing nodes of the same length
            Set<GraphNode> nodesForLength = nodesByLength.get(s.length());
            if (nodesForLength == null) {
                nodesForLength = new HashSet<GraphNode>();
                nodesByLength.put(s.length(), nodesForLength);
            }
            for (GraphNode candidate : nodesForLength) {
                if (isAdjacent(s, candidate.getWord())) {
                    candidate.addNeighbor(n);
                    n.addNeighbor(candidate);
                }
            }
            
            nodesForLength.add(n);
        }

        for (GraphNode n : graph.values()) {
            n.freezeNeighbors();
        }

        System.out.println("Inserted: " + graph.size() + "; adj: " + adjacentCount + "; ana: " + anagramCount);
    }
    // graph node contains a word and adjacent neighbors
    public class GraphNode {
        private String word;
        private Set<GraphNode> neighbors = new HashSet<GraphNode>();

        public GraphNode(String word) {
            this.word = word;
        }
        public String getWord() { return word; }
        public void addNeighbor(GraphNode neighbor) {
            neighbors.add(neighbor);
        }

        /** makes neighbors permanently immutable */
        public void freezeNeighbors() {
            neighbors = Collections.unmodifiableSet(neighbors);
        }
        public Set<GraphNode> getNeighbors() {
            return neighbors;
        }

    }


    boolean isAdjacent(String s1, String s2) {
        adjacentCount++;
        return isOneChange(s1, s2) || isAnagram(s1, s2) /* ||
                isOneShorter(s1, s2) ||
                isOneShorter(s2, s1) */ ;
    }

    boolean isAnagram(String s1, String s2) {
        anagramCount++;
        return sort(s1).equals(sort(s2));
    }

    private List<Character> sort(String s) {
        List<Character> chars = new ArrayList<Character>(s.length());
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
        for (GraphNode n : graph.values()) {
            sb.append(n.getWord()).append('\t');
            for (GraphNode neighbor : n.getNeighbors()) {
                sb.append(neighbor.getWord()).append(',');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public List<String> computePath(String startWord, String endWord) {
        GraphNode startNode = graph.get(startWord);
        if (startNode == null) {
            return Collections.emptyList(); // unknown start
        }
        GraphNode endNode = graph.get(endWord);
        if (endNode == null) {
            return Collections.emptyList(); // unknown end
        }
        if (startNode == endNode) {
            return Collections.singletonList(endWord); // nothing to do
        }

        // traverse graph from startNode setting distances
        Map<GraphNode, Step> nodeToStepMap = new HashMap<GraphNode, Step>();
        Deque<GraphNode> nodesToSearch = new LinkedList<GraphNode>();
        nodeToStepMap.put(startNode, new Step(0, startWord, null));
        nodesToSearch.addLast(startNode);

        int distance = 0;
        int answerDistance = MAX_DISTANCE;
        while (!nodesToSearch.isEmpty() && distance < answerDistance) {
            final GraphNode node = nodesToSearch.removeFirst();
            final Step step = nodeToStepMap.get(node);
            distance = step.getDistance();
            for (GraphNode neighbor : node.getNeighbors()) {
                if (!nodeToStepMap.containsKey(neighbor)) {
                    final Step neighborStep = new Step(distance + 1, neighbor.getWord(), step);
                    nodeToStepMap.put(neighbor, neighborStep);
                    nodesToSearch.addLast(neighbor);
                    if (neighbor == endNode) {
                        answerDistance = distance + 1;
                        // if !findAllSolutions
                        final List<String> path = neighborStep.getPath();
                        System.out.println(startWord + "->" + endWord + "\t" + path + "\tSearched: " + nodeToStepMap.size());
                        return path;
                    }
                }
            }
        }

        // can't get there from here within MAX_DISTANCE
        // System.out.println(graphToString());
        System.out.println(startWord + "->" + endWord + "\t[No Path]\tSearched: " + nodeToStepMap.size());
        return Collections.emptyList();
    }

    private class Step {
        private int distance;
        private String word;
        private Step nextStep;
        public Step(int i, String s, Step step) {
            distance = i;
            word = s;
            nextStep = step;
        }

        public int getDistance() {
            return distance;
        }

        /**
         * Builds a path backwards from the current step to the start (indicated by a null nextStep)
         * @return list of words from the start to here
         */
        public List<String> getPath() {
            LinkedList<String> result = new LinkedList<String>();
            result.add(word);
            Step currentStep = this;
            while (currentStep.nextStep != null) {
                currentStep = currentStep.nextStep;
                result.addFirst(currentStep.word);
            }
            return result;
        }
    }
}
