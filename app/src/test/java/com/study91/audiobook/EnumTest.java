package com.study91.audiobook;

import com.study91.audiobook.dict.SoundType;

import org.junit.Test;

public class EnumTest {
    @Test
    public void testSoundType() {
        SoundType soundType = SoundType.AUDIO_AND_MUSIC;
        System.out.println("声音类型ID=" + soundType.ordinal());
        System.out.println("声音类型值=" + soundType.toString());
        System.out.println("声音类型含义=" + soundType.getString());
        System.out.println("2的声音类型=" + SoundType.values()[2]);
    }
}
