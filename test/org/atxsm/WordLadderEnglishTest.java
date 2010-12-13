package org.atxsm;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Cheng Leong
 * Date: Dec 5, 2010
 * Time: 2:37:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class WordLadderEnglishTest {
    public static WordLadder wl;
    @BeforeClass
    public static void beforeClass() throws Exception {
        wl = new WordLadder("test/org/atxsm/english.txt"); // 47k words takes about 5 mins to load
    }
    @Test
    public void testShortPath() {
        assertArrayEquals(new String[] { "pot", "top" }, wl.computePath("pot", "top").toArray());
    }
    @Test
    public void testDisconnected() {
        assertTrue("Should be no path between bx and ca", wl.computePath("bx", "ca").isEmpty());
    }

}
