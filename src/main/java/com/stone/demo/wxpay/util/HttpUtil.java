package com.stone.demo.wxpay.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

/**
 * @author : Stone
 * @date : 2020/6/23
 */
@Slf4j
public class HttpUtil {


    private int times = 5;

    private final RestTemplate restTemplate = new RestTemplate();

    public String executeGet(final String url) {
        int i = 1;
        String result = "";
        while (this.times > 0) {
            try {
                log.warn("尝试请求第: {} 次", i);
                result = this.restTemplate.getForObject(url, String.class);
                break;
            } catch (final Exception e) {
                i++;
                this.times--;
                log.error("", e);
            }
        }
        log.warn("请求地址: {} ", url);
        log.warn("请求结果: {} ", result);
        return result;
    }
}
