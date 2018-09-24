package nl.martijnvandesande.promasdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by martijn.vandersande on 7/17/17.
 */

public class PromaActivity extends AppCompatActivity {
    private final String version = "0.8";
    public Boolean debug = false;

    private ViewGroup insertIntoContainer;
    public Boolean loadingScreenTimerDone = false;
    public Boolean adNeedsToDisplay = false;

    /**
     * config
     */
    public boolean enableCrossPromotions = false;
    public boolean enableBottomAd = false;
    public boolean enableInterstitial = false;
    public boolean purchaseEnabled = false;

    /**
     * config special option to quickly buy a remove ads SKU
     */
    public String purchaseRemoveAdsSKU;


    public PromaAds promaAds;
    private PromaCrossPromotions promaCrossPromotions;
    public PromaPurchase promaPurchase;

    public String MERCHANT_ID;
    public String LICENSE_KEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String value;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            value = bundle.getString("pushnot");

            System.out.println("=========martijn");
            System.out.println(value);
            Answers.getInstance().logCustom(new CustomEvent("Pushnot opened")
                    .putCustomAttribute("type", value)
            );
        }

    }

    public void setup(){
        ViewGroup vg = (ViewGroup)(getWindow().getDecorView().getRootView());
        setup(vg);
    }

    public void setup(ViewGroup insertIntoContainer){
        this.insertIntoContainer = insertIntoContainer;

        /**
         * show the loader
         */
        ViewGroup vg = (ViewGroup)(getWindow().getDecorView().getRootView());


        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View loading = inflater.inflate(R.layout.loading_screen, null, true);

        vg.addView(loading);

        /**
         * load the purchase library
         */
//        promaPurchase = new PromaPurchase(this);
//        promaPurchase.setDebug(debug);

        /**
         * load the ads
         */
        //@TODO if interstitial enabled then run this code

        promaAds = new PromaAds(this, insertIntoContainer);
        promaAds.setup();

        if(purchaseRemoveAdsSKU == null) {
            promaAds.showBottomAd();
        }

        if(enableCrossPromotions){
            try {
                if(enableBottomAd) {
                    promaCrossPromotions = new PromaCrossPromotions(this, promaAds.adview, (ImageButton) findViewById(R.id.crosspromotionButton));
                }else{
                    promaCrossPromotions = new PromaCrossPromotions(this, (ImageButton) findViewById(R.id.crosspromotionButton));
                }
            }catch(Exception e){

            }
        }

        PromaApplication app = (PromaApplication) getApplication();

        if(app.enableNotifications){
            PromaNotificationJob.schedulePeriodic(getApplicationContext(), true);
        }

    }

    /**
     * callback when an product is purchased
     * @param productId
     */
    public void onProductPurchased(String productId){

    }

    /**
     * callback when the billing library is initialized
     * at this point you can check if someone has the certain product purchased
     */
    public void onBillingInitialized(){

        if(purchaseRemoveAdsSKU != null){

            final boolean isPurchased = promaPurchase.isPurchased(purchaseRemoveAdsSKU);

            if(isPurchased == false){
                promaAds.showBottomAd();
            }else{
                enableInterstitial = false;
            }

            startUpFlow();

        }

    }

    public void listener_interstitial_loaded(){
//        promaAds.showInterstitial();
        System.out.println("========== listener_interstitial_loaded");

    }

//    Handler h = new Handler();
//    int delay = 1500; //1.5 seconds
//    Runnable runnable;

    public void showLoadingScreen(){
        View loader = findViewById(R.id.loading_screen);
        loader.setVisibility(View.VISIBLE);
    }
    public void hideLoadingScreen(){
        View loader = findViewById(R.id.loading_screen);
        loader.setVisibility(View.INVISIBLE);
    }

    public void startUpFlow(){
        /**
         * if we just came from an interstitial
         * it again triggers this onResume.
         * so we skip the ad show
         */
        if(promaAds.backFromAd){
            promaAds.backFromAd = false;
            hideLoadingScreen();
        }else{
            promaAds.startInterstitial();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     * this var is used in the PromaAds onAdLoaded
                     *
                     */
                    loadingScreenTimerDone = true;
                    System.out.println("====== looper done");

                    if (enableInterstitial) {

                        if (promaAds.isInterstitialLoaded()) {
                            System.out.println("====== ad is loaded");
                            continueAfterAdload();

                        } else {
                            System.out.println("====== adNeedsToDisplay");
                            adNeedsToDisplay = true;

                        }

                    } else {
                        hideLoadingScreen();
                    }

                }
            }, 2500);
        }
    }

    @Override
    protected void onPause() {
        promaAds.mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        promaAds.mRewardedVideoAd.resume(this);
        super.onResume();

        /**
         * this is where it startsup
         * but only if the special remove ad sku is not used
         */
        if(purchaseRemoveAdsSKU == null){
            showLoadingScreen();
            startUpFlow();
        }else{
            showLoadingScreen();
            System.out.println("========yesssss");
            promaPurchase = new PromaPurchase(this, LICENSE_KEY, MERCHANT_ID);
            promaPurchase.setDebug(debug);
            //promaPurchase.purchase("noads");
        }

    }

    /**
     * this is called from 2.5 second loading screen or onAdLoaded from PromaAds
     * at this point the ads are loaded successfully
     */
    public void continueAfterAdload(){
        System.out.println("====== conti;hne");
        promaAds.showInterstitial();
        loadingScreenTimerDone = false;

        hideLoadingScreen();

        /**
         * request a new insterstitial
         */
//        promaAds.fuck();
    }

    @Override
    public void onDestroy() {
        promaAds.mRewardedVideoAd.destroy(this);
        if (promaPurchase != null) {
            promaPurchase.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!promaPurchase.bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onRewarded(String id){}

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
;
        try {
            if(enableCrossPromotions) {
                promaCrossPromotions.initialize(insertIntoContainer);
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
        this.promaPurchase.setDebug(debug);
    }
}
