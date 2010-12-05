import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
        String[] words = { "a", "b" };
        wl = new WordLadder(new HashSet(Arrays.asList(words)));
    }

    @Test
    public void testNoPathToStart() throws Exception {
        assertTrue("Should be no path to start",
                wl.computePath("nosuchword", "b").isEmpty());
    }
    @Test
    public void testNoPathToEnd() throws Exception {
        assertTrue("Should be no path to end",
                wl.computePath("a", "nosuchword").isEmpty());
    }
    @Test
    public void testTrivial() throws Exception {
        assertArrayEquals("start is end", new String[] { "a" } , wl.computePath("a", "a").toArray());
    }
}
