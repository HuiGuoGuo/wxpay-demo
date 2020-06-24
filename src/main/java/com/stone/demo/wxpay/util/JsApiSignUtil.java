package com.stone.demo.wxpay.util;

import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * author : Stone
 * time : 2019/11/22 17:39
 */
@Slf4j
public final class JsApiSignUtil {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    private JsApiSignUtil() {
    }


    /**
     * @param ticket
     * @param url
     * @return
     */
    public static Map<String, String> signature(final String ticket, final String url) {
        //注意这里参数名必须全部小写，且必须有序
        final Map<String, String> map = new HashMap<>(3);

        String signature = "";

        final String nonceStr = create_nonce_str();

        final String timestamp = create_timestamp();

        final StringBuilder sb = new StringBuilder();
        sb
                .append("jsapi_ticket=")
                .append(ticket)
                .append("&noncestr=")
                .append(nonceStr)
                .append("&timestamp=")
                .append(timestamp)
                .append("&url=")
                .append(url);
        try {
            final MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(sb.toString().getBytes(DEFAULT_CHARSET));
            signature = byteToHex(crypt.digest());
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        map.put("nonceStr", nonceStr);
        map.put("timestamp", timestamp);
        map.put("signature", signature);
        return map;
    }

    private static String byteToHex(final byte[] hash) {
        final Formatter formatter = new Formatter();
        for (final byte b : hash) {
            formatter.format("%02x", b);
        }
        final String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return WXPayUtil.generateNonceStr();
    }

    private static String create_timestamp() {
        return Long.toString(WXPayUtil.getCurrentTimestamp());
    }



    public static final WXPayConstants.SignType PAY_SIGN_TYPE = WXPayConstants.SignType.HMACSHA256;

    public static Map<String, String> paySign(final String appId, final String prepayId, final String key) throws Exception {
        final Map<String, String> payMap = new HashMap<>(5);
        payMap.put("appId", appId);
        payMap.put("timeStamp", WXPayUtil.getCurrentTimestamp() + "");
        payMap.put("nonceStr", WXPayUtil.generateNonceStr());
        payMap.put("signType", WXPayConstants.HMACSHA256);
        payMap.put("package", "prepay_id=" + prepayId);
        final String paySign = WXPayUtil.generateSignature(payMap, key, PAY_SIGN_TYPE);
        payMap.put("paySign", paySign);
        return payMap;
    }
}
