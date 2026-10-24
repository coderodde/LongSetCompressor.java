package io.github.coderodde.util.compression;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CodeWordTest {

    private BitStringBuilder codeWord;
    
    @Test
    public void toStringTest() {
        codeWord = new BitStringBuilder(new long[]{ 1, 3 }, 130);
        System.out.println(codeWord);
    }
}
