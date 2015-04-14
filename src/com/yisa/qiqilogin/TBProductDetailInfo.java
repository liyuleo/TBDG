package com.yisa.qiqilogin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.AsyncTask;
import android.util.Log;


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
    private HashMap<String, Double> mPriceMap;

    //商品描述, 字数要大于5个字符，小于25000个字符
    private String mDescripe;
    
    //所有分支产品，既多个属性的的组合，注意：这里是通过属性名得到属性的分支ID
    private HashMap<String, Long> mAllPropertys;
    
    //商品的属性中文名称:<id, 中文名称>
    private HashMap<String, String> mPropertyNames;
    
    //商品的属性值中文名称:<id, 中文名称>
	private HashMap<String, String> mPropertyValues;
    
	public TBProductDetailInfo() {
		mPropertyNames = new HashMap<String, String>();
		mAllPropertys = new HashMap<String, Long>();
		mPropertyValues = new HashMap<String, String>();
	}
	
	// 得到商品的中文属性名称：如颜色，大小。具体有多少个属性，由返回的数组长度决定
	public String[] getPropertyNameCN() {
		if (mPropertyNames != null) {
			int size = mPropertyNames.size();
			String[] keyNames = new String[size];
			Iterator<String> propertyNameIterator = mPropertyNames.keySet()
					.iterator();
			int i = 0;
			while (propertyNameIterator.hasNext()) {
				String name = propertyNameIterator.next();
				keyNames[i] = mPropertyNames.get(name);
				i++;
			}
			return keyNames;
		}
		return null;
	}
	
	
	//得到属性ID与属性中文名称的对应关系，得到属性值ID和属性值的实际含义的对应关系
	private void sortPropertyName() {
		if (mSkuList != null) {
			int length = mSkuList.size();
			for (int i = 0; i < length; i++) {
				TBProductSku productSku = mSkuList.get(i);
				mAllPropertys.put(productSku.getProperties(), productSku.getSkuID());
				
				ArrayList<TBProductProperty> productPropertyList = productSku.getTbProductProperties();
				int size = productPropertyList.size();
				for (int j = 0; j < size; j++) {
					TBProductProperty productProperty = productPropertyList.get(j);
					String nameID = productProperty.getNameID();
					String name = productProperty.getName();
					String valueID = productProperty.getValueID();
					String value = productProperty.getValue();
					
					if(!mPropertyNames.containsKey(nameID)){
						mPropertyNames.put(nameID, name);
					}
					
					if(!mPropertyValues.containsKey(valueID)){
						mPropertyValues.put(valueID, value);
					}
					
				}
			}
		}

	}
	
	//得到某个属性的全部值
	public TreeSet<String> getPropertyValueByName(String propertyNameID){
		TreeSet<String> values = new TreeSet<String>(); 
		if(mAllPropertys != null){
			String keyReg =  propertyNameID+ ":(\\d+)";
			Pattern pattern = Pattern.compile(keyReg);
			Matcher matcher = null;
			Iterator<String> propertyNameIterator = mAllPropertys.keySet().iterator();
			while(propertyNameIterator.hasNext()){
				String propertyNames = propertyNameIterator.next();
				if(propertyNames != null){
					matcher = pattern.matcher(propertyNames);
					if(matcher.find()){
						values.add(mPropertyValues.get(matcher.group(1)));
					}
				}
			}
		}
		
		return values;
	}
    
	public String getDescripe() {
		return mDescripe;
	}

	public void setDescripe(String descripe) {
		mDescripe = descripe;
	}

	public HashMap<String, Double> getPriceMap() {
		return mPriceMap;
	}

	public void setPriceMap(HashMap<String, Double> priceMap) {
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
	
	public double getFinalPrice(String property) {
		if (mPriceMap == null) {
			syncFinalPrice();
		}

		mFinalPrice = mPriceMap.get(property);

		return mFinalPrice;
	}

	public void syncFinalPrice() {
		new QueryPriceAsyncTask().execute(mProductID);
	}
	
	public void setFinalPrice(double finalPrice) {
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

	public void setDetailInfoUrl(String detailInfoUrl) {
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

	//设置分支商品列表时，进行商品属性的格式化操作
	public void setSkuList(List<TBProductSku> skuList) {
		mSkuList = skuList;
		sortPropertyName();
		
//		Iterator<String> names = mPropertyNames.keySet().iterator();
//		while(names.hasNext()){
//			String name = names.next();
//			Log.e("liyu", name + ":" + mPropertyNames.get(name));
//			sortPropertyValueByName(name);
//		}
		
//		String[] names = getPropertyNameCN();
//		for (int i = 0; i < names.length; i++) {
//			Log.e("liyu", i + ":" + names[i]);
//			
//		}
	}

	public List<TBProductImg> getImageList() {
		return mImageList;
	}

	public void setImageList(List<TBProductImg> imageList) {
		mImageList = imageList;
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
		
		sb.append("详情页地址:" + mDetailInfoUrl + "\n");
		sb.append("主图片地址:" + mPictureUrl);
		
		return sb.toString();
	}

	class QueryPriceAsyncTask extends AsyncTask<Long, Void, String>{

		@Override
		protected String doInBackground(Long... params) {
			long id = params[0].longValue();
			String source = TBProductDetailInfoParse.getRealPrice(id);
			return source;
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			try {
				mPriceMap = TBProductDetailInfoParse.parseRealPrice(result);
				Log.e("liyu", mPriceMap.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
}
