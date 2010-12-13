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
        wl = new WordLadder("test/org/atxsm/length03.txt"); // 21k words takes about 12min to load, 225M adj
    }
    @Test
    public void testShortPath() {
        assertArrayEquals(new String[] { "A&P", "A&A", "AvA", "evA", "evm", "mev" }, wl.computePath("A&P", "mev").toArray()); // 4603 searched
    }
    @Test
    public void testDisconnected() {
        assertTrue("Should be no path between aX and fE", wl.computePath("aX", "fE").isEmpty());
    }

}
