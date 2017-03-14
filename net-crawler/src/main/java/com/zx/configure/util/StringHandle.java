package com.zx.configure.util;

public class StringHandle {
    public static String splitString(String origin, String link) {
        String temp[] = origin.split("、");
        for (int i = 0; i < temp.length; i++) {
            if (link.contains(temp[i])) {
                link = link.replace(temp[i], temp[i + 1]);
                break;
            }
        }
        return link;
    }
}
