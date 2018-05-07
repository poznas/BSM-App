package com.bsmapps.bulgarskaszkolamagii.points.result_display;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bsmapps.bulgarskaszkolamagii.PhotoVideoFullscreenDisplay;
import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ReportSingleMedia;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/28/2017.
 */

public class ReportMediaAdapter extends RecyclerView.Adapter<ReportMediaAdapter.ReportMediaViewHolder>{

    private LayoutInflater mInflater;
    private Context context;
    List<ReportSingleMedia> mMediaList;


    public ReportMediaAdapter( Context context, List<ReportSingleMedia> mediaList ) {
        mInflater = LayoutInflater.from(context);
        this.mMediaList = mediaList;
        this.context = context;
    }

    @Override
    public ReportMediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_sm_media, parent, false );
        ReportMediaViewHolder holder = new ReportMediaViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ReportMediaViewHolder holder, int position) {
        ReportSingleMedia media = mMediaList.get(position);
        holder.setMediaData(media);
    }

    @Override
    public int getItemCount() {
        return mMediaList.size();
    }

    public class ReportMediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.item_parent)
        View parentView;
        @BindView(R.id.item_thumbnail_parent)
        View thumbnailParentView;
        @BindView(R.id.item_thumbnail_view)
        ImageView thumbnailView;
        @BindView(R.id.item_play_button_view)
        ImageView playButtonView;

        private Intent fullscreenIntent;
        private Bundle fullscreenBundle;

        public ReportMediaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            thumbnailParentView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if( fullscreenIntent != null ){
                context.startActivity(fullscreenIntent);
            }
        }

        public void setMediaData(ReportSingleMedia media) {

            if( getItemCount() == 1 ){
                parentView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }else if(getItemCount() == 2){
                Activity activity = (Activity) context;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int mediaWidth = width/2;

                parentView.setLayoutParams(new ViewGroup.LayoutParams(
                        mediaWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT
                        ));
            }
            Glide.with(context)
                    .load(media.getThumbnailUrl())
                    .into(thumbnailView);
            if( media.getType().equals("video") ){
                playButtonView.setVisibility(View.VISIBLE);
            }
            setThumbnailClickListener(media);
        }

        private void setThumbnailClickListener(ReportSingleMedia media) {

            fullscreenIntent = new Intent(context, PhotoVideoFullscreenDisplay.class);
            fullscreenBundle = new Bundle();
            fullscreenBundle.putString("type",media.getType());
            fullscreenBundle.putString("URL",media.getOrginalUrl());
            fullscreenIntent.putExtras(fullscreenBundle);
        }
    }
}
