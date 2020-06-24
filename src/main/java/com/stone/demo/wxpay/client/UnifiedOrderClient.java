package com.stone.demo.wxpay.client;

import com.stone.demo.wxpay.util.HttpUtil;
import com.stone.demo.wxpay.util.UrlUtil;

import java.nio.charset.StandardCharsets;

import static com.stone.demo.wxpay.util.Constant.*;

/**
 * @author : Stone
 * @date : 2020/6/23
 */
public class UnifiedOrderClient {

    private final HttpUtil httpUtil = new HttpUtil();


    /**
     * 获取授权链接
     *
     * @param redirectUri
     * @param state
     * @return
     */
    public String authorizeUrl(final String appId, final String redirectUri, final String state) {
        final String encodeUrl = UrlUtil.urlEncode(redirectUri, StandardCharsets.UTF_8.name());
        return ENDPOINT_AUTHORIZE + "?appid=" + appId + "&redirect_uri=" + encodeUrl + "&response_type=code&scope=snsapi_base&state=" + state + "#wechat_redirect";
    }


    public String accessTokenWebUrl(final String appId, final String appSecret, final String code) {
        return ENDPOINT_ACCESS_TOKEN_WEB + "?appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
    }


    public String accessTokenWeb(final String code) {
        final String url = this.accessTokenWebUrl(APP_ID, APP_SECRET, code);
        return this.httpUtil.executeGet(url);
    }

}
