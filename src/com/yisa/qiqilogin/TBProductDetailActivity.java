package com.yisa.qiqilogin;

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
    private String mText;
	private String mUrl;
	private String mProductID;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int key = msg.what;
			switch (key) {
			case MESSAGE_GETHTML_FINISHED:
				double postageFee;
				try {
					postageFee = TBProductDetailInfoParse.parsePostageFee(mText);
					mHtmlTextView.setText("postageFee:"+postageFee);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case MESSAGE_GETTEXT_FINISHED:
				TBProductDetailInfo info = TBProductDetailInfoParse.getTBProductDetailInfo(mText);
				mHtmlTextView.setText(info.toString());
				mHtmlTextView.setText("**"+info.getPostageFee());
				Log.e("liyu", "liyu");
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

		TBDetailURLUtil tbDetailURLUtil = new TBDetailURLUtil();
		tbDetailURLUtil.initParms(mProductID);
		
		mUrl = tbDetailURLUtil.generateUrl();

		mHtmlTextView = (TextView) findViewById(R.id.html_text);

		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setVisibility(View.GONE);

		new Thread() {
			@Override
			public void run() {
				try {
					mText = TBProductDetailInfoParse.postUrl(mUrl, "utf-8");
					mHandler.sendEmptyMessage(MESSAGE_GETTEXT_FINISHED);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}.start();

	}

	
}
