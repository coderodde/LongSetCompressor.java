package io.github.coderodde.util.compression;

import java.util.Arrays;

/**
 * This class contains methods for dealing with compression and decompression of
 * sets of {@code long} values.
 */
public class LongSetCompressor {
    
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
    public static BitStringBuilder compressLongArray(long[] array) {
        Arrays.sort(array);
        
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
        
        prependInt (bits, array.length);
        prependLong(bits, array[0]);
        prependByte(bits, (byte) gapBits);
        
        for (int i = 1; i < array.length; ++i) {
            long gap = array[i] - array[i - 1];
            
            prependCode(bits, gap, gapBits);
        }
        
        return bits;
    }
    
    /**
     * Decompresses the input bit string to an (sorted) array of {@code long}
     * values.
     * 
     * @param bits the compressed {@code long} array.
     * 
     * @return the sorted array of compressed values. 
     */
    public static long[] decompressLongSet(BitStringBuilder bits) {
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
        
        return array;
    }
    
    private static void prependCode(BitStringBuilder bits, 
                                    long gap,
                                    int gapBits) {
        
        for (int i = 0; i < gapBits; ++i) {
            boolean bit = (gap & (1L << i)) != 0;
            bits.prependBit(bit);
        }
    }
    
    private static void prependInt(BitStringBuilder bits, int value) {
        for (int i = 0; i < Integer.SIZE; ++i) {
            boolean bit = (value & (1 << i)) != 0;
            bits.prependBit(bit);
        }
    }
    
    private static void prependLong(BitStringBuilder bits, long value) {
        for (int i = 0; i < Long.SIZE; ++i) {
            boolean bit = (value & (1L << i)) != 0;
            bits.prependBit(bit);
        }
    }
    
    private static void prependByte(BitStringBuilder bits, byte value) {
        for (int i = 0; i < Byte.SIZE; ++i) {
            boolean bit = (value & (1 << i)) != 0;
            bits.prependBit(bit);
        }
    }
    
    public static int getBitCount(long[] data) {
        return data.length * Long.SIZE;
    }
}
