package com.stone.demo.wxpay.util;

/**
 * @author : Stone
 * @date : 2020/6/24
 */
public class Constant {

    /**
     * appId
     */
    public static final String APP_ID = "";

    /**
     * appSecret
     */
    public static final String APP_SECRET = "";

    /**
     * mchId 商户号
     */
    public static final String MCH_ID = "";
    /**
     * appKey 支付密钥
     */
    public static final String APP_KEY = "";


    /**
     * 获取 access_token 地址
     */
    public static final String ENDPOINT_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 获取 jsapi_ticket 地址
     */
    public static final String ENDPOINT_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    /**
     * 获取 authorize 地址
     */
    public static final String ENDPOINT_AUTHORIZE = "https://open.weixin.qq.com/connect/oauth2/authorize";

    /**
     * 获取网页授权 access_token 地址
     */
    public static final String ENDPOINT_ACCESS_TOKEN_WEB = "https://api.weixin.qq.com/sns/oauth2/access_token";


}
