import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息
 * 通过小程序抓包购物车接口获取headers和body中的数据填入
 */
public class UserConfig {

    //1：极速达 2：全城配送
    @Deprecated
    public static final String deliveryType = "1";
    //下单目标金额
    public static final Integer targetAmount = 0;
    //是否使用优惠券
    public static final Boolean coupon = false;
    //bark通知id
    public static final String barkId = "";
    //Server酱 用户 Token，可选参数，获取方式：https://sct.ftqq.com/sendkey
    public static final String ftqqSendKey = "";
    /**
     * 抓包小程序，在headers中找到auth-token
     */
    public static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "api-sams.walmartmobile.cn");
        headers.put("Connection", "keep-alive");
        headers.put("Accept", "*/*");
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("Content-Length", "221");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("User-Agent", "SamClub/5.0.45 (iPhone; iOS 15.4; Scale/3.00)");
        headers.put("device-type", "mini_program");
        headers.put("auth-token", "需要填写");
        return headers;
    }

    public static Map<String,Object> getIdInfo() {
        Map<String,Object> idInfo = new HashMap<>();
        idInfo.put("uid", "181817123575");
        idInfo.put("appId", "wxb344a8123eaaf849");
        idInfo.put("saasId", "1818");
        return idInfo;
    }

}
