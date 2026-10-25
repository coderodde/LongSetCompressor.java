package io.github.coderodde.util.compression.benchmark;

import io.github.coderodde.util.compression.BitStringBuilder;
import io.github.coderodde.util.compression.LongSetCompressor;
import io.github.coderodde.util.compression.LongSetCompressor.CompressionResult;
import io.github.coderodde.util.compression.LongSetCompressor.DecompressionResult;
import io.github.coderodde.util.compression.Misc;
import java.util.Arrays;
import java.util.Random;

/**
 * This class contains the benchmark demonstration of the compressor suite.
 */
public final class Benchmark {
    
    private static final int LENGTH       = 1_000_000; 
    private static final long UPPER_BOUND = 50_000_000L;
    private static final Random RANDOM    = new Random(123L);
    
    private Benchmark() {}
    
    public static void main(String[] args) {
        long[] array = Misc.longSet(LENGTH, UPPER_BOUND, RANDOM);
        
        long ta = System.currentTimeMillis();
        // Don't quite need that as the compressor also sorts, but may speed
        // up sorting in the compressor:
        Arrays.sort(array);
        long tb = System.currentTimeMillis();
        
        System.out.printf("Presorting done in %d milliseconds.%n", tb - ta);
        
        CompressionResult cr   = LongSetCompressor.compressLongArray(array);
        DecompressionResult dr = LongSetCompressor.decompressLongSet(
                                                            cr.bitString());
        
        System.out.printf("""
                          Sorting duration: %d milliseconds.
                          Compression duration: %d milliseconds.
                          Compaction duration: %d milliseconds.
                          Raw capacity: %d bits.
                          Compacted capacity: %d bits.
                          Decompression duration: %d milliseconds.
                          Arrays equal: %b.
                          """,
                          cr.sortingDuration(),
                          cr.compressionDuration(),
                          cr.compactionDuration(),
                          cr.rawCapacity(),
                          cr.compactedCapacity(),
                          dr.decompressionDuration(),
                          Arrays.equals(array, dr.intSet()));
    }
}
