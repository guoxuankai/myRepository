package com.rondaful.cloud.seller.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;

public class AmazonTemplateValidate {

	/**
	 * 验证属性依赖选性处理
	 * @param parenttemplate
	 * @param childtemplate
	 * @param categoryPropertyJson
	 */
	public void validateAttrRely (String parenttemplate,String childtemplate,String categoryPropertyJson)
	{
		if(StringUtils.isBlank(parenttemplate))
		{
			return;
		}
		
		if("Music".equalsIgnoreCase(parenttemplate) && 
				("MusicClassical".equalsIgnoreCase(childtemplate) || "musicPopular".equalsIgnoreCase(childtemplate)))
		{
			Pattern p = Pattern.compile("\"MediaType\":\"([^</]+)\",");
			Matcher m = p.matcher(categoryPropertyJson);
			
			Pattern p1 = Pattern.compile("\"VinylRecordDetails\":\"([^</]+)\",");
			Matcher m1 = p1.matcher(categoryPropertyJson);
			
			while(m.find())
			{
				String conext = m.group(1);
				if("lp_record".equals(conext.trim()) && !m1.find())
				{
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,String.format("VinylRecordDetails %s", Utils.i18n("不能为空")));
				}
			}
			return;
		}
		
		if("Video".equalsIgnoreCase(parenttemplate) )
		{
			/*Pattern p = Pattern.compile("\"binding\":\"([^</]+)\",");
			Matcher m = p.matcher(categoryPropertyJson);*/
			Matcher m = getMatcher("binding",categoryPropertyJson);
			
			/*Pattern p1 = Pattern.compile("\"dvdRegion\":([^</]+),");
			Matcher m1 = p1.matcher(categoryPropertyJson);*/
			Matcher m1 = getMatcher("dvdRegion",categoryPropertyJson);
			
			while(m.find())
			{
				String conext = m.group(1);
				if("dvd".equals(conext.trim()) && !m1.find())
				{
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,String.format("DVDRegion %s", Utils.i18n("不能为空")));
				}
			}
			return;
		}
	}
	
	private Matcher getMatcher(String attrName,String categoryPropertyJson)
	{
		Pattern p = Pattern.compile("\""+attrName+"\":\"([^</]+)\",");
		return p.matcher(categoryPropertyJson);
	}
	
	public static void main(String[] args) {
		String sou = "{\"parentage\":\"child\",\"classificationData\":{},\"productType\":{\"videoDVD\":{\"mpaaRating\":\"nr\",\"dvdRegion\":[\"3\"],\"binding\":\"dvd\",\"battery\":{\"batterySubgroup\":[{}]}}},\"variationData\":{}}";
		AmazonTemplateValidate val = new AmazonTemplateValidate();
		val.validateAttrRely("Video", "VideoVHS", sou);
	}
}
