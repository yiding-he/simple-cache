package com.hyd.simplecache.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings({"unchecked"})
public class JsonUtils {

    static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * 对于项目外的类，在这里指定哪些属性是不能序列化到 JSON 的。
     */
    static {
    }

    /**
     * 将指定的对象转化成 JSON 字符串。
     *
     * @param obj 要转化的对象
     *
     * @return 转化成的 JSON 字符串
     */
    public static String toJson(Object obj) {
        return toJson(obj, new Stack<Object>());
    }

    private static String toJson(Object obj, Stack<Object> stack) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof String) {
            return "\"" + addEscapes((String) obj) + "\"";
        } else if (obj instanceof Number) {
            return new BigDecimal(obj.toString()).toString();
        } else if (obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Date) {
            return toJsonDate((Date) obj);
        } else if (obj instanceof Map) {
            stack.push(obj);
            String result = toJasonMap((Map) obj, stack);
            stack.pop();
            return result;
        } else if (obj instanceof List) {
            stack.push(obj);
            String result = toJsonList((List) obj, stack);
            stack.pop();
            return result;
        } else if (obj instanceof Collection) {
            return toJson(new ArrayList((Collection) obj));
        } else if (obj.getClass().isArray()) {
            return toJson(Arrays.asList((Object[]) obj));
        }

        stack.push(obj);
        String result = toJsonBean(obj, stack);
        stack.pop();
        return result;
    }

    private static String toJsonDate(Date date) {
        return new BigDecimal(date.getTime()).toPlainString();
    }

    private static String toJsonBean(Object obj, Stack stack) {
        try {
            StringBuffer buffer = new StringBuffer("{");
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor descriptor : descriptors) {
                if (descriptor.getName().equals("class")) {
                    continue;
                }

                if (notSerializable(descriptor.getName(), obj.getClass())) {
                    continue;
                }

                Object value = descriptor.getReadMethod().invoke(obj);
                if (!stack.contains(value)) {
                    buffer.append("\"")
                            .append(addEscapes(descriptor.getName()))
                            .append("\"")
                            .append(":")
                            .append(toJson(value))
                            .append(",");
                }
            }
            removeTail(buffer);
            return buffer.append("}").toString();
        } catch (Exception e) {
            log.error("", e);
            return "null";
        }
    }

    private static boolean notSerializable(String fieldName, Class objType) {
        if (objType == Class.class) {
            return false;
        }

        try {
            Field field = objType.getDeclaredField(fieldName);
            return Modifier.isTransient(field.getModifiers());
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private static String toJsonList(List list, Stack<Object> stack) {
        StringBuffer buffer = new StringBuffer("[");
        for (Object o : list) {
            if (stack.contains(o)) {
                continue;
            }

            buffer.append(toJson(o, stack)).append(",");
        }
        removeTail(buffer);
        return buffer.append("]").toString();
    }

    private static String toJasonMap(Map map, Stack<Object> stack) {
        StringBuffer buffer = new StringBuffer("{");
        for (Object o : map.keySet()) {
            if (!(o instanceof String)) {
                continue;
            }

            Object value = map.get(o);
            if (stack.contains(value)) {
                continue;
            }

            buffer.append("\"").append(addEscapes((String) o)).append("\":")
                    .append(toJson(value, stack)).append(",");
        }
        removeTail(buffer);
        return buffer.append("}").toString();
    }

    private static void removeTail(StringBuffer buffer) {
        if (buffer.length() > 1) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
    }

    // Copied from google-gson
    // http://code.google.com/p/google-gson/
    // 修正：单引号不需要转义，否则 jQuery 会报错。
    protected static String addEscapes(String str) {
        StringBuilder retval = new StringBuilder();
        char ch;
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case 0:
                    continue;
                case '\b':
                    retval.append("\\b");
                    continue;
                case '\t':
                    retval.append("\\t");
                    continue;
                case '\n':
                    retval.append("\\n");
                    continue;
                case '\f':
                    retval.append("\\f");
                    continue;
                case '\r':
                    retval.append("\\r");
                    continue;
                case '\"':
                    retval.append("\\\"");
                    continue;
                case '\\':
                    retval.append("\\\\");
                    continue;
                default:
                    if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
                        String s = "0000" + Integer.toString(ch, 16);
                        retval.append("\\u").append(s.substring(s.length() - 4, s.length()));
                    } else {
                        retval.append(ch);
                    }
            }
        }
        return retval.toString();
    }

}
