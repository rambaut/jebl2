/*
 * Sequence.java
 *
 * (c) 2002-2005 JEBL Development Core Team
 *
 * This package may be distributed under the
 * Lesser Gnu Public Licence (LGPL)
 */
package jebl.evolution.sequences;

import jebl.evolution.taxa.Taxon;
import jebl.util.Attributable;

import java.util.*;

/**
 * A biomolecular sequence.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: Sequence.java 365 2006-06-28 07:34:56Z pepster $
 */
public interface Sequence extends Attributable, Comparable {

	/**
	 * @return the taxon that this sequence represents (primarily used to match sequences with tree nodes)
	 */
	Taxon getTaxon();

	/**
	 * @return the type of symbols that this sequence is made up of.
	 */
	SequenceType getSequenceType();

	/**
	 * @return a string representing the sequence of symbols.
	 */
	String getString();

	/**
	 * @return an array of state objects.
	 */
	State[] getStates();

	/**
	 * @return an array of state indices.
	 */
	byte[] getStateIndices();

	/**
	 * @return the state at site.
	 */
	State getState(int site);

	/**
	 * Get the length of the sequence
	 * @return the length
	 */
	int getLength();

	/**
	 * Append two sequences together to create a new sequence object. New sequence has the taxon of
	 * the first sequence.
	 * @param sequence1
	 * @param sequence2
	 * @return
	 */
	static Sequence appendSequences(Sequence sequence1, Sequence sequence2) {
		if (sequence1.getSequenceType() != sequence2.getSequenceType()) {
			throw new IllegalArgumentException("sequences to be appended not of the same type");
		}
		State[] states = new State[sequence1.getLength() + sequence2.getLength()];
		System.arraycopy(sequence1.getStates(), 0, states, 0, sequence1.getLength());
		System.arraycopy(sequence2.getStates(), 0, states, sequence1.getLength(), sequence2.getLength());
		return new BasicSequence(sequence1.getSequenceType(), sequence1.getTaxon(), states);
	}

	/**
	 * Returns a sub-sequence for states from, to (inclusive).
	 * @param sequence
	 * @param from
	 * @param to
	 * @return
	 */
	static Sequence getSubSequence(Sequence sequence, int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException("subsequence from is greater than to");
		}
		if (from >= sequence.getLength() || to >= sequence.getLength()) {
			throw new IllegalArgumentException("subsequence range out of bounds");
		}
		State[] states = new State[to - from + 1];
		System.arraycopy(sequence.getStates(), from, states, 0, states.length);
		return new BasicSequence(sequence.getSequenceType(), sequence.getTaxon(), states);
	}

	static Sequence trimSequence(Sequence sequence, List<State> trimStates) {
		State[] sourceStates = sequence.getStates();
		int i = 0;
		while (i < sourceStates.length && trimStates.contains(sourceStates[i])) {
			i++;
		}
		if (i < sourceStates.length) {
			Sequence sequence1 = getSubSequence(sequence, i, sourceStates.length - 1);
			sourceStates = sequence1.getStates();
			i = sourceStates.length - 1;
			while (i > 0 && trimStates.contains(sourceStates[i])) {
				i--;
			}
			return getSubSequence(sequence1, 0, i);
		} else {
			return new BasicSequence(sequence.getSequenceType(), sequence.getTaxon(), new State[] {});
		}
	}

	/**
	 * Strips a sequence of gaps
	 * @param sequence the sequence
	 * @return the stripped sequence
	 */
	static Sequence stripGaps(final Sequence sequence) {
		return new BasicSequence(sequence.getSequenceType(), sequence.getTaxon(), jebl.evolution.sequences.Utils.stripGaps(sequence.getStates()));
	}


	/**
	 * Strips a sequence of any states given
	 * @param sequence the sequence
	 * @param stripStates the states to strip
	 * @return an array of states
	 */
	static Sequence stripStates(final Sequence sequence, final List<State> stripStates) {
		return new BasicSequence(sequence.getSequenceType(), sequence.getTaxon(),
				jebl.evolution.sequences.Utils.stripStates(sequence.getStates(), stripStates));
	}

	/**
	 * Searchers and replaces a sequence of any states given
	 * @param sequence the sequence
	 * @param searchStates the states to search for
	 * @return an array of states
	 */
	static Sequence replaceStates(final Sequence sequence, final List<State> searchStates, State replaceState) {
		return new BasicSequence(sequence.getSequenceType(), sequence.getTaxon(),
				jebl.evolution.sequences.Utils.replaceStates(sequence.getStates(), searchStates, replaceState));
	}


	/**
	 * Counts the number of occurances of a state
	 * @param sequence the sequence string to count
	 * @param state the state
	 * @return the number of occurances
	 */
	static int getStateCount(final Sequence sequence, final State state) {

		int count = 0;
		for (State s : sequence.getStates()) {
			if (s.equals(state)) {
				count ++;
			}
		}

		return count;
	}

}
