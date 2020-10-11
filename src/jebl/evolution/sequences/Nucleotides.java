/*
 * Nucleotides.java
 *
 * (c) 2002-2005 JEBL Development Core Team
 *
 * This package may be distributed under the
 * Lesser Gnu Public Licence (LGPL)
 */
package jebl.evolution.sequences;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Uninstantiable utility class with only static methods.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: Nucleotides.java 986 2009-03-10 01:00:03Z matt_kearse $
 */
public final class Nucleotides {
    private Nucleotides() { } // make class uninstantiable

    public static final String NAME = "nucleotide";

    public static final int CANONICAL_STATE_COUNT = 4;
    public static final int STATE_COUNT = 17;

    public static final NucleotideState A_STATE = new NucleotideState("Adenine", "A", 0, (byte)0b0001);
    public static final NucleotideState C_STATE = new NucleotideState("Cytosine", "C",1, (byte)0b0010);
    public static final NucleotideState G_STATE = new NucleotideState("Guanine", "G", 2, (byte)0b0100);
    public static final NucleotideState T_STATE = new NucleotideState("Thymine", "T", 3, (byte)0b1000);

    // A: 0b0001 1
    // C: 0b0010 2
    // G: 0b0100 4
    // T: 0b1000 8
    // R: (byte)0b0101 5
    // Y: (byte)0b1010 10
    // M: (byte)0b0011 3
    // W: (byte)0b1001 9
    // S: (byte)0b0110 6
    // K: (byte)0b1100 12
    // B: (byte)0b1110 14
    // D: (byte)0b1101 13
    // H: (byte)0b1011 11
    // V: (byte)0b0111 7

    public static final NucleotideState R_STATE = new NucleotideState("A/G", "R", 4, (byte)0b0101, new NucleotideState[] {A_STATE, G_STATE});
    public static final NucleotideState Y_STATE = new NucleotideState("C/T", "Y", 5, (byte)0b1010, new NucleotideState[] {C_STATE, T_STATE});
    public static final NucleotideState M_STATE = new NucleotideState("A/C", "M", 6, (byte)0b0011, new NucleotideState[] {A_STATE, C_STATE});
    public static final NucleotideState W_STATE = new NucleotideState("A/T", "W", 7, (byte)0b1001, new NucleotideState[] {A_STATE, T_STATE});
    public static final NucleotideState S_STATE = new NucleotideState("C/G", "S", 8, (byte)0b0110, new NucleotideState[] {C_STATE, G_STATE});
    public static final NucleotideState K_STATE = new NucleotideState("G/T", "K", 9, (byte)0b1100, new NucleotideState[] {G_STATE, T_STATE});
    public static final NucleotideState B_STATE = new NucleotideState("C/G/T", "B", 10, (byte)0b1110, new NucleotideState[] {C_STATE, G_STATE, T_STATE});
    public static final NucleotideState D_STATE = new NucleotideState("A/G/T", "D", 11, (byte)0b1101, new NucleotideState[] {A_STATE, G_STATE, T_STATE});
    public static final NucleotideState H_STATE = new NucleotideState("A/C/T", "H", 12, (byte)0b1011, new NucleotideState[] {A_STATE, C_STATE, T_STATE});
    public static final NucleotideState V_STATE = new NucleotideState("A/C/G", "V", 13, (byte)0b0111, new NucleotideState[] {A_STATE, C_STATE, G_STATE});
    public static final NucleotideState N_STATE = new NucleotideState("Unknown base", "N", 14, (byte)0b1111, new NucleotideState[] {A_STATE, C_STATE, G_STATE, T_STATE});

    public static final NucleotideState UNKNOWN_STATE = new NucleotideState("Unknown base", "?", 15, (byte)0b1111, new NucleotideState[] {A_STATE, C_STATE, G_STATE, T_STATE});
    public static final NucleotideState GAP_STATE = new NucleotideState("Gap", "-", 16, (byte)0b1111, new NucleotideState[] {A_STATE, C_STATE, G_STATE, T_STATE});

    // Making an array public allows a client to modify its contents. Deprecating on 2007-10-10
    // and will become private in the future. Use {@link #getCanonicalStates} instead.
    @Deprecated
    public static final NucleotideState[] CANONICAL_STATES = new NucleotideState[] {
            A_STATE, C_STATE, G_STATE, T_STATE
    };

    // Making an array public allows a client to modify its contents. Deprecating on 2007-10-10
    // and will become private in the future. Use {@link #getStates} instead.
    @Deprecated
    public static final NucleotideState[] STATES = new NucleotideState[] {
        A_STATE, C_STATE, G_STATE, T_STATE,
        R_STATE, Y_STATE, M_STATE, W_STATE,
        S_STATE, K_STATE, B_STATE, D_STATE,
        H_STATE, V_STATE, N_STATE, UNKNOWN_STATE, GAP_STATE
    };

