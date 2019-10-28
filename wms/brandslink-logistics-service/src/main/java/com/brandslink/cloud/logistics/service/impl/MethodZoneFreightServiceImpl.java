package com.brandslink.cloud.logistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.entity.common.*;
import com.brandslink.cloud.logistics.mapper.CountryRemoteFeeMapper;
import com.brandslink.cloud.logistics.mapper.MethodZoneCountryMapper;
import com.brandslink.cloud.logistics.mapper.MethodZoneFreightMapper;
import com.brandslink.cloud.logistics.model.CountryRemoteFeeModel;
import com.brandslink.cloud.logistics.model.MethodZoneCountryModel;
import com.brandslink.cloud.logistics.model.MethodZoneFreightModel;
import com.brandslink.cloud.logistics.service.IMethodZoneFreightService;
import com.brandslink.cloud.logistics.utils.CheckContinuityIntervalUtils;
import com.brandslink.cloud.logistics.utils.UserUtil;
import com.brandslink.cloud.logistics.utils.ValidateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MethodZoneFreightServiceImpl extends BaseServiceImpl<MethodZoneFreightModel> implements IMethodZoneFreightService {
    @Autowired
    private MethodZoneFreightMapper zoneMapper;
    @Autowired
    private MethodZoneCountryMapper zoneCountryMapper;
    @Autowired
    private CountryRemoteFeeMapper remoteFeeMapper;
    @Autowired
    private ValidateUtils validateUtils;
    @Autowired
    private UserUtil userUtil;

    private final static Logger _log = LoggerFactory.getLogger(MethodZoneFreightServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long editMethodZoneFreight(MethodZoneFreightModel zoneModel) throws Exception {
        _log.info("_________新增/更新邮寄方式时效运费数据入参为：_________{}________", JSON.toJSONString(zoneModel));
        this.validateData(zoneModel);
        Long methodId = zoneModel.getMethodId();
        List<MethodZoneCountryModel> zoneCountryList = zoneModel.getZoneCountryList();
        zoneCountryList.forEach(x -> x.setMethodId(methodId));
        if (isInsertOrUpdate(zoneModel)) {
            zoneModel.setCreateBy(userUtil.getUserName());
            zoneModel.setUpdateBy(userUtil.getUserName());
            zoneMapper.insertSelective(zoneModel);
            if (CollectionUtils.isNotEmpty(zoneCountryList)) {
                this.insertCountryAndRemote(zoneModel);
            }
        } else {
            zoneModel.setUpdateBy(userUtil.getUserName());
            Long zoneId = zoneModel.getId();
            zoneMapper.updateByPrimaryKeySelective(zoneModel);

            List<Long> countryIdList = zoneCountryMapper.selectSharedZoneCountryByZoneID(zoneId);
            zoneCountryMapper.deleteByZoneId(zoneId);
            if (CollectionUtils.isNotEmpty(countryIdList)) {
                remoteFeeMapper.deleteBatchByCountryId(countryIdList);
            }
            if (CollectionUtils.isNotEmpty(zoneCountryList)) {
                this.insertCountryAndRemote(zoneModel);
            }
        }
        return zoneModel.getId();
    }

    private void insertCountryAndRemote(MethodZoneFreightModel zoneModel) {
        List<MethodZoneCountryModel> zoneCountryList = zoneModel.getZoneCountryList();
        for (MethodZoneCountryModel zoneCountry : zoneCountryList) {
            zoneCountry.setZoneId(zoneModel.getId());
            zoneCountryMapper.insertSelective(zoneCountry);
            if (zoneCountry.getIsRemoteFee().intValue() == 1) {
                List<CountryRemoteFeeModel> remoteFeeList = zoneCountry.getRemoteList();
                if (CollectionUtils.isNotEmpty(remoteFeeList)) {
                    remoteFeeList.forEach(x -> x.setCountryId(zoneCountry.getId()));
                    remoteFeeMapper.insertBatch(remoteFeeList);
                }
            }
        }
    }

    /**
     * 判断插入还是更新（true:insert，false:update）
     *
     * @param zoneModel
     * @return
     */
    private boolean isInsertOrUpdate(MethodZoneFreightModel zoneModel) {
        Long zoneId = zoneModel.getId();
        List<MethodZoneCountryModel> zoneCountryList = zoneModel.getZoneCountryList();
        Long methodId = zoneModel.getMethodId();
        List<MethodZoneCountryModel> list = zoneCountryMapper.selectBatchMethodZoneCountryList(methodId, zoneCountryList);
        if (zoneId != null) {
            if (CollectionUtils.isNotEmpty(list)) {
                Set<Long> set = list.stream().map(x -> x.getZoneId()).collect(Collectors.toSet());
                if (set.size() != 1) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家分区不一致，不能修改");
                }
                if (list.stream().filter(x -> x.getZoneId() != zoneId.longValue()).count() != 0) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "含有不在此分区内的国家，不能修改");
                }
            }
            return false;
        } else {
            if (CollectionUtils.isNotEmpty(list)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "含有不在此分区内的国家，不能修改");
            }
            return true;
        }
    }

    private void validateData(MethodZoneFreightModel zoneModel) throws Exception {
        MaxMin promiseDaysVO = JSONObject.parseObject(JSON.toJSONString(zoneModel.getPromiseDays()), MaxMin.class);
        if (promiseDaysVO.getMax().intValue() < 1 || promiseDaysVO.getMin().intValue() < 1 || promiseDaysVO.getMax().intValue() > 50 || promiseDaysVO.getMin().intValue() > 50
                || promiseDaysVO.getMax().intValue() < promiseDaysVO.getMin().intValue()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "时效天数仅能输入大于0，小于50的整数，并且起始不能大于结束");
        }
        zoneModel.setPromiseDays(JSONObject.parseObject(JSONObject.toJSON(promiseDaysVO).toString()));
        MaxMin weightRangeVO = JSONObject.parseObject(JSON.toJSONString(zoneModel.getWeightRange()), MaxMin.class);
        int weightRangeMin = weightRangeVO.getMin().intValue();
        int weightRangeMax = weightRangeVO.getMax().intValue();
        if (weightRangeMax < 0 || weightRangeMin < 0 || weightRangeMax > 200000 || weightRangeMin > 200000 || weightRangeMax < weightRangeMin) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "重量范围仅能输入大于0，小于200000的整数，并且起始不能大于结束");
        }
        this.validateLengthWideHighRule(JSONObject.parseObject(JSON.toJSONString(zoneModel.getLimitLength()), LimitLength.class));
        if (zoneModel.getIsCountBulk().intValue() == 1) {
            JSONObject countBulkRuleObject = zoneModel.getCountBulkRule();
            if (countBulkRuleObject == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "计抛时计抛规则不能为空");
            }
            CountBulkRule countBulkRule = JSONObject.parseObject(JSON.toJSONString(countBulkRuleObject), CountBulkRule.class);
            if (countBulkRule.getInitWeight() == null || countBulkRule.getVolumeWeightTimes() == null || countBulkRule.getUnilateralLong() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "计抛时起重，材积与实重倍数，单边长三者不能同时为空");
            }
            validateUtils.validate(countBulkRule);
        }
        if (zoneModel.getChargeMode().intValue() == 1) {
            JSONObject ruleFirstRenewObject = zoneModel.getRuleFirstRenew();
            if (ruleFirstRenewObject == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "计费方式选择首重+续重时，首重+续重数据对象不能为空");
            }
            validateUtils.validate(JSONObject.parseObject(JSON.toJSONString(ruleFirstRenewObject), RuleFirstRenew.class));
        }
        if (zoneModel.getChargeMode().intValue() == 2) {
            JSONArray ruleSubsectionArray = zoneModel.getRuleSubsection();
            if (CollectionUtils.isEmpty(ruleSubsectionArray)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "计费方式选择分段时，分段计费数据对象不能为空");
            }
            List<RuleSubsection> list = JSONObject.parseArray(JSON.toJSONString(ruleSubsectionArray), RuleSubsection.class);
            Integer beginWeight = list.get(0).getBeginWeight();
            Integer endWeight = list.get(list.size() == 0 ? 0 : (list.size() - 1)).getEndWeight();
            if (weightRangeMin != 0 && weightRangeMax != 0) {
                if (weightRangeMin != beginWeight || weightRangeMax != endWeight) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分段开始段起重和结束段尾重与重量范围重量值不匹配");
                }
            }
            this.checkContinuityInterval(list);
            for (RuleSubsection ruleSubsection : list) {
                Byte segmentFeeType = ruleSubsection.getSegmentFeeType();
                //0：分段内按单位重量收费，1：分段内按首重+续重收费，2：分段内固定收费
                if (segmentFeeType.intValue() == 0) {
                    validateUtils.validate(ruleSubsection.getYuanPerGram());
                } else if (segmentFeeType.intValue() == 1) {
                    CountryRemoteFeeModel remoteFee = ruleSubsection.getRemoteFee();
                    Integer firstWeight = remoteFee.getFirstWeight();
                    BigDecimal firstWeightFee = remoteFee.getFirstWeightFee();
                    Integer renewWeight = remoteFee.getRenewWeight();
                    BigDecimal renewWeightFee = remoteFee.getRenewWeightFee();
                    if (firstWeight.intValue() == 0 || firstWeightFee.equals(BigDecimal.ZERO) ||
                            renewWeight.intValue() == 0 || renewWeightFee.equals(BigDecimal.ZERO)) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分段内按首重+续重收费时重量和费用不能为零");
                    }
                } else if (segmentFeeType.intValue() == 2) {
                    if (ruleSubsection.getFixedCharge().equals(BigDecimal.ZERO)) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分段内固定收费不能为零");
                    }
                }
                validateUtils.validate(ruleSubsection);
            }
        }
        List<MethodZoneCountryModel> zoneCountryList = zoneModel.getZoneCountryList();
        if (zoneCountryList.stream().map(x -> x.getCountry()).collect(Collectors.toSet()).size() != zoneCountryList.size()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家不能重复");
        }
        Byte isRemoteFee = zoneModel.getIsRemoteFee();
        if (1 == isRemoteFee.intValue()) {
            if (zoneModel.getZoneCountryList().stream().filter(x -> x.getIsRemoteFee().intValue() == 1).count() == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分区偏远地区收费，分区内所有国家都未设置偏远收费规则");
            }
            for (MethodZoneCountryModel zoneCountry : zoneCountryList) {
                if (zoneCountry.getIsRemoteFee().intValue() == 1) {
                    List<CountryRemoteFeeModel> remoteFeeList = zoneCountry.getRemoteList();
                    if (remoteFeeList.stream().map(x -> x.getCity()).collect(Collectors.toSet()).size() != remoteFeeList.size()) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "偏远地区城市不能重复");
                    }
                    if (CollectionUtils.isEmpty(remoteFeeList)) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收取偏远地区邮寄费则对应，偏远地区收费数据对象集合不能为空");
                    }
                    for (CountryRemoteFeeModel remoteFee : remoteFeeList) {
                        validateUtils.validate(remoteFee);
                    }
                }
            }
        } else {
            zoneCountryList.forEach(x -> x.setRemoteList(null));
        }
    }

    private void checkContinuityInterval(List<RuleSubsection> list) throws Exception {
        List<int[]> intervalList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int[] arr = {list.get(i).getBeginWeight(), list.get(i).getEndWeight()};
            intervalList.add(arr);
        }
        CheckContinuityIntervalUtils.checkContinuityInterval(intervalList);
    }

    private void validateLengthWideHighRule(LimitLength limitLength) {
        Integer length = limitLength.getLength();
        Integer wide = limitLength.getWide();
        Integer high = limitLength.getHigh();
        Integer threeLength = limitLength.getThreeLength();
        if (length.intValue() == 0) {
            if (wide.intValue() != 0 || high.intValue() != 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当“长=0”时：“宽、高”不可编辑");
            }
        } else {
            if (wide.intValue() == 0 && high.intValue() != 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当“宽=0”时：“高”不可编辑");
            }
        }
        if (length.compareTo(wide) < 0 || wide.compareTo(high) < 0 || length.compareTo(high) < 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "必须满足：长≥宽≥高");
        }
    }

    @Override
    public MethodZoneFreightModel selectZoneByID(Long zoneId) {
        MethodZoneFreightModel zoneModel = zoneMapper.selectZoneByID(zoneId);
        if (zoneModel == null) {
            return null;
        }
        List<MethodZoneCountryModel> zoneCountryList = zoneModel.getZoneCountryList();
        for (MethodZoneCountryModel country : zoneCountryList) {
            if (country.getIsRemoteFee().intValue() == 1) {
                List<CountryRemoteFeeModel> list = remoteFeeMapper.selectByCountryID(country.getId());
                if (CollectionUtils.isNotEmpty(list)) {
                    country.setRemoteList(list);
                } else {
                    country.setRemoteList(new ArrayList<>());
                }
            }
        }

//        this.jsonObjectOrAarrayConverGeneralProp(zoneModel);
        if (zoneModel.getZoneCountryList().stream().filter(x -> x.getIsRemoteFee().intValue() == 1).count() != 0) {
            zoneModel.setIsRemoteFee((byte) 1);
        } else {
            zoneModel.setIsRemoteFee((byte) 0);
        }
        return zoneModel;
    }

