package enigma;

import org.junit.Test;

import static org.junit.Assert.*;
import static enigma.TestUtils.*;

public class AlphabetTest {

    @Test
    public void test1() {
        Alphabet test = new Alphabet("ABCD");
        assertEquals(4, test.size());
        assertTrue(test.contains('A'));
        assertFalse(test.contains('Z'));
        assertEquals(0, test.toInt('A'));
        assertEquals(3, test.toInt('D'));
        assertEquals('B', test.toChar(1));
        assertEquals('A', test.toChar(0));
    }

    @Test
    public void testMoreComplicated() {
        String testString = "abcdyz1892";
        Alphabet test = new Alphabet(testString);
        assertEquals(10, test.size());
        for (int i = 0; i < testString.length(); i++) {
            char curr = testString.charAt(i);
            assertEquals(curr, test.toChar(i));
            assertEquals(i, test.toInt(curr));
            assertTrue(test.contains(curr));
        }
        assertFalse(test.contains('A'));
    }
}
