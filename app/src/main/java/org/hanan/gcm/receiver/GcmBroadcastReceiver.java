package org.hanan.gcm.receiver;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
     	Bundle extras = intent.getExtras();

	}


}
