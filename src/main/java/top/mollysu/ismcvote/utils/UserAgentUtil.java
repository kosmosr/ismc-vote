package top.mollysu.ismcvote.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zengminghao
 * @date 2020/4/28 16:01
 */
@Log4j2
public class UserAgentUtil {
    private static final String USER_AGENT_FILE_NAME = "ua_string.csv";

    public static List<String> read() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(UserAgentUtil.class.getResourceAsStream('/' + USER_AGENT_FILE_NAME))
        )) {
            List<String> userAgents = new ArrayList<>(606);
            String userAgent;
            while (StringUtils.isNotEmpty(userAgent = reader.readLine())) {
                userAgent = userAgent.substring(1, userAgent.length() - 1);
                userAgents.add(userAgent);
            }
            log.info("已读取所有UserAgents, size: {}", userAgents.size());
            return userAgents;
        } catch (IOException e) {
            log.error("读取UserAgents失败，exception: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
