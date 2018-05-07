package com.bsmapps.bulgarskaszkolamagii.points.result_display;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.PhotoVideoFullscreenDisplay;
import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.ZonglerPost;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mlody Danon on 7/27/2017.
 */

public class SMPostResultDisplayActivity extends AppCompatActivity {

    private static final String TAG = "SMPostResultDisplayActivity";

    @BindView(R.id.item_points_view)
    TextView pointsView;
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
    @BindView(R.id.time_text_view)
    TextView timeView;
    @BindView(R.id.date_text_view)
    TextView dateView;

    private Bundle bundle;
    private String bDate;
    private String bTime;
    private Long bPoints;
    private String bTeam;
    private String bRpid;

    private static DatabaseReference mRootRef;
    private DatabaseReference mDatabaseZonglerRef;
    private ValueEventListener mZonglerValueEventListener;
    private ZonglerPost mZonglerPost;

    private Intent fullscreenIntent;
    private Bundle fullscreenBundle;

    @Override
    protected void onStop() {
        dettachZonglerPostReadListener();
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        dettachZonglerPostReadListener();
    }


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_post_result_display);
        ButterKnife.bind(this);

        handleBundleExtras();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseZonglerRef = mRootRef.child("Zongler").child(bRpid);

        attachZonglerPostReadListener();
    }



    private void handleBundleExtras() {

        bundle = this.getIntent().getExtras();
        if( bundle != null ){
            bTeam = bundle.getString("team");
            bDate = bundle.getString("date");
            bTime = bundle.getString("time");
            bPoints = bundle.getLong("points");
            bRpid = bundle.getString("rpid");

            switch (bTeam){
                case "cormeum":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.red));
                    setTitle("Cormeum");
                    break;
                case "sensum":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    setTitle("Sensum");
                    break;
                case "mutinium":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.green));
                    setTitle("Mutinium");
                    break;
                default:
                    break;
            }
        }
    }

    private void attachZonglerPostReadListener() {
        if(mZonglerValueEventListener == null ){
            mZonglerValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mZonglerPost = dataSnapshot.getValue(ZonglerPost.class);
                    setDisplayedInfo();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabaseZonglerRef.addValueEventListener(mZonglerValueEventListener);
        }
    }

    private void dettachZonglerPostReadListener(){
        if(mZonglerValueEventListener != null ){
            mDatabaseZonglerRef.removeEventListener(mZonglerValueEventListener);
            mZonglerValueEventListener = null;
        }
    }

    private void setDisplayedInfo() {
        userNameView.setText(mZonglerPost.getAuthor());
        titleView.setText(mZonglerPost.getTitle());
        bodyView.setText(mZonglerPost.getBody());
        pointsView.setText(bPoints.toString());
        timeView.setText(bTime);
        dateView.setText(bDate);

        Glide.with(this)
                .load(mZonglerPost.getAuthorPhotoUrl())
                .into(userImageView);

        String thumbnailUrl = mZonglerPost.getThumbnailUrl();
        if( thumbnailUrl != null ){

            thumbnailParentView.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(thumbnailUrl)
                    .into(thumbnailView);

            if( mZonglerPost.getVideoUrl() != null ){
                playButtonView.setVisibility(View.VISIBLE);
            }
            setThumbnailClickListener();
        }
    }

    private void setThumbnailClickListener(){

        fullscreenIntent = new Intent(this, PhotoVideoFullscreenDisplay.class);
        fullscreenBundle = new Bundle();

        String ImageUrl = mZonglerPost.getImageUrl();
        String videoUrl = mZonglerPost.getVideoUrl();
        if( ImageUrl != null ){
            fullscreenBundle.putString("type","photo");
            fullscreenBundle.putString("URL",ImageUrl);
        }else{
            fullscreenBundle.putString("type","video");
            fullscreenBundle.putString("URL",videoUrl);
        }
        fullscreenIntent.putExtras(fullscreenBundle);

        thumbnailParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( fullscreenIntent != null ){
                    startActivity(fullscreenIntent);
                }
            }
        });

    }
}
