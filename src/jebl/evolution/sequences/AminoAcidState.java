/*
 * AminoAcidState.java
 *
 * (c) 2002-2005 JEBL Development Core Team
 *
 * This package may be distributed under the
 * Lesser Gnu Public Licence (LGPL)
 */
package jebl.evolution.sequences;

/**
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: AminoAcidState.java 986 2009-03-10 01:00:03Z matt_kearse $
 */
public final class AminoAcidState extends State {
    private final String threeLetterName;
    
    AminoAcidState(String fullName, String threeLetterName, String singleLetterStateCode, int index) {
        super(fullName, singleLetterStateCode, index);
        this.threeLetterName = threeLetterName;
    }

    AminoAcidState(String fullName, String threeLetterName, String singleLetterStateCode, int index, AminoAcidState[] ambiguities) {
        super(fullName, singleLetterStateCode, index, ambiguities);
        this.threeLetterName = threeLetterName;
    }

    @Override
    public int compareTo(Object o) {
        // throws ClassCastException on across-class comparison
        AminoAcidState that = (AminoAcidState) o;
        return super.compareTo(that);
    }

    /**
     * @return the 3 letter name for this amino acid. E.g "Ala" for "Alanine".
     */
    public String getThreeLetterName() {
        return threeLetterName;
    }

    // we do not need to override equals() and hashCode() because there is only one
    // unique instance of each state

    public boolean isGap() {
		return this == AminoAcids.GAP_STATE;
	}

	public boolean isStop() {
		return this == AminoAcids.STOP_STATE;
	}

    public SequenceType getType() { return SequenceType.AMINO_ACID; }
}
