package com.hyd.simplecache.serialization;

public enum PredefinedSerializeMethod {

    FST((byte)0),
    JSON((byte) 1),

    ;

    private byte tag;

    PredefinedSerializeMethod(byte tag) {

        this.tag = tag;
    }

    public byte getTag() {
        return tag;
    }
}
