import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
* Created by IntelliJ IDEA.
* User: Cheng Leong
* Date: Dec 4, 2010
* Time: 9:04:43 PM
* To change this template use File | Settings | File Templates.
*/
public class WordLadderTest {
    public static WordLadder wl;
    @BeforeClass
    public static void beforeClass() throws Exception {
        String[] words = { "a", "b", "aa", "ab", "ac", "bb", "ca" };
        wl = new WordLadder(new HashSet<String>(Arrays.asList(words)));
    }

    @Test
    public void testNoPathToStart() throws Exception {
        assertTrue("Should be no path to start", wl.computePath("nosuchword", "b").isEmpty());
    }
    @Test
    public void testNoPathToEnd() throws Exception {
        assertTrue("Should be no path to end", wl.computePath("a", "nosuchword").isEmpty());
    }
    @Test
    public void testIdentity() throws Exception {
        assertArrayEquals(new String[] { "a" }, wl.computePath("a", "a").toArray());
    }
    @Test
    public void testNeighbors() {
        assertTrue("a is not b's neighbor", wl.graph.get("a").getNeighbors().contains(wl.graph.get("b")));
        assertTrue("b is not a's neighbor", wl.graph.get("b").getNeighbors().contains(wl.graph.get("a")));
    }
    @Test
    public void testOneChange() {
        assertTrue("failed one letter change a b", wl.isOneChange("a", "b"));
        assertTrue("failed one letter change aa ab", wl.isOneChange("aa", "ab"));
        assertTrue("failed one letter change ab bb", wl.isOneChange("ab", "bb"));
    }
    @Test
    public void testAdjacent() {
        assertTrue("failed adjacent a b", wl.isAdjacent("a", "b"));
    }
    @Test
    public void testAnagram() {
        assertTrue("failed anagram abc cab", wl.isAnagram("abc", "cab"));
    }
    @Test
    public void testOneLetterPath() {
        assertArrayEquals(new String[] { "b", "a" }, wl.computePath("b", "a").toArray());
    }
    @Test
    public void testReciprocalOneChange() {
        assertArrayEquals(new String[] { "a", "b" }, wl.computePath("a", "b").toArray());
    }
    @Test
    public void testAnagramPath() {
        assertArrayEquals(new String[] { "ac", "ca" }, wl.computePath("ac", "ca").toArray());
    }
    @Test
    public void testTwoLetterPath() {
        assertArrayEquals(new String[] { "aa", "ab", "bb" }, wl.computePath("aa", "bb").toArray());
    }
    @Test
    public void testDisconnected() {
        assertTrue("Should be no path between b and ca", wl.computePath("b", "ca").isEmpty());
    }
}
