/*
 * GeneticCode.java
 *
 * (c) 2002-2005 JEBL Development Core Team
 *
 * This package may be distributed under the
 * Lesser Gnu Public Licence (LGPL)
 */

package jebl.evolution.sequences;

import jebl.util.MaybeBoolean;

import java.util.*;

/**
 * A set of standard genetic codes.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: GeneticCode.java 1044 2009-12-09 03:33:54Z stevensh $
 */

public final class GeneticCode {
    private final Map<CodonState, AminoAcidState> translationMap;

    private static final CodonState DEFAULT_START_CODON = Codons.getState("ATG");
    private static final Set<CodonState> DEFAULT_START_CODONS = Collections.singleton(DEFAULT_START_CODON);

    public static final GeneticCode
            UNIVERSAL = new GeneticCode("universal", "Standard", "KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSS*CWCLFLF", 1),
            VERTEBRATE_MT = new GeneticCode("vertebrateMitochondrial", "Vertebrate Mitochondrial", "KNKNTTTT*S*SMIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF", 2),
            YEAST = new GeneticCode("yeast", "Yeast Mitochondrial",  "KNKNTTTTRSRSMIMIQHQHPPPPRRRRTTTTEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF", 3),
            MOLD_PROTOZOAN_MT = new GeneticCode("moldProtozoanMitochondrial", "Mold Protozoan Mitochondrial", "KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF", 4),
            MYCOPLASMA = new GeneticCode("mycoplasma", "Mycoplasma", "KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF"),
            INVERTEBRATE_MT = new GeneticCode("invertebrateMitochondrial", "Invertebrate Mitochondrial", "KNKNTTTTSSSSMIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF", 5),
            CILIATE = new GeneticCode("ciliate", "Ciliate", "KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVVQYQYSSSS*CWCLFLF", 6),
            ECHINODERM_MT = new GeneticCode("echinodermMitochondrial", "Echinoderm Mitochondrial", "NNKNTTTTSSSSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF", 9),
            EUPLOTID_NUC = new GeneticCode("euplotidNuclear", "Euplotid Nuclear", "KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSCCWCLFLF", 10),
            BACTERIAL = new GeneticCode("bacterial", "Bacterial", "KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSS*CWCLFLF", 11),
            ALT_YEAST = new GeneticCode("alternativeYeast", "Alternative Yeast", "KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLSLEDEDAAAAGGGGVVVV*Y*YSSSS*CWCLFLF", 12),
            ASCIDIAN_MT = new GeneticCode("ascidianMitochondrial", "Ascidian Mitochondrial", "KNKNTTTTGSGSMIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF", 13),
            FLATWORM_MT = new GeneticCode("flatwormMitochondrial", "Flatworm Mitochondrial", "NNKNTTTTSSSSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVVYY*YSSSSWCWCLFLF", 14),
            BLEPHARISMA_NUC = new GeneticCode("blepharismaNuclear", "Blepharisma Nuclear", "KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*YQYSSSS*CWCLFLF", 15);

    private static final List<GeneticCode> GENETIC_CODES_LIST = Collections.unmodifiableList(Arrays.asList(
            UNIVERSAL, VERTEBRATE_MT, YEAST, MOLD_PROTOZOAN_MT, MYCOPLASMA, INVERTEBRATE_MT,
            CILIATE, ECHINODERM_MT, EUPLOTID_NUC, BACTERIAL, ALT_YEAST, ASCIDIAN_MT,
            FLATWORM_MT, BLEPHARISMA_NUC
    ));

    /**
     * Returns an iterable that allows you to iterate over all the standard genetic codes
     * @return An iterable over the genetic codes
     */
    public static Iterable<GeneticCode> getGeneticCodes() {
        return GENETIC_CODES_LIST;
    }

    public static GeneticCode[] getGeneticCodesArray() {
        return GENETIC_CODES_LIST.toArray(new GeneticCode[GENETIC_CODES_LIST.size()]);
    }

    /**
     * Use of this field is deprecated because being an array it is mutable, i.e. an attacker could
     * potentially replace values in this array.
     * @deprecated use {@link #getGeneticCodes()} instead
     */
    @Deprecated
    public static final GeneticCode[] GENETIC_CODES = GENETIC_CODES_LIST.toArray(new GeneticCode[GENETIC_CODES_LIST.size()]);

    private int ncbiTranslationTableNumber;
    private final Set<CodonState> startCodons;
    private final String name, description, codeTable;

