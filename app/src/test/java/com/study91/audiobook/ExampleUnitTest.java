package com.study91.audiobook;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private final String TAG = "UnitTest";

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testFilename() {
        String filename = getFilename("study91/data/book.db");
        System.out.println(filename);
    }

    private String getFilename(String fullFilename) {
        int start=fullFilename.lastIndexOf("/");
        System.out.println("【/】的位置=" + (start+1));
        int end=fullFilename.lastIndexOf(".");
        System.out.println("【.】的位置=" + end);
        if(start!=-1 && end!=-1){
//            return fullFilename.substring(start+1,end);
            int length = fullFilename.length();
            return fullFilename.substring(start+1,length);
        }else{
            return null;
        }
    }
}