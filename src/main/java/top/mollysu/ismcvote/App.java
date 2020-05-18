package top.mollysu.ismcvote;

import lombok.extern.log4j.Log4j2;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import top.mollysu.ismcvote.core.FateApi;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Hello world!
 */
@Log4j2
public class App {
    public static void main(String[] args) {
        Random random = new Random();
        int randomSeconds = random.nextInt(120);
        LocalDateTime now = LocalDateTime.now();
        log.info("当时时间: {}, 执行时间: {}, randomSeconds: {}", now, now.plusSeconds(randomSeconds), randomSeconds);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("投票程序开始, 当前时间: {}", Instant.now());
                FateApi fateApi = new FateApi();
                BigDecimal fateAccount = fateApi.getFateAccount();
                if (fateAccount.equals(BigDecimal.ZERO) || fateAccount.compareTo(new BigDecimal(50)) < 0) {
                    log.info("投票结束，打码平台余额不足, fateAccount: {}", fateAccount);
                    return;
                }

            }
        }, randomSeconds * 1000);
    }
}
