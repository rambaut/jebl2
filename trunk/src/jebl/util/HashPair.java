package jebl.util;

/**
 * A pair suitable for use in a HashMap.
 *
 * @author Joseph Heled
 *
 * @version $Id: HashPair.java 956 2008-11-30 01:18:20Z rambaut $
 */

public class HashPair<T> {
    public HashPair(T a, T b) {
        first = a;
        second = b;
        if (a == null || b == null) {
            throw new NullPointerException("Expected two non-null objects, got " + a + ", " + b);
        }
    }

    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

    public boolean equals(Object x) {
        if( x instanceof HashPair ) {
            return ((HashPair) x).first.equals(first) &&  ((HashPair )x).second.equals(second);
        }
        return false;
    }

    public final T first;
    public final T second;
}