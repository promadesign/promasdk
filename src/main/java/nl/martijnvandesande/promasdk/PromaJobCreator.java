package nl.martijnvandesande.promasdk;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

class PromaJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case PromaNotificationJob.TAG:
                return new PromaNotificationJob();
            default:
                return null;
        }
    }
}
