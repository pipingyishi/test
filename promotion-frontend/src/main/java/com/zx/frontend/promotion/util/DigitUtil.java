package com.zx.frontend.promotion.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitUtil {
    public static boolean isAllDigit(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
