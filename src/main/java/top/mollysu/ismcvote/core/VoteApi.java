package top.mollysu.ismcvote.core;

import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.joda.time.Instant;
import top.mollysu.ismcvote.constant.VoteConstant;
import top.mollysu.ismcvote.exception.VoteException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author zengminghao
 * @date 2020/4/28 8:57
 */
@Log4j2
public class VoteApi {

    private final MediaType textHtmlMediaType = MediaType.parse("text/html");

    private final String userAgent;

    private OkHttpClient client;

    public VoteApi(String hostname, int port) {
        this();
        initApi(hostname, port);
    }

    /**
     * @param proxyUrl example: 192.168.0.1:8080
     */
    public VoteApi(String proxyUrl) {
        this();
        initApi(proxyUrl);
    }

    private VoteApi() {
//        System.setProperty("jsse.enableSNIExtension", "false");
        VoteCore voteCore = new VoteCore();
        this.userAgent = voteCore.readRandomUserAgent();
    }

    private void initApi(String hostname, int port) {
        initApi(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port)));
    }

    private void initApi(String proxyUrl) {
        String[] proxy = proxyUrl.split(":");
        initApi(proxy[0], Integer.parseInt(proxy[1]));
    }

    /**
     * 初始化Api
     *
     * @param proxy
     */
    private void initApi(Proxy proxy) {
        this.client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .proxy(proxy)
                .proxyAuthenticator((route, response) -> {
                    String credential = Credentials.basic(VoteConstant.PROXY_AUTH_USER, VoteConstant.PROXY_AUTH_PASSWORD);
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                })
                .cookieJar(new VoteCustomCookieJar())
                .build();
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(VoteConstant.DOMAIN)
                .addPathSegments("nxs/202004/5111.html")
                .addQueryParameter("from", "singlemessage")
                .addQueryParameter("isappinstalled", "0")
                .addQueryParameter("security_verify_data", "313534392c383732")
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .headers(getCommonHeaders())
                .addHeader("userAgent", userAgent)
                .addHeader("Sec-Fetch-Dest", "document")
                .build();
        // init cookie
        for (int i = 0; i < 3; i++) {
            try (Response response = client.newCall(request).execute()) {
                log.info("init index: {}, isSuccessful: {}", i, response.isSuccessful());
            } catch (IOException e) {
                log.error("初始化出错, exception: {}", e.getMessage());
            }
        }

    }

    /**
     * 获取HEADER
     *
     * @return
     */
    private Headers getCommonHeaders() {
        return new Headers.Builder()
                .add("Referer", "https://www.ismc.cc/nxs/202004/5111.html?from=singlemessage&isappinstalled=0")
                .add("Sec-Fetch-Mode", "no-cors")
                .add("Sec-Fetch-Site", "same-origin")
                .add("Host", VoteConstant.DOMAIN)
                .add("Origin", "https://www.ismc.cc")
                .build();
    }

    /**
     * 获取验证码
     *
     * @return
     */
    public String getVerifyCode() {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(VoteConstant.DOMAIN)
                .addPathSegments("findex/verify")
                .addQueryParameter("t", String.valueOf(Instant.now().getMillis()))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(getCommonHeaders())
                .addHeader("Sec-Fetch-Dest", "image")
                .addHeader("User-Agent", userAgent)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                log.error("获取验证码失败, response为空");
                throw new VoteException("投票失败，获取验证码失败");
            }

            if (Objects.equals(body.contentType(), textHtmlMediaType)) {
                log.error("获取验证码失败, 响应内容有误, request: {}", request);
                throw new VoteException("投票失败，获取验证码失败");
            }
            // image to base64
            byte[] imageBytes = body.bytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("获取验证码失败, exception: {}", e.toString());
            throw new VoteException("投票失败, 获取验证码失败");
        }
    }

    public void vote(String verify) {
        FormBody formBody = new FormBody.Builder()
                .add("id", "5111")
                .add("verify", verify)
                .build();
        Request request = new Request.Builder()
                .url("https://www.ismc.cc/ajax/vote")
                .post(formBody).build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                log.error("投票失败, 响应为空, request: {}", request);
                throw new VoteException("投票失败");
            }
            if (Objects.equals(body.contentType(), textHtmlMediaType)) {
                log.error("投票失败, 响应内容有误, request: {}", request);
                throw new VoteException("投票失败");
            }
            log.info("Vote: {}", body.string());
        } catch (IOException e) {
            log.error("投票失败，exception: {}", e.toString());
        }
    }
}
