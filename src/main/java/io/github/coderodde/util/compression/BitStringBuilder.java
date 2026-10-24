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
    
    public BitStringBuilder(final int length) {
        checkLength(length);
        this.length = length;
    }
    
    public BitStringBuilder(final long[] bits, final int length) {
        checkLength(length);
        
        this.length   = length;
        int longIndex = 0;
        
        final int longs =  length / Long.SIZE 
                        + (length % Long.SIZE != 0 ? 1 : 0);
        
        this.bits = Arrays.copyOf(bits, longs);
        
        for (int i = 0; i < length;) {
            final boolean bit = readBit(this.bits[longIndex], i % Long.SIZE);
            
            prependBit(bit);
            
            ++i;
            
            if (i % Long.SIZE == 0) {
                ++longIndex;
            }
        }
        
        reverse();
    }
    
    private static boolean readBit(final long bits, final int bitIndex) {
        return ((bits >>> bitIndex) & 1) != 0;
    }
    
    public BitStringBuilder reverse() {
        final BitStringBuilder reversed = new BitStringBuilder(bits, length);
        
        for (int i = length - 1, j = 0; i >= 0; --i, ++j) {
            if (get(i)) {
                reversed.set(j);
            }
        }
        
        return reversed;
    }
    
    public byte[] toByteArray() {
        final int byteArrayLength = 
                length / Byte.SIZE + (length % Byte.SIZE != 0 ? 1 : 0);
        
        final byte[] byteArray = new byte[byteArrayLength];
        
        for (int byteArrayIndex = 0;
                 byteArrayIndex < byteArrayLength;
                 byteArrayIndex++) {
            
            byteArray[byteArrayIndex] = extractByte(byteArrayIndex);
        }
        
        return byteArray;
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
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        
        if (object == null) {
            return false;
        }
    
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        
        final BitStringBuilder other = (BitStringBuilder) object;
        
        if (length() != other.length()) {
            return false;
        }
        
        for (int i = 0; i < length(); ++i) {
            if (get(i) != other.get(i)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(length);
        
        for (int i = length - 1; i >= 0; --i) {
            sb.append(get(i) ? "1" : "0");
        }
        
        return sb.toString();
    }
    
    private byte extractByte(final int byteArrayIndex) {
        final int bitsLongIndex = byteArrayIndex / Long.BYTES;
        final long tmp = bits[bitsLongIndex] >>> (byteArrayIndex * Byte.SIZE);
        return (byte) (tmp & 0xff);
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
    
    private static void checkLength(final int length) {
        if (length < 0) {
            throw new IllegalArgumentException(
                    String.format("length(%d) < 1", length));
        }
    }
}