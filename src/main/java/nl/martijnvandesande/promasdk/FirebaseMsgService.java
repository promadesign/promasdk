package nl.martijnvandesande.promasdk;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMsgService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("mylog", "refresed token"+ token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //remoteMessage.getNotification().la;
        //Log.d("mylog", remoteMessage.getNotification().getTitle());
    }
}


