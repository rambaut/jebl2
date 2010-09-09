package jebl.evolution.distances;

/**
 * @author Matthew Cheung
 * @version $Id$
 */
public class CannotBuildDistanceMatrixException extends IllegalArgumentException {

    public CannotBuildDistanceMatrixException(String matrix, String taxon1, String taxon2){
        super("It is not possible to compute the " + matrix + " genetic distance for these sequences " +
                "because at least one pair of sequences (" + taxon1 + " and " + taxon2 + ") do not overlap "
                + "(or have only ambiguities in common) in the alignment.");
    }

    public CannotBuildDistanceMatrixException(String msg){
        super(msg);
    }
}