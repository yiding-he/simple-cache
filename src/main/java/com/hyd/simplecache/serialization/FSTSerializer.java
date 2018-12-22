package com.hyd.simplecache.serialization;

import org.nustaq.serialization.FSTConfiguration;

public class FSTSerializer implements Serializer {

    private static FSTConfiguration FST;

    static {
        FST = FSTConfiguration.createDefaultConfiguration();
        FST.setForceSerializable(true);
    }

    @Override
    public byte[] serialize(Object object) {
        byte[] content = FST.asByteArray(object);
        return prependBytes(content, (byte) 0);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        byte[] content = removeTag(bytes);
        return FST.asObject(content);
    }
}
