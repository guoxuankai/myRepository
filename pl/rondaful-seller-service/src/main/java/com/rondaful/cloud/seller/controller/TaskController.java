package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.entity.AliexpressPublishListing;
import com.rondaful.cloud.seller.entity.EbayProductCategory;
import com.rondaful.cloud.seller.entity.EbayPublishListingError;
import com.rondaful.cloud.seller.entity.EbayPublishListingNew;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishModel;
import com.rondaful.cloud.seller.enums.AliexpressEnum;
import com.rondaful.cloud.seller.rabbitmq.AliexpressReceiver;
import com.rondaful.cloud.seller.rabbitmq.AliexpressSender;
import com.rondaful.cloud.seller.rabbitmq.EbayOpertionReceiver;
import com.rondaful.cloud.seller.rabbitmq.EmpowerSender;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.service.*;
import com.rondaful.cloud.seller.vo.EmpowerVo;
import com.rondaful.cloud.seller.vo.PublishListingVO;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 基础数据controller
 *
 * @author chenhan
 */
@Api(description = "定时器任务接口")
@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Autowired
    private IAliexpressPublishListingService aliexpressPublishListingService;
    @Autowired
    private AliexpressSender aliexpressSender;
    @Autowired
    private AliexpressReceiver aliexpressReceiver;
    @Autowired
    private AliexpressConfig config;
    @Autowired
    private IEmpowerService empowerService;
    @Autowired
    private IEbayBaseService ebayBaseService;
    @Autowired
    private IEbayPublishListingService ebayPublishListingService;
    @Autowired
    private EbayOpertionReceiver ebayOpertionReceiver;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private EmpowerSender empowerSender;
    @Autowired
    private EbayRecordAttributeSelectService ebayRecordAttributeSelectService;

    @Autowired
    private RemoteCommodityService remoteCommodityService;

    /**
     * 定时执行查询审核中的刊登商品是否审核成功
     */
    @PostMapping("/aliexpressPublishTask")
    public void aliexpressPublishTask() {
        logger.info("定时执行查询审核中的刊登商品是否审核成功start------------------------------------------->");
        List<AliexpressPublishModel> listAliexpressPublishModel = aliexpressPublishListingService.getAliexpressPublishModelList(4);
        logger.info("数据{}条", listAliexpressPublishModel.size());
        listAliexpressPublishModel.forEach(model -> {
            aliexpressSender.send(model);
        });
        logger.info("定时执行查询审核中的刊登商品是否审核成功end------------------------------------------->");

        logger.info("定时执行查询刊登中的刊登商品start------------------------------------------->");
        List<AliexpressPublishModel> listAliexpress = aliexpressPublishListingService.getAliexpressPublishModelList(2);
        listAliexpress.forEach(model -> {
            AliexpressPublishListing aliexpressPublishListingNew = new AliexpressPublishListing();
            aliexpressPublishListingNew.setUpdateTime(new Date());
            aliexpressPublishListingNew.setId(model.getId());
            aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.PUBLISH_FAILED.getCode());
            aliexpressPublishListingService.updateByPrimaryKeySelective(aliexpressPublishListingNew);
            aliexpressPublishListingService.insertAliexpressPublishListingError(model.getId(), "1000", "connection timeout", "Error");
        });
        logger.info("定时执行查询刊登中的刊登商品end------------------------------------------->");

        logger.info("ebay定时执行查询刊登中的刊登商品start------------------------------------------->");
        List<Long> ids = ebayPublishListingService.getByStatusTask(2);
        if(ids!=null && ids.size()>0){
            for(Long id:ids){
                EbayPublishListingNew ebayPublishListing = new EbayPublishListingNew();
                ebayPublishListing.setUpdateTime(new Date());
                ebayPublishListing.setId(id);
                ebayPublishListing.setStatus(5);
                ebayPublishListingService.updateByPrimaryKeySelective(ebayPublishListing);
                EbayPublishListingError listingError = new EbayPublishListingError();
                listingError.setListingId(id.intValue());
                listingError.setCreationTime(new Date());
                listingError.setErrorCode("1000");
                listingError.setSeverityCode("1000");
                listingError.setLongMessage("connection timeout");
                listingError.setShortMessage("connection timeout");
                listingError.setMsg("connection timeout");
                ebayPublishListingService.insertListingError(listingError);
            }
        }
        logger.info("ebay定时执行查询刊登中的刊登商品end------------------------------------------->");

        logger.info("授权店铺到期改状态start------------------------------------------->");
        empowerService.checkEndTime();
        logger.info("授权店铺到期改状态send------------------------------------------->");
    }


    /**
     * 定时执行查询审核中的刊登商品是否审核成功
     */
    @PostMapping("/sendMsgEmpowerTask")
    public void sendMsgEmpowerTask() {
        logger.info("MsgEmpower start------------------------------------------->");
        empowerService.sendMsgEmpower();
        logger.info("MsgEmpower send------------------------------------------->");
    }




    @PostMapping("/test")
    public void test(Long id,int type,String site) {
        try {
            if(type==0) {
                AliexpressPublishModel model = new AliexpressPublishModel();
                model = aliexpressPublishListingService.getPublishModelById(id, 1);
                aliexpressReceiver.process(model);
            }else if(type==1){

                PublishListingVO vo = ebayPublishListingService.findListingById(id.intValue());
                ebayOpertionReceiver.process(vo);
            }else if(type==6){
                ebayRecordAttributeSelectService.saveEbayRecordAttributeSelect(id);
                //ebayRecordAttributeSelectService.getEbayRecordAttributeSelectByPublish(824L,"Australia","B-1-8A7D85B8");
            }else if(type==7){
                PublishListingVO vo = ebayPublishListingService.findListingById(id.intValue());
                ebayPublishListingService.setRemoteOrderRule(vo);

                String skuMaps = remoteCommodityService.getSkuMapByPlatformSku(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform(), 1367+"","46689024004&ZL3P02");
                if(skuMaps!=null) {
                    String result = Utils.returnRemoteResultDataString(skuMaps, "转换失败");
                    JSONObject json = JSONObject.parseObject(result);
                    if (json != null){
                        JSONArray skuList = json.getJSONArray("skuList");
                        for(int i=0;i<skuList.size();i++){
                            JSONObject obj = skuList.getJSONObject(i);
                            obj.get("systemSku");
                            obj.get("specValueEn");
                            obj.get("commodityNameCn");
                            obj.get("specValueCn");
                            obj.get("commodityNameEn");
                            obj.get("skuNum");
                        }
                    }
                }
            }else if(type==2){
                String company ="";
                Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(id.intValue(), null, 1);
                if(sell!=null) {
                    JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
                    if ("true".equals(selljs.getString("success"))) {
                        JSONObject jsonObjectcompany = selljs.getJSONObject("data");
                        company = jsonObjectcompany.getString("supplyId");

                    }
                }
                System.out.println(company);
                String msg="{\"account\":\"test_eBay\",\"company\":\"59\",\"pinlianAccount\":\"785611422@qq.com\",\"platform\":1,\"type\":0}";
                EmpowerVo vo = JSONObject.parseObject(msg, EmpowerVo.class);
                empowerSender.send(vo);
            }else if(type==4){
                ebayBaseService.updateEbaySiteDetailshipping();
            }else if(type==3){
                EbayProductCategory category = new EbayProductCategory();
                category.setSite(site);
                List<EbayProductCategory> listEbayProductCategory = ebayBaseService.findCategoryByValue(category);

                String url="https://www.dianxiaomi.com/ebayCategory/attributeList.json";
                Map<String, String> map= Maps.newHashMap();
                int count=0;
                boolean bool= true;
                for(EbayProductCategory cg:listEbayProductCategory){
                    System.out.println(cg.getId());
                    System.out.println(cg.getId());
                    System.out.println(cg.getId());
                    count++;
                    if(count>=50){
                        Thread.sleep (2000);//暂停10秒
                        count=0;
                    }
                    map.put("site",cg.getSite());
                    map.put("categoryId",cg.getCategoryid());
                    String str = HttpUtil.post(url,map);

                    System.out.println(str);

                    if(str!=null && isJson(str)) {
                        JSONObject jsonObject = JSONObject.parseObject(str);
                        if(jsonObject!=null) {
                            JSONArray jsonArray = jsonObject.getJSONArray("attributeList");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    String minValues = json.getString("minValues");
                                    //String site = json.getString("site");
                                    String categoryId = json.getString("categoryId");
                                    String name = json.getString("name");
                                    if("Brand".equals(name) || "MPN".equals(name)){
                                        continue;
                                    }
                                    if (minValues != null && !"".equals(minValues) && !"0".equals(minValues)) {
                                        StringBuffer sql = new StringBuffer();
                                        sql.append(" UPDATE ebay_product_category_attribute SET min_values=" + minValues + " WHERE id=(SELECT id FROM ebay_product_category_attribute_3 WHERE category_id=" + categoryId + " AND site=\"" + site + "\"");
                                        sql.append(" AND `name`=\"" + name + "\");");
                                        System.out.println(sql);
                                        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                                        String fileName = "F:/sql/" + site + ".txt";
                                        FileWriter writer = new FileWriter(fileName, true);
                                        writer.write(sql.toString() + "\r\n");
                                        writer.close();
                                    }
                                }
                            }
                        }
                    }


                }

            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean isJson(String string) {
        try {
            JSONObject jsonStr = JSONObject.parseObject(string);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }
}


