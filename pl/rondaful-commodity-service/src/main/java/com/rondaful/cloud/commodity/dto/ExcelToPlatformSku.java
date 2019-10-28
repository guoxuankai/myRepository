package com.rondaful.cloud.commodity.dto;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;

import com.rondaful.cloud.commodity.entity.SellerSkuMap;
import com.rondaful.cloud.common.utils.ExcelUtil;

public class ExcelToPlatformSku implements ExcelUtil.ExcelMapRow<SellerSkuMap>{

	@Override
	public SellerSkuMap mapRow(XSSFRow row) {
		try {
            SellerSkuMap map = new SellerSkuMap();
            row.getCell(0).setCellType(CellType.STRING);
            map.setPlatform(row.getCell(0).getStringCellValue());
            
            row.getCell(1).setCellType(CellType.STRING);
            map.setSellerSelfAccount(row.getCell(1).getStringCellValue());//店铺名称
            
            row.getCell(2).setCellType(CellType.STRING);
            map.setPlatformSku(row.getCell(2).getStringCellValue());
            
            row.getCell(3).setCellType(CellType.STRING);
            map.setSkuGroup(row.getCell(3).getStringCellValue().replaceAll("：", ":"));
            
            //默认启用
            map.setStatus(1);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

}
