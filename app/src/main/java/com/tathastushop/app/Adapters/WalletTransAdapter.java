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
import android.widget.TextView;
import com.tathastushop.app.Models.WalletHistoryModel;
import com.tathastushop.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class WalletTransAdapter extends RecyclerView.Adapter<WalletTransAdapter.MyViewHolder> {

    private ArrayList<WalletHistoryModel> WalletHistoryModelArrayList;
    private Context mContext;
    private long time;

    public class MyViewHolder extends RecyclerView.ViewHolder  {


        public TextView memberid, paid_date, amount, incometype, deposit ;

        ArrayList<WalletHistoryModel> WalletHistoryModelArrayList = new ArrayList<WalletHistoryModel>();
        Context ctx;
        private FrameLayout container;

        public MyViewHolder(View view, Context ctx, ArrayList<WalletHistoryModel> WalletHistoryModelArrayList) {
            super(view);
            this.WalletHistoryModelArrayList = WalletHistoryModelArrayList;
            this.ctx = ctx;

            memberid = (TextView) view.findViewById(R.id.tv_memberid);
            deposit = (TextView) view.findViewById(R.id.tv_deposit);
            incometype = (TextView) view.findViewById(R.id.tv_incometype);
            amount = (TextView) view.findViewById(R.id.tv_amount);
            paid_date = (TextView) view.findViewById(R.id.tv_paid_dt);
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);


        }


    }

    public WalletTransAdapter(Context context, ArrayList<WalletHistoryModel> empViewArrayList) {
        mContext = context;
        this.WalletHistoryModelArrayList = empViewArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_wallet_trans_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, WalletHistoryModelArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        final WalletHistoryModel model = WalletHistoryModelArrayList.get(position);

        holder.memberid.setText(String.valueOf(model.getMid()));
        holder.incometype.setText(model.getIncometype());
        holder.deposit.setText(model.getDeposit_withdraw());
//
        holder.amount.setText("₹" + model.getAmount());
//        holder.amount.setText("₹ " + WalletHistoryModel.getAmount());
//        holder.mobile.setText(WalletHistoryModel.getMobile());
//
        DateFormat formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Date date = null;
                date = (Date) formatter.parse(model.getDate());
                time = date.getTime();
            }
            holder.paid_date.setText(getDate(time).getTime().toLocaleString());


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
        return WalletHistoryModelArrayList.size();
    }

}
