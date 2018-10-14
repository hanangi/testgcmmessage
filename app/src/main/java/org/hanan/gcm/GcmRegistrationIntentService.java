package org.hanan.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hanang on 26/05/16.
 */
public class GcmRegistrationIntentService extends IntentService {

	private static final String TAG = "GcmRegistration";
	private final static String IP = "192.168.2.108";

	public GcmRegistrationIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {


		try {
			// R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
			InstanceID instanceID = InstanceID.getInstance(this);
			instanceID.deleteInstanceID();

			//String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
			String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
			new TokenSender(token).execute();

			// Subscribe to topic channels
			//subscribeTopics(token);
		} catch (Exception e) {
			Log.e(TAG,"Failed to complete token refresh: " + e.getMessage(), e);
			// If an exception happens while fetching the new token or updating our registration data
			// on a third-party server, this ensures that we'll attempt the update at a later time.
		}
	}

	private class TokenSender extends AsyncTask<String, Void, String> {

		private final String token;

		private TokenSender(String token) {
			this.token = token;
		}

		@Override
		protected String doInBackground(String... params) {
			HashMap<String, String> map = new HashMap<>();
			map.put("isGcm" ,"false");
			map.put("token" ,token);
			String response = performPostCall("http://" + IP + ":8080/api/v1/token", map);
			Log.i(TAG, "send to server " + response);
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}

		private String  performPostCall(String requestURL, HashMap<String, String> postDataParams) {

			URL url;
			String response = "";
			try {
				url = new URL(requestURL);

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(15000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);


				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(getPostDataString(postDataParams));

				writer.flush();
				writer.close();
				os.close();
				int responseCode=conn.getResponseCode();

				if (responseCode == HttpsURLConnection.HTTP_OK) {
					String line;
					BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
					while ((line=br.readLine()) != null) {
						response+=line;
					}
				}
				else {
					response="";

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();
			boolean first = true;
			for(Map.Entry<String, String> entry : params.entrySet()){
				if (first)
					first = false;
				else
					result.append("&");

				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}

			return result.toString();
		}
	}
}
