package com.yisa.qiqilogin;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 展示淘宝的商品
 * 
 * @author leo 2015.04.01
 */
public class TBProductPreviewAdapter extends BaseAdapter {
	private static final String LOG_TAG = "TBProductPreviewAdapter";
	// 图片缓存类
	private LruCache<String, Bitmap> mLruCache;
	private List<TBProductPreviewInfo> mLists;
	private LayoutInflater mLayoutInflater;
	// 记录正在下载或者已经下载的图片
	private HashMap<String, DownloadBitmapAsyncTask> mTasks;

	private int mWidth;
	private int mHeight;

	public TBProductPreviewAdapter(Context context,
			List<TBProductPreviewInfo> lists) {
		mLists = lists;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mTasks = new HashMap<String, TBProductPreviewAdapter.DownloadBitmapAsyncTask>();

		mWidth = 240;
		mHeight = 180;
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		// 设置图片缓存大小为maxMemory的1/6
		int cacheSize = maxMemory / 6;

		mLruCache = new LruCache<String, Bitmap>(cacheSize);

	}

	@Override
	public int getCount() {
		return mLists.size();
	}

	@Override
	public TBProductPreviewInfo getItem(int position) {
		return mLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.tb_produt_preview_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.mTitleTextView = (TextView) convertView
					.findViewById(R.id.product_title);
			viewHolder.mPriceTextView = (TextView) convertView
					.findViewById(R.id.product_price);
			viewHolder.mSalesTextView = (TextView) convertView
					.findViewById(R.id.product_sales);
			viewHolder.mPreviewImageView = (ImageView) convertView
					.findViewById(R.id.product_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		TBProductPreviewInfo info = getItem(position);
		viewHolder.bind(info);

		return convertView;
	}

	// 为ImageView设置图片(Image) 1 从缓存中获取图片 2 若图片不在缓存中则为其设置默认图片
	private void setImageForImageView(String imageUrl, ImageView imageView) {
		Bitmap bitmap = getBitmapFromLruCache(imageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.empty_photo);
			DownloadBitmapAsyncTask downloadBitmapAsyncTask = new DownloadBitmapAsyncTask(
					imageView);
			if (!mTasks.containsKey(imageUrl)) {
				mTasks.put(imageUrl, downloadBitmapAsyncTask);
				downloadBitmapAsyncTask.execute(imageUrl);
			}
		}
	}

	// 将图片存储到LruCache
	public void addBitmapToLruCache(String key, Bitmap bitmap) {
		if (getBitmapFromLruCache(key) == null) {
			mLruCache.put(key, bitmap);
		}
	}

	// 从LruCache缓存获取图片
	public Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	class ViewHolder {
		TextView mTitleTextView;
		TextView mPriceTextView;
		TextView mSalesTextView;
		ImageView mPreviewImageView;

		void bind(TBProductPreviewInfo info) {
			mTitleTextView.setText(info.getProductName());
			mPriceTextView.setText(String.valueOf(info.getPrice()));
			mSalesTextView.setText(info.getSalesLabel());
			mPreviewImageView.setTag(info.getPictureUrl());
			mPreviewImageView.setTag(mPreviewImageView.getId(),
					info.getProductName());
			setImageForImageView(info.getPictureUrl(), mPreviewImageView);
		}
	}

	// 异步下载图片的任务类
	class DownloadBitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
		private String mUrl;

		private final WeakReference<ImageView> imageViewReference;

		public DownloadBitmapAsyncTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			mUrl = params[0];
			Bitmap bitmap = downloadBitmap(mUrl, mWidth, mHeight);
			if (bitmap != null) {
				// 下载完后,将其缓存到LrcCache
				addBitmapToLruCache(mUrl, bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// 下载完成后,找到其对应的ImageView显示图片
			if (result != null) {
				ImageView imageView = imageViewReference.get();
				if(imageView != null){
					String url = (String) imageView.getTag();
					if (url != null && url.equals(mUrl)) {
						imageView.setImageBitmap(result);
						mTasks.remove(url);
					}
				}
			}
		}

		// 获取Bitmap
		private Bitmap downloadBitmap(String imageUrl, int width, int height) {
			Bitmap bitmap = null;
			HttpURLConnection httpURLConnection = null;
			try {
				URL url = new URL(imageUrl);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setConnectTimeout(5 * 1000);
				httpURLConnection.setReadTimeout(10 * 1000);
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.setDoInput(true);
				bitmap = Utilities.getBitmapThumbnail(BitmapFactory
						.decodeStream(httpURLConnection.getInputStream()),
						width, height);

			} catch (Exception e) {
				Log.e(LOG_TAG, "DownLoad Image Failure:");
				e.printStackTrace();
			} finally {
				if (httpURLConnection != null) {
					httpURLConnection.disconnect();
				}
			}
			return bitmap;
		}
	}
}
