package com.owncloud.activity;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.MultiStatus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.owncloud.R;
import com.owncloud.login.LoginSelectionActivity;
import com.sufalam.WebdavMethodImpl;

public class SplashActivity extends WebdavMethodImpl {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {
			public void run() {

				if (isOnline()) {
					try {

						if ((!(pref.getString(PREF_USERNAME, null) == null))
								&& (!(pref.getString(PREF_PASSWORD, null) == null))
								&& (!(pref.getString(PREF_SERVERURL, null) == null))
								&& (!(pref.getString(PREF_BASEURL, null) == null))
								&& (!(pref.getString(PREF_UNLINK, null) == null))) {

							if (!pref.getString(PREF_UNLINK, null)
									.equals("true")) {
								url = pref.getString(PREF_SERVERURL, null);
								baseUrl = pref.getString(PREF_BASEURL, null);

								HostConfiguration config = new HostConfiguration();
								config.setHost(url);

								MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
								HttpConnectionManagerParams params = new HttpConnectionManagerParams();

								params.setMaxConnectionsPerHost(config, 5);
								httpClient = new HttpClient(manager);

								httpClient.setHostConfiguration(config);

								Credentials cred = new UsernamePasswordCredentials(
										pref.getString(PREF_USERNAME, null),
										pref.getString(PREF_PASSWORD, null));
								httpClient.getState().setCredentials(
										AuthScope.ANY, cred);

								MultiStatus ms = null;
								try {
									ms = listAll(url, httpClient);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (ms != null) {
									if ((!(pref.getString(PREF_PASSCODE, null) == null))
											&& (pref.getString(PREF_PASSCODE,
													null).equals("true"))) {
										startActivity(new Intent(
												getApplicationContext(),
												PassCodeActivity.class)
												.putExtra("From", "splash"));
										finish();
									} else {
										startActivity(new Intent(
												getApplicationContext(),
												DashBoardActivity.class)
												.putExtra("From", "PassCodeOff"));
										finish();
									}
								} else {
									webServerAlert();
								}
							} else {
								startActivity(new Intent(
										getApplicationContext(),
										LoginSelectionActivity.class));
								finish();
							}
						} else {
							startActivity(new Intent(getApplicationContext(),
									LoginSelectionActivity.class));
							finish();
						}
					} catch (Exception w) {
						// TODO Auto-generated catch block
						w.printStackTrace();
						Log.i("Splash Exception === > ", w.toString());
						Intent it = new Intent(getApplicationContext(),
								LoginSelectionActivity.class).putExtra("From",
								"Splash");
						startActivity(it);
						finish();

					}
				} else {
					WebNetworkAlert();
				}
				// startActivity(new
				// Intent(getApplicationContext(),LoginSelectionActivity.class));
				// finish();
			}
		}, 800);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		finish();
		super.onDestroy();
	}
}
