package com.tathastushop.app.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.Models.OrdersModel;
import com.tathastushop.app.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private ArrayList<OrdersModel> ordersArrayList;
    private Context mContext;
    private long time;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView pname,pcode, qty, price, percent, nett, date, total, dateLabel;
        ImageView status, product;


        ArrayList<OrdersModel> ordersArrayList = new ArrayList<OrdersModel>();
        Context ctx;
        private FrameLayout container;

        MyViewHolder(View view, Context ctx, ArrayList<OrdersModel> ordersArrayList) {
            super(view);
            this.ordersArrayList = ordersArrayList;
            this.ctx = ctx;
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);

            status = (ImageView) view.findViewById(R.id.iv_status);
            product = (ImageView) view.findViewById(R.id.iv_product);

            pname = (TextView) view.findViewById(R.id.tv_pname);
            pcode = (TextView) view.findViewById(R.id.tv_pcode);
            qty = (TextView) view.findViewById(R.id.tv_qty);
            total = (TextView) view.findViewById(R.id.tv_total);
            price = (TextView) view.findViewById(R.id.tv_price);
            percent = (TextView) view.findViewById(R.id.tv_percent);
            nett = (TextView) view.findViewById(R.id.tv_nett);
            date = (TextView) view.findViewById(R.id.tv_reg_dt);
            dateLabel = (TextView) view.findViewById(R.id.date_label);

        }

    }

    public OrderAdapter(Context context, ArrayList<OrdersModel> ordersArrayList) {
        mContext = context;
        this.ordersArrayList = ordersArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_order_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, ordersArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        final OrdersModel model = ordersArrayList.get(position);


        Picasso.get().load(APIConfig.BASE_IMAGE + "/products/" + model.getImage())
                .placeholder(R.drawable.no_image)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.product);

        String dateOrder = null;
        if (model.getStatus().equals("1")) {
            holder.status.setImageResource(R.drawable.success_icon);
            holder.dateLabel.setText("Delivered Date");
            dateOrder = model.getDelivered_date();
        } else {
            holder.status.setImageResource(R.drawable.pending);
            holder.dateLabel.setText("Order Date");
            dateOrder = model.getOrder_date();
        }

        holder.pname.setText(model.getPname());
        holder.pcode.setText("Product Code : "+model.getPcode());
        holder.qty.setText("Quantity : "+model.getQty());
        holder.total.setText("₹"+model.getTotal());

        holder.price.setText("Price : ₹" + model.getPrice());
        holder.percent.setText(model.getDiscount() + "%");
        holder.nett.setText("₹" + model.getNett());

        DateFormat formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Date date = null;
                date = (Date) formatter.parse(dateOrder);
                time = date.getTime();
            }
            holder.date.setText(getDate(time).getTime().toLocaleString());


        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        return ordersArrayList.size();
    }

}
