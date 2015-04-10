package com.yisa.qiqilogin;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, AdapterView.OnItemClickListener {
	// 用于淘宝网页的解析，下面四个关键字在未来可能有所改动
	private static final String TB_KEY_MODS = "mods";
	private static final String TB_KEY_ITEMLIST = "itemlist";
	private static final String TB_KEY_DATA = "data";
	private static final String TB_KEY_AUCTIONS = "auctions";

	// 商品名称
	private static final String TB_KEY_PRODUCT_NAME = "raw_title";
	// 商品ID
	private static final String TB_KEY_PRODUCT_ID = "nid";
	// 商店名称
	private static final String TB_KEY_SHOP_NAME = "nick";
	// 商品价格
	private static final String TB_KEY_PRICE = "view_price";
	// 商品折扣
	private static final String TB_KEY_FEE = "view_fee";
	// 位置信息
	private static final String TB_KEY_LOCATION = "item_loc";
	// 已付款人数
	private static final String TB_KEY_SALES_LABEL = "view_sales";
	// 图片的URL地址
	private static final String TB_KEY_PICTURE_URL = "pic_url";
	// 商品具体信息的URL地址
	private static final String TB_KEY_DETAIL_URL = "detail_url";

	// 淘宝搜索的URL
	private static final String TB_SEARCH_URL = "http://s.taobao.com/search?initiative_id=staobaoz_20120515&q=";

	private static final String LOG_TAG = "leo";
	private static final int MESSAGE_GETHTML_FINISHED = 1;
	private Button mSearchButton;
	private TextView mHtmlTextView;
	private EditText mKeyEditText;
	private GridView mGridView;

	private TBProductPreviewAdapter mPreviewAdapter;
	private Document mDocument;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int key = msg.what;
			switch (key) {
			case MESSAGE_GETHTML_FINISHED:
				if (mDocument != null) {
					Elements elements = mDocument.select("script");
					int size = elements.size();
					Log.d(LOG_TAG, "size 1:" + size);
					for (int i = 0; i < size; i++) {
						Element element = elements.get(i);
						String data = element.data().trim();
						if (data.startsWith("g_page_config = ")) {
							int startIndex = data.indexOf("{");
							int endIndex = data.lastIndexOf("}");
							data = data.substring(startIndex, endIndex + 1);
							List<TBProductPreviewInfo> products = getProductInfos(data);
							mPreviewAdapter = new TBProductPreviewAdapter(
									getApplicationContext(), products);
							mGridView.setAdapter(mPreviewAdapter);
							StringBuffer stringBuffer = new StringBuffer();
							int length = products.size();
							for (int j = 0; j < length; j++) {
								stringBuffer.append(j + " : "
										+ products.get(j).toString() + "\n");
							}
							mHtmlTextView.setText(stringBuffer.toString());
							break;
						}
					}
				}

				break;

			default:
				break;
			}
		}

	};

	/**
	 * 获得商品列表
	 * 
	 * @param html
	 *            符合JSON规范的字符串
	 * @return 返回一个列表
	 */
	private List<TBProductPreviewInfo> getProductInfos(String html) {
		List<TBProductPreviewInfo> tbProducts = new ArrayList<TBProductPreviewInfo>();
		try {
			JSONObject products = new JSONObject(html);
			JSONObject mods = products.getJSONObject(TB_KEY_MODS);
			JSONObject itemlist = mods.getJSONObject(TB_KEY_ITEMLIST);
			JSONObject data = itemlist.getJSONObject(TB_KEY_DATA);
			JSONArray auctionsArray = data.getJSONArray(TB_KEY_AUCTIONS);
			int length = auctionsArray.length();
			for (int i = 0; i < length; i++) {
				TBProductPreviewInfo previewInfo = new TBProductPreviewInfo();
				JSONObject jsonObject = (JSONObject) auctionsArray.get(i);
				// 商品名称
				previewInfo.setProductName(jsonObject
						.getString(TB_KEY_PRODUCT_NAME));
				// 商品ID
				previewInfo.setProductID(Long.parseLong(jsonObject
						.getString(TB_KEY_PRODUCT_ID)));
				// 商店名称
				previewInfo.setShopName(jsonObject.getString(TB_KEY_SHOP_NAME));
				// 商品价格
				previewInfo.setPrice(Float.parseFloat(jsonObject
						.getString(TB_KEY_PRICE)));
				// 商品折扣
				previewInfo.setFee(Float.parseFloat(jsonObject
						.getString(TB_KEY_FEE)));
				// 位置信息
				previewInfo.setLocation(jsonObject.getString(TB_KEY_LOCATION));
				// 已付款人数
				previewInfo.setSalesLabel(jsonObject
						.getString(TB_KEY_SALES_LABEL));
				// 图片的URL地址
				previewInfo.setPictureUrl(jsonObject
						.getString(TB_KEY_PICTURE_URL));
				// 商品具体信息的URL地址
				previewInfo.setDetailInfoUrl(jsonObject
						.getString(TB_KEY_DETAIL_URL));

				tbProducts.add(previewInfo);
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "parse json failure:" + e.getMessage());
		}

		return tbProducts;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSearchButton = (Button) findViewById(R.id.search_button);
		mSearchButton.setOnClickListener(this);

		mHtmlTextView = (TextView) findViewById(R.id.html_text);
		mKeyEditText = (EditText) findViewById(R.id.key_edittext);
		mGridView = (GridView) findViewById(R.id.grid_view);
		
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.search_button:
			mHtmlTextView.setText("refresh");
			new Thread() {
				@Override
				public void run() {
					try {
						String key = URLEncoder.encode(mKeyEditText.getText()
								.toString(), "utf-8");
						String url = TB_SEARCH_URL + key;
						// mDocument = Jsoup.parse(new
						// URL(url).openStream(),"GBK", url);
						mDocument = Jsoup.connect(url).timeout(15000)
								.userAgent("Mozilla").get();
					} catch (IOException e) {
						e.printStackTrace();
					}

					mHandler.sendEmptyMessage(MESSAGE_GETHTML_FINISHED);
				}

			}.start();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TBProductPreviewInfo info = mPreviewAdapter.getItem(position);
		String url = info.getDetailInfoUrl();
		String productID = String.valueOf(info.getProductID());
		Intent intent = new Intent(this, TBProductDetailActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("id", productID);
		startActivity(intent);
	}

}
