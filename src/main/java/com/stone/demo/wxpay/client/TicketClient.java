package com.stone.demo.wxpay.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stone.demo.wxpay.util.HttpUtil;
import com.stone.demo.wxpay.util.JsApiSignUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.stone.demo.wxpay.util.Constant.ENDPOINT_ACCESS_TOKEN;
import static com.stone.demo.wxpay.util.Constant.ENDPOINT_TICKET;

/**
 * @author : Stone
 * @date : 2020/6/23
 */

@Slf4j
public class TicketClient {


    private final HttpUtil httpUtil = new HttpUtil();



    /**
     * 拼接 access_token url
     *
     * @param appId
     * @param appSecret
     * @return
     */
    private static String accessTokenUrl(final String appId, final String appSecret) {
        return new StringBuilder(ENDPOINT_ACCESS_TOKEN)
                .append("?appid=")
                .append(appId)
                .append("&secret=")
                .append(appSecret)
                .append("&grant_type=")
                .append("client_credential").toString();
    }

    /**
     * 获取access_token
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public String getAccessToken(final String appId, final String appSecret) {
        final String url = TicketClient.accessTokenUrl(appId, appSecret);
        return httpUtil.executeGet(url);
    }

    /**
     * 拼接 jsapi_ticket url
     *
     * @param accessToken
     * @return
     */
    public static String ticketUrl(final String accessToken) {
        return new StringBuilder(ENDPOINT_TICKET)
                .append("?access_token=")
                .append(accessToken)
                .append("&type=")
                .append("jsapi").toString();
    }

    /**
     * 获取jsapi_ticket
     *
     * @param accessToken
     * @return
     */
    public String getTicket(final String accessToken) {
        final String url = TicketClient.ticketUrl(accessToken);
        return this.httpUtil.executeGet(url);
    }

}
