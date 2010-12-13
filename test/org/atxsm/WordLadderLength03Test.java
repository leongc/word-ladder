package org.atxsm;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Cheng Leong
 * Date: Dec 5, 2010
 * Time: 2:37:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class WordLadderLength03Test {
    public static WordLadder wl;
    @BeforeClass
    public static void beforeClass() throws Exception {
        wl = new WordLadder("test/org/atxsm/length02.txt"); // 1022 words takes about 1.5 sec to load
    }
    @Test
    public void testShortPath() {
        assertArrayEquals(new String[] { "po", "pp", "tp" }, wl.computePath("po", "tp").toArray());
    }
    @Test
    public void testDisconnected() {
        assertTrue("Should be no path between aX and fE", wl.computePath("aX", "fE").isEmpty());
    }

}
