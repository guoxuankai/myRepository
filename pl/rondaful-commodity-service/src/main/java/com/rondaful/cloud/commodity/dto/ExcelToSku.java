package com.rondaful.cloud.commodity.dto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;

import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.CommodityDetails;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.common.utils.ExcelUtil;

public class ExcelToSku implements ExcelUtil.ExcelMapRow<CommodityBase>{

	@Override
	public CommodityBase mapRow(XSSFRow row) {
		try {
        	CommodityBase base = new CommodityBase();
        	CommodityDetails commodityDetails=new CommodityDetails();
        	List<CommoditySpec> commoditySpecList=new ArrayList<CommoditySpec>();
        	CommoditySpec spec=new CommoditySpec();
        	
        	if (row.getCell(0) != null) {
        		row.getCell(0).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(0).getStringCellValue())) {
        			base.setSupplierSpu(row.getCell(0).getStringCellValue());
				}
        	}
        	
        	if (row.getCell(1) != null) {
        		row.getCell(1).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(1).getStringCellValue())) {
        			spec.setSupplierSku(row.getCell(1).getStringCellValue());
        		}
			}
        	
        	if (row.getCell(2) != null) {
           	 	row.getCell(2).setCellType(CellType.STRING);
               	if (StringUtils.isNotBlank(row.getCell(2).getStringCellValue())) {
               		base.setBrandName(row.getCell(2).getStringCellValue());
               	}
        	}
        	
        	if (row.getCell(3) != null) {
            	row.getCell(3).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(3).getStringCellValue())) {
            		base.setCategoryName1(row.getCell(3).getStringCellValue());
            	}
            }

            if (row.getCell(4) != null) {
            	row.getCell(4).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(4).getStringCellValue())) {
            		base.setCategoryName2(row.getCell(4).getStringCellValue());
            	}
            }

            if (row.getCell(5) != null) {
            	row.getCell(5).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(5).getStringCellValue())) {
            		base.setCategoryName3(row.getCell(5).getStringCellValue());
            	}
            }
        	
            if (row.getCell(6) != null) {
            	row.getCell(6).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(6).getStringCellValue())) {
            		base.setDefaultRepository(row.getCell(6).getStringCellValue());
            	}
            }
        	
            if (row.getCell(7) != null) {
            	row.getCell(7).setCellType(CellType.BOOLEAN);
                base.setIsPrivateModel(row.getCell(7).getBooleanCellValue());
            }
            
            if (row.getCell(8) != null) {
            	row.getCell(8).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(8).getStringCellValue())) {
            		base.setProducer(row.getCell(8).getStringCellValue());
            	}
            }
            
            if (row.getCell(9) != null) {
            	row.getCell(9).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(9).getStringCellValue())) {
            		base.setProductLogisticsAttributes(row.getCell(9).getStringCellValue());
            	}
            }
            
            if (row.getCell(10) != null) {
            	row.getCell(10).setCellType(CellType.NUMERIC);
            	if (HSSFDateUtil.isCellDateFormatted(row.getCell(10))) {
            		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            		if (row.getCell(10).getNumericCellValue()>0) {
            			base.setProductMarketTime(sdf.format(HSSFDateUtil.getJavaDate(row.getCell(10).getNumericCellValue())).toString());
					}
				}
            }

            StringBuilder platformSb=new StringBuilder();
            if (row.getCell(11) != null) {
            	row.getCell(11).setCellType(CellType.BOOLEAN);
                if (row.getCell(11).getBooleanCellValue()) {
            	   platformSb.append("eBay").append(",");
                }
            }
            if (row.getCell(12) != null) {
            	row.getCell(12).setCellType(CellType.BOOLEAN);
                if (row.getCell(12).getBooleanCellValue()) {
            	   platformSb.append("Amazon").append(",");
                }
            }
            if (row.getCell(13) != null) {
            	row.getCell(13).setCellType(CellType.BOOLEAN);
                if (row.getCell(13).getBooleanCellValue()) {
            	   platformSb.append("Wish").append(",");
                }
            }
            if (row.getCell(14) != null) {
            	row.getCell(14).setCellType(CellType.BOOLEAN);
                if (row.getCell(14).getBooleanCellValue()) {
            	   platformSb.append("AliExpress").append(",");
                }
            }
            if(platformSb.length()>0) {
            	base.setVendibilityPlatform(platformSb.toString().substring(0, platformSb.toString().length()-1));
            }
            
            if (row.getCell(15) != null) {
            	row.getCell(15).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(15).getStringCellValue())) {
            		commodityDetails.setMasterPicture(row.getCell(15).getStringCellValue());
            	}
            }

            StringBuilder spuAdditionalPicSb=new StringBuilder();
            if (row.getCell(16) != null) {
            	row.getCell(16).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(16).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(16).getStringCellValue()).append("|");
				}
            }
            if (row.getCell(17) != null) {
            	row.getCell(17).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(17).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(17).getStringCellValue()).append("|");
				}
            }
            if (row.getCell(18) != null) {
            	row.getCell(18).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(18).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(18).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(19) != null) {
            	row.getCell(19).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(19).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(19).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(20) != null) {
            	row.getCell(20).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(20).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(20).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(21) != null) {
            	row.getCell(21).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(21).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(21).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(22) != null) {
            	row.getCell(22).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(22).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(22).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(23) != null) {
            	row.getCell(23).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(23).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(23).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(24) != null) {
            	row.getCell(24).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(24).getStringCellValue())) {
            		spuAdditionalPicSb.append(row.getCell(24).getStringCellValue()).append("|");
            	}
            }
            if(spuAdditionalPicSb.length()>0) {
            	commodityDetails.setAdditionalPicture(spuAdditionalPicSb.toString().substring(0, spuAdditionalPicSb.toString().length()-1));
            }
            
            if (row.getCell(25) != null) {
            	row.getCell(25).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(25).getStringCellValue())) {
            		spec.setMasterPicture(row.getCell(25).getStringCellValue());
            	}
            }
            
            StringBuilder skuAdditionalPicSb=new StringBuilder();
            if (row.getCell(26) != null) {
            	row.getCell(26).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(26).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(26).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(27) != null) {
            	row.getCell(27).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(27).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(27).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(28) != null) {
            	row.getCell(28).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(28).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(28).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(29) != null) {
            	row.getCell(29).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(29).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(29).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(30) != null) {
            	row.getCell(30).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(30).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(30).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(31) != null) {
            	row.getCell(31).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(31).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(31).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(32) != null) {
            	row.getCell(32).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(32).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(32).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(33) != null) {
            	row.getCell(33).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(33).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(33).getStringCellValue()).append("|");
            	}
            }
            if (row.getCell(34) != null) {
            	row.getCell(34).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(34).getStringCellValue())) {
            		skuAdditionalPicSb.append(row.getCell(34).getStringCellValue()).append("|");
            	}
            }
            if(skuAdditionalPicSb.length()>0) {
            	spec.setAdditionalPicture(skuAdditionalPicSb.toString().substring(0, skuAdditionalPicSb.toString().length()-1));
            }
            
            StringBuilder searchKeyWordSb=new StringBuilder();
            if (row.getCell(35) != null) {
            	row.getCell(35).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(35).getStringCellValue())) {
            		searchKeyWordSb.append("EN===").append(row.getCell(35).getStringCellValue());
            	}
            }
            if (row.getCell(36) != null) {
            	row.getCell(36).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(36).getStringCellValue())) {
            		searchKeyWordSb.append(":::").append("CN===").append(row.getCell(36).getStringCellValue());
            	}
            }
            if (row.getCell(37) != null) {
            	row.getCell(37).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(37).getStringCellValue())) {
            		searchKeyWordSb.append(":::").append("FR===").append(row.getCell(37).getStringCellValue());
            	}
            }
            if (row.getCell(38) != null) {
            	row.getCell(38).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(38).getStringCellValue())) {
            		searchKeyWordSb.append(":::").append("DE===").append(row.getCell(38).getStringCellValue());
            	}
            }
            if (row.getCell(39) != null) {
            	row.getCell(39).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(39).getStringCellValue())) {
            		searchKeyWordSb.append(":::").append("IT===").append(row.getCell(39).getStringCellValue());
            	}
            }
            if (StringUtils.isNotBlank(searchKeyWordSb.toString())) {
            	commodityDetails.setSearchKeywords(searchKeyWordSb.toString());
			}
            
            StringBuilder commodityDescSb=new StringBuilder();
            if (row.getCell(40) != null) {
            	row.getCell(40).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(40).getStringCellValue())) {
            		commodityDescSb.append("EN===").append(row.getCell(40).getStringCellValue());
            	}
            }
            if (row.getCell(41) != null) {
            	row.getCell(41).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(41).getStringCellValue())) {
            		commodityDescSb.append(":::").append("CN===").append(row.getCell(41).getStringCellValue());
            	}
            }
            if (row.getCell(42) != null) {
            	row.getCell(42).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(42).getStringCellValue())) {
            		commodityDescSb.append(":::").append("FR===").append(row.getCell(42).getStringCellValue());
            	}
            }
            if (row.getCell(43) != null) {
            	row.getCell(43).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(43).getStringCellValue())) {
            		commodityDescSb.append(":::").append("DE===").append(row.getCell(43).getStringCellValue());
            	}
            }
            if (row.getCell(44) != null) {
            	row.getCell(44).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(44).getStringCellValue())) {
            		commodityDescSb.append(":::").append("IT===").append(row.getCell(44).getStringCellValue());
            	}
            }
            if (StringUtils.isNotBlank(commodityDescSb.toString())) {
            	commodityDetails.setCommodityDesc(commodityDescSb.toString());
			}
            
            StringBuilder packingListSb=new StringBuilder();
            if (row.getCell(45) != null) {
            	row.getCell(45).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(45).getStringCellValue())) {
            		packingListSb.append("EN===").append(row.getCell(45).getStringCellValue());
            	}
            }
            if (row.getCell(46) != null) {
            	row.getCell(46).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(46).getStringCellValue())) {
            		packingListSb.append(":::").append("CN===").append(row.getCell(46).getStringCellValue());
            	}
            }
            if (row.getCell(47) != null) {
            	row.getCell(47).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(47).getStringCellValue())) {
            		packingListSb.append(":::").append("FR===").append(row.getCell(47).getStringCellValue());
            	}
            }
            if (row.getCell(48) != null) {
            	row.getCell(48).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(48).getStringCellValue())) {
            		packingListSb.append(":::").append("DE===").append(row.getCell(48).getStringCellValue());
            	}
            }
            if (row.getCell(49) != null) {
            	row.getCell(49).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(49).getStringCellValue())) {
            		packingListSb.append(":::").append("IT===").append(row.getCell(49).getStringCellValue());
            	}
            }
            if (StringUtils.isNotBlank(packingListSb.toString())) {
            	commodityDetails.setPackingList(packingListSb.toString());
            }
            
            StringBuilder strength1Sb=new StringBuilder();
            if (row.getCell(50) != null) {
            	row.getCell(50).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(50).getStringCellValue())) {
            		strength1Sb.append("EN===").append(row.getCell(50).getStringCellValue());
            	}
            }
            if (row.getCell(51) != null) {
            	row.getCell(51).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(51).getStringCellValue())) {
            		strength1Sb.append(":::").append("CN===").append(row.getCell(51).getStringCellValue());
            	}
            }
            if (row.getCell(52) != null) {
            	row.getCell(52).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(52).getStringCellValue())) {
            		strength1Sb.append(":::").append("FR===").append(row.getCell(52).getStringCellValue());
            	}
            }
            if (row.getCell(53) != null) {
            	row.getCell(53).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(53).getStringCellValue())) {
            		strength1Sb.append(":::").append("DE===").append(row.getCell(53).getStringCellValue());
            	}
            }
            if (row.getCell(54) != null) {
            	row.getCell(54).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(54).getStringCellValue())) {
            		strength1Sb.append(":::").append("IT===").append(row.getCell(54).getStringCellValue());
            	}
            }
            if (StringUtils.isNotBlank(strength1Sb.toString())) {
            	commodityDetails.setStrength1(strength1Sb.toString());
            }
            
            if (row.getCell(55) != null) {
       		 	row.getCell(55).setCellType(CellType.NUMERIC);
                spec.setCommodityPrice(new BigDecimal(row.getCell(55).getNumericCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
            }
            
            StringBuilder specSb=new StringBuilder();
            if (row.getCell(56) != null && row.getCell(57) != null) {
            	row.getCell(56).setCellType(CellType.STRING);
            	row.getCell(57).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(56).getStringCellValue()) && StringUtils.isNotBlank(row.getCell(57).getStringCellValue())) {
            		specSb.append(row.getCell(56).getStringCellValue().replace(":", "").replace("|", ""))
            			.append(":")
            			.append(row.getCell(57).getStringCellValue().replace(":", "").replace("|", ""))
            			.append("|");
				}
			}
            if (row.getCell(58) != null && row.getCell(59) != null) {
            	row.getCell(58).setCellType(CellType.STRING);
            	row.getCell(59).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(58).getStringCellValue()) && StringUtils.isNotBlank(row.getCell(59).getStringCellValue())) {
            		specSb.append(row.getCell(58).getStringCellValue().replace(":", "").replace("|", ""))
            			.append(":")
            			.append(row.getCell(59).getStringCellValue().replace(":", "").replace("|", ""))
            			.append("|");
            	}
			}
            if (row.getCell(60) != null && row.getCell(61) != null) {
            	row.getCell(60).setCellType(CellType.STRING);
            	row.getCell(61).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(60).getStringCellValue()) && StringUtils.isNotBlank(row.getCell(61).getStringCellValue())) {
            		specSb.append(row.getCell(60).getStringCellValue().replace(":", "").replace("|", ""))
            			.append(":")
            			.append(row.getCell(61).getStringCellValue().replace(":", "").replace("|", ""))
            			.append("|");
            	}
			}
            if (row.getCell(62) != null && row.getCell(63) != null) {
            	row.getCell(62).setCellType(CellType.STRING);
            	row.getCell(63).setCellType(CellType.STRING);
            	if (StringUtils.isNotBlank(row.getCell(62).getStringCellValue()) && StringUtils.isNotBlank(row.getCell(63).getStringCellValue())) {
            		specSb.append(row.getCell(62).getStringCellValue().replace(":", "").replace("|", ""))
            			.append(":")
            			.append(row.getCell(63).getStringCellValue().replace(":", "").replace("|", ""))
            			.append("|");
            	}
			}
            if(specSb.length()>0) {
            	spec.setCommoditySpec(specSb.toString().substring(0, specSb.toString().length()-1));
            }
            
            if (row.getCell(64) != null) {
        		row.getCell(64).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(64).getStringCellValue())) {
        			spec.setCommodityNameCn(row.getCell(64).getStringCellValue());
        		}
        	}
            
        	if (row.getCell(65) != null) {
        		row.getCell(65).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(65).getStringCellValue())) {
        			spec.setCommodityNameEn(row.getCell(65).getStringCellValue());
        		}
        	}
            
        	if (row.getCell(66) != null) {
        		row.getCell(66).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(66).getStringCellValue())) {
        			spec.setCommodityHeight(new BigDecimal(row.getCell(66).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
				}
        	}
            
        	if (row.getCell(67) != null) {
        		row.getCell(67).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(67).getStringCellValue())) {
        			spec.setCommodityLength(new BigDecimal(row.getCell(67).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
        		}
        	}
            
        	if (row.getCell(68) != null) {
        		row.getCell(68).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(68).getStringCellValue())) {
        			spec.setCommodityWidth(new BigDecimal(row.getCell(68).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
        		}
        	}
            
        	if (row.getCell(69) != null) {
        		row.getCell(69).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(69).getStringCellValue())) {
        			spec.setCommodityWeight(new BigDecimal(row.getCell(69).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
        		}
        	}
            
        	if (row.getCell(70) != null) {
        		row.getCell(70).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(70).getStringCellValue())) {
        			spec.setPackingHeight(new BigDecimal(row.getCell(70).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
        		}
        	}

        	if (row.getCell(71) != null) {
        		row.getCell(71).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(71).getStringCellValue())) {
        			spec.setPackingLength(new BigDecimal(row.getCell(71).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
        		}
        	}

        	if (row.getCell(72) != null) {
        		row.getCell(72).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(72).getStringCellValue())) {
        			spec.setPackingWidth(new BigDecimal(row.getCell(72).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
        		}
        	}
            
        	if (row.getCell(73) != null) {
        		row.getCell(73).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(73).getStringCellValue())) {
        			spec.setPackingWeight(new BigDecimal(row.getCell(73).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
				}
        	}
            
        	if (row.getCell(74) != null) {
        		row.getCell(74).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(74).getStringCellValue())) {
        			spec.setCustomsNameCn(row.getCell(74).getStringCellValue());
        		}
        	}

        	if (row.getCell(75) != null) {
        		row.getCell(75).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(75).getStringCellValue())) {
        			spec.setCustomsNameEn(row.getCell(75).getStringCellValue());
        		}
        	}

        	if (row.getCell(76) != null) {
        		row.getCell(76).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(76).getStringCellValue())) {
        			spec.setCustomsPrice(new BigDecimal(row.getCell(76).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
        		}
        	}
            
        	if (row.getCell(77) != null) {
        		row.getCell(77).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(77).getStringCellValue())) {
        			spec.setCustomsWeight(new BigDecimal(row.getCell(77).getStringCellValue()).setScale(2, BigDecimal.ROUND_DOWN));
        		}
        	}

        	if (row.getCell(78) != null) {
        		row.getCell(78).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(78).getStringCellValue())) {
        			spec.setCustomsCode(row.getCell(78).getStringCellValue());
        		}
        	}
        	if (row.getCell(79) != null) {
        		row.getCell(79).setCellType(CellType.BOOLEAN);
        		int freeFreight=row.getCell(79).getBooleanCellValue()==true?1:0;
        		base.setFreeFreight(freeFreight);
        	}
        	
        	if (row.getCell(80) != null) {
        		row.getCell(80).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(80).getStringCellValue())) {
        			String[] sellerIdArr=row.getCell(80).getStringCellValue().split(",");
        			if (sellerIdArr != null && sellerIdArr.length>0) {
						base.setBelongSeller(Arrays.asList(sellerIdArr));
					}
        		}
        	}
        	if (row.getCell(81) != null) {
        		row.getCell(81).setCellType(CellType.STRING);
        		if (StringUtils.isNotBlank(row.getCell(81).getStringCellValue())) {
        			spec.setWarehousePriceGroup(row.getCell(81).getStringCellValue());
        		}
        	}
        	
            commoditySpecList.add(spec);
            
            base.setCommodityDetails(commodityDetails);
            base.setCommoditySpecList(commoditySpecList);
            return base;
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
	}

}
