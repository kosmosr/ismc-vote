package top.mollysu.ismcvote.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.Instant;
import top.mollysu.ismcvote.constant.FateConstant;
import top.mollysu.ismcvote.exception.VoteException;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author zengminghao
 * @date 2020/5/18 9:59
 */
@Log4j2
public class FateApi {
    private final OkHttpClient client;

    public FateApi() {
        this.client = new OkHttpClient();
    }

    /**
     * 获取用户余额查询接口
     *
     * @return
     */
    public BigDecimal getFateAccount() {
        String timestamp = String.valueOf(Instant.now().getMillis() / 1000);
        FormBody body = new FormBody.Builder()
                .add("user_id", FateConstant.FATE_PD_ID)
                .add("timestamp", timestamp)
                .add("sign", getSign(timestamp)).build();
        Request request = new Request.Builder()
                .url("http://pred.fateadm.com/api/custval")
                .post(body).build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                log.error("【FateApi】用户余额查询接口，调用失败, 响应为空, request: {}", request);
                throw new VoteException("【FateApi】用户余额查询接口，调用失败");
            }
            JSONObject jsonObject = JSON.parseObject(responseBody.string());
            if (jsonObject.getInteger("RetCode").equals(0)) {
                JSONObject rspData = jsonObject.getJSONObject("RspData");
                return rspData.getBigDecimal("cust_val");
            } else {
                log.error("【FateApi】用户余额查询接口，调用失败, response: {}", jsonObject);
                throw new VoteException("【FateApi】用户余额查询接口，调用失败");
            }
        } catch (IOException e) {
            log.error("【FateApi】用户余额查询接口，调用失败，exception: {}", e.toString());
            throw new VoteException("【FateApi】用户余额查询接口，调用失败");
        }
    }

    /**
     * 识别验证码
     *
     * @param verify 验证码的base64格式
     * @return
     */
    public String regVerifyCode(String verify) {
        String timestamp = String.valueOf(Instant.now().getMillis() / 1000);
        FormBody formBody = new FormBody.Builder()
                .add("user_id", FateConstant.FATE_PD_ID)
                .add("timestamp", timestamp)
                .add("predict_type", "10400")
                .add("img_data", verify)
                .add("sign", getSign(timestamp)).build();

        Request request = new Request.Builder()
                .url("http://pred.fateadm.com/api/capreg")
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                log.error("【FateApi】识别验证码接口，调用失败, 响应为空, request: {}", request);
                throw new VoteException("【FateApi】识别验证码接口，调用失败");
            }
            JSONObject jsonObject = JSON.parseObject(body.string());
            if (jsonObject.getInteger("RetCode").equals(0)) {
                JSONObject rspData = jsonObject.getJSONObject("RspData");
                return rspData.getString("result");
            } else {
                log.error("【FateApi】识别验证码接口，调用失败, response: {}", jsonObject);
                throw new VoteException("【FateApi】识别验证码接口，调用失败");
            }
        } catch (IOException e) {
            log.error("【FateApi】识别验证码接口，调用失败", e);
            throw new VoteException("【FateApi】识别验证码接口，调用失败");
        }
    }

    private String getSign(String timestamp) {
        return DigestUtils.md5Hex(FateConstant.FATE_PD_ID + timestamp + DigestUtils.md5Hex(timestamp + FateConstant.FATE_PD_KEY));
    }
}

