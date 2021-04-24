package com.tathastushop.app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tathastushop.app.Models.TransactionModel;
import com.tathastushop.app.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class TransAdapter extends RecyclerView.Adapter<TransAdapter.MyViewHolder> {

    private ArrayList<TransactionModel> TransactionModelArrayList;
    private Context mContext;
    private long time;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView memberid, date, amount, bhn, bankname, acno, comment, date_label,ifsc_code;
        LinearLayout commentLayout;
        RelativeLayout layout;

        ArrayList<TransactionModel> TransactionModelArrayList = new ArrayList<TransactionModel>();
        Context ctx;
        private FrameLayout container;

        public MyViewHolder(View view, Context ctx, ArrayList<TransactionModel> TransactionModelArrayList) {
            super(view);
            this.TransactionModelArrayList = TransactionModelArrayList;
            this.ctx = ctx;

            memberid = (TextView) view.findViewById(R.id.tv_memberid);
            bhn = (TextView) view.findViewById(R.id.tv_bhn);
            acno = (TextView) view.findViewById(R.id.tv_acno);
            amount = (TextView) view.findViewById(R.id.tv_amount);
            bankname = (TextView) view.findViewById(R.id.tv_bank_name);
            date = (TextView) view.findViewById(R.id.tv_paid_dt);
            comment = (TextView) view.findViewById(R.id.tv_comment);
            date_label = (TextView) view.findViewById(R.id.label_date);
            ifsc_code = (TextView) view.findViewById(R.id.tv_ifsc_code);
            commentLayout = (LinearLayout) view.findViewById(R.id.comment_layout);
            layout = (RelativeLayout) view.findViewById(R.id.single_layout);
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);


        }

    }

    public TransAdapter(Context context, ArrayList<TransactionModel> empViewArrayList) {
        mContext = context;
        this.TransactionModelArrayList = empViewArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_trans_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, TransactionModelArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        final TransactionModel model = TransactionModelArrayList.get(position);
        String dateString = null;

        holder.memberid.setText(String.valueOf(model.getMid()));
        holder.amount.setText("$" + model.getAmount());
        if (!model.getAcname().equals("null"))
            holder.bhn.setText(model.getAcname());
        holder.bankname.setText(model.getBankname());
        holder.acno.setText(model.getAcno());
        holder.ifsc_code.setText(model.getIfsccode());

        if (model.getStatus().equals("1")) {
            dateString = model.getPaydate();
            holder.date_label.setText("Paid Date");
            holder.commentLayout.setVisibility(View.GONE);
            holder.layout.setBackgroundColor(Color.parseColor("#b2dfdb"));
        } else {
            dateString = model.getReqdate();
            holder.date_label.setText("Request Date");
            holder.commentLayout.setVisibility(View.GONE);
            holder.layout.setBackgroundColor(Color.parseColor("#ffcdd2"));
        }

        DateFormat formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Date date = null;
                date = (Date) formatter.parse(dateString);
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
        return TransactionModelArrayList.size();
    }

}
