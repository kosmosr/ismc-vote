package top.mollysu.ismcvote.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import top.mollysu.ismcvote.constant.SCConstant;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.IOException;

/**
 * @author kosmosr
 * @date 2020/6/2 10:06
 * Server酱通知
 */
@Log4j2
public class NotificationApi {
    private final OkHttpClient client;

    public static NotificationApi instance = new NotificationApi();

    private NotificationApi() {
        this.client = new OkHttpClient.Builder().hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).build();
    }

    public void sendNotification(String title, String content) {
        String url = String.format("https://sc.ftqq.com/%s.send", SCConstant.SC_KEY);
        FormBody formBody = new FormBody.Builder()
                .add("text", title)
                .add("desp", content).build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                log.error("【Server酱】发送通知失败, request: {}", request);
            }
            JSONObject jsonObject = JSON.parseObject(body.string());
            if (!"success".equals(jsonObject.getString("errmsg"))) {
                log.error("【Server酱】发送通知失败, response: {}", jsonObject);
            }
        } catch (IOException e) {
            log.error("【Server酱】发送通知失败", e);
        }
    }
}
