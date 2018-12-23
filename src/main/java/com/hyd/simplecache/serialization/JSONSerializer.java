package com.hyd.simplecache.serialization;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hyd.simplecache.Element;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JSONSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {
        String json = JSON.toJSONString(object);

        return prependBytes(
                json.getBytes(UTF_8),
                PredefinedSerializeMethod.JSON.getTag()
        );
    }

    @Override
    public Object deserialize(byte[] bytes) {
        byte[] content = removeTag(bytes);
        return JSON.parseObject(new String(content, UTF_8));
    }

    @Override
    public <T> Element<T> deserialize(byte[] bytes, Class<T> type) {
        byte[] content = removeTag(bytes);
        return JSON.parseObject(new String(content, UTF_8),
                new TypeReference<Element<T>>(type) {
                });
    }
}
