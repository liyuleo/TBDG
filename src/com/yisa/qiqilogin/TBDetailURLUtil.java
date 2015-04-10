package com.yisa.qiqilogin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
/**
 * 生成TB 开放API的URL地址
 * @author leo 2015.04.08
 *
 */
public final class TBDetailURLUtil {

	private static final String TB_DETAIL_URL = "http://gw.api.taobao.com/router/rest?";
	private static final String APP_KEY = "app_key";
	private static final String APP_KEY_VALUE = "21345292";
	private static final String FIELDS_KEY = "fields";
	private static final String FORMAT_KEY = "format";
	private static final String FORMAT_KEY_VALUE = "json";
	private static final String METHOD_KEY = "method";
	private static final String METHOD_KEY_VALUE = "taobao.item.get";

	private static final String NUM_IID_KEY = "num_iid";
	private static final String SECRET_VALUE = "51c9de954ab73d41998535432e906eb1";
	private static final String SIGN_METHOD_KEY = "sign_method";
	private static final String SIGN_METHOD_KEY_VALUE = "md5";
	private static final String TIMESTAMP_KEY = "timestamp";
	private static final String VERSION_KEY = "v";
	private static final String VERSION_KEY_VALUE = "2.0";

	// URL中的所有参数列表
	private TreeMap<String, String> mParmsMap;

	public TBDetailURLUtil(){
		
	}
	
	// 生成签名：32位的大写MD5码
	public String generateMD5() {
		if (mParmsMap != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(SECRET_VALUE);
			// 升序排列
			Iterator<String> keys = mParmsMap.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (key != null) {
					sb.append(key + mParmsMap.get(key));
				}
			}
			sb.append(SECRET_VALUE);

			return md5(sb.toString());
		}

		return null;
	}

	public String generateUrl() {
		String sign = generateMD5();
		if (sign != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(TB_DETAIL_URL);
			sb.append("sign=" + sign + "&");
			Iterator<String> keys = mParmsMap.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (key != null) {
					sb.append(key);
					sb.append("=");
					try {
						sb.append(URLEncoder.encode(mParmsMap.get(key), "utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					sb.append("&");
				}
			}
			return sb.toString();
		}
		return null;
	}

	// 简单的初始化一下固定的参数
	public void initParms() {
		if (mParmsMap == null) {
			mParmsMap = new TreeMap<String, String>();
		}
		
		setAppKeyParms(APP_KEY_VALUE);
		setFormatParms(FORMAT_KEY_VALUE);
		setMethodParms(METHOD_KEY_VALUE);
		setSignMethodParms(SIGN_METHOD_KEY_VALUE);
		setVersionParms(VERSION_KEY_VALUE);
		//默认查询这些字段
		setFieldsParms(TBProductDetailInfoParse.QUERY_FIELDS);
		setTimeStampParms();
	}

	public void initParms(String productID) {
		initParms();		
		setProdutIDParms(productID);
	}

	/**
	 * 设置返回数据的格式
	 * @param value xml/json
	 */
	public void setFormatParms(String value) {
		mParmsMap.put(FORMAT_KEY, value);
	}

	/**
	 * 设置API的版本
	 * @param value 例如：2.0
	 */
	public void setVersionParms(String value) {
		mParmsMap.put(VERSION_KEY, value);
	}

	/**
	 * 设置需要调用的淘宝API方法名
	 * @param value 例如：taobao.item.get
	 */
	public void setMethodParms(String value) {
		mParmsMap.put(METHOD_KEY, value);
	}

	/**
	 * 设置签名方式
	 * @param value md5/hmac
	 */
	public void setSignMethodParms(String value) {
		mParmsMap.put(SIGN_METHOD_KEY, value);
	}
	
	/**
	 * 设置需要查询的商品的ID
	 * @param value md5/hmac
	 */
	public void setProdutIDParms(String value) {
		mParmsMap.put(NUM_IID_KEY, value);
	}

	/**
	 * 设置申请到的app_key
	 * @param value
	 */
	public void setAppKeyParms(String value) {
		mParmsMap.put(APP_KEY, value);
	}

	/**
	 * 设置需要查询的字段
	 * 
	 * @param
	 */
	public void setFieldsParms(String value) {
		mParmsMap.put(FIELDS_KEY, value);
	}

	/**
	 * 设置时间戳
	 * @param yyyy-MM-dd HH:mm:ss格式化后的日期字符串
	 */
	public void setTimeStampParms() {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		mParmsMap.put(TIMESTAMP_KEY, timeStamp);
	}

	// MD5加密
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString().toUpperCase();
	}
}
