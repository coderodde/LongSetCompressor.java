package io.github.coderodde.util.compression.benchmark;

import io.github.coderodde.util.compression.BitStringBuilder;
import io.github.coderodde.util.compression.LongSetCompressor;
import io.github.coderodde.util.compression.Misc;
import java.util.Arrays;
import java.util.Random;

/**
 * This class contains the benchmark demonstration of the compressor suite.
 */
public final class Benchmark {
    
    private static final int LENGTH    = 1_000_000; 
    private static final Random RANDOM = new Random(123L);
    
    private Benchmark() {}
    
    public static void main(String[] args) {
        long[] array = Misc.longSet(LENGTH, RANDOM);
        
        long ta = System.currentTimeMillis();
        
        BitStringBuilder bits = LongSetCompressor.compressLongArray(array);
        
        long tb = System.currentTimeMillis();
        long compressionDuration = tb - ta;
        
        ta = System.currentTimeMillis();
        long[] result = LongSetCompressor.decompressLongSet(bits);
        tb = System.currentTimeMillis();
        long decompressionDuration = tb - ta;
        
        System.out.printf("""
                          Uncomressed array takes %9d bits.
                          Compressed data takes   %9d bits.
                          Compressed in:   %d milliseconds.
                          Decompressed in: %d milliseconds.
                          Compression/decompression agrees: %b.
                          """,
                          LongSetCompressor.getBitCount(array),
                          bits.length(),
                          compressionDuration,
                          decompressionDuration,
                          Arrays.equals(array, result));
    }
}
