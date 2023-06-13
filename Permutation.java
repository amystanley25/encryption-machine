package enigma;

import static enigma.EnigmaException.*;

/**
 * Represents a permutation of a range of integers starting at 0 corresponding
 * to the characters of an alphabet.
 *
 * @author Amy Stanley
 */
class Permutation<Count> {

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    private String[] _cycles;

    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        if (cycles == "") {
            _cycles = new String[]{""};
        } else {
            String curr = cycles.substring(1, cycles.length() - 1);
            _cycles = curr.split("\\)\\(|\\)\\s\\(");
        }
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        int index = wrap(p);
        char cp = _alphabet.toChar(index);
        char permuted = permute(cp);
        return alphabet().toInt(permuted);
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        int index = wrap(c);
        char cc = _alphabet.toChar(index);
        char inverted = invert(cc);
        return alphabet().toInt(inverted);
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        char result = p;
        for (int i = 0; i < _cycles.length; i++) {
            int index = _cycles[i].indexOf(p);
            if (index == -1) {
                continue;
            } else {
                if (index + 1 >= _cycles[i].length()) {
                    index = 0;
                    result = _cycles[i].charAt(index);
                } else {
                    result = _cycles[i].charAt(index + 1);
                }
            }
            break;
        }
        return result;
    }

    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        char result = c;
        for (int i = 0; i < _cycles.length; i++) {
            int index = _cycles[i].indexOf(c);
            if (index == -1) {
                continue;
            } else {
                if (index - 1 < 0) {
                    index = _cycles[i].length() - 1;
                    result = _cycles[i].charAt(index);
                } else {
                    result = _cycles[i].charAt(index - 1);
                }
            }
            break;
        }
        return result;
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a
     * permutation for which no value maps to itself).
     */
    boolean derangement() {
        for (int i = 0; i < alphabet().size(); i++) {
            if (wrap(permute(i)) == i) {
                return false;
            }
        }
        return true;
    }

    /**
     * Alphabet of this permutation.
     */
    private Alphabet _alphabet;
}
