import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class FM_Sketch {
    private static final double PHI = 0.77351D;
    private int num_groups; // number of hash groups
    private int m;  // number of hash functions in a hash group
    private int l;  // bitmap size
   
    private boolean[][][] bitmaps;
    private HashFunction [][] hashes;

    public FM_Sketch(int numGroups, int numHash, int bitmapSize) {
        this.num_groups = numGroups;
        this.m = numHash;
        this.l = bitmapSize;

        bitmaps = new boolean[num_groups][m][l];
        hashes = new HashFunction[num_groups][m];

        generateHashFunctions();
    }

    private void generateHashFunctions() {
        Map<Integer, Collection<Integer>> mnMap = new HashMap <Integer, Collection<Integer>>();
        for (int i=0; i<num_groups;i++) {
            for (int j=0; j<m; j++) {
                hashes[i][j] = generateFunction(mnMap);
            }
        }
    }

    private HashFunction generateFunction(Map<Integer, Collection<Integer>> map) {
        int m = 0;
        do {
            m = (int) (Integer.MAX_VALUE * Math.random());
        } while (m % 2 == 0);

        int n = 0;
        do {
            n = (int) (Integer.MAX_VALUE * Math.random());
        } while (n % 2 ==0 || contains(map, m, n));

        Collection<Integer> valueCollection = map.get(m);
        if (valueCollection == null) {
            valueCollection = new HashSet<Integer>();
            map.put(m, valueCollection);
        }
        valueCollection.add(n);

        return new HashFunction(3, 2, 1);
    }

    private static boolean contains(Map<Integer, Collection<Integer>> map, int m, int n) {
        Collection<Integer> valueList = map.get(m);
        return (valueList != null) && (valueList.contains(n));
    }

    public static void main(String[] args) {
        HashFunction test = new HashFunction(0, 0, 1);
        FM_Sketch skt = new FM_Sketch(4, 3, 2);

        // long v = test.hash(123);
        // System.out.println(v);
    }

    private static class HashFunction {
        private int mm;
        private int mn;
        private int m_bitmap_size;
        private long pow_bitmap_size;

        public HashFunction(int m, int n, int bitmap_size) {
            if (bitmap_size > 64) {
                throw new IllegalArgumentException("bitmap size cannot be greater than 64.");
            }
            this.mm = m;
            this.mn = n;
            this.m_bitmap_size = bitmap_size;
            this.pow_bitmap_size = 1 << m_bitmap_size;

        }

        public long hash(Object o) {
            if (o instanceof String) {
                return hash(((String) o).hashCode());
            }
            if (o instanceof Number) {
                return hash(String.valueOf(o).hashCode());
            }
            return hash(o.hashCode());
        }

        public long hash(long hashCode) {
            return mm + mn * hashCode;
        }
    }
}
