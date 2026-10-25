package io.github.coderodde.util.compression;

import java.util.Random;

/**
 * This class contains some utility facilities.
 */
public final class Misc {
    
    private Misc() {}
    
    public static long[] longSet(int length, long upperBound, Random random) {
        long[] array = new long[length];
        
        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextLong() % upperBound;
        }
        
        return array;
    }
}
