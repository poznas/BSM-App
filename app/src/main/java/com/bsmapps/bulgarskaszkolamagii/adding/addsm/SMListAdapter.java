package com.bsmapps.bulgarskaszkolamagii.adding.addsm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.CalendarDay;
import com.bsmapps.bulgarskaszkolamagii.beans.SideMissionInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/29/2017.
 */

public class SMListAdapter extends FirebaseRecyclerAdapter<SideMissionInfo, SMListAdapter.SMListViewHolder> {

    private final Context context;
    private String mTeam;

    public SMListAdapter(Context context, Query ref, String team ) {
        super(SideMissionInfo.class, R.layout.item_name_and_arrow, SMListViewHolder.class, ref);
        this.context = context;
        this.mTeam = team;
    }

    @Override
    public SMListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_name_and_arrow, parent, false);

        return new SMListViewHolder(itemView);
    }

    @Override
    protected void populateViewHolder(SMListViewHolder viewHolder, SideMissionInfo info, int position) {
        viewHolder.setInfo(info);
    }

    public class SMListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.item_name_text_view)
        TextView sideMissionName;
        @BindView(R.id.item_parent)
        View sideMissionParent;

        private Intent addSMDetailsIntent;
        private Bundle addSMDetailsBundle;

        public SMListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sideMissionParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if( addSMDetailsIntent != null ){
                context.startActivity(addSMDetailsIntent);
                ((Activity)context).finish();
            }
        }

        public void setInfo(SideMissionInfo info) {

            sideMissionName.setText(info.getName());
            addSMDetailsBundle = new Bundle();
            addSMDetailsBundle.putString("sm_name",info.getName());
            addSMDetailsBundle.putString("team",mTeam);
            if( info.getPost() ){
                addSMDetailsIntent = new Intent(context,AddSMPostActivity.class);
            }else{
                addSMDetailsIntent = new Intent(context,AddSMDetailsActivity.class);
            }
            addSMDetailsIntent.putExtras(addSMDetailsBundle);
        }
    }
}
