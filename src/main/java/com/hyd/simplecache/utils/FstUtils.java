package com.hyd.simplecache.utils;

import org.nustaq.serialization.FSTConfiguration;

public class FstUtils {

    private static FSTConfiguration FST;

    static {
        FST = FSTConfiguration.createDefaultConfiguration();
        FST.setForceSerializable(true);
    }

    public static byte[] serialize(Object obj) {
        return FST.asByteArray(obj);
    }

    public static Object deserialize(byte[] bytes) {
        return FST.asObject(bytes);
    }
}
