package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/**
 * Enigma simulator.
 *
 * @author Amy Stanley
 */
public final class Main {

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            CommandArgs options =
                    new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                        + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Open the necessary files for non-option arguments ARGS (see comment
     * on main).
     */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        Machine enigma = readConfig();
        String msg = "";
        boolean checkStar = false;
        while (_input.hasNextLine()) {
            String first = _input.nextLine();
            if (first.startsWith("*")) {
                setUp(enigma, first);
                checkStar = true;
            } else {
                if (!checkStar) {
                    throw error("Invalid Config");
                }
                String encoded = enigma.convert(first);
                printMessageLine(encoded);
            }
        }
    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            String alphabet = _config.next();
            if (alphabet.equals("")) {
                throw new EnigmaException("No characters in config");
            }
            boolean asterisk = alphabet.contains("*");
            boolean closedParen = alphabet.contains(")");
            boolean openParen = alphabet.contains("(");
            if (asterisk || closedParen || openParen) {
                throw new EnigmaException("Invalid characters in config");
            }
            _alphabet = new Alphabet(alphabet);
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            ArrayList<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            String rotorSpec = _config.next();
            String cycles = "";
            String notches = "";
            while (_config.hasNext("\\(.+\\)")) {
                cycles += _config.next();
            }
            Permutation permutation = new Permutation(cycles, _alphabet);
            if (rotorSpec.charAt(0) == 'M') {
                notches += rotorSpec.substring(1);
                return new MovingRotor(rotorName, permutation, notches);
            } else if (rotorSpec.charAt(0) == 'N') {
                return new FixedRotor(rotorName, permutation);
            } else if (rotorSpec.charAt(0) == 'R') {
                return new Reflector(rotorName, permutation);
            } else {
                throw new EnigmaException("Wrong rotor type");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        try {
            String[] myRotors = new String[M.numRotors()];
            String plug = "";
            Scanner setup = new Scanner(settings);
            setup.next();
            for (int i = 0; i < myRotors.length; i++) {
                myRotors[i] = setup.next();
            }
            M.insertRotors(myRotors);
            M.setRotors(setup.next());
            while (setup.hasNext("\\(.+\\)")) {
                plug += setup.next();
            }
            Permutation perm = new Permutation(plug, _alphabet);
            M.setPlugboard(perm);
        } catch (NoSuchElementException excp) {
            throw error("settings poorly formatted");
        }
    }

    /**
     * Return true iff verbose option specified.
     */
    static boolean verbose() {
        return _verbose;
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 1) {
            _output.print(msg.charAt(i));
            if (((i + 1) % 5 == 0)) {
                _output.print(" ");
            }
        }
        _output.print("\n");
    }

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;

    /**
     * Source of input messages.
     */
    private Scanner _input;

    /**
     * Source of machine configuration.
     */
    private Scanner _config;

    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;

    /**
     * True if --verbose specified.
     */
    private static boolean _verbose;
}
