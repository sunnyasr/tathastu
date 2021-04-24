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

import com.tathastushop.app.Models.MemberModel;
import com.tathastushop.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class LevelTreeAdapter extends RecyclerView.Adapter<LevelTreeAdapter.MyViewHolder> {

    private ArrayList<MemberModel> LevelFinalArrayList;
    private Context mContext;
    private long time;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView memberid, regdate, sponsorid, username, dateLabel;

        ArrayList<MemberModel> LevelFinalArrayList = new ArrayList<MemberModel>();
        Context ctx;
        private FrameLayout container;

        public MyViewHolder(View view, Context ctx, ArrayList<MemberModel> LevelFinalArrayList) {
            super(view);
            this.LevelFinalArrayList = LevelFinalArrayList;
            this.ctx = ctx;

            memberid = (TextView) view.findViewById(R.id.tv_userid);
            username = (TextView) view.findViewById(R.id.tv_name);
            sponsorid = (TextView) view.findViewById(R.id.tv_refid);
            dateLabel = (TextView) view.findViewById(R.id.label_date);
            regdate = (TextView) view.findViewById(R.id.tv_reg_dt);
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);


        }

    }

    public LevelTreeAdapter(Context context, ArrayList<MemberModel> empViewArrayList) {
        mContext = context;
        this.LevelFinalArrayList = empViewArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_level_tree_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, LevelFinalArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        final MemberModel levelFinal = LevelFinalArrayList.get(position);


        holder.memberid.setText(levelFinal.getMemberid());
        holder.username.setText(levelFinal.getUsername());
        holder.sponsorid.setText(levelFinal.getRefferal());

        holder.dateLabel.setText("Register Date");


        DateFormat formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Date date = null;
                date = (Date) formatter.parse(levelFinal.getRegdate());
                time = date.getTime();
            }
            holder.regdate.setText(getDate(time).getTime().toLocaleString());


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
        return LevelFinalArrayList.size();
    }

}
