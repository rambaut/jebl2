package jebl.evolution.sequences;

/**
 * @author Andrew Rambaut
 */
public class TranslatedSequence extends FilteredSequence {

	/**
	 * Create a TranslatedSequence from a source codon or nucleotide sequence
	 * @param source
	 * @param geneticCode
	 */
	public TranslatedSequence(Sequence source, GeneticCode geneticCode) {
		this(source, geneticCode, 1);
	}

	/**
	 * Create a TranslatedSequence from a source codon or nucleotide sequence
	 * @param source
	 * @param geneticCode
	 * @param frame indexed from 1
     */
	public TranslatedSequence(Sequence source, GeneticCode geneticCode, int frame) {
		super(source);

		this.geneticCode = geneticCode;
		this.frame = frame;
	}

	protected State[] filterSequence(Sequence source) {
		return jebl.evolution.sequences.Utils.translate(source.getStates(), geneticCode, frame);
	}

    /**
     * @return the type of symbols that this sequence is made up of.
     */
    public SequenceType getSequenceType() {
        return SequenceType.AMINO_ACID;
    }

	private final GeneticCode geneticCode;
	private final int frame;

}
