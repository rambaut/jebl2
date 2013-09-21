/*
 * BasicSequence.java
 *
 * (c) 2002-2005 JEBL Development Core Team
 *
 * This package may be distributed under the
 * Lesser Gnu Public Licence (LGPL)
 */
package jebl.evolution.sequences;

import jebl.evolution.taxa.Taxon;
import jebl.util.AttributableHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A codon implementation of the Sequence interface.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @version $Id: BasicSequence.java 1042 2009-12-08 00:14:20Z amyzeta $
 */
public class CodonSequence implements Sequence {

    /**
     * Creates a sequence with a name corresponding to the taxon name
     *
     * @param taxon
     * @param states
     */
    public CodonSequence(Taxon taxon, State[] states) {

        this.sequenceType = SequenceType.CODON;
        this.taxon = taxon;
        this.sequenceStates = new CodonState[states.length];
        for (int i = 0; i < states.length; i++) {
            sequenceStates[i] = states[i];
        }
    }

    /**
     * @return the type of symbols that this sequence is made up of.
     */
    public SequenceType getSequenceType() {
        return sequenceType;
    }

    /**
     * @return a string representing the sequence of symbols.
     */
    public String getString() {
        StringBuilder buffer = new StringBuilder(sequenceStates.length);
        for (State state : sequenceStates) {
            buffer.append(state.getCode());
        }
        return buffer.toString();
    }

    public String getCleanString() {
        StringBuilder buffer = new StringBuilder(sequenceStates.length);
        for (State state : sequenceStates) {
            if (state.isAmbiguous() || state.isGap()) continue;
            buffer.append(state.getCode());
        }
        return buffer.toString();
    }

    /**
     * @return an array of state objects.
     */
    public State[] getStates() {
        return sequenceType.toStateArray(getStateIndices());
    }

    public byte[] getStateIndices() {
        byte results[]=new byte[sequenceStates.length];
        for (int i = 0; i < sequenceStates.length; i++) {
             results [i] = (byte) getState(i).getIndex();
        }
        return results;
    }


    /**
     * Get the sequence characters representing the sequence.
     * This return is a byte[] rather than a char[]
     * to avoid using twice as much memory as necessary.
     * The individual elements of the returned array can be cast to chars.
     * @return the sequence characters as an array of characters.
     */
    public byte[] getSequenceCharacters() {
        throw new UnsupportedOperationException("codons don't have single character codes");
//        return sequenceStates;
    }

    /**
     * @return the state at site.
     */
    public State getState(int site) {
        return sequenceStates[site];
    }

    /**
     * Returns the length of the sequence
     *
     * @return the length
     */
    public int getLength() {
        return sequenceStates.length;
    }

    /**
     * @return that taxon that this sequence represents (primarily used to match sequences with tree nodes)
     */
    public Taxon getTaxon() {
        return taxon;
    }

    /**
     * Sequences are compared by their taxa
     *
     * @param o another sequence
     * @return an integer
     */
    public int compareTo(Object o) {
        return taxon.compareTo(((Sequence) o).getTaxon());
    }

    public String toString() {
        return getString();
    }
    
    // Attributable IMPLEMENTATION

    public void setAttribute(String name, Object value) {
        if (helper == null) {
            helper = new AttributableHelper();
        }
        helper.setAttribute(name, value);
    }

    public Object getAttribute(String name) {
        if (helper == null) {
            return null;
        }
        return helper.getAttribute(name);
    }

    public void removeAttribute(String name) {
        if (helper != null) {
            helper.removeAttribute(name);
        }
    }

    public Set<String> getAttributeNames() {
        if (helper == null) {
            return Collections.emptySet();
        }
        return helper.getAttributeNames();
    }

    public Map<String, Object> getAttributeMap() {
        if (helper == null) {
            return Collections.emptyMap();
        }
        return helper.getAttributeMap();
    }

    private AttributableHelper helper = null;

    // private members

    private final Taxon taxon;
    private final SequenceType sequenceType;
    private final State[] sequenceStates;

   // private Map<String, Object> attributeMap = null;
   @Override
   public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;

       CodonSequence that = (CodonSequence) o;

       if (!Arrays.equals(sequenceStates, that.sequenceStates)) return false;
       if (sequenceType != null ? !sequenceType.equals(that.sequenceType) : that.sequenceType != null) return false;
       if (taxon != null ? !taxon.equals(that.taxon) : that.taxon != null) return false;

       return true;
   }

    @Override
    public int hashCode() {
        int result = (taxon != null ? taxon.hashCode() : 0);
        result = 31 * result + (sequenceType != null ? sequenceType.hashCode() : 0);
        result = 31 * result + (sequenceStates != null ? Arrays.hashCode(sequenceStates) : 0);
        return result;
    }
}
