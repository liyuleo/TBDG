package com.yisa.qiqilogin;

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
	private String mPropertieName;

	public TBProductSku() {
		
	}

	public TBProductSku(long skuID, int stock, double price, String properties,
			String propertieName) {
		mSkuID = skuID;
		mStock = stock;
		mPrice = price;
		mProperties = properties;
		mPropertieName = propertieName;
	}

	@Override
	public String toString(){
		return "ID:" + mSkuID + ",库存:" + mStock + ",价格:" + mPrice + ",属性:" + mProperties + ",中文属性:" + mPropertieName;
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

	public void setmPrice(double price) {
		mPrice = price;
	}

	public String getProperties() {
		return mProperties;
	}

	public void setProperties(String properties) {
		mProperties = properties;
	}

	public String getPropertieName() {
		return mPropertieName;
	}

	public void setPropertieName(String propertieName) {
		mPropertieName = propertieName;
	}

}
