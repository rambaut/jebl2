/*
 * State.java
 *
 * (c) 2002-2005 JEBL Development Core Team
 *
 * This package may be distributed under the
 * Lesser Gnu Public Licence (LGPL)
 */
package jebl.evolution.sequences;

import java.util.*;

/**
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: State.java 1002 2009-05-27 03:33:19Z matt_kearse $
 */
public abstract class State implements Comparable {

    public State(String name, String stateCode, int index) {
        this.name = name;
        this.stateCode = stateCode;

        this.ambiguities = Collections.singleton(this);
        this.index = index;
    }

    public State(String name, String stateCode, int index, State[] ambiguities) {
        this.name = name;
        this.stateCode = stateCode;
        this.ambiguities = Collections.unmodifiableSortedSet(new TreeSet<State>(Arrays.asList(ambiguities)));
        this.index = index;
    }

    /**
     * Returns the 1 letter code for this state.
     * @return the 1 letter code for this state.
     */
    public String getCode() {
        return stateCode;
    }

    public int getIndex() {
        return index;
    }

    /**
     * A descriptive name for this state. e.g. "Phenylalanine" or "Adenine".
     * @return A descriptive name for this state. e.g. "Phenylalanine" or "Adenine".
     */
    public String getFullName() { return name; }

    /**
     * The 1 letter code (for legacy purposes). Same as {@link #getCode()}
     * @return The 1 letter code (for legacy purposes). Same as {@link #getCode()}
     * @see #getFullName()
     * @deprecated you probably want to use {@link #getFullName()} or {@link #getCode()}.
     */
    @Deprecated public String getName() { return stateCode; }

    public boolean isAmbiguous() {
        return getCanonicalStates().size() > 1;
    }

    public Set<State> getCanonicalStates() {
        return ambiguities;
    }

    /**
     * @param other another state to check for the equality with.
     * @return true if the other state is or possibly is equal to this state, taking ambiguities into account,
     *         i.e. if the ambiguity sets of this and the other state intersect.
     */
    public boolean possiblyEqual(State other) {
        for (State state : getCanonicalStates()) {
            for (State state1 : other.getCanonicalStates()) {
                if(state.equals (state1)) return true;
            }
        }
        return false;
    }

    public int compareTo(Object o) {
        return index - ((State)o).index;
    }

    public String toString() { return stateCode; }

    public abstract boolean isGap();

    public abstract SequenceType getType();

    private String stateCode;
    private String name;
    private Set<State> ambiguities;
    private int index;

    /**
     * Determine how much in common these potentially ambigous states have as a fraction between 0 and 1
     * 2 non-ambiguous states will return 0.
     * 2 identical non-ambiguous states will return 1.
     * e.g. for Nucleotides
     * R,A = 0.5
     * R,G = 0.5
     * R,M = 0.25
     * @param other another state to compare with
     * @return the fraction of canonical states that the 2 potentially ambiguous states have in common between 0 and 1.
     */
    public double fractionEqual(State other) {
        int totalStates= 0;
        int sameStates = 0;
        if (isGap() || other.isGap()) {
            return 1.0;
        }
        for (State state : getCanonicalStates()) {
            for (State state1 : other.getCanonicalStates()) {
                totalStates++;
                if (state.equals(state1)) {
                    sameStates++;
                }
            }
        }
        return ((double)sameStates)/totalStates;
    }

}
