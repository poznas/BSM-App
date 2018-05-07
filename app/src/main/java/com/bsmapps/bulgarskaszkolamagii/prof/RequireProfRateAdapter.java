package com.bsmapps.bulgarskaszkolamagii.prof;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.PendingReport;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.key;

/**
 * Created by Mlody Danon on 8/8/2017.
 */

public class RequireProfRateAdapter extends RecyclerView.Adapter<RequireProfRateAdapter.RequireProfRateViewHolder>{

    private LayoutInflater mInflater;
    private Context context;
    List<PendingReport> mReportList;
    List<String> mRpidList;
    private int previousAmountOfReports;

    public RequireProfRateAdapter(Context context, List<PendingReport> List, List<String> Ids, int previous ) {
        mInflater = LayoutInflater.from(context);
        this.mReportList = List;
        this.mRpidList = Ids;
        this.context = context;
        previousAmountOfReports = previous;
    }


    @Override
    public RequireProfRateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_report, parent, false);

        return new RequireProfRateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RequireProfRateViewHolder holder, int position) {
        PendingReport currentPost = mReportList.get(position);
        String currentRpid = mRpidList.get(position);

        holder.setReportData( currentPost, currentRpid );
    }

    @Override
    public int getItemCount() {
        return mReportList.size();
    }

    public class RequireProfRateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.item_parent)
        View parentView;
        @BindView(R.id.item_sm_name_view)
        TextView smNameView;
        @BindView(R.id.item_user_image_view)
        CircleImageView userImageView;
        @BindView(R.id.item_user_name_view)
        TextView userNameView;
        @BindView(R.id.item_time_text_view)
        TextView timeView;
        @BindView(R.id.item_date_text_view)
        TextView dateView;

        private Intent ProfRateIntent;
        private Bundle ProfRateBundle;

        public RequireProfRateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            parentView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if( ProfRateIntent != null ){
                context.startActivity(ProfRateIntent);
                ((Activity)context).finish();
            }
        }

        public void setReportData(PendingReport report, String key) {

            Date dateObject = new Date(report.getTimestamp());
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

            String date = sdfDate.format(dateObject);
            String time = sdfTime.format(dateObject);

            dateView.setText(date);
            timeView.setText(time);

            smNameView.setText(report.getSm_name());
            userNameView.setText(report.getPerforming_user());

            Glide.with(context)
                    .load(report.getUser_photoUrl())
                    .into(userImageView);

            ProfRateBundle = new Bundle();
            ProfRateBundle.putString("sm_name",report.getSm_name());
            ProfRateBundle.putString("user_name",report.getPerforming_user());
            ProfRateBundle.putString("user_image_url",report.getUser_photoUrl());
            ProfRateBundle.putString("time",time);
            ProfRateBundle.putString("date",date);
            ProfRateBundle.putString("rpid",key);
            ProfRateBundle.putInt("previousAmountOfReports",previousAmountOfReports);
            if( report.getPost() == false ){
                ProfRateIntent = new Intent(context, ProfRateSideMissionActivity.class);
                ProfRateIntent.putExtras(ProfRateBundle);
            }else if( report.getPost() == true ){
                ProfRateIntent = new Intent(context, ProfRateSMPostActivity.class);
                ProfRateIntent.putExtras(ProfRateBundle);
            }
        }
    }
}
