package com.hyd.simplecache.serialization;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {

    private static final Map<Byte, Serializer> SERIALIZER_MAP = new HashMap<>();

    static {
        registerSerializer(new FSTSerializer(), PredefinedSerializeMethod.FST.getTag());
        registerSerializer(new JSONSerializer(), PredefinedSerializeMethod.JSON.getTag());
    }

    public static synchronized void registerSerializer(Serializer serializer, byte tag) {

        if (SERIALIZER_MAP.containsKey(tag)) {
            throw new IllegalArgumentException("Tag " + tag +
                    " has been used for " + SERIALIZER_MAP.get(tag).getClass());
        }

        SERIALIZER_MAP.put(tag, serializer);
    }

    public static Serializer getSerializer(byte tag) {
        return SERIALIZER_MAP.get(tag);
    }
}
