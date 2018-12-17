package com.hyd.simplecache.utils;

public class Str {

    public static boolean notBlank(String s) {
        return s != null && s.trim().length() > 0;
    }

    public static String removeEnd(String s, String end) {
        if (s == null || end == null) {
            return s;
        }

        if (!s.endsWith(end)) {
            return s;
        }

        return s.substring(0, s.length() - end.length());
    }
}
