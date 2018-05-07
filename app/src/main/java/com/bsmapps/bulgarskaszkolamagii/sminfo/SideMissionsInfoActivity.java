package com.bsmapps.bulgarskaszkolamagii.sminfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class SideMissionsInfoActivity extends AppCompatActivity {

    private static final String TAG = "SideMissionsInfoActivity";

    @BindView(R.id.simple_recycler)
    RecyclerView sideMissionsInfoRecycler;

    private static DatabaseReference mRootRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recycler_list);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        initializeSMInfoRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initializeSMInfoRecycler() {
        GoogleDriveLinkAdapter adapter = new GoogleDriveLinkAdapter(this,
                mRootRef.child("SideMissionsDocs").orderByChild("name"));
        sideMissionsInfoRecycler.setAdapter(adapter);
        sideMissionsInfoRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
