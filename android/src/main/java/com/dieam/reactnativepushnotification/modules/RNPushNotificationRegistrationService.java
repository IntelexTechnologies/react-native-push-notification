package com.dieam.reactnativepushnotification.modules;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import static com.dieam.reactnativepushnotification.modules.RNPushNotification.LOG_TAG;

public class RNPushNotificationRegistrationService extends IntentService {
   
    private static final String TAG = "RNPushNotification";

    public RNPushNotificationRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String SenderID = intent.getStringExtra("senderID");
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(SenderID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            sendRegistrationToken(token);
        } catch (Exception e) {
            String errorStr = TAG + " failed to process intent " + intent;
            errorStr = replaceCRLFWithUnderscore(errorStr);
            Log.e(LOG_TAG, errorStr, e);
        }
    }

    private void sendRegistrationToken(String token) {
        Intent intent = new Intent(this.getPackageName() + ".RNPushNotificationRegisteredToken");
        intent.putExtra("token", token);
        sendBroadcast(intent);
    }

    private String replaceCRLFWithUnderscore(String value) {
        // Replace any carriage returns and line feeds with an underscore to prevent log injection attacks.
        // Ref: ESAPI library https://github.com/javabeanz/owasp-security-logging/blob/master/owasp-security-logging-common/src/main/java/org/owasp/security/logging/Utils.java
		return value.replace('\n', '_').replace('\r', '_');
	}
}
