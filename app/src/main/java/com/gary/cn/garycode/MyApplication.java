package com.gary.cn.garycode;

import android.app.Application;

import com.gary.cn.garycode.utils.CrashHandler;


/**
 * The Class MyApplication.
 */
public class MyApplication extends Application {

	/** The instance. */
	private static MyApplication mApp;
	

	@Override
	public void onCreate() {
		super.onCreate();
		
		//initImageLoader(this);

		mApp = this;
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}

	/**
	 * MyApplication Gets the single instance of MyApplication.
	 * 
	 * @return single instance of MyApplication
	 */
	public static MyApplication getInstance() {
		return mApp;
	}


	
}
