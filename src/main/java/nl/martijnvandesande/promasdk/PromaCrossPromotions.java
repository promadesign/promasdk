package nl.martijnvandesande.promasdk;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by martijn.vandersande on 11/2/16.
 */


//android:layout_height="?android:attr/listPreferredItemHeight"

public class PromaCrossPromotions implements View.OnClickListener{

    private final float version = 0.5f;
    private long cacheExpiration = 43200;

    //data:image/png;base64,
    private static final String expand_button = "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";
    private static final String expand_button_active = "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";

    private FirebaseRemoteConfig remoteConfig;
    private PromaCrossPromotionsAdapter recyleviewAdapter;
    private ArrayList<PromaCrossPromotionsModel> dataset = new ArrayList<>();
    private RecyclerView recyclerView;
    private int backgroundColor = Color.parseColor("#aaaaaa");

    private boolean debug = false;

    Activity context;
    private ScrollView layout;
    private ImageButton button;
    private boolean isCollapsed = true;
    private int orriginalHeight;

    private int expandTo;

    public boolean initialized = false;
    private HashMap<String, Object> default_config = new HashMap<>();

    private View insertAbove;

    public PromaCrossPromotions(Activity context, ImageButton button){
        this(context, null, button);
    }

    public PromaCrossPromotions(Activity context, View insertAbove, ImageButton button){
        this.button = button;

        this.insertAbove = insertAbove;

        this.context = context;

        orriginalHeight = 0;

        default_config.put("crosspromotions_enabled", "false");
        default_config.put("promotions", "[]");

        byte[] imageByteArray = Base64.decode(expand_button, Base64.DEFAULT);
//
        Glide.with(context)
                .load(imageByteArray)
                .asBitmap()
                .into(button);

    }


    private void setupUI(){

        ui_createScrollview();

        ui_createTableLayout();

        this.insertIntoContainer.addView(layout);

        ui_buttonPlacement();

    }

    private void ui_buttonPlacement(){

        ImageButton btn = (ImageButton)context.findViewById(R.id.crosspromotionButton);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btn.getLayoutParams();

        params.addRule(RelativeLayout.ABOVE, layout.getId());

        btn.setLayoutParams(params);

    }

    private void ui_createTableLayout(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        RecyclerView rv = new RecyclerView(context);
        rv.setLayoutManager(new GridLayoutManager(this.context, 3));
        rv.setLayoutParams(params);
        layout.addView(rv);

    }

    private void ui_createScrollview(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);

        if(insertAbove != null){
            params.addRule(RelativeLayout.ABOVE, insertAbove.getId());
        }

        params.setMargins(0,0,0,90);

        layout = new ScrollView(context);

        layout.setId(PromaUtils.generateViewId());

        layout.setBackgroundColor(backgroundColor);
        layout.setLayoutParams(params);
    }

    private ViewGroup insertIntoContainer;

    public void initialize(ViewGroup insertIntoContainer){

        this.insertIntoContainer = insertIntoContainer;

        if(initialized){
            return;
        }
        initialized = true;
        setupUI();

//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        int screenHeight = displayMetrics.heightPixels;
        int screenHeight = this.insertIntoContainer.getMeasuredHeight();
        //adview height

        int adheight = 0;
        if(insertAbove != null){
            adheight = insertAbove.getMeasuredHeight();
        }

        int buttonheight = button.getMeasuredHeight();

        expandTo = (screenHeight - adheight)-buttonheight - 400; //was 130

        this.addButtonAction();

        remoteConfig = FirebaseRemoteConfig.getInstance();

        if(debug){
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(debug)
                    .build();
            remoteConfig.setConfigSettings(configSettings);
            //expire the cache immediately for development mode.

            cacheExpiration = 0;

        }

        remoteConfig.setDefaults(default_config);

        remoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        remoteConfig.activateFetched();

                            if(remoteConfig.getString("crosspromotions_enabled").equals("true")) {
                                context.findViewById(R.id.crosspromotionButton).setVisibility(View.VISIBLE);
                                loadPromotions();
                            }
                    } else {
                    }
                }
            });
    }

    private void loadPromotions(){

        String promotions = remoteConfig.getString("promotions");

        try {

            JSONObject reader = new JSONObject(promotions);

            JSONArray jsonArray = reader.optJSONArray("promotions");

            recyclerView = (RecyclerView)layout.getChildAt(0);

            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String icon = jsonObject.getString("icon");
                final String link = jsonObject.getString("link");
                final String name = jsonObject.getString("name");

                /**
                 * skip if the link is the link(packagename) of this app
                 */
                if(link.equals(this.context.getApplicationContext().getPackageName())){
                    continue;
                }

                dataset.add(new PromaCrossPromotionsModel(link, name, icon));

            }

            recyleviewAdapter = new PromaCrossPromotionsAdapter(this.context, dataset, this);

            recyclerView.setAdapter(recyleviewAdapter);

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("======================== parse error "+e);

        }

    }

    private void openStoreOrApp(String packageName){

        String storeType = getStore();
//        System.out.println("====="+storeType);
        String uri;

        if(storeType.equals("com.amazon.venezia")){
            uri = "amzn://apps/android?p="+packageName;
            Answers.getInstance().logCustom(new CustomEvent("Promotion clicked amazon"));
        }else{
            uri = "market://details?id=" + packageName;
        }

        try {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//            if (launchIntent != null) {
            context.startActivity(launchIntent);//null pointer check in case package name was not found
//            }
//            System.out.println("====end try");
        }catch(Exception e){
//            System.out.println("catch");
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        }




    }

    public String getStore() {
        String result = "unknown";

        try {
            result = context.getPackageManager()
                    .getInstallerPackageName(context.getPackageName());
        } catch (Throwable e) {

        }

        if(result == null){
            result = "unknown";
        }

        return result;
    }

    private void addButtonAction(){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isCollapsed){
                    expand();
                    isCollapsed = false;
                }else{
                    collapse();
                    isCollapsed = true;
                }


            }
        });

    }

    public void expand(){

        final ViewGroup.LayoutParams lParams = layout.getLayoutParams();

        ValueAnimator va = ValueAnimator.ofInt(orriginalHeight, expandTo);
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                lParams.height = value;
                layout.setLayoutParams(lParams);

            }
        });
        va.start();

        byte[] imageByteArray = Base64.decode(expand_button_active, Base64.DEFAULT);
//
        Glide.with(context)
                .load(imageByteArray)
                .asBitmap()
                .into(button);

    }

    public void collapse(){

        final ViewGroup.LayoutParams lParams = layout.getLayoutParams();

        ValueAnimator va = ValueAnimator.ofInt(expandTo, orriginalHeight);
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                lParams.height = value;
                layout.setLayoutParams(lParams);

            }
        });
        va.start();

        byte[] imageByteArray = Base64.decode(expand_button, Base64.DEFAULT);
//
        Glide.with(context)
                .load(imageByteArray)
                .asBitmap()
                .into(button);

    }

    @Override
    public void onClick(View v) {
        int itemPosition = recyclerView.getChildLayoutPosition(v);
        try {
            openStoreOrApp(dataset.get(itemPosition).link);

            Answers.getInstance().logCustom(new CustomEvent("Promotion clicked")
                    .putCustomAttribute("Game", dataset.get(itemPosition).name)
                    );

        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + dataset.get(itemPosition).link)));
        }

    }

}
