package com.yisa.qiqilogin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * TB商品的分组结构
 * 
 * @author liyu 2015.04.08
 * 
 */
public class TBProductSku {
	// Sku的ID
	private long mSkuID;
	
	// 商品的库存数量
	private int mStock;
	
	// 商品的标价，一般不是实际价格
	private double mPrice;
	
	// sku的销售属性组合字符串（颜色，大小，等等，可通过类目API获取某类目下的销售属性）,格式是p1:v1;p2:v2
	private String mProperties;
	
	// sku所对应的销售属性的中文名字串，格式如：pid1:vid1:pid_name1:vid_name1;pid2:vid2:pid_name2:vid_name2……
	private String mPropertieNameCN;
    
	//属性列表可不能有多个属性（颜色，尺寸等）
	private ArrayList<TBProductProperty> mTbProductProperties; 
	
	public TBProductSku() {
		
	}

	public TBProductSku(long skuID, int stock, double price, String properties,
			String propertieName) {
		mTbProductProperties = new ArrayList<TBProductProperty>();
		setSkuID(skuID);
		setStock(stock);
		setPrice(price);
		setProperties(properties);
		setPropertieNameCN(propertieName);
	}

	public ArrayList<TBProductProperty> getTbProductProperties(){
		return mTbProductProperties;
	}
	
	@Override
	public String toString(){
		return "ID:" + mSkuID + ",库存:" + mStock + ",价格:" + mPrice + ",属性:" + mProperties + ",中文属性:" + mPropertieNameCN;
	}
	
	public long getSkuID() {
		return mSkuID;
	}

	public void setSkuID(long skuID) {
		mSkuID = skuID;
	}

	public int getStock() {
		return mStock;
	}

	public void setStock(int stock) {
		mStock = stock;
	}

	public double getPrice() {
		return mPrice;
	}

	public void setPrice(double price) {
		mPrice = price;
	}

	public String getProperties() {
		return mProperties;
	}

	public void setProperties(String properties) {
		mProperties = TBProductDetailInfoParse.sortParms(properties);
	}

	public String getPropertieNameCN() {
		return mPropertieNameCN;
	}

	public void setPropertieNameCN(String propertieName) {
		mPropertieNameCN = propertieName;
		formatProperty(propertieName);
	}
	
	private void formatProperty(String source) {
		if (source != null) {
			String keyReg = "(\\d+):(\\d+):(\\w+):(\\w+)";
			Pattern patten = Pattern.compile(keyReg, Pattern.DOTALL);
			Matcher matcher = patten.matcher(source);
			TBProductProperty productProperty;
			while(matcher.find()){
				productProperty = new TBProductProperty();
				productProperty.setNameID(matcher.group(1));
				productProperty.setName(matcher.group(3));
				productProperty.setValueID(matcher.group(2));
				productProperty.setValue(matcher.group(4));
				mTbProductProperties.add(productProperty);
			}
			
			//对属性组进行排序
			Collections.sort(mTbProductProperties, new PropertyCompator());
		}
	}

	//根据属性名ID,属性值ID进行排序
	class PropertyCompator implements Comparator<TBProductProperty> {
		@Override
		public int compare(TBProductProperty p1, TBProductProperty p2) {
			return p1.getComparatorFileds().compareTo(p2.getComparatorFileds());
		}

	}
}
