package nl.martijnvandesande.promasdk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Calendar;
import java.util.Random;

import nl.martijnvandesande.promasdk.PromaActivity;

public class PromaNotificationJob extends Job {

    static final String TAG = "show_notification_job_tag";
    String CHANNEL_ID = "channel_1";
//    List<String> notificationList = Arrays.asList(
//            "Battery is fully charged!",
//            "Are u ready to TASE someone?",
//            "Let the power be with you",
//            "I want to feel used...! BZZZT",
//            "Dont forget to PRANK!"
//    );
//    String[] notificationList;


    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        saveCurrentJob(getContext(), 0);

        String[] messages = getContext().getResources().getStringArray(R.array.pushnotification_messages);


        //Random rand = ;
        String text = messages[new Random().nextInt(messages.length)];
        //String text = notificationList.get(rand.nextInt(notificationList.size()));


        createNotificationChannel();

        String type = "weekly";
        if(!getIsDailyPast(getContext())){
            type = "daily";
        }

        Notification notification = buildNotification(text, type);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(new Random().nextInt(), notification);

        //op dit punt is de daily en de weekly al gescheduled
        //cancel de job
        if(getIsDailyPast(getContext())){
            cancelJob(getCurrentJob(getContext()));
            return Result.SUCCESS;
        }

        schedulePeriodic(getContext(), false);

        if(!getIsDailyPast(getContext())){
            setIsDailyPast(getContext(), true);
        }

        return Result.SUCCESS;
    }

    public static int schedulePeriodic(Context context, boolean isDaily) {

        int currentjob = getCurrentJob(context);

        if(currentjob != 0){
            cancelJob(currentjob);
        }

        Calendar cal = Calendar.getInstance();

        //daily schedule just show it once
        if(isDaily){
            setIsDailyPast(context, false);
            cal.add(Calendar.SECOND, 10);
        }else{
            //weekly schedule
            cal.add(Calendar.SECOND, 20);
        }

        Long futurems = cal.getTimeInMillis();
        Long finalms = futurems - System.currentTimeMillis();
        int jobId = new JobRequest.Builder(PromaNotificationJob.TAG)
                .setExact(finalms) //10_000L
                .build()
                .schedule();

        saveCurrentJob(context, jobId);



        return jobId;
    }

    private static SharedPreferences getPreferences(Context context){

        return context.getSharedPreferences(context.getPackageName()+".jobs", Context.MODE_PRIVATE);
    }

    private static int getCurrentJob(Context context){
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getInt("currentjob", 0);
    }

    private static void saveCurrentJob(Context context, int jobId){
        SharedPreferences sharedPref = getPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("currentjob", jobId);
        editor.commit();
    }

    private static boolean getIsDailyPast(Context context){
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getBoolean("dailydone", false);
    }

    private static void setIsDailyPast(Context context, boolean dailydone){
        SharedPreferences sharedPref = getPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("dailydone", dailydone);
        editor.commit();
    }


    static void cancelJob(int jobId){
        JobManager.instance().cancel(jobId);
    }

    private Notification buildNotification(String text, String type){

        Intent intent = new Intent(getContext(), PromaActivity.class);
        intent.putExtra("pushnot", type);

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Security Taser")
                .setContentText(text)
                //.setVisibility(Notification.VISIBILITY_PRIVATE)
                .setContentIntent(pendingIntent)
                .setLocalOnly(true)
                .setAutoCancel(true) //auto removes when tabbed
                //.setCategory(NotificationCompat.) // dit niet gebruiken
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return mBuilder.build();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channel_name = "Info";
            String channel_description = "Reminder to use the app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channel_name, importance);
            //channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setDescription(channel_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}




