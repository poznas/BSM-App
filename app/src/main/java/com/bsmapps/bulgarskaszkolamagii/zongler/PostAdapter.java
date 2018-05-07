package com.bsmapps.bulgarskaszkolamagii.zongler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.PhotoVideoFullscreenDisplay;
import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.CalendarDay;
import com.bsmapps.bulgarskaszkolamagii.beans.ZonglerPost;
import com.bsmapps.bulgarskaszkolamagii.calendar.CalendarDaysAdapter;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mlody Danon on 7/28/2017.
 */

public class PostAdapter extends FirebaseRecyclerAdapter<ZonglerPost, PostAdapter.PostDaysViewHolder> {

    private final Context context;

    public PostAdapter(Context context, Query ref) {
        super(ZonglerPost.class, R.layout.item_zongler_post, PostDaysViewHolder.class, ref);
        this.context = context;
    }

    @Override
    public PostDaysViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_zongler_post, parent, false);

        return new PostDaysViewHolder(itemView);
    }

    @Override
    protected void populateViewHolder(PostAdapter.PostDaysViewHolder viewHolder, ZonglerPost post, int position) {
        viewHolder.setPost(post);
        
    }

    public class PostDaysViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.item_user_name_view)
        TextView userNameView;
        @BindView(R.id.item_user_image_view)
        CircleImageView userImageView;
        @BindView(R.id.item_title_view)
        TextView titleView;
        @BindView(R.id.thumbnail_parent_view)
        View thumbnailParentView;
        @BindView(R.id.thumbnail_view)
        ImageView thumbnailView;
        @BindView(R.id.play_button_view)
        ImageView playButtonView;
        @BindView(R.id.item_body_view)
        TextView bodyView;
        @BindView(R.id.item_time_view)
        TextView timeView;
        @BindView(R.id.item_date_view)
        TextView dateView;

        private Intent fullscreenIntent;
        private Bundle fullscreenBundle;

        public PostDaysViewHolder(View itemView) {
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

        public void setPost(ZonglerPost post) {

            Date dateObject = new Date(post.getTimestamp());
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

            String date = sdfDate.format(dateObject);
            String time = sdfTime.format(dateObject);

            dateView.setText(date);
            timeView.setText(time);
            userNameView.setText(post.getAuthor());
            titleView.setText(post.getTitle());

            String body = post.getBody().replace("\n","\r\n");
            bodyView.setText(body);

            Glide.with(context)
                    .load(post.getAuthorPhotoUrl())
                    .into(userImageView);

            String thumbnailUrl = post.getThumbnailUrl();

            if( thumbnailUrl != null ) {

                thumbnailParentView.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(thumbnailUrl)
                        .into(thumbnailView);
                if( post.getVideoUrl() != null ){
                    playButtonView.setVisibility(View.VISIBLE);
                }
                setThumbnailClickListener(post);
            }
        }

        private void setThumbnailClickListener(ZonglerPost post){

            fullscreenIntent = new Intent(context, PhotoVideoFullscreenDisplay.class);
            fullscreenBundle = new Bundle();

            String ImageUrl = post.getImageUrl();
            String videoUrl = post.getVideoUrl();
            if( ImageUrl != null ){
                fullscreenBundle.putString("type","photo");
                fullscreenBundle.putString("URL",ImageUrl);
            }else{
                fullscreenBundle.putString("type","video");
                fullscreenBundle.putString("URL",videoUrl);
            }
            fullscreenIntent.putExtras(fullscreenBundle);
        }
    }
}
