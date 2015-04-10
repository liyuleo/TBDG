package com.yisa.qiqilogin;

import java.util.HashMap;
import java.util.List;

/**
 * TB商品的具体信息
 * @author liyu 2015.04.08
 */
public class TBProductDetailInfo {
	// 商品店名
	private String mShopName;
	
	// 商品名称
	private String mProductName;
	
	// 商品ID
	private long mProductID;
	
	//商品的邮费，需要动态获取
	private double mPostage;
	
	// 商品所属的叶子类目 id
	private long mCID;
	
	// 商品价格，不一定是实际价格
	private double mPrice;
	
	// 商品的最终价格，也是实际价格，需要动态获取
	private double mFinalPrice;
	
	// 商品主图片地址
	private String mPictureUrl;
	
	// 商品位置
	private String mLocation;
	
	// 商品具体信息链接地址
	private String mDetailInfoUrl;
	
	// 商品库存，没有勾选其他选项（如尺寸，颜色，规格等选项）时使用这个来显示库存 
	private int mStock;
	
	// 该商品的分组列表
	private List<TBProductSku> mSkuList;
	
	// 该商品的分组列表
	private List<TBProductImg> mImageList;
	
	//商品的实际价格HashMap，和具体的选项相关联的
    private HashMap<String, Float> mPriceMap;

    //商品描述, 字数要大于5个字符，小于25000个字符
    private String mDescripe;
    
    //商品属性名称。标识着props内容里面的pid和vid所对应的名称。格式为：pid1:vid1:pid_name1:vid_name1;pid2:vid2:pid_name2:vid_name2……(注：属性名称中的冒号":"被转换为："#cln#"; 分号";"被转换为："#scln#" )
    private String mPropNames;
    
    public String getAllSkuPropNames(){
    	StringBuffer sb = new StringBuffer();
    	if(mSkuList != null){
    		int length = mSkuList.size();
    		for (int i = 0; i < length; i++) {
    			TBProductSku productSku = mSkuList.get(i);
    			sb.append(productSku.getPropertieName());
    			sb.append(";");
			}
    	}
    	
    	return sb.toString();
    }
    
    
	public String getPropNames() {
		return mPropNames;
	}

	public void setPropNames(String propNames) {
		mPropNames = propNames;
	}

	public String getDescripe() {
		return mDescripe;
	}

	public void setDescripe(String descripe) {
		mDescripe = descripe;
	}

	public HashMap<String, Float> getPriceMap() {
		return mPriceMap;
	}

	public void setPriceMap(HashMap<String, Float> priceMap) {
		mPriceMap = priceMap;
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

	public long getCID() {
		return mCID;
	}

	public void setCID(long cid) {
		mCID = cid;
	}
	
	public double getPostage() {
		return mPostage;
	}

	public void setPostage(double postage) {
		mPostage = postage;
	}

	public double getPrice() {
		return mPrice;
	}

	public void setPrice(double price) {
		mPrice = price;
	}
	
	public double getFinalPrice() {
		return mFinalPrice;
	}

	public void setmFinalPrice(double finalPrice) {
		mFinalPrice = finalPrice;
	}

	public String getPictureUrl() {
		return mPictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		mPictureUrl = pictureUrl;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String location) {
		mLocation = location;
	}

	public String getDetailInfoUrl() {
		return mDetailInfoUrl;
	}

	public void setmDetailInfoUrl(String detailInfoUrl) {
		mDetailInfoUrl = detailInfoUrl;
	}

	public int getStock() {
		return mStock;
	}

	public void setStock(int stock) {
		mStock = stock;
	}

	public List<TBProductSku> getSkuList() {
		return mSkuList;
	}

	public void setSkuList(List<TBProductSku> skuList) {
		mSkuList = skuList;
	}

	public List<TBProductImg> getImageList() {
		return mImageList;
	}

	public void setImageList(List<TBProductImg> imageList) {
		mImageList = imageList;
	}

	public TBProductDetailInfo() {
		
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("名称:" + mProductName + "\n");
		sb.append("店名:" + mShopName + "\n");
		sb.append("价格:" + mPrice + "\n");
		sb.append("所在地:" + mLocation + "\n");
		sb.append("ID:" + mProductID + "\n");
		sb.append("CID:" + mCID + "\n");
		sb.append("库存:" +  mStock + "\n");
		
		if(mSkuList !=  null){
			sb.append("mSkuList.size:"+mSkuList.size() + "\n");
		}
		
		if(mImageList !=  null){
			sb.append("mImageList.size:"+mImageList.size() + "\n");
		}
		
		sb.append("属性名称:" +  mPropNames + "\n");
		sb.append("详情页地址:" + mDetailInfoUrl + "\n");
		sb.append("主图片地址:" + mPictureUrl);
		
		return sb.toString();
	}

}
