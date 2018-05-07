package com.bsmapps.bulgarskaszkolamagii.judge;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.PendingReport;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JudgeSMListActivity extends AppCompatActivity {

    private static final String TAG = "JudgeSMListActivity";


    @BindView(R.id.simple_recycler)
    RecyclerView PendingReportsRecycler;

    private static DatabaseReference mRootRef;
    private DatabaseReference mDatabaseReportRatesRef;
    private ValueEventListener[] ReportRatesValueEventListeners;
    private DatabaseReference mDatabasePendingReportsRef;
    private ValueEventListener PendingReportsValueEventListener;

    private FirebaseAuth mAuth;
    private static FirebaseUser mFirebaseUser;

    private List<PendingReport> mPendingReports;
    private List<String> mRpids;
    private List<String> ReferenceRpids;

    private boolean[] ReadReportRate;

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

        setTitle("Oceń");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = this.getIntent().getExtras();
        if( bundle != null ){
            previousAmountOfReports = bundle.getInt("previousAmountOfReports");
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        initializePendingReportsListener();
    }

    private void initializePendingReportsListener() {
        mDatabasePendingReportsRef = mRootRef.child("pendingReports");
        mDatabaseReportRatesRef = mRootRef.child("ReportRates");
        if( PendingReportsValueEventListener == null ){
            PendingReportsValueEventListener =
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mPendingReports = new ArrayList<>();
                            mRpids = new ArrayList<>();
                            for( DataSnapshot child : dataSnapshot.getChildren() ){
                                mPendingReports.add(child.getValue(PendingReport.class));
                                mRpids.add(child.getKey());
                            }
                            ReportRatesValueEventListeners =  new ValueEventListener[mRpids.size()];
                            ReadReportRate =  new boolean[mRpids.size()];
                            initializeReportRatesListeners();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    };
                    mDatabasePendingReportsRef.addValueEventListener(PendingReportsValueEventListener);
        }
    }

    private void initializeReportRatesListeners() {
        ReferenceRpids = new ArrayList<>(mRpids);
        final List<PendingReport> temporaryPendingReports = new ArrayList<>(mPendingReports);
        final List<String> temporaryRpids = new ArrayList<>(mRpids);

        for( int i = 0; i<mRpids.size(); i++ ){
            if( ReportRatesValueEventListeners[i] == null ){
                final int finalI = i;
                ReportRatesValueEventListeners[i] = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for( DataSnapshot child : dataSnapshot.getChildren() ){
                            if( child.getKey().equals(mFirebaseUser.getUid())){
                                temporaryPendingReports.remove(mPendingReports.get(finalI));
                                temporaryRpids.remove(mRpids.get(finalI));
                                Log.d(TAG, "-----"+ mRpids.get(finalI)+ " already judged by "+ mFirebaseUser.getUid());
                                break;
                            }
                        }
                        ReadReportRate[finalI] = true;
                        if( allReportRatesRead() ){
                            mPendingReports = temporaryPendingReports;
                            mRpids = temporaryRpids;
                            for( String id : mRpids ){ Log.d(TAG,"----------------> " +id);}
                            initializePendingReportsRecycler();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                mDatabaseReportRatesRef.child(mRpids.get(i))
                        .addValueEventListener(ReportRatesValueEventListeners[i]);
            }
        }
    }

    private boolean allReportRatesRead() {
        for( int i=0; i<mRpids.size(); i++ ){
            if( !ReadReportRate[i] ){
                return false;
            }
        }
        return true;
    }

    private void initializePendingReportsRecycler() {
        PendingReportAdapter adapter = new PendingReportAdapter(this,
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
        if( PendingReportsValueEventListener != null ){
            mDatabasePendingReportsRef.removeEventListener(
                    PendingReportsValueEventListener
            );
            PendingReportsValueEventListener = null;
        }
        if( ReportRatesValueEventListeners != null ){
            for( int i=0; i< ReportRatesValueEventListeners.length; i++ ){
                if( ReportRatesValueEventListeners[i] != null ){
                    mDatabaseReportRatesRef.child(ReferenceRpids.get(i))
                            .removeEventListener(ReportRatesValueEventListeners[i]);
                    ReportRatesValueEventListeners[i] = null;
                }
            }
        }
    }
}
