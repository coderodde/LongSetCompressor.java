package io.github.coderodde.util.compression;

import java.util.Random;

/**
 * This class contains some utility facilities.
 */
public final class Misc {
    
    private static final int    LENGTH      = 1_000;
    private static final int    UPPER_BOUND = 100_000;
    private static final Random RANDOM      = new Random(123L);
    
    private Misc() {}
    
    public static long[] longSet(int length, Random random) {
        long[] array = new long[length];
        
        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextInt(UPPER_BOUND);
        }
        
        return array;
    }
    
    public static long[] longSet(int length) {
        return longSet(length, RANDOM);
    }
    
    public static long[] longSet(Random random) {
        return longSet(LENGTH, random);
    }
    
    public static long[] longSet(long seed) {
        return longSet(new Random(seed));
    }
    
    public static long[] longSet() {
        return longSet(LENGTH, RANDOM);
    }
}
