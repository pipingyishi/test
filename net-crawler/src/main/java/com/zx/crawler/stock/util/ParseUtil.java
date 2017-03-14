package com.zx.crawler.stock.util;

import org.apache.commons.lang3.StringUtils;

public class ParseUtil {
    public static int safeParseInt(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0;
        }
        try {
            if (str.contains(".")) {
                return new Double(Double.parseDouble(str)).intValue();
            }
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
