package com.dieam.reactnativepushnotification.modules;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

import static com.dieam.reactnativepushnotification.modules.RNPushNotification.LOG_TAG;

/**
 * Set alarms for scheduled notification after system reboot.
 */
public class RNPushNotificationBootEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {    
        Log.i(LOG_TAG, "RNPushNotificationBootEventReceiver loading scheduled notifications");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(RNPushNotificationHelper.PREFERENCES_KEY, Context.MODE_PRIVATE);
            Set<String> ids = sharedPreferences.getAll().keySet();

            Application applicationContext = (Application) context.getApplicationContext();
            RNPushNotificationHelper rnPushNotificationHelper = new RNPushNotificationHelper(applicationContext);

            for (String id : ids) {
                try {
                    String notificationAttributesJson = sharedPreferences.getString(id, null);
                    if (notificationAttributesJson != null) {
                        RNPushNotificationAttributes notificationAttributes = RNPushNotificationAttributes.fromJson(notificationAttributesJson);

                        if (notificationAttributes.getFireDate() < System.currentTimeMillis()) {
                            String infoStr = "RNPushNotificationBootEventReceiver: Showing notification for " +
                            notificationAttributes.getId();
                            infoStr = replaceCRLFWithUnderscore(infoStr);
                            Log.i(LOG_TAG, infoStr);
                            rnPushNotificationHelper.sendToNotificationCentre(notificationAttributes.toBundle());
                        } else {
                            String infoStr ="RNPushNotificationBootEventReceiver: Scheduling notification for " +
                            notificationAttributes.getId();
                            infoStr = replaceCRLFWithUnderscore(infoStr);
                            Log.i(LOG_TAG, infoStr);
                            rnPushNotificationHelper.sendNotificationScheduledCore(notificationAttributes.toBundle());
                        }
                    }
		}catch (Exception e) {
		    Log.e(LOG_TAG, "Problem with boot receiver loading notification " + id, e);
		}
		
	    }
        }
    }

    private String replaceCRLFWithUnderscore(String value) {
        // Replace any carriage returns and line feeds with an underscore to prevent log injection attacks.
        // Ref: ESAPI library https://github.com/javabeanz/owasp-security-logging/blob/master/owasp-security-logging-common/src/main/java/org/owasp/security/logging/Utils.java
		return value.replace('\n', '_').replace('\r', '_');
	}
}
