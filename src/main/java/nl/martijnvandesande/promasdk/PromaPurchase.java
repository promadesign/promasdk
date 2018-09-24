package nl.martijnvandesande.promasdk;

import android.content.Context;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

/**
 * Created by martijn.vandersande on 6/23/17.
 */

public class PromaPurchase implements BillingProcessor.IBillingHandler{

    private Context context;
    private Boolean debug = false;

    public BillingProcessor bp;

    public PromaPurchase(Context context, String LICENSE_KEY, String MERCHANT_ID){

        this.context = context;

        bp = new BillingProcessor(context, LICENSE_KEY, MERCHANT_ID, this);
//        bp = new BillingProcessor(context, LICENSE_KEY, this);

    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public boolean isPurchased(String sku){
        if(debug){
            return true;
        }
        return bp.isPurchased(sku);
    }

    public void purchase(String SKU){

        if(debug){
            PromaActivity ac = (PromaActivity)context;
            ac.onProductPurchased(SKU);
            return;
        }

        bp.purchase((PromaActivity)context, SKU);

    }


    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * handle products that are bought
     * @param productId
     * @param details
     */
    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        PromaActivity ac = (PromaActivity)context;
        ac.onProductPurchased(productId);
    }

    /*
    * Called when purchase history was restored and the list of all owned PRODUCT ID's
    * was loaded from Google Play
    */
    @Override
    public void onPurchaseHistoryRestored() {
//        showToast("onPurchaseHistoryRestored");
        for(String sku : bp.listOwnedProducts()){

        }

//            Log.d("TASER", "Owned Managed Product: " + sku);
//                for(String sku : bp.listOwnedSubscriptions())
//                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
//                updateTextViews();

//        System.out.println("------------------------------wtfffffff");
//        System.out.println(bp.isPurchased("android.test.purchased"));
    }

    public void release(){
        bp.release();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
//        showToast("onBillingError: " + Integer.toString(errorCode));
    }

    @Override
    public void onBillingInitialized() {
        PromaActivity ac = (PromaActivity)context;
        ac.onBillingInitialized();
    }
}
