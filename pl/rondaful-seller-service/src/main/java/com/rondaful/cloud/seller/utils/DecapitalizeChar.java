package com.rondaful.cloud.seller.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Set;

/**
 *	字符转换
 * @author ouxiangfeng
 *
 */
public class DecapitalizeChar {
	
	public static final String CLASS_PATH_URI_FIX = "com.rondaful.cloud.seller.generated.";

	public static JSONObject allTem = null;

	public static Set<String> keys = null;


	
	/**
	 * 仅首字母为大写
	 * @param name
	 * @return
	 */
	 public static String decapitalizeUpperCaseFirst(String name)
	 {
		 char chars[] = name.toCharArray();
		 chars[0] =  Character.toUpperCase(chars[0]);
		 if(chars.length == 2) //如果只有二个字母的，需要二个都转成大写
         {
            chars[1] = Character.toUpperCase(chars[1]);
         }
		 if(chars.length >= 3 && Character.isUpperCase(chars[2])) //如果第三个是大写，则前面所有的都为大写
        {
            chars[0] = Character.toUpperCase(chars[0]);
            chars[1] = Character.toUpperCase(chars[1]);
        }
		 return new String(chars);
	 }





	/**
	 *       将首字母改变为大写
	 * @param name
	 * @return
	 */
    public static String decapitalizeUpperCase(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        if(chars.length == 2) //如果只有二个字母的，需要二个都转成大写
        {
            chars[0] = Character.toUpperCase(chars[0]);
            chars[1] = Character.toUpperCase(chars[1]);
            return new String(chars);
        }
        if(chars.length == 3) //如果只有二个字母的，需要二个都转成大写
        {
            chars[0] = Character.toUpperCase(chars[0]);
            chars[1] = Character.toUpperCase(chars[1]);
            chars[2] = Character.toUpperCase(chars[2]);
            return new String(chars);
        }
        if(chars.length >= 3 && Character.isUpperCase(chars[2])) //如果第三个是大写，则前面所有的都为大写
        {
        	
            chars[0] = Character.toUpperCase(chars[0]);
            chars[1] = Character.toUpperCase(chars[1]);
            return new String(chars);
        }
        
        if (name.length() > 1 && /*Character.isLowerCase(name.charAt(1)) &&*/
                Character.isLowerCase(name.charAt(0))) {
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
        return name;
    }
    
    /**
	 *       将首字母改变为小写
	 * @param name
	 * @return
	 */
    public static String decapitalizeLowerCase(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && /*Character.isUpperCase(name.charAt(1)) &&*/
                Character.isUpperCase(name.charAt(0))) {
        	char chars[] = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
        return name;
    }
    
    /**
     * @param name
     * @return
     */
    public static String compile(String name)
    {
    	return CLASS_PATH_URI_FIX + name;
    }
    
    /**
     * 	大写字母加下划线转大写 如：
     * UserNamePass -> USER_NAME_PASS
     * 
     * @param value
     * @return
     */
	public static String toUplowerStr(String value)
	{

		
		if(value.indexOf("-") != -1)
		{
			// 原"-"换为空字符串
			value = value.replaceAll("-", "_");
			//return value.toUpperCase();
		}

		if(value.indexOf(" ") != -1)
        {
            return value.replaceAll(" ","_").toUpperCase();
        }

		//value = value.replaceAll("-", "_");
		//如果第一位是数字，则加下画线，如ProcessorSeriesTypeValues下的值
		if(NumberUtils.isNumber(String.valueOf(value.charAt(0))))
		{
			value = "_" + value;
		}
		char chars [] = value.toCharArray();	
		StringBuilder builder = new StringBuilder();
		if(value.equals(value.toUpperCase()))
		{
			return value;
		}
		
		for(int i=0 ; i < chars.length ;i++)
		{
			// 如果前一个与当前的字每都为大写，则当前的字母不需要处理
			if(i > 0 && Character.isUpperCase(chars[i-1]) && Character.isUpperCase(chars[i]))
			{
				builder.append(chars[i]);
				continue;
			}
			if(Character.isUpperCase(chars[i]) && i >0)
			{
				if(chars[i-1] == '_')
				{
					builder.append(chars[i]);
				}else
				{
					builder.append("_").append(chars[i]);
				}
			}else
			{
				builder.append(chars[i]);
			}
			
		}
		return builder.toString().toUpperCase();
	}
	
    public static void main(String[] args) {
    	//System.out.println(DecapitalizeChar.toUplowerStr("5x86"));
		JSONObject object = JSONObject.parseObject(tem);

		String largeappliances = DecapitalizeChar.sameParent("largeappliances");
		String refrigerationappliance = DecapitalizeChar.sameChild(largeappliances, "refrigerationappliance");


		System.out.println(DecapitalizeChar.decapitalizeLowerCase("CupSize"));
	}



	public static String tem = "{\n" +
			"    \"toysBaby\": [\n" +
			"      \"ToysAndGames\",\n" +
			"      \"BabyProducts\"\n" +
			"    ],\n" +
			"    \"labSupplies\": [\n" +
			"      \"labSupply\",\n" +
			"      \"safetySupply\"\n" +
			"    ],\n" +
			"    \"beauty\": [\n" +
			"      \"beautyMisc\",\n" +
			"      \"skinCareProduct\",\n" +
			"      \"hairCareProduct\",\n" +
			"      \"bodyCareProduct\",\n" +
			"      \"makeUp\",\n" +
			"      \"fragrance\",\n" +
			"      \"hairRemovalAndShavingProduct\"\n" +
			"    ],\n" +
			"    \"gourmet\": [\n" +
			"      \"gourmetMisc\"\n" +
			"    ],\n" +
			"    \"collectibles\": [\n" +
			"      \"advertisementCollectibles\",\n" +
			"      \"historicalCollectibles\"\n" +
			"    ],\n" +
			"    \"sportsMemorabilia\": [\n" +
			"      \"SportsMemorabilia\",\n" +
			"      \"TradingCardsCardsSets\",\n" +
			"      \"TradingCardsGradedCardsInserts\",\n" +
			"      \"TradingCardsUngradedInserts\",\n" +
			"      \"TradingCardsFactorySealed\",\n" +
			"      \"TradingCardsMiscTradingCards\"\n" +
			"    ],\n" +
			"    \"office\": [\n" +
			"      \"artSupplies\",\n" +
			"      \"educationalSupplies\",\n" +
			"      \"officeProducts\",\n" +
			"      \"paperProducts\",\n" +
			"      \"writingInstruments\",\n" +
			"      \"barCode\",\n" +
			"      \"calculator\",\n" +
			"      \"inkToner\",\n" +
			"      \"multifunctionDevice\",\n" +
			"      \"officeElectronics\",\n" +
			"      \"officePhone\",\n" +
			"      \"officePrinter\",\n" +
			"      \"officeScanner\",\n" +
			"      \"voiceRecorder\",\n" +
			"      \"printerConsumable\"\n" +
			"    ],\n" +
			"    \"video\": [\n" +
			"      \"videoDVD\",\n" +
			"      \"videoVHS\"\n" +
			"    ],\n" +
			"    \"petSupplies\": [\n" +
			"      \"petSuppliesMisc\"\n" +
			"    ],\n" +
			"    \"autoAccessory\": [\n" +
			"      \"autoAccessoryMisc\",\n" +
			"      \"autoPart\",\n" +
			"      \"powersportsPart\",\n" +
			"      \"powersportsVehicle\",\n" +
			"      \"protectiveGear\",\n" +
			"      \"helmet\",\n" +
			"      \"ridingApparel\",\n" +
			"      \"tire\",\n" +
			"      \"rims\",\n" +
			"      \"tireAndWheel\",\n" +
			"      \"vehicle\",\n" +
			"      \"motorcyclepart\",\n" +
			"      \"motorcycleaccessory\",\n" +
			"      \"ridinggloves\",\n" +
			"      \"ridingboots\",\n" +
			"      \"autooil\",\n" +
			"      \"autobattery\",\n" +
			"      \"autochemical\",\n" +
			"      \"cleaningOrRepairKit\"\n" +
			"    ],\n" +
			"    \"music\": [\n" +
			"      \"musicPopular\",\n" +
			"      \"musicClassical\"\n" +
			"    ],\n" +
			"    \"luggage\": [\n" +
			"      \"Luggage\"\n" +
			"    ],\n" +
			"    \"furniture\": [\n" +
			"      \"Furniture\"\n" +
			"    ],\n" +
			"    \"rawMaterials\": [\n" +
			"      \"CeramicBalls\",\n" +
			"      \"CeramicTubing\",\n" +
			"      \"Ceramics\",\n" +
			"      \"MetalBalls\",\n" +
			"      \"MetalMesh\",\n" +
			"      \"MetalTubing\",\n" +
			"      \"Metals\",\n" +
			"      \"PlasticBalls\",\n" +
			"      \"PlasticMesh\",\n" +
			"      \"PlasticTubing\",\n" +
			"      \"Plastics\",\n" +
			"      \"RawMaterials\",\n" +
			"      \"Wire\"\n" +
			"    ],\n" +
			"    \"largeAppliances\": [\n" +
			"      \"airConditioner\",\n" +
			"      \"applianceAccessory\",\n" +
			"      \"cookingOven\",\n" +
			"      \"cooktop\",\n" +
			"      \"dishwasher\",\n" +
			"      \"laundryAppliance\",\n" +
			"      \"microwaveOven\",\n" +
			"      \"range\",\n" +
			"      \"refrigerationAppliance\",\n" +
			"      \"trashCompactor\",\n" +
			"      \"ventHood\"\n" +
			"    ],\n" +
			"    \"softwareVideoGames\": [\n" +
			"      \"software\",\n" +
			"      \"handheldSoftwareDownloads\",\n" +
			"      \"softwareGames\",\n" +
			"      \"videoGames\",\n" +
			"      \"videoGamesAccessories\",\n" +
			"      \"videoGamesHardware\"\n" +
			"    ],\n" +
			"    \"arts\": [\n" +
			"      \"fineArt\",\n" +
			"      \"fineArtEditioned\"\n" +
			"    ],\n" +
			"    \"computers\": [\n" +
			"      \"carryingCaseOrBag\",\n" +
			"      \"computerAddOn\",\n" +
			"      \"computerComponent\",\n" +
			"      \"computerCoolingDevice\",\n" +
			"      \"computerDriveOrStorage\",\n" +
			"      \"computerInputDevice\",\n" +
			"      \"computerProcessor\",\n" +
			"      \"computerSpeaker\",\n" +
			"      \"computer\",\n" +
			"      \"flashMemory\",\n" +
			"      \"inkOrToner\",\n" +
			"      \"keyboards\",\n" +
			"      \"memoryReader\",\n" +
			"      \"monitor\",\n" +
			"      \"motherboard\",\n" +
			"      \"networkingDevice\",\n" +
			"      \"notebookComputer\",\n" +
			"      \"personalComputer\",\n" +
			"      \"printer\",\n" +
			"      \"ramMemory\",\n" +
			"      \"scanner\",\n" +
			"      \"soundCard\",\n" +
			"      \"systemCabinet\",\n" +
			"      \"systemPowerDevice\",\n" +
			"      \"tabletComputer\",\n" +
			"      \"videoCard\",\n" +
			"      \"videoProjector\",\n" +
			"      \"webcam\"\n" +
			"    ],\n" +
			"    \"jewelry\": [\n" +
			"      \"watch\",\n" +
			"      \"fashionNecklaceBraceletAnklet\",\n" +
			"      \"fashionRing\",\n" +
			"      \"fashionEarring\",\n" +
			"      \"fashionOther\",\n" +
			"      \"fineNecklaceBraceletAnklet\",\n" +
			"      \"fineRing\",\n" +
			"      \"fineEarring\",\n" +
			"      \"fineOther\"\n" +
			"    ],\n" +
			"    \"ce\": [\n" +
			"      \"antenna\",\n" +
			"      \"audioVideoAccessory\",\n" +
			"      \"avFurniture\",\n" +
			"      \"barCodeReader\",\n" +
			"      \"ceBinocular\",\n" +
			"      \"ceCamcorder\",\n" +
			"      \"cameraBagsAndCases\",\n" +
			"      \"ceBattery\",\n" +
			"      \"ceBlankMedia\",\n" +
			"      \"cableOrAdapter\",\n" +
			"      \"ceCameraFlash\",\n" +
			"      \"cameraLenses\",\n" +
			"      \"cameraOtherAccessories\",\n" +
			"      \"cameraPowerSupply\",\n" +
			"      \"carAlarm\",\n" +
			"      \"carAudioOrTheater\",\n" +
			"      \"carElectronics\",\n" +
			"      \"consumerElectronics\",\n" +
			"      \"ceDigitalCamera\",\n" +
			"      \"digitalPictureFrame\",\n" +
			"      \"digitalVideoRecorder\",\n" +
			"      \"dvdPlayerOrRecorder\",\n" +
			"      \"ceFilmCamera\",\n" +
			"      \"gpsOrNavigationAccessory\",\n" +
			"      \"gpsOrNavigationSystem\",\n" +
			"      \"handheldOrPDA\",\n" +
			"      \"headphones\",\n" +
			"      \"homeTheaterSystemOrHTIB\",\n" +
			"      \"kindleAccessories\",\n" +
			"      \"kindleEReaderAccessories\",\n" +
			"      \"kindleFireAccessories\",\n" +
			"      \"mediaPlayer\",\n" +
			"      \"mediaPlayerOrEReaderAccessory\",\n" +
			"      \"mediaStorage\",\n" +
			"      \"miscAudioComponents\",\n" +
			"      \"pc\",\n" +
			"      \"pda\",\n" +
			"      \"phone\",\n" +
			"      \"phoneAccessory\",\n" +
			"      \"photographicStudioItems\",\n" +
			"      \"portableAudio\",\n" +
			"      \"portableAvDevice\",\n" +
			"      \"powerSuppliesOrProtection\",\n" +
			"      \"radarDetector\",\n" +
			"      \"radioOrClockRadio\",\n" +
			"      \"receiverOrAmplifier\",\n" +
			"      \"remoteControl\",\n" +
			"      \"speakers\",\n" +
			"      \"stereoShelfSystem\",\n" +
			"      \"ceTelescope\",\n" +
			"      \"television\",\n" +
			"      \"tuner\",\n" +
			"      \"tvCombos\",\n" +
			"      \"twoWayRadio\",\n" +
			"      \"vcr\",\n" +
			"      \"ceVideoProjector\",\n" +
			"      \"videoProjectorsAndAccessories\",\n" +
			"      \"networkAdapter\"\n" +
			"    ],\n" +
			"    \"powerTransmission\": [\n" +
			"      \"BearingsAndBushings\",\n" +
			"      \"Belts\",\n" +
			"      \"CompressionSprings\",\n" +
			"      \"ExtensionSprings\",\n" +
			"      \"FlexibleCouplings\",\n" +
			"      \"Gears\",\n" +
			"      \"RigidCouplings\",\n" +
			"      \"ShaftCollar\",\n" +
			"      \"TorsionSprings\",\n" +
			"      \"LinearGuidesAndRails\",\n" +
			"      \"Pulleys\",\n" +
			"      \"RollerChain\",\n" +
			"      \"CouplingsCollarsAndUniversalJoints\",\n" +
			"      \"Springs\",\n" +
			"      \"Sprockets\",\n" +
			"      \"UniversalJoints\"\n" +
			"    ],\n" +
			"    \"toys\": [\n" +
			"      \"toysAndGames\",\n" +
			"      \"hobbies\",\n" +
			"      \"collectibleCard\",\n" +
			"      \"costume\",\n" +
			"      \"puzzles\",\n" +
			"      \"games\",\n" +
			"      \"models\",\n" +
			"      \"childrensCostume\",\n" +
			"      \"partySupplies\"\n" +
			"    ],\n" +
			"    \"sports\": [\n" +
			"      \"SportingGoods\",\n" +
			"      \"GolfClubHybrid\",\n" +
			"      \"GolfClubIron\",\n" +
			"      \"GolfClubPutter\",\n" +
			"      \"GolfClubWedge\",\n" +
			"      \"GolfClubWood\",\n" +
			"      \"GolfClubs\",\n" +
			"      \"SportGloves\"\n" +
			"    ],\n" +
			"    \"tiresAndWheels\": [\n" +
			"      \"tires\",\n" +
			"      \"wheels\",\n" +
			"      \"tireAndWheelAssemblies\"\n" +
			"    ],\n" +
			"    \"homeImprovement\": [\n" +
			"      \"buildingMaterials\",\n" +
			"      \"hardware\",\n" +
			"      \"electrical\",\n" +
			"      \"plumbingFixtures\",\n" +
			"      \"tools\",\n" +
			"      \"organizersAndStorage\",\n" +
			"      \"majorHomeAppliances\",\n" +
			"      \"securityElectronics\"\n" +
			"    ],\n" +
			"    \"coins\": [\n" +
			"      \"coin\",\n" +
			"      \"collectibleCoins\",\n" +
			"      \"bullion\"\n" +
			"    ],\n" +
			"    \"educationSupplies\": [\n" +
			"      \"teachingEquipment\"\n" +
			"    ],\n" +
			"    \"motorcycles\": [\n" +
			"      \"vehicles\",\n" +
			"      \"protectiveClothing\",\n" +
			"      \"helmets\",\n" +
			"      \"motorcyclesRidingBoots\",\n" +
			"      \"gloves\",\n" +
			"      \"accessories\",\n" +
			"      \"parts\"\n" +
			"    ],\n" +
			"    \"mechanicalFasteners\": [\n" +
			"      \"mechanicalFasteners\"\n" +
			"    ],\n" +
			"    \"foodAndBeverages\": [\n" +
			"      \"food\",\n" +
			"      \"householdSupplies\",\n" +
			"      \"beverages\",\n" +
			"      \"hardLiquor\",\n" +
			"      \"alcoholicBeverages\",\n" +
			"      \"wine\",\n" +
			"      \"beer\",\n" +
			"      \"spirits\"\n" +
			"    ],\n" +
			"    \"health\": [\n" +
			"      \"healthMisc\",\n" +
			"      \"personalCareAppliances\",\n" +
			"      \"prescriptionDrug\",\n" +
			"      \"dietarySupplements\",\n" +
			"      \"otcMedication\",\n" +
			"      \"prescriptionEyewear\",\n" +
			"      \"sexualWellness\",\n" +
			"      \"medicalSupplies\"\n" +
			"    ],\n" +
			"    \"lighting\": [\n" +
			"      \"lightsAndFixtures\",\n" +
			"      \"lightingAccessories\",\n" +
			"      \"lightBulbs\"\n" +
			"    ],\n" +
			"    \"foodServiceAndJanSan\": [\n" +
			"      \"foodServiceAndJanSan\"\n" +
			"    ],\n" +
			"    \"professionalHealthCare\": [\n" +
			"      \"professionalHealthCare\",\n" +
			"      \"medicalDevice\"\n" +
			"    ],\n" +
			"    \"shoes\": [\n" +
			"      \"Accessory\",\n" +
			"      \"Bag\",\n" +
			"      \"Shoes\",\n" +
			"      \"ShoeAccessory\",\n" +
			"      \"Handbag\",\n" +
			"      \"Eyewear\",\n" +
			"      \"Boot\"\n" +
			"    ],\n" +
			"    \"cameraPhoto\": [\n" +
			"      \"filmCamera\",\n" +
			"      \"camcorder\",\n" +
			"      \"digitalCamera\",\n" +
			"      \"digitalFrame\",\n" +
			"      \"binocular\",\n" +
			"      \"surveillanceSystem\",\n" +
			"      \"telescope\",\n" +
			"      \"microscope\",\n" +
			"      \"darkroom\",\n" +
			"      \"lens\",\n" +
			"      \"lensAccessory\",\n" +
			"      \"filter\",\n" +
			"      \"film\",\n" +
			"      \"bagCase\",\n" +
			"      \"blankMedia\",\n" +
			"      \"photoPaper\",\n" +
			"      \"cleaner\",\n" +
			"      \"flash\",\n" +
			"      \"tripodStand\",\n" +
			"      \"lighting\",\n" +
			"      \"projection\",\n" +
			"      \"photoStudio\",\n" +
			"      \"lightMeter\",\n" +
			"      \"powerSupply\",\n" +
			"      \"otherAccessory\"\n" +
			"    ],\n" +
			"    \"home\": [\n" +
			"      \"bedAndBath\",\n" +
			"      \"furnitureAndDecor\",\n" +
			"      \"kitchen\",\n" +
			"      \"outdoorLiving\",\n" +
			"      \"seedsAndPlants\",\n" +
			"      \"art\",\n" +
			"      \"fabric\",\n" +
			"      \"vacuumCleaner\",\n" +
			"      \"mattress\",\n" +
			"      \"bed\",\n" +
			"      \"headboard\",\n" +
			"      \"dresser\",\n" +
			"      \"cabinet\",\n" +
			"      \"chair\",\n" +
			"      \"table\",\n" +
			"      \"bench\",\n" +
			"      \"sofa\",\n" +
			"      \"desk\",\n" +
			"      \"floorCover\",\n" +
			"      \"bakeware\",\n" +
			"      \"cookware\",\n" +
			"      \"cutlery\",\n" +
			"      \"dinnerware\",\n" +
			"      \"serveware\",\n" +
			"      \"kitchenTools\",\n" +
			"      \"smallHomeAppliances\",\n" +
			"      \"home\"\n" +
			"    ],\n" +
			"    \"wineAndAlcohol\": [\n" +
			"      \"wine\",\n" +
			"      \"spirits\",\n" +
			"      \"beer\"\n" +
			"    ],\n" +
			"    \"industrial\": [\n" +
			"      \"abrasives\",\n" +
			"      \"adhesivesAndSealants\",\n" +
			"      \"cuttingTools\",\n" +
			"      \"electronicComponents\",\n" +
			"      \"gears\",\n" +
			"      \"grommets\",\n" +
			"      \"industrialHose\",\n" +
			"      \"industrialWheels\",\n" +
			"      \"mechanicalComponents\",\n" +
			"      \"oRings\",\n" +
			"      \"precisionMeasuring\",\n" +
			"      \"adhesiveTapes\"\n" +
			"    ],\n" +
			"    \"entertainmentCollectibles\": [\n" +
			"      \"entertainmentMemorabilia\"\n" +
			"    ],\n" +
			"    \"books\": [\n" +
			"      \"booksMisc\"\n" +
			"    ],\n" +
			"    \"errorParent\": [\n" +
			"      \"clothingAccessories\",\n" +
			"      \"miscellaneous\",\n" +
			"      \"tools\",\n" +
			"      \"fba\",\n" +
			"      \"materialHandling\",\n" +
			"      \"euCompliance\",\n" +
			"      \"additionalProductInformation\",\n" +
			"      \"luxuryBeauty\",\n" +
			"      \"threeDPrinting\"\n" +
			"    ],\n" +
			"    \"outdoors\": [\n" +
			"      \"outdoorRecreationProduct\",\n" +
			"      \"campingEquipment\",\n" +
			"      \"cyclingEquipment\",\n" +
			"      \"fishingEquipment\"\n" +
			"    ],\n" +
			"    \"musicalInstruments\": [\n" +
			"      \"brassAndWoodwindInstruments\",\n" +
			"      \"guitars\",\n" +
			"      \"instrumentPartsAndAccessories\",\n" +
			"      \"keyboardInstruments\",\n" +
			"      \"miscWorldInstruments\",\n" +
			"      \"percussionInstruments\",\n" +
			"      \"soundAndRecordingEquipment\",\n" +
			"      \"stringedInstruments\"\n" +
			"    ],\n" +
			"    \"baby\": [\n" +
			"      \"babyProducts\",\n" +
			"      \"infantToddlerCarSeat\",\n" +
			"      \"stroller\"\n" +
			"    ],\n" +
			"    \"giftCard\": [\n" +
			"      \"giftCard\",\n" +
			"      \"physicalGiftCard\",\n" +
			"      \"electronicGiftCard\"\n" +
			"    ],\n" +
			"    \"clothing\": [\n" +
			"      \"Shirt\",\n" +
			"      \"Sweater\",\n" +
			"      \"Pants\",\n" +
			"      \"Shorts\",\n" +
			"      \"Skirt\",\n" +
			"      \"Dress\",\n" +
			"      \"Suit\",\n" +
			"      \"Blazer\",\n" +
			"      \"Outerwear\",\n" +
			"      \"SocksHosiery\",\n" +
			"      \"Underwear\",\n" +
			"      \"Bra\",\n" +
			"      \"Shoes\",\n" +
			"      \"Hat\",\n" +
			"      \"Bag\",\n" +
			"      \"Accessory\",\n" +
			"      \"Jewelry\",\n" +
			"      \"Sleepwear\",\n" +
			"      \"Swimwear\",\n" +
			"      \"PersonalBodyCare\",\n" +
			"      \"HomeAccessory\",\n" +
			"      \"NonApparelMisc\",\n" +
			"      \"Kimono\",\n" +
			"      \"Obi\",\n" +
			"      \"Chanchanko\",\n" +
			"      \"Jinbei\",\n" +
			"      \"Yukata\",\n" +
			"      \"EthnicWear\",\n" +
			"      \"Costume\",\n" +
			"      \"AdultCostume\",\n" +
			"      \"BabyCostume\",\n" +
			"      \"ChildrensCostume\"\n" +
			"    ],\n" +
			"    \"wireless\": [\n" +
			"      \"wirelessAccessories\",\n" +
			"      \"wirelessDownloads\"\n" +
			"    ]\n" +
			"  }";


	static {
		try {
			allTem = JSONObject.parseObject(tem);
			keys = allTem.keySet();
		}catch (Exception ignored){

		}
	}


	public static String sameParent(String p){
		for(String str : keys){
			if(str.equalsIgnoreCase(p)){
				return str;
			}
		}
		return p;
	}

	public static String sameChild(String p,String c){
		JSONArray jsonArray = allTem.getJSONArray(p);
		if(jsonArray != null){
			for(Object o : jsonArray){
				String s = o.toString();
				if(s.equalsIgnoreCase(c)){
					return s;
				}
			}
		}
		return c;
	}






}
