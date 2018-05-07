package com.bsmapps.bulgarskaszkolamagii.prof;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.PendingReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfRateSMListActivity extends AppCompatActivity {

    @BindView(R.id.simple_recycler)
    RecyclerView PendingReportsRecycler;

    private static DatabaseReference mRootRef;
    private DatabaseReference mDatabaseRequireProfRateRef;
    private ValueEventListener RequireProfRateValueEventListener;

    private List<PendingReport> mPendingReports;
    private List<String> mRpids;

    private int previousAmountOfReports;

    @Override
    protected void onStop() {
        super.onStop();
        dettachFirebaseListeners();
    }
    @Override
    protected void onPause() {
        super.onPause();
        dettachFirebaseListeners();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recycler_list);
        ButterKnife.bind(this);

        setTitle("Opiniuj");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = this.getIntent().getExtras();
        if( bundle != null ){
            previousAmountOfReports = bundle.getInt("previousAmountOfReports");
        }

        InitializePendingReportListener();
    }

    private void InitializePendingReportListener() {
        mDatabaseRequireProfRateRef = mRootRef.child("requireProfRate");
        if( RequireProfRateValueEventListener == null ){
            RequireProfRateValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPendingReports = new ArrayList<>();
                    mRpids = new ArrayList<>();
                    for( DataSnapshot child : dataSnapshot.getChildren() ){
                        mPendingReports.add(child.getValue(PendingReport.class));
                        mRpids.add(child.getKey());
                    }
                    InitializeRequireProfRatesRecycler();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseRequireProfRateRef.addValueEventListener(RequireProfRateValueEventListener);
        }
    }

    private void InitializeRequireProfRatesRecycler() {
        RequireProfRateAdapter adapter = new RequireProfRateAdapter(this,
                mPendingReports,
                mRpids,
                previousAmountOfReports);
        PendingReportsRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        PendingReportsRecycler.setLayoutManager(layoutManager);
    }


    private void dettachFirebaseListeners(){
        if( RequireProfRateValueEventListener != null ){
            mDatabaseRequireProfRateRef.removeEventListener(
                    RequireProfRateValueEventListener
            );
            RequireProfRateValueEventListener = null;
        }
    }
}
