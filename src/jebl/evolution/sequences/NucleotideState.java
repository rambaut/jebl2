/*
 * NucleotideState.java
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
 * @version $Id: NucleotideState.java 924 2008-06-18 05:58:28Z matt_kearse $
 */
public final class NucleotideState extends State {

    NucleotideState(String name, String stateCode, int index, byte bitCode) {
        super(name, stateCode, index);
        this.bitCode = bitCode;
    }

    NucleotideState(String name, String stateCode, int index, byte bitCode, NucleotideState[] ambiguities) {
        super(name, stateCode, index, ambiguities);
        this.bitCode = bitCode;
    }

    @Override
    public int compareTo(Object o) {
        // throws ClassCastException on across-class comparison
        NucleotideState that = (NucleotideState) o;
        return super.compareTo(that);
    }

    @Override
    public boolean possiblyEqual(State other) {
        return (bitCode & ((NucleotideState)other).bitCode) > 0;
    }

    public boolean isGap() {
		return this == Nucleotides.GAP_STATE;
	}

    public SequenceType getType() { return SequenceType.NUCLEOTIDE; }

    // a set of bits representing nucleotide states 0b0001 = A, 0b0010 = C, etc
    public final byte bitCode;
}
