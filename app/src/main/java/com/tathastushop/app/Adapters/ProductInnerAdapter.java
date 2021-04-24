package com.tathastushop.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tathastushop.app.Activities.BuyInnerActivity;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.Models.ProductModel;
import com.tathastushop.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class ProductInnerAdapter extends RecyclerView.Adapter<ProductInnerAdapter.MyViewHolder> {

    private ArrayList<ProductModel> downlineArrayList;
    private Context mContext;
    private long time;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView product_image;
        TextView product_name, product_price;
//        Button btnBuy;


        ArrayList<ProductModel> downlineArrayList = new ArrayList<ProductModel>();
        Context ctx;
        private FrameLayout container;

        MyViewHolder(View view, Context ctx, ArrayList<ProductModel> downlineArrayList) {
            super(view);
            this.downlineArrayList = downlineArrayList;
            this.ctx = ctx;
            product_image = (ImageView) view.findViewById(R.id.iv_product);
            product_name = (TextView) view.findViewById(R.id.product_desc);
            product_price = (TextView) view.findViewById(R.id.product_price);
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);
            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ProductModel productModel = this.downlineArrayList.get(position);
            Intent intent = new Intent(this.ctx, BuyInnerActivity.class);
            intent.putExtra("pname", productModel.getPname());
            intent.putExtra("desc", productModel.getDescription());
            intent.putExtra("pid", productModel.getProductid());
            intent.putExtra("mrp", productModel.getMrp());
            intent.putExtra("discount", productModel.getDiscount());
            intent.putExtra("price", productModel.getPrice());
            intent.putExtra("shipping",productModel.getShipping());
            intent.putExtra("pimage", productModel.getImage());
            intent.putExtra("pcode", productModel.getPcode());
            this.ctx.startActivity(intent);

        }
    }

    public ProductInnerAdapter(Context context, ArrayList<ProductModel> empViewArrayList) {
        mContext = context;
        this.downlineArrayList = empViewArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_inner_product_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, downlineArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        final ProductModel product = downlineArrayList.get(position);
//        Picasso.get()
//                .load(APIConfig.BASE_IMAGE +  "products/"+product.getImage())
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                .networkPolicy(NetworkPolicy.NO_CACHE)
////                .networkPolicy(NetworkPolicy.OFFLINE)
//                .into(holder.product_image);
//        Toast.makeText(mContext, "Position : "+position, Toast.LENGTH_SHORT).show();
        Glide.with(mContext).load(APIConfig.BASE_IMAGE +  "products/"+product.getImage())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.product_image);
        holder.product_price.setText("MRP " + product.getPrice());
        holder.product_name.setText(product.getPname()+"("+product.getPcode()+")");


    }

    private Calendar getDate(long time) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        return cal;
    }

    private void setAnimation(FrameLayout container, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        container.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return downlineArrayList.size();
    }

}
