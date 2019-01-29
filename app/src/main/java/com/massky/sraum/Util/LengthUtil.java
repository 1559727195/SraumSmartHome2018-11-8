package com.massky.sraum.Util;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by zhu on 2018/9/19.
 */

public class LengthUtil {

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
            return true;
        }
        return false;
    }


    public static int doit(String s) {
        int characters = 0;
        int numbers = 0;
        for (char c : s.toCharArray()) {
//            if ((c >= 33 && c <= 47) || (c >= 58 && c <= 64) || (c >= 91 && c <= 96) || (c >= 123 && c <= 126))
//                symbols++;
//            if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122))
//                characters++;
////            if (c >= 48 && c <= 57)
////                numbers++;
            if (isChinese(c)) {
                numbers++;
            } else {
                characters++;
            }
        }

        return numbers * 2 + characters;
    }

    /**
     * 分割字符串显示
     * @param s
     * @return
     */
    public static String doit_spit_str(String s) {
        int characters = 0;
        int numbers = 0;
        String str = "";
        for (int i = 0; i < s.toCharArray().length; i++) {
            if (isChinese(s.toCharArray()[i])) {
                numbers++;
            } else {
                characters++;
            }
            if (numbers * 2 + characters >= 6) {
                String first = s.substring(0, i + 1);
                String splits = s.substring(i + 1);
                str = first + "\n" + splits;
                break;
            }
        }

        if (numbers * 2 + characters < 6) {
            str = s;
        }
        return str;
    }
}
