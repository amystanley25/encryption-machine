package enigma;

import static enigma.EnigmaException.*;

/**
 * Class that represents a reflector in the enigma.
 *
 * @author Amy Stanley
 */
class Reflector extends FixedRotor {

    /**
     * A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM.
     */
    Reflector(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("Reflector must always be at position 0");
        }
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    int convertBackward(int e) {
        throw error("Reflector cannot convert backwards", e);
    }

}
