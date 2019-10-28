package com.brandslink.cloud.logistics.utils;

import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;

import java.util.List;

public class CheckContinuityIntervalUtils {

    /**
     * 效验是否是 连续 区间 内容 
     * 效验内容   0-100 
     *       100-200 
     *       200-300 
     *       300-- 正无穷 
     *      当前计算公式   
     *      0< X <=100
     *      100< X <=200
     *      200< X <=无穷
     *
     * @return
     */
    public static void checkContinuityInterval(List<int[]> list) throws Exception {
        if (!list.isEmpty()) {
            int begin = 0;
            int[] first = list.get(0);
            if (first.length != 0) {
                if (first.length == 2) {
                    for (int i = 0; i < first.length; i++) {
                        /*if (first[0] != 0) {
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前的起始位置不为0");
                        }*/
                        begin = first[1];
                        break;
                    }
                } else {
                    throw new Exception("当前只有开区间，没有闭合区间");
                }
            }
            for (int i = 1; i < list.size(); i++) {
                int[] array = list.get(i);
                if (array.length < 2) {
                    array[1] = 100000000;
                } else {
                    if (array[0] != begin) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "区间设置不符合规范，开始区间 是" + array[0] + "结束区间 是" + array[1]);
                    }
                    begin = array[1];
                }
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "传入数据为空");
        }
    }
}
