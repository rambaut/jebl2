/*
 * AminoAcids.java
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
 * @version $Id: AminoAcids.java 986 2009-03-10 01:00:03Z matt_kearse $
 */
public final class AminoAcids {
    private AminoAcids() { } // make class uninstantiable
    
    public static final String NAME = "amino acid";

    public static final int CANONICAL_STATE_COUNT = 22;
    public static final int STATE_COUNT = 29;

    public static final AminoAcidState A_STATE = new AminoAcidState("Alanine", "Ala", "A", 0);
    public static final AminoAcidState C_STATE = new AminoAcidState("Cysteine", "Cys", "C", 1);
    public static final AminoAcidState D_STATE = new AminoAcidState("Aspartic acid", "Asp", "D", 2);
    public static final AminoAcidState E_STATE = new AminoAcidState("Glutamic acid", "Glu", "E", 3);
    public static final AminoAcidState F_STATE = new AminoAcidState("Phenylalanine", "Phe", "F", 4);
    public static final AminoAcidState G_STATE = new AminoAcidState("Glycine", "Gly", "G", 5);
    public static final AminoAcidState H_STATE = new AminoAcidState("Histidine", "His", "H", 6);
    public static final AminoAcidState I_STATE = new AminoAcidState("Isoleucine", "Ile", "I", 7);
    public static final AminoAcidState K_STATE = new AminoAcidState("Lysine", "Lys", "K", 8);
    public static final AminoAcidState L_STATE = new AminoAcidState("Leucine", "Leu", "L", 9);
    public static final AminoAcidState M_STATE = new AminoAcidState("Methionine", "Met", "M", 10);
    public static final AminoAcidState N_STATE = new AminoAcidState("Asparagine", "Asn", "N", 11);
    public static final AminoAcidState P_STATE = new AminoAcidState("Proline", "Pro", "P", 12);    
    public static final AminoAcidState Q_STATE = new AminoAcidState("Glutamine", "Gln", "Q", 13);
    public static final AminoAcidState R_STATE = new AminoAcidState("Arginine", "Arg", "R", 14);
    public static final AminoAcidState S_STATE = new AminoAcidState("Serine", "Ser", "S", 15);
    public static final AminoAcidState T_STATE = new AminoAcidState("Threonine", "Thr", "T", 16);
    public static final AminoAcidState V_STATE = new AminoAcidState("Valine", "Val", "V", 17);
    public static final AminoAcidState W_STATE = new AminoAcidState("Tryptophan", "Trp", "W", 18);
    public static final AminoAcidState Y_STATE = new AminoAcidState("Tyrosine", "Tyr", "Y", 19);

    // TT: We've ordered all the other states alphabetically but I think it's better to let
    // Selenocysteine be #20 because it's the first "nonstandard" amino acid.
    public static final AminoAcidState U_STATE = new AminoAcidState("Selenocysteine", "Sec", "U", 20);
    // According to Wikipedia (2007-10-30), "O" for Pyrrolysine is now an official IUPAC
    // recommendation. BioJava already used this code by 2006-12-14 at the latest, although
    // I (TT) think it wasn't an IUPAC recommendation at that time.
    public static final AminoAcidState O_STATE = new AminoAcidState("Pyrrolysine", "Pyl", "O", 21);

    // Making an array public allows a client to modify its contents. Deprecating on 2007-10-10
    // and will become private in the future. Use {@link #getCanonicalStates} instead.
    @Deprecated
    public static final AminoAcidState[] CANONICAL_STATES = new AminoAcidState[]{
            A_STATE, C_STATE, D_STATE, E_STATE, F_STATE,
            G_STATE, H_STATE, I_STATE, K_STATE, L_STATE,
            M_STATE, N_STATE, P_STATE, Q_STATE, R_STATE,
            S_STATE, T_STATE, V_STATE, W_STATE, Y_STATE,
            U_STATE, O_STATE,
    };

    public static final AminoAcidState B_STATE = new AminoAcidState("Asparagine or aspartic acid", "Asx", "B", 22, new AminoAcidState[]{D_STATE, N_STATE});
    public static final AminoAcidState Z_STATE = new AminoAcidState("Glutamine or glutamic acid", "Glx", "Z", 23, new AminoAcidState[]{E_STATE, Q_STATE});
    public static final AminoAcidState J_STATE = new AminoAcidState("Leucine or Isoleucine", "Xle", "J", 24, new AminoAcidState[]{I_STATE, L_STATE});
    public static final AminoAcidState X_STATE = new AminoAcidState("Unknown amino acid", "Xaa", "X", 25, CANONICAL_STATES);
    public static final AminoAcidState UNKNOWN_STATE = new AminoAcidState("Unknown amino acid", "Xaa", "X", 26, CANONICAL_STATES);
    public static final AminoAcidState STOP_STATE = new AminoAcidState("Stop codon", " * ","*", 27);
    public static final AminoAcidState GAP_STATE = new AminoAcidState("Gap",  " - ", "-", 28, CANONICAL_STATES); // This really shouldn't include the canonical states, but I'm scared changing it may break stuff.

    // Making an array public allows a client to modify its contents. Deprecating on 2007-10-10
    // and will become private in the future. Use {@link #getStates} instead.
    @Deprecated
    public static final AminoAcidState[] STATES = new AminoAcidState[] {
            // Canonical states
            A_STATE, C_STATE, D_STATE, E_STATE, F_STATE,
            G_STATE, H_STATE, I_STATE, K_STATE, L_STATE,
            M_STATE, N_STATE, P_STATE, Q_STATE, R_STATE,
            S_STATE, T_STATE, V_STATE, W_STATE, Y_STATE,
            U_STATE, O_STATE,
            // Ambiguity states
            B_STATE, Z_STATE, J_STATE, X_STATE, UNKNOWN_STATE,
            // STOP_STATE represents the amino acid equivalent of a stop codon to cater for
            // situations arising from converting coding DNA to an amino acid sequences.
            STOP_STATE,
            GAP_STATE
    };

