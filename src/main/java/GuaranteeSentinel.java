import cn.hutool.core.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 保供套餐抢购模式 可长时间运行
 */
public class GuaranteeSentinel {

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    public static void main(String[] args) {

        //执行任务请求间隔时间最小值
        int sleepMillisMin = 1000;
        //执行任务请求间隔时间最大值
        int sleepMillisMax = 5000;

        //单轮轮询时请求异常（服务器高峰期限流策略）尝试次数
        int loopTryCount = 8;

        //60次以后长时间等待10分钟左右
        int longWaitCount = 0;

        Api.init(UserConfig.deliveryType);
        Map<String, Object> deliveryAddressDetail = Api.getDeliveryAddressDetail();
        Map<String, Object> storeDetail = Api.getMiniUnLoginStoreList(Double.parseDouble((String) deliveryAddressDetail.get("latitude")), Double.parseDouble((String) deliveryAddressDetail.get("longitude")));


        List<GoodDto> saveGoodList = new ArrayList<>();

        boolean first = true;
        while (!Api.context.containsKey("end")) {
            try {
                if (first) {
                    first = false;
                } else {
                    if (longWaitCount++ > 60) {
                        longWaitCount = 0;
                        sleep(RandomUtil.randomInt(50000, 70000));
                    } else {
                        sleep(RandomUtil.randomInt(sleepMillisMin, sleepMillisMax));
                    }
                }

                List<GoodDto> goodDtos = null;
                for (int i = 0; i < loopTryCount && goodDtos == null; i++) {
                    goodDtos = Api.getGoodsListByCategoryId(storeDetail);
                    if (goodDtos == null) {
                        sleep(RandomUtil.randomInt(500, 1000));
                    }

                }
                if (goodDtos == null) {
                    continue;
                }
                if (saveGoodList.containsAll(goodDtos)) {
                    System.out.println("全部套餐都已经下单");
                    continue;
                }

                for (int i = 0; i < loopTryCount && Api.context.get("capacityData") == null; i++) {
                    Map<String, Object> capacityData = Api.getCapacityData(storeDetail);
                    if (capacityData == null) {
                        sleep(RandomUtil.randomInt(500, 1000));
                        continue;
                    }
                    Api.context.put("capacityData", capacityData);
                }
                if (Api.context.get("capacityData") == null) {
                    continue;
                }

                Boolean addFlag = null;
                goodDtos.removeAll(saveGoodList);
                if (!goodDtos.isEmpty()) {
                    for (int i = 0; i < loopTryCount && addFlag == null; i++) {
                        addFlag = Api.addCartGoodsInfo(goodDtos);
                        if (addFlag == null){
                            sleep(RandomUtil.randomInt(500, 1000));
                        }
                    }
                }
                if (addFlag == null) {
                    continue;
                }

                for (int i = 0; i < 50; i++) {
                    if (Api.commitPay(goodDtos, (Map<String, Object>) Api.context.get("capacityData"), deliveryAddressDetail, storeDetail)) {
                        Api.play();
                        saveGoodList.addAll(goodDtos);
                        break;
                    }
                    sleep(RandomUtil.randomInt(50, 100));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
