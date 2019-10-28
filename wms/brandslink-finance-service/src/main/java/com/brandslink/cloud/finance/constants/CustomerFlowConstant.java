package com.brandslink.cloud.finance.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangzefei
 * @Classname CustomerFlowConstant
 * @Description 客户流水常量
 * @Date 2019/8/30 9:10
 */
public class CustomerFlowConstant {

    /**
     * 流水类型
     */
    public static final Integer COST_TYPE_STORAGE=1;//存储费
    public static final Integer COST_TYPE_IN_STOCK=2;//入库费
    public static final Integer COST_TYPE_RETURN=3;//销退费
    public static final Integer COST_TYPE_OUT_STOCK=4;//出库费
    public static final Integer COST_TYPE_INTERCEPT=5;//订单拦截费
    public static final Integer COST_TYPE_LOGISTICS=6;//物流费
    public static final Integer COST_TYPE_RECHARGE=7;//充值费

    /**
     * 流水详情类型
     */
    public static final Integer DETAIL_TYPE_STORAGE=1;//存储费
    public static final Integer DETAIL_TYPE_EXEMPT=2;//入库费(免检)
    public static final Integer DETAIL_TYPE_RETURN=3;//销退费
    public static final Integer DETAIL_TYPE_OUT_STOCK=4;//出库操作费
    public static final Integer DETAIL_TYPE_SPOT=5;//入库费(抽检)
    public static final Integer DETAIL_TYPE_ALL=6;//入库费(全检)

    /**
     * 配置详情类型
     */
    public static final Integer CELLS_TYPE_WEIGHT=1; //重量(kg)
    public static final Integer CELLS_TYPE_LENGTH=2;  //长(cm)
    public static final Integer CELLS_TYPE_WIDTH=3; //宽(cm)
    public static final Integer CELLS_TYPE_HEIGHT=4;//高(cm)

    public static final Integer CELLS_TYPE_STORAGE=10; //存储费库龄(天)
    public static final Integer CELLS_TYPE_UPLOAD=15;  //卸货费配置(kg)
    public static final Integer CELLS_TYPE_OPERATE=20; //操作费
    public static final Integer CELLS_TYPE_PACK=21;//打包费

    public static final Integer CELLS_TYPE_QC_EXEMPT=30;//来料-QC免检(元/件)
    public static final Integer CELLS_TYPE_QC_SPOT=31;//来料-QC抽检(元/件)
    public static final Integer CELLS_TYPE_QC_ALL=32;//来料-QC全检(元/件)

    public static final Integer CELLS_TYPE_SHELVE_SOURCE=33;//来料-上架费(元/件)
    public static final Integer CELLS_TYPE_SHELVE_ALLOT=34;//调拨-上架费(元/件)
    public static final Integer CELLS_TYPE_SHELVE_RETURN=35;//退货-上架费(元/件)

    public static final Integer CELLS_TYPE_CHECK=40;//盘点费(元/件)

    public static final Integer CELLS_TYPE_INTERCEPT_PICK_NOT=50;//B2C未拣货-拦截(元/包裹)
    public static final Integer CELLS_TYPE_INTERCEPT_REVIEW_NOT=51;//B2C未复核-拦截(元/包裹)
    public static final Integer CELLS_TYPE_INTERCEPT_PACK_NOT=52;//B2C未集包-拦截(元/包裹)
    public static final Integer CELLS_TYPE_INTERCEPT_PACK=53;//B2C已集包-拦截(元/包裹)
    public static final Integer CELLS_TYPE_BAR_CODE=54;//代贴条码费(元/件)
    public static final Integer CELLS_TYPE_PACK_CHANGE=55;//更换包装费(元/件)
    public static final Integer CELLS_TYPE_DESTROY=56;//销毁费(元/件)


    /**
     * 财务报价类型
     */
    public static final Integer QUOTE_TYPE_STORAGE=1;//存储费
    public static final Integer QUOTE_TYPE_UPLOAD=2;//入库卸货费
    public static final Integer QUOTE_TYPE_OPERATE=3;//入库操作费
    public static final Integer QUOTE_TYPE_OUTSTOCK=4;//B2C出库费
    public static final Integer QUOTE_TYPE_OUTSTOCK_NOT=5;//非B2C出库费
    public static final Integer QUOTE_TYPE_CHECK=6;//盘点费报价
    public static final Integer QUOTE_TYPE_INCREMENT=7;//增值费报价

    /**
     * 费用订单号前缀
     */
    public static final String BILL_NO_CC="CC"; //存储账单号前缀
    public static final String BILL_NO_RK="RK"; //入库账单号前缀
    public static final String BILL_NO_XT="XT"; //销退账单号前缀
    public static final String BILL_NO_CK="CK"; //出库账单号前缀
    public static final String BILL_NO_LJ="LJ"; //订单拦截账单号前缀
    public static final String BILL_NO_WL="WL"; //物流账单号前缀
    public static final String BILL_NO_CZ="CZ"; //充值账单号前缀

    public static final String BILL_NO_PT="PT"; //平台账单号前缀




    /**
     * 充值凭证上传格式
     */
    public static final String REGEXP_IMG=".(jpg|jpeg|png|JPG|JPEG|PNG)$";

    /**
     *  流水详情类型对应配置类型
     */
    public static final Map<Integer,Integer> TYPE_MAP=new HashMap<Integer,Integer>(5){
        {
            put(DETAIL_TYPE_EXEMPT,CELLS_TYPE_QC_EXEMPT);
            put(DETAIL_TYPE_SPOT,CELLS_TYPE_QC_SPOT);
            put(DETAIL_TYPE_ALL,CELLS_TYPE_QC_ALL);
        }
    };

}
