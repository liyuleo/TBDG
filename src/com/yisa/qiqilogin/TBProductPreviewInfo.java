package com.yisa.qiqilogin;

/**
 * 淘宝的产品预览信息
 * 
 * @author leo 2015.04.01
 */
public class TBProductPreviewInfo {
	// 商品店名
	private String mShopName;
	// 商品名称
	private String mProductName;
	// 商品ID
	private long mProductID;
	// 商品价格
	private float mPrice;
	// 商品折扣
	private float mFee;
	// 付款人数
	private String mSalesLabel;
	// 商品位置
	private String mLocation;
	// 商品预览图链接地址
	private String mPictureUrl;
	// 商品具体信息链接地址
	private String mDetailInfoUrl;

	public TBProductPreviewInfo() {

	}

	public String getShopName() {
		return mShopName;
	}

	public void setShopName(String shopName) {
		mShopName = shopName;
	}

	public String getProductName() {
		return mProductName;
	}

	public void setProductName(String productName) {
		mProductName = productName;
	}

	public long getProductID() {
		return mProductID;
	}

	public void setProductID(long productID) {
		mProductID = productID;
	}

	public float getPrice() {
		return mPrice;
	}

	public void setPrice(float price) {
		mPrice = price;
	}

	public float getFee() {
		return mFee;
	}

	public void setFee(float fee) {
		mFee = fee;
	}

	public String getSalesLabel() {
		return mSalesLabel;
	}

	public void setSalesLabel(String sales) {
		mSalesLabel = sales;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String location) {
		mLocation = location;
	}

	public String getPictureUrl() {
		return mPictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		mPictureUrl = pictureUrl;
	}

	public String getDetailInfoUrl() {
		return mDetailInfoUrl;
	}

	public void setDetailInfoUrl(String detailInfoUrl) {
		this.mDetailInfoUrl = detailInfoUrl;
	}

	@Override
	public String toString() {
		return mProductName + ":" + mPrice + ":" + mLocation + ":" + mShopName
				+ ":" + mSalesLabel;
	}

}
