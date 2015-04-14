package com.yisa.qiqilogin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 解析TB商品的具体信息，这个具体信息是通过TB开放平台的标准API获得，以JSON格式返回
 * 
 * @author leo 2015.04.08
 */
public class TBProductDetailInfoParse {
	public static final String QUERY_FIELDS = "cid,desc,detail_url,item_img,location,nick,num,num_iid,pic_url,price,sku,title";

	private static final String LOG = "TBProductDetailInfoParse";

	static final String KEY_ITEM_GET_RESPONSE = "item_get_response";

	static final String KEY_ITEM = "item";

	// 商品所属的叶子类目 id
	static final String KEY_CID = "cid";

	// 商品具体信息页面的url
	static final String KEY_DETAIL_URL = "detail_url";
	
	// 商品描述, 字数要大于5个字符，小于25000个字符
	static final String KEY_DESC = "desc";

	// 商品库存，没有勾选其他选项（如尺寸，颜色，规格等选项）时使用这个来显示库存
	static final String KEY_NUM = "num";

	// 商品数字id
	static final String KEY_NUM_IID = "num_iid";

	// 商品标题,不能超过60字节
	static final String KEY_TITLE = "title";

	//商品属性 格式：pid:vid;pid:vid
    static final String KEY_PROPS = "props";
	
	//商品属性名称。标识着props内容里面的pid和vid所对应的名称。
	static final String KEY_PROPS_NAME = "props_name";
	
	// Sku列表:fields中只设置sku可以返回Sku结构体中所有字段，如果设置为sku.sku_id、sku.properties、sku.quantity等形式就只会返回相应的字段
	static final String KEY_SKUS = "skus";

	// Sku列表项
	static final String KEY_SKU = "sku";

	// SKU ID
	static final String KEY_SKU_ID = "sku_id";

	// SKU 库存
	static final String KEY_SKU_QUANTITY = "quantity";

	// SKU 价格
	static final String KEY_SKU_PRICE = "price";

	// SKU sku的销售属性组合字符串（颜色，大小，等等，可通过类目API获取某类目下的销售属性）,格式是p1:v1;p2:v2
	static final String KEY_SKU_PROPERTIES = "properties";

	// SKU
	// sku所对应的销售属性的中文名字串，格式如：pid1:vid1:pid_name1:vid_name1;pid2:vid2:pid_name2:vid_name2……
	static final String KEY_SKU_PROPERTIES_NAME = "properties_name";

	// 商品图片列表(包括主图)。fields中只设置item_img可以返回ItemImg结构体中所有字段，如果设置为item_img.id、item_img.url、item_img.position等形式就只会返回相应的字段
	static final String KEY_ITEM_IMGS = "item_imgs";

	// 商品图片列表项
	static final String KEY_ITEM_IMG = "item_img";

	// 商品图片的id,主图默认为0
	static final String KEY_ITEM_IMG_ID = "id";

	// 商品图片的地址
	static final String KEY_ITEM_IMG_URL = "url";

	// 商品图片的排列位置，多图时可选
	static final String KEY_ITEM_IMG_POSITION = "position";

	// 商品价格，格式：5.00；单位：元；精确到：分(不是最终价格)
	static final String KEY_PRICE = "price";

	// 商品所在地
	static final String KEY_LOCALTION = "location";

	// 所在城市（中文名称）
	static final String KEY_CITY = "city";

	// 所在省份（中文名称）
	static final String KEY_STATE = "state";

	// 商品主图片地址
	static final String KEY_PIC_URL = "pic_url";

	// 卖家昵称
	static final String KEY_NICK = "nick";

	/**
	 * 解析字符串，获得TB某个商品的具体信息
	 * 
	 * @param soucrce
	 *            JSON格式的字符串
	 * @return
	 */
	public static TBProductDetailInfo getTBProductDetailInfo(String soucrce) {
		TBProductDetailInfo info = new TBProductDetailInfo();
		try {
			JSONObject products = new JSONObject(soucrce);
			JSONObject itemFromResponse = products.getJSONObject(KEY_ITEM_GET_RESPONSE);
			JSONObject item = itemFromResponse.getJSONObject(KEY_ITEM);
			info.setProductName(item.getString(KEY_TITLE));
			info.setShopName(item.getString(KEY_NICK));
			info.setPrice(item.getDouble(KEY_PRICE));
			info.setCID(item.getLong(KEY_CID));
			info.setProductID(item.getLong(KEY_NUM_IID));
			info.syncFinalPrice();
			info.setDescripe(item.getString(KEY_DESC));
			info.setStock(item.getInt(KEY_NUM));
			info.setDetailInfoUrl(item.getString(KEY_DETAIL_URL));
			info.setPictureUrl(item.getString(KEY_PIC_URL));
			info.setLocation(parseLocation(item.getJSONObject(KEY_LOCALTION)));
			info.setImageList(parseImages(item.getJSONObject(KEY_ITEM_IMGS)));
			info.setSkuList(parseSkus(item.getJSONObject(KEY_SKUS)));
			
		} catch (JSONException e) {
			Log.e(LOG, "parse JSON exception:" + e.getMessage());
			e.printStackTrace();
		}
		return info;
	}