    /**
     * @param name the name of the genetic code
     * @return the genetic code such that {@link #getDescription()} equals name
     */
    public static GeneticCode valueOf(String name) {
        for(GeneticCode code : GENETIC_CODES_LIST) {
            if(code.getDescription().equals(name)){
                return code;
            }
        }
        return null;
    }

    /**
     * @param NCBITranslationTableNumber the name of the genetic code
     * @return the genetic code such that {@link #getNcbiTranslationTableNumber()} equals NCBITranslationTableNumber
     */
    public static GeneticCode valueOf(int NCBITranslationTableNumber) {
        for(GeneticCode code : GENETIC_CODES_LIST) {
            if(code.getNcbiTranslationTableNumber() == NCBITranslationTableNumber){
                return code;
            }
        }
        return null;
    }

    /**
     * Same as {@link #GeneticCode(String, String, String, int, java.util.Set)}(name, description, codeTable, -1, DEFAULT_START_CODONS).
     */
    private GeneticCode(String name, String description, String codeTable) {
        this(name, description, codeTable, -1, DEFAULT_START_CODONS);
    }

    /**
     * Same as {@link #GeneticCode(String, String, String, int, java.util.Set)}(name, description, codeTable, ncbiTranslationTableNumber, DEFAULT_START_CODONS).
     */
    private GeneticCode(String name, String description, String codeTable, int ncbiTranslationTableNumber) {
        this(name, description, codeTable, ncbiTranslationTableNumber, DEFAULT_START_CODONS);
    }

    /**
     * Constructs a new GeneticCode.
     * @param name Name of the genetic code (from GENBANK)
     * @param description Description of the genetic code (from GENBANK)
     * @param codeTable A length-64 string of uppercase amino acid characters (see {@link jebl.evolution.sequences.AminoAcids#getState(char)}),
     *        each character representing the translation of one triplet, with the triplet translations being in the order
     *        AAA, AAC, AAG, AAT, ACA etc. (i.e. first codon position is most significant, and nucleotides come in the
     *        order A, C, G, T) (Note: This is not the order used by the Genbank website).
     * @param ncbiTranslationTableNumber the number used by NCBI to represent this genetic code or -1 if none. Eg. 1 = Standard...
     * @param startCodons Set of start codons (defaults to ATG only).
     *        Note that 23% of E.Coli are not ATG (see http://en.wikipedia.org/wiki/Start_codon).
     *        See also http://www.biomatters.com/userforum/comments.php?DiscussionID=177
     */
    private GeneticCode(final String name, final String description, final String codeTable, int ncbiTranslationTableNumber, Set<CodonState> startCodons) {
        this.name = name;
        this.description = description;
        this.codeTable = codeTable;
        this.ncbiTranslationTableNumber = ncbiTranslationTableNumber;
        this.startCodons = startCodons;

        Map<CodonState, AminoAcidState> translationMap = new TreeMap<CodonState, AminoAcidState>();

        if (codeTable.length() != 64) {
            throw new IllegalArgumentException("Code Table length does not match number of codon states");
        }

        for (int i = 0; i < codeTable.length(); i++) {
            CodonState codonState = Codons.CANONICAL_STATES[i];
            AminoAcidState aminoAcidState = AminoAcids.getState(codeTable.substring(i, i+1));
            translationMap.put(codonState, aminoAcidState);
        }
        translationMap.put(Codons.getGapState(), AminoAcids.getGapState());
        translationMap.put(Codons.getUnknownState(), AminoAcids.getUnknownState());

        this.translationMap = Collections.unmodifiableMap(translationMap);
    }

    /**
	 * Returns the name of the genetic code
     * @return the name of this genetic code
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the description of the genetic code
     * @return the description of this genetic code
	 */
	public String getDescription() {
		return description;
	}

    /**
     * Returns a length-64 string that for each nucleotide triplet contains the single-character
     * amino acid code (see {@link AminoAcids} to which that triplet is translated in this genetic code.
     * @return the string passed to the constructor as the <code>codeTable</code> argument.
     */
    public String getCodeTable() {
        return codeTable;
    }

	/**
	 * Returns the state associated with AminoAcid represented by codonState.
	 * Note that the state is the canonical state (generated combinatorially)
	 * @see AminoAcids
	 * @see Codons
	 * @return '?' if codon unknown
	 */
	public AminoAcidState getTranslation(CodonState codonState) {
        //System.out.println(codonState.getCode());
        return translationMap.get(codonState);
	}

