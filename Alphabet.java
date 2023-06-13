package enigma;
import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Amy Stanley
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        if (chars.length() < 1) {
            throw error("Invalid alphabet less than 0");
        }

        letters = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            letters[i] = chars.charAt(i);
        }

        for (int i = 0; i < letters.length; i++) {
            for (int j = i + 1; j < letters.length; j++) {
                if (letters[i] == letters[j]) {
                    throw error("Duplicate character in alphabet");
                }
            }
        }
    }
    /** Char Array to keep track of Alphabet for the Machine. */
    private char[] letters;

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return letters.length;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < letters.length; i++) {
            if (letters[i] == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= letters.length) {
            throw error("character index out of range");
        }
        return letters[index];
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < size(); i++) {
            if (letters[i] == ch) {
                return i;
            }
        }
        throw new EnigmaException("character not in alphabet");
    }
}
