package io.github.coderodde.util.compression;

import java.util.Arrays;

/**
 * This class implements a <b>binary</b> code word in data compression 
 * scenarios.
 */
public class BitStringBuilder {

    private int length  = 0;
    private long[] bits = new long[3];
    
    /**
     * Prepends the input bit to this bit string builder. The input bit becomes
     * the most significant bit in the string.
     * 
     * @param bit the bit to prepend.
     */
    public void prependBit(final boolean bit) {
        ++length;
        
        if (length == bits.length * Long.SIZE) {
            this.bits = Arrays.copyOf(this.bits, length * 2);
        }
        
        if (bit) {
            set(length - 1);
        }
    }
    
    /**
     * Prepends up to 64 bits from {@code gap}.
     * 
     * @param gap     the integer containing the bits to prepend.
     * @param gapBits the number of bits to prepend.
     */
    public void prependCode(long gap, int gapBits) {
        gapBits = Math.min(gapBits, Long.SIZE);
        
        for (int i = 0; i < gapBits; ++i) {
            boolean bit = (gap & (1L << i)) != 0;
            prependBit(bit);
        }
    }
    
    /**
     * Prepends a 32-bit integer completely.
     * 
     * @param value the integer to prepend.
     */
    public void prependInt(int value) {
        for (int i = 0; i < Integer.SIZE; ++i) {
            boolean bit = (value & (1 << i)) != 0;
            prependBit(bit);
        }
    }
    
    /**
     * Prepends a 64-bit integer completely.
     * 
     * @param value the integer to prepend.
     */
    public void prependLong(long value) {
        for (int i = 0; i < Long.SIZE; ++i) {
            boolean bit = (value & (1L << i)) != 0;
            prependBit(bit);
        }
    }
    
    /**
     * Prepends a 8-bit integer completely.
     * 
     * @param value the integer to prepend.
     */
    public void prependByte(byte value) {
        for (int i = 0; i < Byte.SIZE; ++i) {
            boolean bit = (value & (1 << i)) != 0;
            prependBit(bit);
        }
    }
    
    public int length() {
        return length;
    }
    
    public int capacity() {
        return Long.SIZE * bits.length;
    }
    
    public void compact() {
        int capacity = length / Long.SIZE + (length % Long.SIZE != 0 ? 1 : 0);
        bits         = Arrays.copyOf(bits, capacity);
    }
    
    /**
     * Reads the {@code index}th least significant bit.
     * 
     * @param index the bit index.
     * 
     * @return the target bit.
     */
    public boolean get(final int index) {
        checkIndex(index);
        
        final long bits = this.bits[index / Long.SIZE];
        final long mask = 1L << index;
        return ((bits & mask) != 0);
    }
    
    /**
     * Sets the {@code index}th least significant bit.
     * 
     * @param index the bit index.
     */
    public void set(final int index) {
        checkIndex(index);
        
        final int bitsLongIndex   = index / Long.SIZE;
        final long mask           = 1L << index;
        this.bits[bitsLongIndex] |= mask;
    }
    
    /**
     * Reads an integer starting from the {@code bitIndex}th bit.
     * 
     * @param bitIndex the starting bit index.
     * 
     * @return the target 32-bit integer.
     */
    public int readInt(final int bitIndex) {
        int data = 0;
        
        for (int i = 0; i < Integer.SIZE; ++i) {
            boolean bit = get(bitIndex + i);
            
            if (bit) {
                data |= (1 << i);
            }
        }
        
        return data;
    }
    
    /**
     * Reads an integer starting from the {@code bitIndex}th bit.
     * 
     * @param bitIndex the starting bit index.
     * 
     * @return the target 64-bit integer.
     */
    public long readLong(final int bitIndex) {
        long data = 0;
        
        for (int i = 0; i < Long.SIZE; ++i) {
            boolean bit = get(bitIndex + i);
            
            if (bit) {
                data |= (1L << i);
            }
        }
        
        return data;
    }
    
    /**
     * Reads an integer starting from the {@code bitIndex}th bit.
     * 
     * @param bitIndex the starting bit index.
     * 
     * @return the target 8-bit integer.
     */
    public byte readByte(final int bitIndex) {
        byte data = 0;
        
        for (int i = 0; i < Byte.SIZE; ++i) {
            boolean bit = get(bitIndex + i);
            
            if (bit) {
                data |= ((byte)(1) << i);
            }
        }
        
        return data;
    }
    
    /**
     * Reads raw bits starting from the {@code bitIndex}th bit.
     * 
     * @param bitIndex the starting bit index.
     * @param bitCount
     * 
     * @return the target {@code bitCount}-bit integer.
     */
    public long readBits(final int bitIndex, int bitCount) {
        long data = 0;
        bitCount  = Math.min(bitCount, Long.SIZE);
        
        for (int i = 0; i < bitCount; ++i) {
            boolean bit = get(bitIndex + i);
            
            if (bit) {
                data |= (1L << i);
            }
        }
        
        return data;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(length);
        
        for (int i = length - 1; i >= 0; --i) {
            sb.append(get(i) ? "1" : "0");
        }
        
        return sb.toString();
    }
    
    private void checkIndex(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    String.format("index(%d) < 0", index));
        }
        
        if (index >= this.length) {
            throw new IndexOutOfBoundsException(
                    String.format("index(%d) >= length(%d)", 
                                  index, 
                                  length));
        }
    }
}