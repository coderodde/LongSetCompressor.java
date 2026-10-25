package io.github.coderodde.util.compression;

import io.github.coderodde.util.compression.LongSetCompressor.CompressionResult;
import io.github.coderodde.util.compression.LongSetCompressor.DecompressionResult;
import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LongSetCompressorTest {
    
    private static final int STRESS_TEST_ITERATIONS = 20;
    private static final int LENGTH_UPPER_BOUND     = 2000;
    private static final long VALUE_UPPER_BOUND     = 50_000L;
    private static final Random RANDOM              = new Random(13L);
    
    @Test
    public void compressDecompress() {
        
        Random random = new Random(13L);
        
        for (int i = 0; i < STRESS_TEST_ITERATIONS; ++i) {
            int length    = random.nextInt(LENGTH_UPPER_BOUND);
            long[] array  = Misc.longSet(length, VALUE_UPPER_BOUND, random);
            
            CompressionResult cr = LongSetCompressor.compressLongArray(array);
            
            DecompressionResult dr = 
                LongSetCompressor.decompressLongSet(cr.bitString());
            
            assertTrue(Arrays.equals(array, dr.intSet()));
        }
    }
}
