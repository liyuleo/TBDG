package com.yisa.qiqilogin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
	private double mPostageFee;
	
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
	
	// 该商品的图片列表
	private List<TBProductImg> mImageList;
	
	//商品的实际价格HashMap，和具体的选项相关联的
    private HashMap<String, Double> mPriceMap;

    //商品描述, 字数要大于5个字符，小于25000个字符
    private String mDescripe;
    
    //所有分支产品，既多个属性的的组合，注意：这里是通过属性名得到属性的分支
    private HashMap<String, TBProductSku> mAllPropertysMap;
    
    //商品的属性中文名称:<id, 中文名称>
    private HashMap<String, String> mPropertyNamesCNMap;
    
    //商品的属性值中文名称:<id, 中文名称>
	private HashMap<String, String> mPropertyValuesCNMap;
    
	//查询价格的异步任务是否已经开始
	private boolean mQuaryPriceStart = false;
	
	//查询邮资的异步任务是否已经开始
	private boolean mQuaryPostageFeeStart = false;
	
	public TBProductDetailInfo() {
		mPropertyNamesCNMap = new HashMap<String, String>();
		mAllPropertysMap = new HashMap<String, TBProductSku>();
		mPropertyValuesCNMap = new HashMap<String, String>();
	}
	
	// 得到商品的中文属性名称：如颜色，大小。具体有多少个属性，由返回的数组长度决定
	public String[] getPropertyNameCN() {
		if (mPropertyNamesCNMap != null) {
			int size = mPropertyNamesCNMap.size();
			String[] keyNames = new String[size];
			Iterator<String> propertyNameIterator = mPropertyNamesCNMap.keySet()
					.iterator();
			int i = 0;
			while (propertyNameIterator.hasNext()) {
				String name = propertyNameIterator.next();
				keyNames[i] = mPropertyNamesCNMap.get(name);
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
				
				mAllPropertysMap.put(productSku.getProperties(), productSku);
				
				ArrayList<TBProductProperty> productPropertyList = productSku.getTbProductProperties();
				int size = productPropertyList.size();
				for (int j = 0; j < size; j++) {
					TBProductProperty productProperty = productPropertyList.get(j);
					String nameID = productProperty.getNameID();
					String name = productProperty.getName();
					String valueID = productProperty.getValueID();
					String value = productProperty.getValue();
					
					if(!mPropertyNamesCNMap.containsKey(nameID)){
						mPropertyNamesCNMap.put(nameID, name);
					}
					
					if(!mPropertyValuesCNMap.containsKey(valueID)){
						mPropertyValuesCNMap.put(valueID, value);
					}
					
				}
			}
		}

	}
	
	//得到某个属性的全部值
	public TreeSet<String> getPropertyValueByName(String propertyNameID){
		TreeSet<String> values = new TreeSet<String>(); 
		if(mAllPropertysMap != null){
			String keyReg =  propertyNameID+ ":(\\d+)";
			Pattern pattern = Pattern.compile(keyReg);
			Matcher matcher = null;
			Iterator<String> propertyNameIterator = mAllPropertysMap.keySet().iterator();
			while(propertyNameIterator.hasNext()){
				String propertyNames = propertyNameIterator.next();
				if(propertyNames != null){
					matcher = pattern.matcher(propertyNames);
					if(matcher.find()){
						values.add(mPropertyValuesCNMap.get(matcher.group(1)));
					}
				}
			}
		}
		
		return values;
	}
    
	//得到商品的所有分支属性的数组
	public String[] getAllProperities() {
		String[] properities = null;
		if (mAllPropertysMap != null) {
			Set<String> keySet = mAllPropertysMap.keySet();
			int size = keySet.size();
			properities = new String[size];
			Iterator<String> iterator = keySet.iterator();
			int index = 0;
			while (iterator.hasNext()) {
				properities[index] = iterator.next();
				index++;
			}
		}
		return properities;
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
	
	public double getPostageFee() {
		return mPostageFee;
	}

	public void setPostageFee(double postage) {
		mPostageFee = postage;
	}

	//开始异步查询商品的邮费
	public void syncPostageFee(){
		if(!mQuaryPostageFeeStart){
			mQuaryPostageFeeStart = true;
			new QueryPostageFeeAsyncTask().execute(mProductID);
		}
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
		
		String key = TBProductDetailInfoParse.sortParms(property);

		if(mPriceMap != null){
			mFinalPrice = mPriceMap.get(key);
		}

		return mFinalPrice;
	}

	//开始异步查询分支商品的价格
	public void syncFinalPrice() {
		if(!mQuaryPriceStart){
			new QueryPriceAsyncTask().execute(mProductID);
			mQuaryPriceStart = true;
		}
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
	}

	public List<TBProductImg> getImageList() {
		return mImageList;
	}

	public void setImageList(List<TBProductImg> imageList) {
		mImageList = imageList;
	}

	// 通过属性组，得到特定的商品分支，要对属性组进行重新排序
	public TBProductSku getSkuByProperities(String properities) {
		if (mAllPropertysMap != null) {
			String key = TBProductDetailInfoParse.sortParms(properities);
			return mAllPropertysMap.get(key);
		}
		return null;
	}

	// 通过属性组，得到特定的商品分支的ID
	public long getSkuIDByProperities(String properities) {
		TBProductSku sku = getSkuByProperities(properities);
		if (sku != null) {
			return sku.getSkuID();
		}
		return -1L;
	}

	// 通过属性组，得到特定的商品分支的库存
	public int getSkuStockByProperities(String properities) {
		TBProductSku sku = getSkuByProperities(properities);
		if (sku != null) {
			return sku.getStock();
		}
		return 0;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("名称:" + mProductName + "\n");
		sb.append("店名:" + mShopName + "\n");
		sb.append("价格:" + mPrice + "\n");
		sb.append("所在地:" + mLocation + "\n");
		sb.append("ID:" + mProductID + "\n");
		Log.e("liyu", "toString");
		sb.append("邮费:" + mPostageFee + "\n");
		sb.append("库存:" +  mStock + "\n");
		
		if(mSkuList !=  null){
			sb.append("mSkuList.size:"+mSkuList.size() + "\n");
		}
		
		String[] properities = getAllProperities();
		
		for (int i = 0; i < properities.length; i++) {
			String properity = properities[i];
			TBProductSku sku = getSkuByProperities(properity);
			ArrayList<TBProductProperty> list = sku.getTbProductProperties();
			int size = list.size();
			for (int j = 0; j < size; j++) {
				TBProductProperty tbProperty = list.get(j);
				sb.append(tbProperty.toString()+"\n");
			}
			sb.append("价格:"+getFinalPrice(properity) + "\n");
			sb.append("库存:"+sku.getStock() + "\n");
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
				Log.e("liyu", "QueryPriceAsyncTask done");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	class QueryPostageFeeAsyncTask extends AsyncTask<Long, Void, String> {

		@Override
		protected String doInBackground(Long... params) {
			long id = params[0].longValue();
			String source = TBProductDetailInfoParse.getRealPrice(id);
			return source;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				mPostageFee = TBProductDetailInfoParse.parsePostageFee(result);
				Log.e("liyu", "QueryPostageFeeAsyncTask done:" + mPostageFee);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
