package io.github.coderodde.util.compression;

import java.util.Arrays;

/**
 * This class contains methods for dealing with compression and decompression of
 * sets of {@code long} values.
 */
public class LongSetCompressor {
    
    public static final record CompressionResult(BitStringBuilder bitString,
                                                 long sortingDuration,
                                                 long compressionDuration,
                                                 long compactionDuration,
                                                 long totalDuration,
                                                 int rawCapacity,
                                                 int compactedCapacity) {
        
    }
    
    public static final record DecompressionResult(long[] intSet,
                                                   long decompressionDuration) {
        
    }
    
    private LongSetCompressor() {}

    /**
     * Compresses the {@code array} to a bit string. The algorithm first sorts
     * the input array. Then, it computes the maximum gap between two 
     * consecutive array components. The size of the array, the first (smallest)
     * value, and the gap bit count are serialized to the beginning of the bit
     * string builder. Finally, the rest of the array is serialized by encoding
     * current gaps between consecutive values.
     * 
     * @param array the array to compress.
     * 
     * @return the bit string builder containing the compressed binary data. 
     */
    public static CompressionResult compressLongArray(long[] array) {
        long ta = System.currentTimeMillis();
        Arrays.sort(array);
        long tb = System.currentTimeMillis();
        long sortingDuration = tb - ta;
        
        ta = System.currentTimeMillis();
        long maximumGap = 0L;
        
        for (int i = 1; i < array.length; ++i) {
            long gap = array[i] - array[i - 1];
            
            if (maximumGap < gap) {
                maximumGap = gap;
            }
        }
        
        int gapBits = 0;
        
        while (maximumGap != 0L) {
            maximumGap >>>= 1;
            ++gapBits;
        }
        
        BitStringBuilder bits = new BitStringBuilder();
        
        bits.prependInt(array.length);
        bits.prependLong(array[0]);
        bits.prependByte((byte) gapBits);
        
        for (int i = 1; i < array.length; ++i) {
            long gap = array[i] - array[i - 1];
            
            bits.prependCode(gap, gapBits);
        }
        
        tb = System.currentTimeMillis();
        long compressionDuration = tb - ta;
        int rawCapacity = bits.capacity();
        
        ta = System.currentTimeMillis();
        bits.compact();
        tb = System.currentTimeMillis();
        long compactionDuration = tb - ta;
        int compactedCapacity = bits.capacity();
        
        return new CompressionResult(bits,
                                     sortingDuration,
                                     compressionDuration,
                                     compactionDuration,
                                     sortingDuration 
                                             + compressionDuration 
                                             + compactionDuration,
                                     rawCapacity,
                                     compactedCapacity);
    }
    
    /**
     * Decompresses the input bit string to an (sorted) array of {@code long}
     * values.
     * 
     * @param bits the compressed {@code long} array.
     * 
     * @return the sorted array of compressed values. 
     */
    public static DecompressionResult decompressLongSet(BitStringBuilder bits) {
        long ta      = System.currentTimeMillis();
        int  length  = bits.readInt(0);
        long minimum = bits.readLong(Integer.SIZE);
        byte gapBits = bits.readByte(Integer.SIZE + Long.SIZE);
        
        long[] array = new long[length];
        array[0]     = minimum;
        int bitIndex = Integer.SIZE + Long.SIZE + Byte.SIZE;
        
        for (int i = 1; i < length; ++i) {
            long gap = bits.readBits(bitIndex, gapBits);
            array[i] = array[i - 1] + gap;
            bitIndex += gapBits;
        }
        
        long decompressionDuration = System.currentTimeMillis() - ta;
        return new DecompressionResult(array, decompressionDuration);
    }
    
    public static int getBitCount(long[] data) {
        return data.length * Long.SIZE;
    }
}