    // Making an array public allows a client to modify its contents. Deprecating on 2007-10-10
    // and will become private in the future. Use {@link #getComplementaryState} instead.
    @Deprecated
    public static final NucleotideState[] COMPLEMENTARY_STATES = new NucleotideState[]{
            T_STATE, G_STATE, C_STATE, A_STATE,
            Y_STATE, R_STATE, K_STATE, W_STATE,
            S_STATE, M_STATE, V_STATE, H_STATE,
            D_STATE, B_STATE, N_STATE, UNKNOWN_STATE, GAP_STATE
    };

    public static NucleotideState getComplementaryState(NucleotideState state) {
        return COMPLEMENTARY_STATES[state.getIndex()];
    }

    private static final int STATES_BY_CODE_SIZE = 128;

    // Static utility functions

	public static int getStateCount() { return STATE_COUNT; }

    /**
     *
     * @return A list of all possible states, including the gap and ambiguity states.
     */
    public static List<State> getStates() { return Collections.unmodifiableList(Arrays.asList((State[])STATES)); }

	public static int getCanonicalStateCount() { return CANONICAL_STATE_COUNT; }

	public static List<NucleotideState> getCanonicalStates() { return Collections.unmodifiableList(Arrays.asList(CANONICAL_STATES)); }

    public static NucleotideState getState(char code) {
        if (code < 0 || code >= STATES_BY_CODE_SIZE) {
            return null;
        }
        return statesByCode[code];
	}

    public static NucleotideState getState(String code) {
        return getState(code.charAt(0));
    }

    public static NucleotideState getState(int index) {
        return STATES[index];
    }

	public static NucleotideState getUnknownState() { return UNKNOWN_STATE; }

	public static NucleotideState getGapState() { return GAP_STATE; }

	public static boolean isUnknown(NucleotideState state) { return state == UNKNOWN_STATE; }

	public static boolean isGap(NucleotideState state) { return state == GAP_STATE; }

    /**
     * @return true if state1 and state2 are different non-ambigous states that are a transition.
     * (i.e. A-G or C-T)
     * @see #isPossibleTransition(State, State)
     */
    public static boolean isTransition(State state1, State state2) {
        // use A,G is even and C,T are odd
        if (state1.isGap() || state1.isAmbiguous() || state2.isGap() || state2.isAmbiguous() || state1==state2)
            return false;
        return ((state1.getIndex() + state2.getIndex()) & 0x1) == 0; // checks if sum is even
    }

    /**
     * @return true if state1 and state2 are different non-ambigous states that are a transversion.
     * (i.e. A-C or A-T or G-C or G-T)
     * @see #isPossibleTransversion(State, State)
     */
    public static boolean isTransversion(State state1, State state2) {
        if (state1.isGap() || state1.isAmbiguous() || state2.isGap() || state2.isAmbiguous() || state1 == state2)
            return false;
        return !isTransition(state1, state2);
    }

    /**
     * @return true if there is a possible transition betwen these states.
     * Possible transition means there is at least 1 transition between 
     * at least one of the possible combintaitons of non-ambiguous cannonical states
     * represented by the ambiguity symbols. If neither state is ambigous this
     * method returns true if and only if the states are a transversion.
     */
    public static boolean isPossibleTransition(char c1, char c2) {
        if (c1 > 127 || c2 > 127) return false;
        return isPossibleTransition[c1][c2];
    }

    /**
     * @return true if there is a possible transversion betwen these states.
     * Possible transversion means there is at least 1 transversion between
     * at least one of the possible combintaitons of non-ambiguous cannonical states
     * represented by the ambiguity symbols. If neither state is ambigous this
     * method returns true if and only if the states are a transversion.
     */
    public static boolean isPossibleTransversion(char c1, char c2) {
        if (c1 > 127 || c2 > 127) return false;
        return isPossibleTransversion[c1][c2];
    }

    /**
     * @return true if there is a possible transition betwen these states.
     * Possible transition means there is at least 1 transition between
     * at least one of the possible combintaitons of non-ambiguous cannonical states
     * represented by the ambiguity symbols. If neither state is ambigous this
     * method returns true if and only if the states are a transversion.
     */
    public static boolean isPossibleTransition(State s1, State s2) {
        return isPossibleTransition[s1.getCode().charAt(0)][s2.getCode().charAt(0)];
    }

    /**
     * @return true if there is a possible transversion betwen these states.
     * Possible transversion means there is at least 1 transversion between
     * at least one of the possible combintaitons of non-ambiguous cannonical states
     * represented by the ambiguity symbols. If neither state is ambigous this
     * method returns true if and only if the states are a transversion.
     */
    public static boolean isPossibleTransversion(State s1, State s2) {
        return isPossibleTransversion[s1.getCode().charAt(0)][s2.getCode().charAt(0)];
    }

