package com.rondaful.cloud.seller.utils;

import java.util.Random;

public class GeneratePlateformSku {
    public static void main(String[] ard ){
        System.out.println(GeneratePlateformSku.getAliexpressPlateformSku("1232dsfsd"));
    }
    //品连sku+分隔符（*、&、#、|。取4个字符随机一位）+后缀（随机6位数字和字母组合）
    public static String getAliexpressPlateformSku(String sku){
        Random rand=new Random();//随机用以下三个随机生成器
        String[] separator = {"*","&","#","|"};
        int index=rand.nextInt(4);
        StringBuilder sb=new StringBuilder();
        sb.append(sku);
        sb.append(separator[index]);
        String str = GeneratePlateformSku.createRandomCharData(6);
        sb.append(str.toUpperCase());
        return sb.toString();
    }
    //
    public static String getEbayPlateformSku(String sku,int length){
        Random rand=new Random();//随机用以下三个随机生成器
        String[] separator = {"*","&","#","|"};
        int index=rand.nextInt(4);
        StringBuilder sb=new StringBuilder();
        sb.append(sku);
        sb.append(separator[index]);
        String str = GeneratePlateformSku.createRandomCharData(length);
        sb.append(str.toUpperCase());
        return sb.toString();
    }
    //根据指定长度生成字母和数字的随机数
    //0~9的ASCII为48~57
    //A~Z的ASCII为65~90
    //a~z的ASCII为97~122
    public static String createRandomCharData(int length)
    {
        StringBuilder sb=new StringBuilder();
        Random rand=new Random();//随机用以下三个随机生成器
        Random randdata=new Random();
        int data=0;
        for(int i=0;i<length;i++)
        {
            int index=rand.nextInt(3);
            //目的是随机选择生成数字，大小写字母
            switch(index)
            {
                case 0:
                    data=randdata.nextInt(10);//仅仅会生成0~9
                    sb.append(data);
                    break;
                case 1:
                    data=randdata.nextInt(26)+65;//保证只会产生65~90之间的整数
                    sb.append((char)data);
                    break;
                case 2:
                    data=randdata.nextInt(26)+97;//保证只会产生97~122之间的整数
                    sb.append((char)data);
                    break;
            }
        }
        String result=sb.toString();
        return result;
    }
}
