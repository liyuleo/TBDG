package com.yisa.qiqilogin;

/**
 * TB商品的图片结构信息
 * 
 * @author leo 2015.04.08
 */
public class TBProductImg {
	// 图片链接地址
	private String mUrl;
	// 图片放在第几张（多图时可设置）
	private int mPosition;
	// 商品图片的id，和商品相对应（主图id默认为0）
	private long mID;

	public TBProductImg() {

	}

	public TBProductImg(String url, int position, long id) {
		mUrl = url;
		mPosition = position;
		mID = id;
	}

	@Override
	public String toString(){
		return "mID:" + mID + ",mPosition:" + mPosition + ",mUrl:" + mUrl;
	}
	
	public String getUrl() {
		return mUrl;
	}

	public void setmUrl(String url) {
		mUrl = url;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setmPosition(int position) {
		mPosition = position;
	}

	public long getID() {
		return mID;
	}

	public void setmID(long id) {
		mID = id;
	}

}
