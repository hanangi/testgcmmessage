package org.hanan.gcm.receiver;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {


	private static final String TAG = GcmBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
     	Bundle extras = intent.getExtras();

		Map<String, Object> payload = new HashMap<>();
		for (String key : extras.keySet()) {
			Object value = extras.get(key);
			payload.put(key, value.toString());
		}

		Log.i(TAG, "Message payload: " + payload);

	}
}