    // The following three arrays only contain meta information for the first 20 amino acids. They are not
    // used anywhere in Geneious and could potentially be removed/replaced
    // Chemical classifications
    public static final StateClassification CHEMICAL_CLASSIFICATION = new StateClassification.Default("chemical",
            new String[]{"alphatic", "phenylalanine", "sulphur", "glycine", "hydroxyl", "tryptophan", "tyrosine", "proline", "acidic", "amide", "basic"},
            new State[][]{{A_STATE, V_STATE, I_STATE, L_STATE}, {F_STATE}, {C_STATE, M_STATE}, {G_STATE}, {S_STATE, T_STATE},
                    {W_STATE}, {Y_STATE}, {P_STATE}, {D_STATE, E_STATE}, {N_STATE, Q_STATE}, {H_STATE, K_STATE, R_STATE}});

    // Hydropathy classifications
    public static final StateClassification HYDROPATHY_CLASSIFICATION = new StateClassification.Default("hydropathy",
            // should the first entry be "hydrophobic" instead of "hydropathic" ?
            new String[]{"hydropathic", "neutral", "hydrophilic"},
            new State[][]{{I_STATE, V_STATE, L_STATE, F_STATE, C_STATE, M_STATE, A_STATE, W_STATE},
                    {G_STATE, T_STATE, S_STATE, Y_STATE, P_STATE, H_STATE},
                    {D_STATE, E_STATE, K_STATE, N_STATE, Q_STATE, R_STATE}});

    // TT: I think the unit used here may be Angstrom^3, but I'm not sure; see http://www.imb-jena.de/IMAGE_AA.html
    public static final StateClassification VOLUME_CLASSIFICATION = new StateClassification.Default("volume",
            new String[]{"60-90", "108-117", "138-154", "162-174", "189-228"},
            new State[][]{{G_STATE, A_STATE, S_STATE}, {C_STATE, D_STATE, P_STATE, N_STATE, T_STATE},
                    {E_STATE, V_STATE, Q_STATE, H_STATE}, {M_STATE, I_STATE, L_STATE, K_STATE, R_STATE},
                    {F_STATE, Y_STATE, W_STATE}});

    /**
     * A table to map state numbers (0-27) to their three letter codes.
     */
    private static final String[] AMINOACID_TRIPLETS = {
            // A     C      D      E      F      G      H      I      K
            "Ala", "Cys", "Asp", "Glu", "Phe", "Gly", "His", "Ile", "Lys",
            // L     M      N      P      Q      R      S      T      V
            "Leu", "Met", "Asn", "Pro", "Gln", "Arg", "Ser", "Thr", "Val",
            //W      Y      U      O
            "Trp", "Tyr", "Sec", "Pyl",
            //B      Z      J      X      *      ?     -
            "Asx", "Glx", "Xle", " X ", " * ", " ? ", " - "
    };
    private static final int STATE_BY_CODE_SIZE = 128;

    public static int getStateCount() {
        return STATE_COUNT;
    }

    //public static List<State> getStates() { return Collections.unmodifiableList(Arrays.asList((State[])STATES)); }
    public static List<AminoAcidState> getStates() {
        return Collections.unmodifiableList(Arrays.asList(STATES));
    }

    public static int getCanonicalStateCount() {
        return CANONICAL_STATE_COUNT;
    }

    public static List<State> getCanonicalStates() {
        return Collections.unmodifiableList(Arrays.asList((State[]) CANONICAL_STATES));
    }

    public static AminoAcidState getState(char code) {
        if (code < 0 || code >= STATE_BY_CODE_SIZE) {
            return null;
        }
        return statesByCode[code];
    }

    public static AminoAcidState getState(String code) {
        return getState(code.charAt(0));
    }

    public static AminoAcidState getState(int index) {
        return STATES[index];
    }

    public static AminoAcidState getUnknownState() {
        return UNKNOWN_STATE;
    }

    public static AminoAcidState getGapState() {
        return GAP_STATE;
    }

    public static boolean isUnknown(AminoAcidState state) {
        return state == UNKNOWN_STATE;
    }

    public static boolean isGap(AminoAcidState state) {
        return state == GAP_STATE;
    }

    // TT: This method is badly named because the word "triplet" is usually used for
    // a sequence of three DNA/RNA states that represent one amino acid; the
    // three-letter name for amino acids is just called their three-letter code, I think.
    public static String getTripletCode(AminoAcidState state) {
        return AMINOACID_TRIPLETS[state.getIndex()];
    }

    public static AminoAcidState[] toStateArray(String sequenceString) {
        AminoAcidState[] seq = new AminoAcidState[sequenceString.length()];
        for (int i = 0; i < seq.length; i++) {
            seq[i] = getState(sequenceString.charAt(i));
        }
        return seq;
    }

    public static AminoAcidState[] toStateArray(byte[] indexArray) {
        AminoAcidState[] seq = new AminoAcidState[indexArray.length];
        for (int i = 0; i < seq.length; i++) {
            seq[i] = getState(indexArray[i]);
        }
        return seq;
    }

    private static final AminoAcidState[] statesByCode;

    static {
        statesByCode = new AminoAcidState[STATE_BY_CODE_SIZE];
        for (int i = 0; i < statesByCode.length; i++) {
            // Undefined characters are mapped to null
            statesByCode[i] = null;
        }

        for (AminoAcidState state : STATES) {
            final char code = state.getCode().charAt(0);
            statesByCode[code] = state;
            statesByCode[Character.toLowerCase(code)] = state;
        }
    }
}
