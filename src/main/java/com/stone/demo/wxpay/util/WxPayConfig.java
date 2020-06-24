package com.stone.demo.wxpay.util;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;

import java.io.InputStream;

/**
 * @author : Stone
 * @date : 2020/6/3
 */
public class WxPayConfig extends WXPayConfig {

    private String appId;

    private String mchId ;

    private String key ;

    private String domain;

    public WxPayConfig() {
    }

    public WxPayConfig(String appId, String mchId, String key, String domain) {
        this.appId = appId;
        this.mchId = mchId;
        this.key = key;
        this.domain = domain;
    }

    @Override
    protected String getAppID() {
        return this.appId;
    }

    @Override
    protected String getMchID() {
        return this.mchId;
    }

    @Override
    protected String getKey() {
        return this.key;
    }

    @Override
    protected InputStream getCertStream() {
        return null;
    }

    @Override
    protected IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String s, long l, Exception e) {

            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo(domain,true);
            }
        };
    }
}
