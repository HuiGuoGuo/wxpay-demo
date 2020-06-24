package com.stone.demo.wxpay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import com.stone.demo.wxpay.client.TicketClient;
import com.stone.demo.wxpay.client.UnifiedOrderClient;
import com.stone.demo.wxpay.util.JsApiSignUtil;
import com.stone.demo.wxpay.util.WxPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.wxpay.sdk.WXPayConstants.DOMAIN_API;
import static com.stone.demo.wxpay.util.Constant.*;

/**
 * @author : Stone
 * @date : 2020/6/24
 */
@Slf4j
@Controller
@RequestMapping(value = "/wx")
public class WxController {


    private TicketClient ticketClient = new TicketClient();

    private UnifiedOrderClient unifiedOrderClient = new UnifiedOrderClient();


    /**
     * 返回成功xml
     */
    private static final String RETURN_MSG_XML = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

    /**
     * 返回失败xml
     */
    private static final String RES_FAIL_XML = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[报文为空]]></return_msg></xml>";


    @ResponseBody
    @GetMapping(value = "/getConfigInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getConfigInfo(@RequestParam(name = "url") String url) {
        final String accessTokenResult = ticketClient.getAccessToken(APP_ID, APP_SECRET);
        final JSONObject tokenResult = JSON.parseObject(accessTokenResult);
        final String accessToken = tokenResult.getString("access_token");
        log.info("获取到的access_token为: {} ", accessToken);
        final String jsTicketResult = ticketClient.getTicket(accessToken);
        final JSONObject ticketResult = JSON.parseObject(jsTicketResult);
        final String ticket = ticketResult.getString("ticket");
        log.info("获取到的ticket为: {} ", ticket);
        final Map<String, String> map = JsApiSignUtil.signature(ticket, url);
        log.info("map : {}", map);
        map.put("appId", APP_ID);
        return map;
    }

    /**
     * 网页授权跳转
     *
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    public void authorize(HttpServletResponse response) throws IOException {
        String redirectUrl = "http://miao.com/wx/accessTokenWeb";
        String state = UUID.randomUUID().toString().replace("-", "");
        final String authorizeUrl = unifiedOrderClient.authorizeUrl(APP_ID, redirectUrl, state);
        response.sendRedirect(authorizeUrl);
    }

    /**
     * 网页授权获取 openId 我这里为了测试直接简化把openId放在url返回到pay页面,实际开发应存储在后台并从后台获取
     *
     * @param code
     * @param state
     */
    @GetMapping(value = "/accessTokenWeb", produces = MediaType.APPLICATION_JSON_VALUE)
    public void accessTokenWeb(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state, HttpServletResponse response) throws IOException {
        final String accessTokenResult = unifiedOrderClient.accessTokenWeb(code);
        final JSONObject resultJson = JSON.parseObject(accessTokenResult);
        response.sendRedirect("http://miao.com/pay.html?openId=" + resultJson.getString("openid"));
    }

    /**
     * 统一下单接口
     *
     * @param openId
     * @param total
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping(value = "/unifiedOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object unifiedOrder(@RequestParam(name = "openId") String openId, @RequestParam(name = "total") BigDecimal total, HttpServletRequest request) throws Exception {
        WXPayConfig config = new WxPayConfig(APP_ID, MCH_ID, APP_KEY, DOMAIN_API);
        WXPay wxpay = new WXPay(config);
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "支付0.01元");
        //订单号,这里自己生成 ，对应自己实际唯一订单号 最长32位
        data.put("out_trade_no", UUID.randomUUID().toString().replace("-", ""));
        data.put("device_info", "WEB");
        data.put("fee_type", "CNY");
        //单位分,所以要把金额*100
        data.put("total_fee", total.multiply(new BigDecimal(100)).intValue() + "");
        data.put("spbill_create_ip", request.getRemoteAddr());
        //支付成功微信回调地址,见 com.stone.demo.wxpay.controller.WxController.notify
        data.put("notify_url", "http://miao.com/wx/notify");
        // 此处指定为JS API支付
        data.put("trade_type", "JSAPI");
        data.put("openid", openId);
        Map<String, String> resp = wxpay.unifiedOrder(data);
        log.info("统一下单返回值: {}", resp);
        //封装返回参数
        return JsApiSignUtil.paySign(APP_ID, resp.get("prepay_id"), APP_KEY);
    }

    @GetMapping(value = "/notify")
    public void notify(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String result = RES_FAIL_XML;
        try {
            final String notifyMap = getNotifyMap(request);
            log.info("支付通知参数: {} ", notifyMap);
            result = RETURN_MSG_XML;
            //判断签名是否正确
            //处理业务逻辑
        } catch (Exception e) {
            e.printStackTrace();
        }
        final BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(result.getBytes());
        out.flush();
        out.close();
    }


    private static String getNotifyMap(final HttpServletRequest request) throws Exception {
        final InputStream inStream = request.getInputStream();
        final ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }

        // 获取微信调用我们notify_url的返回信息
        final String result = new String(outSteam.toByteArray(), StandardCharsets.UTF_8);
        log.info("微信支付----{}----", result);
        // 关闭流
        outSteam.close();
        inStream.close();
        return result;
    }


}
