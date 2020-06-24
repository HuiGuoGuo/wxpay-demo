package com.stone.demo.wxpay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtil {

    public static String urlEncode(String string, String enc) {
        try {
            return URLEncoder.encode(string, enc);
        } catch (UnsupportedEncodingException e) {
            return string;
        }
    }
}
