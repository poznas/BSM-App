package com.bsmapps.bulgarskaszkolamagii.mcinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.sminfo.GoogleDriveLinkAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class MainCompetitionInfoActivity extends AppCompatActivity {

    private static final String TAG = "MainCompetitionInfoActivity";

    //This class is mostly same as SideMissionsInfoActivity
    //so using its classes and layouts

    @BindView(R.id.simple_recycler)
    RecyclerView mainCompetitionsInfoRecycler;

    private static DatabaseReference mRootRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recycler_list);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        initializeMCInfoRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initializeMCInfoRecycler() {
        GoogleDriveLinkAdapter adapter = new GoogleDriveLinkAdapter(this,
                mRootRef.child("MainCompetitionsDocs").orderByChild("name")); // the only difference
        mainCompetitionsInfoRecycler.setAdapter(adapter);
        mainCompetitionsInfoRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
