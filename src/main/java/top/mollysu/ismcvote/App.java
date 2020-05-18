package top.mollysu.ismcvote;

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import top.mollysu.ismcvote.core.FateApi;
import top.mollysu.ismcvote.core.VoteApi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 */
@Log4j2
public class App {
    public static void main(String[] args) {
        Random random = new Random();
        int randomSeconds = random.nextInt(5);
        LocalDateTime now = LocalDateTime.now();
        log.info("当时时间: {}, 执行时间: {}, randomSeconds: {}", now, now.plusSeconds(randomSeconds), randomSeconds);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("投票程序开始, 当前时间: {}", Instant.now());
                log.info("env: {}, properties: {}", System.getenv(), System.getProperties());
                FateApi fateApi = new FateApi();
                BigDecimal fateAccount = fateApi.getFateAccount();
                if (fateAccount.equals(BigDecimal.ZERO) || fateAccount.compareTo(new BigDecimal(50)) < 0) {
                    log.info("投票结束，打码平台余额不足, fateAccount: {}", fateAccount);
                } else {
                    OkHttpClient client = new OkHttpClient();
                    String orderId = System.getenv("orderId");
                    String signature = System.getenv("signature");
                    String url = String.format("https://dps.kdlapi.com/api/getdps/?orderid=%s&num=1&signature=%s&pt=1&dedup=1&sep=2", orderId, signature);
                    Request request = new Request.Builder()
                            .url(url).get().build();
                    try (Response response = client.newCall(request).execute()) {
                        String proxy = response.body().string();
                        VoteApi voteApi = new VoteApi(proxy);

                        AtomicInteger i = new AtomicInteger();
                        voteApi.getVerifyCode();
                        while (i.get() < 5) {
                            try {
                                Optional.ofNullable(voteApi.getVerifyCode())
                                        .map(verifyCode -> {
                                            String regVerifyCode = fateApi.regVerifyCode(verifyCode);
                                            if (regVerifyCode == null || regVerifyCode.indexOf(0) == '0') {
                                                return null;
                                            }
                                            return regVerifyCode;
                                        })
                                        .ifPresent(verifyCode -> {
                                            voteApi.vote(verifyCode);
                                            i.getAndIncrement();
                                        });
                            } catch (Exception e) {
                                log.error("投票失败, exception: {}", e.toString());
                                break;
                            }
                        }
                    } catch (IOException e) {
                        log.error("获取代理失败！exception: {}", e.toString());
                    }
                }
            }
        }, randomSeconds * 1000);
    }
}
