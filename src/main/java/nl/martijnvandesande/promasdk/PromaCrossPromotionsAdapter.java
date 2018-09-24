package nl.martijnvandesande.promasdk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by martijn.vandersande on 1/4/17.
 */



public class PromaCrossPromotionsAdapter extends RecyclerView.Adapter<PromaCrossPromotionsAdapter.ViewHolder> {
    private ArrayList<PromaCrossPromotionsModel> dataset;
    private Context context;
    private PromaCrossPromotions main;

    public PromaCrossPromotionsAdapter(Context context) {
        this.context = context;
    }

    public PromaCrossPromotionsAdapter(Context context, ArrayList<PromaCrossPromotionsModel> dataset, PromaCrossPromotions main) {
        this.dataset = dataset;
        this.main = main;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.promotion_row, viewGroup, false);
        view.setOnClickListener(this.main);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.name.setText(dataset.get(i).name);
//        viewHolder.icon.setImageBitmap(dataset.get(i).icon);

        Glide.with(this.context)
                .load(dataset.get(i).icon)
                .asBitmap()
                .into(viewHolder.icon);
//        .into(new BitmapImageViewTarget(viewHolder.imagen) {
//            @Override
//            protected void setResource(Bitmap resource) {
//                super.setResource(resource);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ImageView icon;
        public ViewHolder(View view) {
            super(view);

            name = (TextView)view.findViewById(R.id.name);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }

}


