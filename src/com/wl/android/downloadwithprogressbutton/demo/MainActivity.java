/**   
 * 文件名：MainActivity.java   
 * 包名:com.wl.android.downloadwithprogressbutton.demo
 * @Author:wangliu94@163.com
 * @Description:TODO
 * 版本信息：V1.0 
 * 日期：2014-10-31   
 * Copyright Ecity(Wuhan) Corporation 2014    
 * 版权所有   
 *   
 */

package com.wl.android.downloadwithprogressbutton.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.wl.android.downloadwithprocessbutton.view.SubmitProcessButton;
import com.wl.android.downloadwithprogressbutton.R;
import com.wl.android.downloadwithprogressbutton.downloads.Downloads;
import com.wl.android.downloadwithprogressbutton.provider.DownloadManager;
import com.wl.android.downloadwithprogressbutton.provider.DownloadManager.Request;

/**
 * @类名：MainActivity
 * @description:
 * @author : wangliu94@163.com
 * @version : 2014-10-31 下午01:20:09
 */

public class MainActivity extends Activity implements OnClickListener {
	public static final String DOWNLOADFOLDER = "Trinea";
	public static final String DOWNLOAD_FILE_NAME = "MeiLiShuo.apk";
	public static final String APK_URL = "http://img.meilishuo.net/css/images/AndroidShare/Meilishuo_3.6.1_10006.apk";
	public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";

	private DownloadChangeObserver downloadObserver;
	private DownloadManager mDownloadManager;
	private BroadcastReceiver completeReceiver = null;
	private long downloadId = 0;
	private SubmitProcessButton bt_download;
	private Button bt_downloadList = null;
	private MyHandler handler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bt_download = (SubmitProcessButton) findViewById(R.id.bt_download);
		bt_download.setOnClickListener(this);
		
		bt_downloadList = (Button) findViewById(R.id.show_download_list_button);
		bt_downloadList.setOnClickListener(this);
		mDownloadManager = new DownloadManager(getContentResolver(),
				getPackageName());
		downloadObserver = new DownloadChangeObserver();
		completeReceiver = new CompleteReceiver();
		registerReceiver(completeReceiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		handler = new MyHandler();
	}

	private void startDownload(String url) {

		Uri srcUri = Uri.parse(url);
		DownloadManager.Request request = new Request(srcUri);
		request.setShowRunningNotification(false);
		request.setDestinationInExternalPublicDir(DOWNLOADFOLDER, "/");
		request.setDescription("");
		request.setTitle(DOWNLOAD_FILE_NAME);
		request.setVisibleInDownloadsUi(false);
//		request.setNotiClass(DownLoadListActivity.class.toString());
		downloadId = mDownloadManager.enqueue(request);

	}

	class CompleteReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			/**
			 * get the id of download which have download success, if the id is
			 * my id and it's status is successful, then install it
			 **/
			long completeDownloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);

			// if download successful, update local Path
			if (mDownloadManager.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
				String title = mDownloadManager
						.getTitleById(completeDownloadId);
				if (title != null) {
					Toast.makeText(context, "\"" + title + "\"数据下载完成",
							Toast.LENGTH_LONG).show();
				}

			}
		}
	}

	class DownloadChangeObserver extends ContentObserver {

		public DownloadChangeObserver() {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			updateView();

		}

	}

	public void updateView() {	
		handler.sendEmptyMessage(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/** observer download change **/
		getContentResolver().registerContentObserver(Downloads.CONTENT_URI,
				true, downloadObserver);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getContentResolver().unregisterContentObserver(downloadObserver);
	}

	class MyHandler extends Handler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int[] bytesAndStatus = mDownloadManager
					.getBytesAndStatus(downloadId);
			int status = bytesAndStatus[2];
			if (mDownloadManager.isDownLoaded(downloadId)) {
				bt_download.setProgress(100);

			} else if (status == DownloadManager.STATUS_RUNNING) {
				int percent = getDownLoadedPercent(bytesAndStatus[0],
						bytesAndStatus[1]);
				if (percent > 0) {
					bt_download.setProgress(percent);
				}

			} else if (status == DownloadManager.STATUS_PENDING) {

				bt_download.setText("连接中");

			} else if (status == DownloadManager.STATUS_PAUSED) {
				bt_download.setProgress(-1);
				bt_download.setText("继续下载");
			} else if (status == DownloadManager.STATUS_FAILED) {
				bt_download.setProgress(-1);
				bt_download.setText("重新下载");
			} else {
				// 如果任务不属于上面任何一个分支，则将按钮设为Normal状态，即点击开始下载
				bt_download.setProgress(0);
			}
		}

	}

	/**
	 * 此方法描述的是：获取文件下载的百分比
	 * 
	 * @param progress
	 * @param max
	 * @return return int
	 * @author : wangliu94@163.com
	 * @version : 2014-8-9 下午04:21:16
	 */

	public int getDownLoadedPercent(long progress, long max) {
		int rate = 0;
		if (progress <= 0 || max <= 0) {
			rate = 0;
		} else if (progress >= max) {
			rate = 100;
		} else {
			rate = (int) ((double) progress / max * 100);
		}
		return rate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_download:
			int[] bytesAndStatus = mDownloadManager
					.getBytesAndStatus(downloadId);
			int status = bytesAndStatus[2];
			switch (status) {
			/**
			 * 仅仅依靠STATUS_SUCCESSFUL不足以判断文件已经下载成功，有可能本地下载文件已经被意外删除， 所以需要加入文件位置判断
			 */
			case DownloadManager.STATUS_SUCCESSFUL:
				if (mDownloadManager.isDownLoaded(downloadId)) {
					Toast.makeText(MainActivity.this, "点击编辑", Toast.LENGTH_LONG).show();
				}

				break;
			case DownloadManager.STATUS_RUNNING:
				Toast.makeText(MainActivity.this, "下载暂停", Toast.LENGTH_LONG).show();
				mDownloadManager.pauseDownload(downloadId);
				break;
			case DownloadManager.STATUS_PENDING:
				Toast.makeText(MainActivity.this, "下载取消", Toast.LENGTH_LONG).show();
				mDownloadManager.remove(downloadId);
				break;
			case DownloadManager.STATUS_FAILED:
				Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_LONG).show();
				mDownloadManager.restartDownload(downloadId);
				break;
			case DownloadManager.STATUS_PAUSED:
				Toast.makeText(MainActivity.this, "继续下载", Toast.LENGTH_LONG).show();
				mDownloadManager.resumeDownload(downloadId);
				break;
			default:
				startDownload(APK_URL);
				break;

			}

			break;
		case R.id.show_download_list_button:
			Intent intent = new Intent();
			break;

		default:
			break;
		}

	}

	 
	    /* (non-Javadoc)   
	     * @see android.app.Activity#onDestroy()   
	     */   
	    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(completeReceiver);
	}
	
	

}
