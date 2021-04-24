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
import com.tathastushop.app.Models.LevelIncomeModel;
import com.tathastushop.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class LevelIncomeAdapter extends RecyclerView.Adapter<LevelIncomeAdapter.MyViewHolder> {

    private ArrayList<LevelIncomeModel> LevelIncomeArrayList;
    private Context mContext;
    private long time;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView levelid, amount, date, levelincome, percent;
        View view_left;

        ArrayList<LevelIncomeModel> LevelIncomeArrayList = new ArrayList<LevelIncomeModel>();
        Context ctx;
        private FrameLayout container;

        MyViewHolder(View view, Context ctx, ArrayList<LevelIncomeModel> LevelIncomeArrayList) {
            super(view);
            this.LevelIncomeArrayList = LevelIncomeArrayList;
            this.ctx = ctx;
//            name = (TextView) view.findViewById(R.id.tv_store_name);
            levelid = (TextView) view.findViewById(R.id.tv_level);
//            member = (TextView) view.findViewById(R.id.tv_member);
            amount = (TextView) view.findViewById(R.id.tv_amount);
            date = (TextView) view.findViewById(R.id.tv_reg_dt);
            levelincome = (TextView) view.findViewById(R.id.levelincome);
            percent = (TextView) view.findViewById(R.id.percent);
            view_left = (View) view.findViewById(R.id.view_left);
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);


        }

        @Override
        public void onClick(View v) {
           /* int position = getAdapterPosition();
            EmpView notification = this.empViewArrayList.get(position);
            String time = notification.getTime();
            String title = notification.getTitle();
            String message = notification.getMessage();
            String messageID = String.valueOf(notification.getMessageid());
            Intent intent = new Intent(this.ctx, EmpNoticeDetailsActivity.class);
            intent.putExtra("time", time);
            intent.putExtra("message", message);
            intent.putExtra("messageId", messageID);
            intent.putExtra("title", title);
            this.ctx.startActivity(intent);*/
        }
    }

    public LevelIncomeAdapter(Context context, ArrayList<LevelIncomeModel> empViewArrayList) {
        mContext = context;
        this.LevelIncomeArrayList = empViewArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_level_income_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, LevelIncomeArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        final LevelIncomeModel levelIncome = LevelIncomeArrayList.get(position);
        holder.amount.setText("₹" + levelIncome.getAmount());
//        holder.member.setText(levelIncome.getMemberid());
        holder.levelid.setText("" + levelIncome.getLevelid());
//        holder.name.setText(levelIncome.getName());
        holder.levelincome.setText("₹" + levelIncome.getNett());
        holder.percent.setText( levelIncome.getPercent()+"%");

//        if (levelIncome.isPaid()) {
//            holder.view_left.setBackgroundColor(Color.parseColor("#5ec800"));
//        } else {
//            holder.view_left.setBackgroundColor(Color.parseColor("#e61953"));
//        }


        DateFormat formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Date date = null;
                date = (Date) formatter.parse(levelIncome.getCreated_date());
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
        return LevelIncomeArrayList.size();
    }

}
