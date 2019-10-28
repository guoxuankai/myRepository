package com.rondaful.cloud.seller.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.seller.entity.PrefixCode;
import com.rondaful.cloud.seller.mapper.PrefixCodeMapper;
import com.rondaful.cloud.seller.service.PrefixCodeService;
import com.rondaful.cloud.seller.task.GenerateUpcThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author guoxuankai
 * @date 2019/6/10
 */
@Service
public class PrefixCodeServiceImpl extends BaseServiceImpl<PrefixCode> implements PrefixCodeService {


    @Autowired
    private PrefixCodeMapper prefixCodeMapper;

    @Override
    public List<String> generateRandomUpc(String code) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String randomChar = getRandomChar(5);
            String s = code + randomChar;
            String validateCode = GenerateUpcThread.getValidateCode(s);
            list.add(s + validateCode);
        }

        return list;
    }

    @Override
    public boolean isRepeat(String code) {
        PrefixCode p = prefixCodeMapper.getByCode(code);
        if (p == null) {
            return false;
        }
        return true;
    }

    @Override
    public int getUpcAmout() {
        return prefixCodeMapper.getUpcAmout();
    }


    public static String getRandomChar(int length) {            //生成随机字符串
        char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(chr[random.nextInt(10)]);
        }
        return buffer.toString();
    }

}
