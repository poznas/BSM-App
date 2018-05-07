package com.bsmapps.bulgarskaszkolamagii.points;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class PointsListActivity  extends AppCompatActivity {

    private static final String TAG = "PointsListActivity";

    @BindView(R.id.points_recycler)
    RecyclerView PointsRecycler;
    @BindView(R.id.team_points_view)
    TextView TeamPointsView;
    @BindView(R.id.team_image_view)
    ImageView TeamImageView;

    private static DatabaseReference mRootRef;
    private DatabaseReference mDatabaseScoreRef;
    private ValueEventListener mScoreValueEventListener;

    private String teamId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_list);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = this.getIntent().getExtras();
        if( bundle != null ){
            teamId = bundle.getString("team");
        }

        mDatabaseScoreRef = mRootRef.child("SCORES").child(teamId);

        handeTeamProperities();

        attachScoreDatabaseReadListener();
        initializePointsRecycler();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        dettachScoreDatabaseReadListener();
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        dettachScoreDatabaseReadListener();
    }

    private void initializePointsRecycler() {
        PointsAdapter adapter = new PointsAdapter(this,
                mRootRef.child(teamId+"AllPoints").orderByChild("timestamp"), teamId);
        PointsRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        PointsRecycler.setLayoutManager(layoutManager);
    }

    private void attachScoreDatabaseReadListener(){
        if( mScoreValueEventListener == null ){
            mScoreValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long points = (Long) dataSnapshot.getValue();
                    TeamPointsView.setText(points.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseScoreRef.addValueEventListener(mScoreValueEventListener);
        }
    }

    private void dettachScoreDatabaseReadListener(){
        if (mScoreValueEventListener != null ){
            mDatabaseScoreRef.removeEventListener(mScoreValueEventListener);
            mScoreValueEventListener = null;
        }
    }

    private void handeTeamProperities(){

        if( teamId.equals("cormeum")){
            TeamPointsView.setTextColor(ContextCompat.getColor(this, R.color.red));
            TeamImageView.setImageResource(R.mipmap.cormeum);
            setTitle("Cormeum");
        }else if(teamId.equals("sensum")){
            TeamPointsView.setTextColor(ContextCompat.getColor(this, R.color.blue));
            TeamImageView.setImageResource(R.mipmap.sensum);
            setTitle("Sensum");
        }else if( teamId.equals("mutinium")){
            TeamPointsView.setTextColor(ContextCompat.getColor(this, R.color.green));
            TeamImageView.setImageResource(R.mipmap.mutinium);
            setTitle("Mutinium");
        }
    }
}