//    /**
//     * 将JavaBean中属性类型为JSONObject和JSONArray的字段转为相应VO/List普通属性
//     *
//     * @param zoneModel
//     */
//    private void jsonObjectOrAarrayConverGeneralProp(MethodZoneFreightModel zoneModel) {
//        JSONArray unsupportCargo = zoneModel.getUnsupportCargo();
//        if (CollectionUtils.isNotEmpty(unsupportCargo)) {
//            zoneModel.setUnsupportCargoList(JSONObject.parseArray(zoneModel.getUnsupportCargo().toJSONString(), LogisticsCargoPropVO.class));
//            zoneModel.setUnsupportCargo(null);
//        }
//        zoneModel.setPromiseDaysVO(JSONObject.parseObject(JSONObject.toJSONString(zoneModel.getPromiseDays()), MaxMin.class));
//        zoneModel.setPromiseDays(null);
//        zoneModel.setWeightRangeVO(JSONObject.parseObject(JSONObject.toJSONString(zoneModel.getWeightRange()), MaxMin.class));
//        zoneModel.setWeightRange(null);
//        zoneModel.setLimitLengthVO(JSONObject.parseObject(JSONObject.toJSONString(zoneModel.getLimitLength()), LimitLengthVO.class));
//        zoneModel.setLimitLength(null);
//        JSONObject countBulkRule = zoneModel.getCountBulkRule();
//        if (countBulkRule != null) {
//            zoneModel.setCountBulkRuleVO(JSONObject.parseObject(JSONObject.toJSONString(countBulkRule), CountBulkRuleVO.class));
//            zoneModel.setCountBulkRule(null);
//        }
//        JSONObject ruleFirstRenew = zoneModel.getRuleFirstRenew();
//        if (ruleFirstRenew != null) {
//            zoneModel.setRuleFirstRenewVO(JSONObject.parseObject(JSONObject.toJSONString(ruleFirstRenew), RuleFirstRenewVO.class));
//            zoneModel.setRuleFirstRenew(null);
//        }
//        JSONArray ruleSubsection = zoneModel.getRuleSubsection();
//        if (ruleSubsection != null) {
//            zoneModel.setRuleSubsectionList(JSONObject.parseArray(ruleSubsection.toJSONString(), RuleSubsection.class));
//            zoneModel.setRuleSubsection(null);
//        }
//    }

    @Override
    public List<MethodZoneCountryModel> selectZoneIDByMethodIDCountry(Long methodId, String[] countryArray) {
        return zoneCountryMapper.selectZoneIDByMethodIDCountry(methodId, countryArray);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteZoneByID(Long zoneId) {
        MethodZoneFreightModel zoneFreight = zoneMapper.selectZoneByID(zoneId);
        zoneMapper.deleteByPrimaryKey(zoneId);
        if (zoneFreight != null) {
            List<MethodZoneCountryModel> countryList = zoneFreight.getZoneCountryList();
            List<Long> countryIDList = countryList.stream().map(x -> x.getId()).collect(Collectors.toList());
            zoneCountryMapper.deleteByZoneId(zoneFreight.getId());
            Set<Long> set = countryList.stream().filter(x -> x.getIsRemoteFee().intValue() == 1).map(a -> a.getId()).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(set)) {
                remoteFeeMapper.deleteBatchByCountryId(countryIDList);
            }
        }
    }

    @Override
    public Page<MethodZoneFreightModel> selectMethodZoneList(Long methodId) {
        List<MethodZoneFreightModel> list = zoneMapper.selectMethodZoneList(methodId);
        for (MethodZoneFreightModel zoneFreight : list) {
//            this.jsonObjectOrAarrayConverGeneralProp(zoneFreight);
            if (zoneFreight.getZoneCountryList().stream().filter(x -> x.getIsRemoteFee().intValue() == 1).count() != 0) {
                zoneFreight.setIsRemoteFee((byte) 1);
            } else {
                zoneFreight.setIsRemoteFee((byte) 0);
            }
        }
        return new Page<>(list);
    }
}