	private static String parseLocation(JSONObject location)
			throws JSONException {
		StringBuffer sb = new StringBuffer();
		String state = location.getString(KEY_STATE);
		if (state != null) {
			sb.append(state);
		}
		String city = location.getString(KEY_CITY);
		if (city != null) {
			sb.append(" ");
			sb.append(city);
		}
		return sb.toString();
	}

	// 解析SKU
	private static List<TBProductSku> parseSkus(JSONObject skus)
			throws JSONException {
		JSONArray skuArray = skus.getJSONArray(KEY_SKU);
		int length = skuArray.length();
		List<TBProductSku> tbProductSkusList = new ArrayList<TBProductSku>(
				length);
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = (JSONObject) skuArray.get(i);
			long skuID = jsonObject.getLong(KEY_SKU_ID);
			int stock = jsonObject.getInt(KEY_SKU_QUANTITY);
			double price = jsonObject.getDouble(KEY_SKU_PRICE);
			
			String properties = jsonObject.getString(KEY_SKU_PROPERTIES);
			String propertieName = jsonObject.getString(KEY_SKU_PROPERTIES_NAME);
			
			TBProductSku tbProductSku = new TBProductSku(skuID, stock, price, properties, propertieName);
			tbProductSkusList.add(tbProductSku);
		}
		return tbProductSkusList;
	}

	// 解析图片项
	private static List<TBProductImg> parseImages(JSONObject images)
			throws JSONException {
		JSONArray imageArray = images.getJSONArray(KEY_ITEM_IMG);
		int length = imageArray.length();
		List<TBProductImg> tbProductImageList = new ArrayList<TBProductImg>(length);
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = (JSONObject) imageArray.get(i);
			String url = jsonObject.getString(KEY_ITEM_IMG_URL);
			int position = jsonObject.getInt(KEY_ITEM_IMG_POSITION);
			long id = jsonObject.getLong(KEY_ITEM_IMG_ID);
			TBProductImg tbProductImg = new TBProductImg(url, position, id);
			tbProductImageList.add(tbProductImg);
		}
		return tbProductImageList;
	}

	// 联网获取商品的促销价格
	public static String getRealPrice(long id) {
		InputStream is = null;
		String result = "";
		String url = getRealPriceUrl(id);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Accept-Language",
					"zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,ja;q=0.2");
			httppost.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36");
			httppost.setHeader("Accept", "*/*");
			httppost.setHeader(
					"Referer",
					"http://item.taobao.com/item.htm?spm=a1z09.2.9.19.azHCe7&id=20157949682&_u=i11i9ljj5737&qq-pf-to=pcqq.c2c");
			httppost.setHeader("Connection", "keep-alive");
			httppost.setHeader("Cache-Control", "max-age=0");
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			is = entity.getContent();
		} catch (Exception e) {
			return "Fail to establish http connection!" + e.toString();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "gbk"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result = sb.toString();
		} catch (Exception e) {
			return "Fail to convert net stream!";
		}

		return result;
	}

	// 获取促销价格的URL
	private static String getRealPriceUrl(long id) {
		return "http://detailskip.taobao.com/json/sib.htm?itemId="
				+ id
				+ "&sellerId=123959625&p=1&rcid=50008163&sts=269553664,1170936092170387524,33554560,70373043404803&chnl=&price=6000&shopId=&vd=1&skil=false&pf=1&al=false&ap=0&ss=0&free=1&st=1&ct=1&prior=1&ref=";
	}

	// 解析商品的促销价格
	public static HashMap<String, Double> parseRealPrice(String source) throws Exception {
		// String priceReg = "price:\"(\\d+\\.?\\d*)\"";
		// String keyReg = ";(\\d+:\\d+;)+";
		// String keyReg = "\";(\\d+:\\d+;)+\":\\["+"(.*?])";
		String keyReg = "\";((\\d+:\\d+;)+)\":\\["
				+ ".*?(price:\"(\\d+\\.?\\d*)\")" + ".*?]";
		int startIndex = source.indexOf("g_config.PromoData=");
		int endIndex = source.indexOf("g_config.vdata.asyncViewer=");
		source = source.substring(startIndex, endIndex).trim();

		HashMap<String, Double> prices = new HashMap<String, Double>();

		Pattern patten = Pattern.compile(keyReg, Pattern.DOTALL);
		Matcher matcher = patten.matcher(source);
		String name = null;
		String value = null;

		while (matcher.find()) {
			name = matcher.group(1);
			value = matcher.group(4);
			if (name != null) {
				prices.put(name, Double.parseDouble(value));
			}
		}

		return prices;
	}
	
	
}
