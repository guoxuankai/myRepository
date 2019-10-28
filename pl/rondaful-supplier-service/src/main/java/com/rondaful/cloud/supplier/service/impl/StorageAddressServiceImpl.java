package com.rondaful.cloud.supplier.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.storage.StorageAddress;
import com.rondaful.cloud.supplier.mapper.StorageAddressMapper;
import com.rondaful.cloud.supplier.model.dto.inventory.AddressDTO;
import com.rondaful.cloud.supplier.service.IStorageAddressService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/7
 * @Description:
 */
@Service("storageAddressServiceImpl")
public class StorageAddressServiceImpl implements IStorageAddressService {

    @Autowired
    private StorageAddressMapper addressMapper;

    /**
     * 添加联系地址
     *
     * @param dto
     * @return
     */
    @Override
    public Integer add(AddressDTO dto) {
        StorageAddress address=new StorageAddress();
        BeanUtils.copyProperties(dto,address);
        return this.addressMapper.insert(address);
    }

    /**
     * 修改联系地址
     *
     * @param dto
     * @return
     */
    @Override
    public Integer update(AddressDTO dto) {
        StorageAddress exits=this.addressMapper.selectByPrimaryKey(dto.getId().longValue());
        if (exits==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改对象不存在");
        }
        StorageAddress address=new StorageAddress();
        BeanUtils.copyProperties(dto,address);
        return this.addressMapper.updateByPrimaryKey(address);
    }

    /**
     * 删除联系地址
     *
     * @param id
     * @return
     */
    @Override
    public Integer del(Integer id) {
        StorageAddress exits=this.addressMapper.selectByPrimaryKey(id.longValue());
        if (exits==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改对象不存在");
        }
        return this.addressMapper.deleteByPrimaryKey(id.longValue());
    }

    /**
     * 获取所有地址
     *
     * @param supplierId
     * @return
     */
    @Override
    public PageDTO<AddressDTO> getsBySupplierId(Integer supplierId, String phone, Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage,pageSize);
        List<StorageAddress> list=this.addressMapper.getsBySupplierId(supplierId,phone);
        PageInfo<StorageAddress> pageInfo=new PageInfo<>(list);
        PageDTO<AddressDTO> result=new PageDTO<>((int)pageInfo.getTotal(),currentPage);
        if (CollectionUtils.isEmpty(pageInfo.getList())){
            return result;
        }
        List<AddressDTO> datas=new ArrayList<>();
        for (StorageAddress address:pageInfo.getList()) {
            AddressDTO dto=new AddressDTO();
            BeanUtils.copyProperties(address,dto);
            datas.add(dto);
        }
        result.setList(datas);
        return result;
    }

}