    /**
	 * Returns the state associated with AminoAcid represented by the three nucleotides.
	 * If one or more of the nucleotides are ambiguous, and all combinations translate to the
     * same protein, then this method will return that protein
	 * @see AminoAcids
	 * @see Codons
	 * @return '?' if codon unknown
	 */
    public AminoAcidState getTranslation(NucleotideState nucleotide1, NucleotideState nucleotide2, NucleotideState nucleotide3){
        CodonState translateState = null;
        if (nucleotide1.isGap() && nucleotide2.isGap() && nucleotide3.isGap()) {
			translateState = Codons.GAP_STATE;
		}

		if (nucleotide1.isAmbiguous() || nucleotide2.isAmbiguous() || nucleotide3.isAmbiguous()) {
            for(State a : nucleotide1.getCanonicalStates()){
                for(State b : nucleotide2.getCanonicalStates()){
                    for(State c : nucleotide3.getCanonicalStates()){
                        CodonState thisDisambiguation = Codons.getState(a.getCode() + b.getCode() + c.getCode());
                        //initial setup
                        if(translateState == null)
                            translateState = thisDisambiguation;
                        // If different nucleotide disambiguations yield different amino acids, translation is unknown
                        if(!translationMap.get(translateState).equals(translationMap.get(thisDisambiguation)))
                            return translationMap.get(Codons.UNKNOWN_STATE);
                    }
                }
            }
            return translationMap.get(translateState);
        } else {
            String code = nucleotide1.getCode() + nucleotide2.getCode() + nucleotide3.getCode();
            translateState = Codons.getState(code);
            return translationMap.get(translateState);
        }
    }

    /**
	 * Returns the state associated with AminoAcid represented by the three nucleotides.
	 * If one or more of the nucleotides are ambiguous, and all combinations translate to the
     * same protein, then this method will return that protein
     * @param nucleotides a string consisting of exactly 3 residues in any case.
	 * @see AminoAcids
	 * @see Codons
	 * @return '?' if codon unknown
	 */
    public AminoAcidState getTranslation(String nucleotides) {
        if (nucleotides.length()!=3) throw new IllegalArgumentException("getTranslation requires a nucleotide triplet. (given "+nucleotides.length()+" characters)");
        NucleotideState n1=Nucleotides.getState(nucleotides.charAt(0));
        NucleotideState n2=Nucleotides.getState(nucleotides.charAt(1));
        NucleotideState n3=Nucleotides.getState(nucleotides.charAt(2));
        if (n1==null) {
            n1=Nucleotides.UNKNOWN_STATE;
        }
        if (n2==null) {
            n2=Nucleotides.UNKNOWN_STATE;
        }
        if (n3==null) {
            n3=Nucleotides.UNKNOWN_STATE;
        }
        return getTranslation(n1,n2,n3);
    }

    /**
     * Extracts the three nucleotide or ambiguity states from a nucleotide triplet string
     * @param tripletString The string to be checked
     * @return an array containing the three NucleotideStates corresponding to the tripletString
     * @throws IllegalArgumentException if tripletString doesn't consist of 3 nucleotide or ambiguity symbols
     * @throws NullPointerException if tripletString is null
     */
    private State[] getTripletStates(String tripletString) throws IllegalArgumentException {
        boolean isValidTriplet = (tripletString.length() == 3);
        State[] states = new State[3];
        for (int i = 0; i < 3; i++) {
            states[i] = Nucleotides.getState(tripletString.charAt(i));
            isValidTriplet &= (states[i] != null);
        }
        if (!isValidTriplet) {
            throw new IllegalArgumentException("Expected valid nucleotide triplet, got '" + tripletString + "'");
        } else {
            return states;
        }
    }

    /**
     * Checks whether all possible disambiguations of a given nucleotide triplet
     * string represents a start codon.
     *
     * @param tripletString A string of length 3, with each character representing one nucleotide or ambiguity symbol
     * @return Whether all possible disambiguations of tripletString represent a start codon.
     * @throws IllegalArgumentException if tripletString doesn't consist of 3 nucleotide or ambiguity symbols
     * @throws NullPointerException if tripletString is null
     */
    public MaybeBoolean isStartCodonString(String tripletString) throws IllegalArgumentException{
        State[] states = getTripletStates(tripletString);
        boolean startFound = false, nonStartFound = false;
        for (State a : states[0].getCanonicalStates()) {
            for (State b : states[1].getCanonicalStates()) {
                for (State c : states[2].getCanonicalStates()) {
                    CodonState codonState = Codons.getState(a.getCode() + b.getCode() + c.getCode());
                    boolean isStart = startCodons.contains(codonState);
                    startFound = (startFound || isStart);
                    nonStartFound = (nonStartFound || !isStart);
                    // IntelliJ 6.0.5 claims the following expression is always false, but this is not true.
                    if (startFound && nonStartFound) {
                        return MaybeBoolean.Maybe;
                    }
                }
            }
        }
        return startFound ? MaybeBoolean.True : MaybeBoolean.False;
    }

