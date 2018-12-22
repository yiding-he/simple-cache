package com.hyd.simplecache.serialization;

import org.junit.Test;

public class SerializerFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void registerSerializer() {
        SerializerFactory.registerSerializer(null, (byte) 1);
    }
}