package top.mollysu.ismcvote.core;

import lombok.extern.log4j.Log4j2;
import top.mollysu.ismcvote.utils.UserAgentUtil;

import java.util.List;
import java.util.Random;

/**
 * @author zengminghao
 * @date 2020/4/28 8:59
 */
@Log4j2
public class VoteCore {

    private final List<String> userAgents;

    public VoteCore() {
        this.userAgents = UserAgentUtil.read();
    }

    protected String readRandomUserAgent() {
        if (userAgents.isEmpty()) return "";
        Random random = new Random();
        int index = random.nextInt(userAgents.size() - 1);
        return userAgents.get(index);
    }
}
