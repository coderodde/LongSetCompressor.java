package io.github.coderodde.util.compression;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LongSetCompressorTest {
    
    private static final int STRESS_TEST_ITERATIONS = 20;
    private static final int LENGTH_UPPER_BOUND     = 2000;
    
    @Test
    public void compressDecompress() {
        
        Random random = new Random(13L);
        
        for (int i = 0; i < STRESS_TEST_ITERATIONS; ++i) {
            int length    = random.nextInt(LENGTH_UPPER_BOUND);
            long[] array  = Misc.longSet(length, random);
            
            BitStringBuilder bits = LongSetCompressor.compressLongArray(array);
            
            long[] result = LongSetCompressor.decompressLongSet(bits);
            
            assertTrue(Arrays.equals(array, result));
        }
    }
}