    private static boolean calculateIsPossibleTransition(State s1, State s2) {
        if (s1.isGap() || s2.isGap()) return false;
        for (State state1 : s1.getCanonicalStates()) {
            for (State state2 : s2.getCanonicalStates()) {
                if (isTransition(state1, state2)) return true;
            }
        }
        return false;
    }

    private static boolean calculateIsPossibleTransversion(State s1, State s2) {
        if (s1.isGap() || s2.isGap()) return false;
        for (State state1 : s1.getCanonicalStates()) {
            for (State state2 : s2.getCanonicalStates()) {
                if (isTransversion(state1, state2)) return true;
            }
        }
        return false;
    }

   public static boolean isPurine(State state) {
        if (state.isAmbiguous()) {
            // return true only if all its ambiguities are isPurine()
            for (State state1 : state.getCanonicalStates()) {
                if(! isPurine(state1)) return false;
            }
            return true;
        }
        return state == A_STATE || state == G_STATE;
    }

    public static boolean isPyrimidine(State state) {
        if (state.isAmbiguous()) {
            // return true only if all its ambiguities are isPyrimidine()
            for (State state1 : state.getCanonicalStates()) {
                if (! isPyrimidine(state1)) return false;
            }
            return true;
        }
        return state == C_STATE || state == T_STATE;
    }
    public static boolean isGCstate(State state) {
        return ( state == G_STATE || state == C_STATE || state == S_STATE);
    }
    
    public static boolean isATstate(State state) {
        return (state == A_STATE || state == T_STATE || state == W_STATE);
    }

    public String getName() { return "Nucleotides"; }

	public static NucleotideState[] toStateArray(String sequenceString) {
		NucleotideState[] seq = new NucleotideState[sequenceString.length()];
		for (int i = 0; i < seq.length; i++) {
			seq[i] = getState(sequenceString.charAt(i));
		}
		return seq;
	}

	public static NucleotideState[] toStateArray(byte[] indexArray) {
	    NucleotideState[] seq = new NucleotideState[indexArray.length];
	    for (int i = 0; i < seq.length; i++) {
	        seq[i] = getState(indexArray[i]);
	    }
	    return seq;
	}

    /**
     * Convert an array of nucleotide states into an array of codon states
     * @param states the nucleotide states
     * @param readingFrame the reading frame (1 to 3)
     * @return the codon states
     */
    public static CodonState[] toCodons(final State[] states, int readingFrame) {
        if (states == null) throw new NullPointerException("States array is null");
        if (states.length == 0) return new CodonState[0];

        if (readingFrame < 1 || readingFrame > 3) {
            throw new IllegalArgumentException("Reading frame should be between 1 and 3");
        }

        if (states[0] instanceof NucleotideState) {
            int offset = readingFrame - 1;
            int length = states.length - offset;

            if (length == 0) return new CodonState[0];

            CodonState[] conversion = new CodonState[length / 3];
            for (int i = 0; i < conversion.length; i++) {
                conversion[i] = Codons.getState(
                        (NucleotideState)states[i * 3 + offset],
                        (NucleotideState)states[(i * 3) + offset + 1],
                        (NucleotideState)states[(i * 3) + offset + 2]);
            }
            return conversion;
        } else {
            throw new IllegalArgumentException("Given states are not nucleotides so cannot be converted");
        }
    }

    private static final NucleotideState[] statesByCode;

    static {
        statesByCode = new NucleotideState[STATES_BY_CODE_SIZE];
        for (int i = 0; i < statesByCode.length; i++) {
            // Undefined characters are mapped to null
            statesByCode[i] = null;
        }

        for (NucleotideState state : STATES) {
            statesByCode[state.getCode().charAt(0)] = state;
            statesByCode[Character.toLowerCase(state.getCode().charAt(0))] = state;
        }

        statesByCode['u'] = T_STATE;
        statesByCode['U'] = T_STATE;
    }


    private static boolean[][] isPossibleTransition;
    private static boolean[][] isPossibleTransversion;

    static {
        isPossibleTransition = new boolean[128][128];
        isPossibleTransversion = new boolean[128][128];
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                char c1 = (char) i;
                char c2 = (char) j;
                NucleotideState s1 = Nucleotides.getState(c1);
                NucleotideState s2 = Nucleotides.getState(c2);
                isPossibleTransition[i][j] = s1 != null && s2 != null && calculateIsPossibleTransition(s1, s2);
                isPossibleTransversion[i][j] = s1 != null && s2 != null && calculateIsPossibleTransversion(s1, s2);
            }
        }
    }


}