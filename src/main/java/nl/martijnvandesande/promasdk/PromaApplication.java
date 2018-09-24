package nl.martijnvandesande.promasdk;

import android.app.Application;

import com.evernote.android.job.JobManager;

public class PromaApplication extends Application {

    public boolean enableNotifications = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if(this.enableNotifications) {
            JobManager.create(this).addJobCreator(new PromaJobCreator());
        }
    }

}
