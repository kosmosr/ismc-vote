package top.mollysu.ismcvote;

import org.junit.Assert;
import org.junit.Test;
import top.mollysu.ismcvote.utils.UserAgentUtil;

import java.util.List;

/**
 * @author zengminghao
 * @date 2020/4/28 9:30
 */
public class ReadUAFileTest {
    @Test
    public void read() {
        List<String> read = UserAgentUtil.read();
        Assert.assertFalse(read.isEmpty());
    }
}