    /**
     * As of 2007-07-30, {@link jebl.evolution.sequences.CodonState}s exist only
     * for nonambiguous nucleotide triplets. Therefore, this method cannot be
     * used to check if an ambiguous triplet of nucleotides codes for a transcription,
     * start and therefore this method is deprecated.
     * @param codonState
     * @return True if the specified codonState codes for a transcription start under this genetic code.
     */
    @Deprecated
    public boolean isStartCodon(CodonState codonState) {
        return isStartCodonString(codonState.getCode()) == MaybeBoolean.True;
    }

    /**
     * As of 2007-07-30, {@link jebl.evolution.sequences.CodonState}s exist only
     * for nonambiguous nucleotide triplets. Therefore, this method cannot be
     * used to check if an ambiguous triplet of nucleotides codes for a stop,
     * and therefore this method is deprecated.
     * @param codonState
     * @return True if the specified codonState codes for a stop under this genetic code.
     */
    @Deprecated
    public boolean isStopCodon(CodonState codonState) {
        return isStopCodonString(codonState.getCode()) == MaybeBoolean.True;
    }

    /**
	 * Checks whether a given String represents a stop codon.
     * @param tripletString A string of length 3, with each character representing one nucleotide or ambiguity symbol
     * @return true if tripletString represents a stop codon.
     * @throws IllegalArgumentException if tripletString doesn't consist of 3 nucleotide or ambiguity symbols
     * @throws NullPointerException if tripletString is null
	 */
	public MaybeBoolean isStopCodonString(String tripletString) throws IllegalArgumentException {
        State[] states = getTripletStates(tripletString);
        boolean stopFound = false, nonStopFound = false;
        // For non-ambiguous states, each of these loops will be over a single element
        for (State a : states[0].getCanonicalStates()) {
            for (State b : states[1].getCanonicalStates()) {
                for (State c : states[2].getCanonicalStates()) {
                    CodonState codonState = Codons.getState(a.getCode() + b.getCode() + c.getCode());
                    boolean isStop = translationMap.get(codonState).equals(AminoAcids.STOP_STATE);
                    stopFound = (stopFound || isStop);
                    nonStopFound = (nonStopFound || !isStop);
                    // IntelliJ 6.0.5 claims the following expression is always false, but this is not true.
                    if (stopFound && nonStopFound) {
                        return MaybeBoolean.Maybe;
                    }
                }
            }
        }
        return stopFound ? MaybeBoolean.True : MaybeBoolean.False;
    }

	/**
	 * @return all the possible codons for a given amino acid
	 */
	public Set<CodonState> getCodonsForAminoAcid(AminoAcidState aminoAcidState) {
        Set<CodonState> aaSet = new HashSet<CodonState>();
        for (CodonState state : translationMap.keySet()) {
            if (translationMap.get(state) == aminoAcidState) {
                aaSet.add(state);
            }
        }
        return aaSet;
	}

    /**
     * @return the codon states of starts
     */
    public Set<CodonState> getStartCodons() {
        return Collections.unmodifiableSet(startCodons);
    }

    /**
	 * @return the codon states of stops.
	 */
	public Set<CodonState> getStopCodons() {
        Set<CodonState> stopSet = new HashSet<CodonState>();
        for (CodonState state : translationMap.keySet()) {
            if (isStopCodonString(state.getCode()) == MaybeBoolean.True) {
                stopSet.add(state);
            }
        }
        return stopSet;
	}

	/**
	 * Returns the number of terminator amino acids.
	 */
	public int getStopCodonCount() {
        int count = 0;
        for (AminoAcidState state : translationMap.values()) {
            if (state == AminoAcids.STOP_STATE) {
                count++;
            }
        }
		return count;
	}

    /**
     *
     * @return the number used by NCBI to represent this genetic code or -1 if none. Eg. 1 = Standard...
     */
    public int getNcbiTranslationTableNumber() {
        return ncbiTranslationTableNumber;
    }

    /**
     * Same as getDescription() (so that GeneticCode objects can be used e.g. in a JComboBox).
     * @return the description of this genetic code
     */
    public String toString() {
        return getDescription();
    }
}
