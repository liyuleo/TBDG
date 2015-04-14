package com.yisa.qiqilogin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.nodes.Document;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class TBProductDetailActivity extends Activity {

	private static final int MESSAGE_GETHTML_FINISHED = 1;
	private static final int MESSAGE_GETTEXT_FINISHED = 2;
	private TextView mHtmlTextView;
	private WebView mWebView;
	private Document mDocument;
    private String mText;
	private String mUrl;
	private String mProductID;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int key = msg.what;
			switch (key) {
			case MESSAGE_GETHTML_FINISHED:
				if (mDocument != null) {
					Log.e("liyu", "mDocument.text():"+mDocument.text());
					TBProductDetailInfo info = TBProductDetailInfoParse.getTBProductDetailInfo(mDocument.text());
					Log.e("liyu", "info:"+info);
					mHtmlTextView.setText(info.toString());
				} else {
					try {
						StringBuffer sb = new StringBuffer();
						HashMap<String, Double> prices = TBProductDetailInfoParse.parseRealPrice(mText);
						Iterator<String> iterator = prices.keySet().iterator();
						while(iterator.hasNext()){
							String name = iterator.next();
							double value = prices.get(name);
							sb.append(name + ":" + value + "\n");
						}
						mHtmlTextView.setText(sb.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				break;
			case MESSAGE_GETTEXT_FINISHED:
				TBProductDetailInfo info = TBProductDetailInfoParse.getTBProductDetailInfo(mText);
				//info.getAllSkuPropNames();
				
				mHtmlTextView.setText(info.toString());
//				mWebView.setVisibility(View.VISIBLE);
//				mWebView.loadDataWithBaseURL(info.getDetailInfoUrl(), info.getDescripe(), "text/html", "utf-8", null);
			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_info);

		Intent intent = getIntent();
		mProductID = intent.getExtras().getString("id", null);
		Log.e("liyu", "mProductID:"+mProductID);
		
		//mUrl = intent.getExtras().getString("url", null);
	    //邮费
		//mUrl = "http://detailskip.taobao.com/json/deliveryFee.htm?itemId=43484521236";
		
		TBDetailURLUtil tbDetailURLUtil = new TBDetailURLUtil();
		tbDetailURLUtil.initParms(mProductID);
		mUrl = tbDetailURLUtil.generateUrl();
		Log.e("liyu", "mUrl:" + mUrl);
		
		mHtmlTextView = (TextView) findViewById(R.id.html_text);

		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setVisibility(View.GONE);
		//mWebView.loadUrl(mUrl);
		new Thread() {
			@Override
			public void run() {
				try {
//					mDocument = Jsoup.connect(mUrl).timeout(15000).get();
//					mText = TBProductDetailInfoParse.getRealPrice(Long.parseLong(mProductID));
//					mHandler.sendEmptyMessage(MESSAGE_GETHTML_FINISHED);
					mText = postUrl(mUrl,"utf-8");
					mHandler.sendEmptyMessage(MESSAGE_GETTEXT_FINISHED);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}.start();

	}

	//发送Post请求，并指定返回内容的编码格式
	public static String postUrl(String url, String encoding) {
		InputStream is = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			is = entity.getContent();
		} catch (Exception e) {
			return "Fail to establish http connection!" + e.toString();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, encoding));
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
}
