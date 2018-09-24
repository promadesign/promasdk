package nl.martijnvandesande.promasdk;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/**
 * Created by martijn.vandersande on 6/29/17.
 */

public class PromaAds {

    private Context context;



    private PromaActivity activity;
    private InterstitialAd mInterstitialAd;
    public RewardedVideoAd mRewardedVideoAd;
    private String rewardVideoId = "";

    public AdView adview;
    public Boolean backFromAd = false;
    private AdRequest adRequest;
    private ViewGroup insertIntoContainer;

    private static String TEST_DEVICE = "88C2944D6CB65567F40005DA460E97E4";


    public PromaAds(Context context, ViewGroup insertIntoContainer){
        this.context = context;
        MobileAds.initialize(context,"ca-app-pub-1306569564337830~6280418502");
        this.activity = (PromaActivity) context;
        this.insertIntoContainer = insertIntoContainer;

        this.startInterstitialListeners();
        this.startRewardVideoListeners();

        this.loadRewardedVideoAd();

    }

    public void setup(){

        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice(TEST_DEVICE)
                .build();


//        this.startInterstitial();

        /**
         * add the bottom banner view layout to the view
         */
        this.preInitAd();

    }

    public void startInterstitial(){

        mInterstitialAd.loadAd(adRequest);
    }

    private void preInitAd(){

        adview = new AdView(context);
        adview.setAdSize(AdSize.BANNER);
        adview.setAdUnitId(context.getString(R.string.banner_ad_unit_id));
        adview.setId(PromaUtils.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);

        adview.setLayoutParams(params);

        insertIntoContainer.addView(adview);

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                        .addTestDevice(TEST_DEVICE)
                        .build());
    }

    public void showRewardVideo(String id){
        if (mRewardedVideoAd.isLoaded()) {
            backFromAd = true;
            rewardVideoId = id;
            mRewardedVideoAd.show();
        }
    }

    private void startRewardVideoListeners(){
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                PromaActivity act = (PromaActivity)context;
                act.onRewarded(rewardVideoId);

//                Toast.makeText(context, "onRewarded! id "+rewardVideoId+" currency: " + rewardItem.getType() + "  amount: " +
//                        rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
                rewardVideoId = "";
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
    }

    private void startInterstitialListeners(){

        //interstitial
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.banner_ad_interstitial_id));

        try {

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    activity.listener_interstitial_loaded();
                    System.out.println("====== ad loadeddddd");

                    if(activity.loadingScreenTimerDone) {
                        if (activity.adNeedsToDisplay) {
                            activity.adNeedsToDisplay = false;
                            activity.continueAfterAdload();
                        }
                    }
                }
                @Override
                public void onAdOpened() {
                    backFromAd = true;
                }

                @Override
                public void onAdClosed() {

                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    backFromAd = true;

                    activity.hideLoadingScreen();
                }
            });
        }catch(Exception e){

        }



    }

    public void showBottomAd(){
//        AdView adview = (AdView) activity.findViewById(R.id.adView);
        adview.loadAd(adRequest);
    }

    public void showInterstitial(){

        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }

    }

//    public void show(){
////        loadAndShowBottomAd();
//
//        /**
//         * the load eventually also shows the interstitial
//         */
//        loadInterstitial();
//        //showInterstitial();
//    }

    public Boolean isInterstitialLoaded(){
        return mInterstitialAd.isLoaded();
    }

}
