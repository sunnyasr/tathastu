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
import android.widget.TextView;

import com.tathastushop.app.Activities.LevelFinalActivity;
import com.tathastushop.app.Models.LevelModel;
import com.tathastushop.app.R;

import java.util.ArrayList;


public class LevelS1Adapter extends RecyclerView.Adapter<LevelS1Adapter.MyViewHolder> {

    private ArrayList<LevelModel> levelArrayList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView level, count;


        ArrayList<LevelModel> levelArrayList = new ArrayList<LevelModel>();
        Context ctx;
        private FrameLayout container;

        public MyViewHolder(View view, Context ctx, ArrayList<LevelModel> levelArrayList) {
            super(view);
            this.levelArrayList = levelArrayList;
            this.ctx = ctx;
            view.setOnClickListener(this);

            level = (TextView) view.findViewById(R.id.tv_level);
            count = (TextView) view.findViewById(R.id.tv_count);
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);
            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            LevelModel level = this.levelArrayList.get(position);
            Intent intent = new Intent(this.ctx, LevelFinalActivity.class);
            intent.putExtra("level", level.getLevel());
            intent.putExtra("position", position);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.ctx.startActivity(intent);
        }
    }


    public LevelS1Adapter(Context context, ArrayList<LevelModel> levelArrayList) {
        mContext = context;
        this.levelArrayList = levelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_single_level_tree, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, levelArrayList);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);

        final LevelModel level = levelArrayList.get(position);
        holder.level.setText(level.getLevel());
        holder.count.setText(level.getCount());
    }

    private void setAnimation(FrameLayout container, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        container.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return levelArrayList.size();
    }

}
