package io.github.coderodde.util.compression;

import java.util.Arrays;

/**
 * This class implements a <b>binary</b> code word in data compression 
 * scenarios.
 */
public class BitStringBuilder {

    private int length;
    private long[] bits;
    
    public BitStringBuilder() {
        bits = new long[1];
    }
    
    public void prependBit(final boolean bit) {
        ++length;
        
        if (length == bits.length * Long.SIZE) {
            this.bits = Arrays.copyOf(this.bits, length * 2);
        }
        
        if (bit) {
            set(length - 1);
        }
    }
    
    public int length() {
        return length;
    }
    
    public boolean get(final int index) {
        checkIndex(index);
        
        final long bits = this.bits[index / Long.SIZE];
        final long mask = 1L << index;
        return ((bits & mask) != 0);
    }
    
    public void set(final int index) {
        checkIndex(index);
        
        final int bitsLongIndex   = index / Long.SIZE;
        final long mask           = 1L << index;
        this.bits[bitsLongIndex] |= mask;
    }
    
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
    
    public long readBits(final int bitIndex, final int bitCount) {
        long data = 0;
        
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