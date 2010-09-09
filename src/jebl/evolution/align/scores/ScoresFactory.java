package jebl.evolution.align.scores;

import java.lang.reflect.Constructor;

public class ScoresFactory {
    public static final Blosum45 BLOSUM_45 = new Blosum45();
    public static final Blosum50 BLOSUM_50 = new Blosum50();
    public static final Blosum55 BLOSUM_55 = new Blosum55 ();
    public static final Blosum60 BLOSUM_60 = new Blosum60 ();
    public static final Blosum62 BLOSUM_62 = new Blosum62 ();
    public static final Blosum65 BLOSUM_65 = new Blosum65 ();
    public static final Blosum70 BLOSUM_70 = new Blosum70 ();
    public static final Blosum75 BLOSUM_75 = new Blosum75 ();
    public static final Blosum80 BLOSUM_80 = new Blosum80 ();
    public static final Blosum85 BLOSUM_85 = new Blosum85 ();
    public static final Blosum90 BLOSUM_90 = new Blosum90 ();
    public static final Pam100 PAM_100 = new Pam100();
    public static final Pam110 PAM_110 = new Pam110 ();
    public static final Pam120 PAM_120 = new Pam120 ();
    public static final Pam130 PAM_130 = new Pam130();
    public static final Pam140 PAM_140 = new Pam140 ();
    public static final Pam150 PAM_150 = new Pam150 ();
    public static final Pam160 PAM_160 = new Pam160 ();
    public static final Pam170 PAM_170 = new Pam170 ();
    public static final Pam180 PAM_180 = new Pam180();
    public static final Pam190 PAM_190 = new Pam190 ();
    public static final Pam200 PAM_200 = new Pam200 ();
    public static final Pam210 PAM_210 = new Pam210 ();
    public static final Pam220 PAM_220 = new Pam220 ();
    public static final Pam230 PAM_230 = new Pam230 ();
    public static final Pam240 PAM_240 = new Pam240 ();
    public static final Pam250 PAM_250 = new Pam250 ();
    public static final AminoAcidScores AMINO_ACID_IDENTITY = new AminoAcidScores(1,0) {
        @Override
        public String toString() {
            return "Identity";
        }
    };
    public static final NucleotideScores NUCLEOTIDE_51_PERCENT_SIMILARITY = new NucleotideScores("51% similarity", 5.0f, -3.0f);
    public static final NucleotideScores NUCLEOTIDE_65_PERCENT_SIMILARITY = new NucleotideScores("65% similarity", 5.0f, -4.0f);
    public static final NucleotideScores NUCLEOTIDE_70_PERCENT_SIMILARITY = new NucleotideScores("70% similarity (IUB)", 5 * 1.0f, 5 * -0.9f);
    public static final NucleotideScores NUCLEOTIDE_93_PERCENT_SIMILARITY = new NucleotideScores("93% similarity", 5.0f, -9.0261674571825044f);

    /**
	 * For any matrix.
	 * 
	 * @param nameVal name and value of the matrix in String form. (eg Blosum45).
	 * @return	substitution matrix of given name.
	 */
	public static Scores generateScores(String nameVal) {
		int i = 0;
		while((int)nameVal.charAt(i) > 64) { //while(charAt(i) is a letter)
			i++;
		}
		Scores sub = null;
		String name = nameVal.substring(0, i);
		String val = nameVal.substring(i, nameVal.length());
		try {
			if(val.indexOf(".") != -1)
				sub = generateScores(name, Float.parseFloat(val));
			else
				sub = generateScores(name, Integer.parseInt(val));
		}
		catch(Exception e) {
			System.out.println("no such substitution matrix!\n" + e);
		}
		return sub;
	}

	/**
	 * For Blosum and Pam matrices
	 * 
	 * @param name "Blosum" or "Pam"
	 * @param val currently 45 - 90 or 100 - 250
	 * @return	substitution matrix given by name and val.
	 */
	public static Scores generateScores(String name, int val) {
		
		Scores sub = null;
		try {
			Class c = Class.forName("jebl.evolution.align.scores." + name + val);
			sub = (Scores)(c.newInstance());
		}
		catch(Exception e) {
			System.out.println("no such substitution matrix!\n" + e);
		}

		return sub;
		
	}
	
	/**
	 * For calculated nucleotide matrices.
	 * 
	 * @param name Currently only JukesCantor
	 * @param val val used to calculate matrix. eg. evolutionary distance d.
	 * @return substitution matrix calculated using val.
	 */
	public static Scores generateScores(String name, float val) {
		
		Scores sub = null;
		try {
			Class c = Class.forName("jebl.evolution.align.scores." + name);
			Constructor con[] = c.getConstructors();
			sub = (Scores)(con[0].newInstance(new Object[] {new Float(val)}));
		}
        
        catch(Exception e) {
			System.out.println("no such substitution matrix!\n" + e);
		}

		return sub;
	}

    public static AminoAcidScores[] getAvailableAminoAcidScores () {
        return new AminoAcidScores[] {BLOSUM_45, BLOSUM_50, BLOSUM_55, BLOSUM_60,
                BLOSUM_62, BLOSUM_65, BLOSUM_70, BLOSUM_75, BLOSUM_80,
                BLOSUM_85, BLOSUM_90, PAM_100, PAM_110, PAM_120,
                PAM_130, PAM_140, PAM_150, PAM_160, PAM_170,
                PAM_180, PAM_190, PAM_200, PAM_210, PAM_220,
                PAM_230, PAM_240, PAM_250,
                AMINO_ACID_IDENTITY,
        };
    }
    public static NucleotideScores[] getAvailableNucleotideScores () {
        return new NucleotideScores[] {
                NUCLEOTIDE_51_PERCENT_SIMILARITY,
                NUCLEOTIDE_65_PERCENT_SIMILARITY,
               // new NucleotideScores("70% similarity (IUB)", 1.0f, -0.9f),

                // This seems like a bad choice as it implies a very high kappa
               //  new NucleotideScores("93% similarity, Transition/Transversion", 1, -1,-5, 0),

                NUCLEOTIDE_70_PERCENT_SIMILARITY,


              //  new NucleotideScores("88% similarity", 5.0f, -7.2810419984342278f),
                NUCLEOTIDE_93_PERCENT_SIMILARITY,


//                new NucleotideScores("Assembly", 10.0f, -9.0f),

                new NucleotideScores("Transition/Transversion", 5, 1, -4, 0, true),
                new NucleotideScores("Identity",1,0, 0),

                /*,new Hamming()*/};
    }
}