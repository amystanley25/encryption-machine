package enigma;


import java.util.Collection;

import static enigma.EnigmaException.*;

/**
 * Class that represents a complete enigma machine.
 *
 * @author Amy Stanley
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors.toArray(new Rotor[0]);
        _rotors = new Rotor[_numRotors];
        _plugboard = new Permutation("", _alphabet);
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     * #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     * undefined results.
     */
    Rotor getRotor(int k) {
        return _rotors[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        int count = 0;
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _allRotors.length; j++) {
                if (rotors[i].equals(_allRotors[j].name())) {
                    _rotors[i] = _allRotors[j];
                    count++;
                }
            }
            if (count == 0) {
                throw error("Bad rotor Name");
            }
        }
        for (int i = 0; i < _rotors.length; i++) {
            for (int j = i + 1; j < _rotors.length; j++) {
                if (_rotors[i] == _rotors[j]) {
                    throw error("Duplicate rotor Name");
                }
            }
        }
        if (!_rotors[0].reflecting()) {
            throw error("Reflector in wrong place");
        }
    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 characters in my alphabet. The first letter refers
     * to the leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw error("Incorrect String Length of Setting");
        }
        for (int i = 1; i < _rotors.length; i++) {
            _rotors[i].set(setting.charAt(i - 1));
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw error("Character in String Setting not in alphabet");
            }
        }
    }

    /**
     * Return the current plugboard's permutation.
     */
    Permutation plugboard() {
        return _plugboard;
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * the machine.
     */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /**
     * Advance all rotors to their next position.
     */
    private void advanceRotors() {
        boolean[] canMove = new boolean[numRotors()];
        for (int i = 1; i < numRotors(); i++) {
            boolean currRotor = _rotors[i].rotates() && _rotors[i].atNotch();
            boolean leftRotor = _rotors[i - 1].rotates();
            if (currRotor && leftRotor) {
                canMove[i - 1] = true;
                canMove[i] = true;
            }
        }
        if (numRotors() > 1) {
            canMove[numRotors() - 1] = true;
        }

        for (int i = 0; i < numRotors(); i++) {
            if (canMove[i]) {
                _rotors[i].advance();
            }
        }
    }

    /**
     * Return the result of applying the rotors to the character C (as an
     * index in the range 0..alphabet size - 1).
     */
    private int applyRotors(int c) {
        for (int in = _numRotors - 1; in >= 0; in -= 1) {
            c = _rotors[in].convertForward(c);
        }
        for (int out = 1; out < _numRotors; out += 1) {
            c = _rotors[out].convertBackward(c);
        }
        return c;
    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        String output = "";
        msg = msg.replaceAll("\\s", "");
        for (int i = 0; i < msg.length(); i++) {
            int intVal = _alphabet.toInt(msg.charAt(i));
            char encoded = _alphabet.toChar(convert(intVal));
            output += encoded;
        }
        return output;
    }

    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;
    /** Total number of rotors. */
    private int _numRotors;
    /** Total number of pawls. */
    private int _pawls;
    /** Char Array to keep track of Alphabet for the Machine. */
    private Permutation _plugboard;
    /** Array of Rotors actively used in the machine. */
    private Rotor[] _rotors;
    /** Array of all rotor possibilities in the machine, of
     * which only some are selected from this collection. */
    private Rotor[] _allRotors;
}
