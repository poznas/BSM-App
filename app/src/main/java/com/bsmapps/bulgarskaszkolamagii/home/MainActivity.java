package com.bsmapps.bulgarskaszkolamagii.home;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.Privilege;
import com.bsmapps.bulgarskaszkolamagii.beans.User;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.PendingReport;
import com.bsmapps.bulgarskaszkolamagii.login.LoginActivity;
import com.bsmapps.bulgarskaszkolamagii.points.PointsListActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.handle;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivity";

    @BindView(R.id.activity_main_privileges_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.privileges_progress_bar)
    ProgressBar privilegesProgressBar;

    @BindView(R.id.cormeum_points)
    TextView mCormeumPointsTextView;
    @BindView(R.id.sensum_points)
    TextView mSensumPointsTextView;
    @BindView(R.id.mutinium_points)
    TextView mMutiniumPointsTextView;

    @BindView(R.id.cormeum_image)
    ImageView mCormeumImageView;
    @BindView(R.id.sensum_image)
    ImageView mSensumImageView;
    @BindView(R.id.mutinium_image)
    ImageView mMutiniumImageView;

    private Intent mListPointsIntent;
    private Bundle mBundle;

    private DatabaseReference mDatabaseScoresRef;
    private ValueEventListener mScoresValueEventListener;

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private static FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mRootRef;

    private static User mCurrentUserData;
    private ValueEventListener mUserValueEventListener;
    private DatabaseReference mDatabaseUserRef;

    private List<Privilege> privileges;
    PrivilegesAdapter adapter;


    private List<PendingReport> mPendingReports;
    private List<String> mRpids;
    private List<String> ReferenceRpids;

    private DatabaseReference mDatabaseReportRatesRef;
    private ValueEventListener[] ReportRatesValueEventListeners;
    private DatabaseReference mDatabasePendingReportsRef;
    private ValueEventListener PendingReportsValueEventListener;

    
    private DatabaseReference mDatabaseRequireProfRateRef;
    private ValueEventListener RequireProfRateValueEventListener;
    private int requireProfRateAmount = 0;

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "invoke onStart()");
        mAuth.addAuthStateListener(mAuthListener);
        attachScoresDatabaseReadListener();
        initializePendingReportsListener();
        InitializeProfRateReportsListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "invoke onResume()");
        initializePendingReportsListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "-- invoke onStop()");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "-- invoke onPause()");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "invoke onCreate()");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handleBundles();


        SetFirebaseData();
        setTeamImagesListeners();
        initializeFirebaseAuthListener();
        attachUserDatabaseReadListener();
    }

    private void handleBundles() {
        Log.i(TAG, "invoke handleBundles()");
        final Bundle bundle = this.getIntent().getExtras();
        if( bundle != null ){
            if( bundle.getInt("previousAmountOfReports") == 1 ){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updatePrivilege(0, "Oceń");
                            }
                        });
                    }
                }).start();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeFirebaseAuthListener() {
        Log.i(TAG, "+ invoke initializeFirebaseAuthListener()");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    updateUserDatabaseWith(mFirebaseUser);
                    Log.d("@@@@", "home:signed_in:" + mFirebaseUser.getUid());
                } else {
                    Log.d("@@@@", "home:signed_out");
                    Intent login = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        };
    }

    private void attachScoresDatabaseReadListener(){

        if( mScoresValueEventListener == null ){
            Log.i(TAG, "+ invoke attachScoresDatabaseReadListener()");
            mScoresValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long cormeumPoints = (Long) dataSnapshot.child("cormeum").getValue();
                    Long sensumPoints = (Long) dataSnapshot.child("sensum").getValue();
                    Long mutiniumPoints = (Long) dataSnapshot.child("mutinium").getValue();

                    mCormeumPointsTextView.setText(cormeumPoints.toString());
                    mSensumPointsTextView.setText(sensumPoints.toString());
                    mMutiniumPointsTextView.setText(mutiniumPoints.toString());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseScoresRef.addValueEventListener(mScoresValueEventListener);
        }
    }
    private void attachUserDatabaseReadListener(){

        if( mUserValueEventListener == null ){
            Log.i(TAG, "+ invoke attachUserDatabaseReadListener()");
            mUserValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCurrentUserData = dataSnapshot.getValue(User.class);
                    if( mCurrentUserData.getLabel() != null ){
                        initializePrivilegesRecycler();
                        initializePendingReportsListener();
                        if( mCurrentUserData.getLabel().equals("judge") ){
                            FirebaseMessaging.getInstance().subscribeToTopic("reportsToJudge");
                        }else{
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("reportsToJudge");
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseUserRef.addValueEventListener(mUserValueEventListener);
        }
    }
    private void detachDatabaseReadListener() {
        Log.i(TAG, "-- invoke detachDatabaseReadListener()");
        if (mUserValueEventListener != null) {
            mDatabaseUserRef.removeEventListener(mUserValueEventListener);
            mUserValueEventListener = null;
        }
        if (mScoresValueEventListener != null ){
            mDatabaseScoresRef.removeEventListener(mScoresValueEventListener);
            mScoresValueEventListener = null;
        }
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
        if( RequireProfRateValueEventListener != null ){
            mDatabaseRequireProfRateRef.removeEventListener(
                    RequireProfRateValueEventListener
            );
            RequireProfRateValueEventListener = null;
        }
    }

    private void initializePrivilegesRecycler() {
        if( adapter != null ){ return; }
        Log.i(TAG, "+ invoke initializePrivilegesRecycler()");
        getPrivileges();
        adapter = new PrivilegesAdapter(this,
                privileges,
                mCurrentUserData.getTeam());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        privilegesProgressBar.setVisibility(View.GONE);
        mRecyclerView.setAdapter(adapter);

        initializePendingReportsListener();
        InitializeProfRateReportsListener();
    }

    private void InitializeProfRateReportsListener() {
        if( mCurrentUserData == null
                || mCurrentUserData.getLabel() == null
                || !mCurrentUserData.getLabel().equals("professor")){
            return;
        }
        Log.i(TAG, "+ invoke InitializeProfRateReportsListener()");
        mDatabaseRequireProfRateRef = mRootRef.child("requireProfRate");

        if( RequireProfRateValueEventListener != null ){
            mDatabaseRequireProfRateRef.removeEventListener(
                    RequireProfRateValueEventListener
            );
            RequireProfRateValueEventListener = null;
        }
        RequireProfRateValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requireProfRateAmount = 0;
                for( DataSnapshot child : dataSnapshot.getChildren() ){
                    requireProfRateAmount += 1;
                }
                updatePrivilege(requireProfRateAmount, "Opiniuj");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabaseRequireProfRateRef.addValueEventListener(RequireProfRateValueEventListener);
    }

    private void initializePendingReportsListener() {
        if( mCurrentUserData == null
                || mCurrentUserData.getLabel() == null
                || !mCurrentUserData.getLabel().equals("judge")){
            return;
        }
        Log.i(TAG, "+ invoke initializePendingReportsListener()");
        mDatabasePendingReportsRef = mRootRef.child("pendingReports");
        mDatabaseReportRatesRef = mRootRef.child("ReportRates");
        if (PendingReportsValueEventListener == null) {
            PendingReportsValueEventListener =
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mPendingReports = new ArrayList<>();
                            mRpids = new ArrayList<>();
                            if( dataSnapshot.getChildrenCount() == 0 ){
                                updatePrivilege(0, "Oceń");
                            }
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                mPendingReports.add(child.getValue(PendingReport.class));
                                mRpids.add(child.getKey());
                            }
                            ReportRatesValueEventListeners = new ValueEventListener[mRpids.size()];
                            initializeReportRatesListeners( mRpids, mPendingReports );
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
            mDatabasePendingReportsRef.addValueEventListener(PendingReportsValueEventListener);
        }
    }

    private void initializeReportRatesListeners(
            final List<String> inputRpids,
            final List<PendingReport> inputPendingReports ) {

        if( mFirebaseUser == null ){ return; }
        Log.i(TAG, "+ invoke initializeReportRatesListeners()");
        ReferenceRpids = new ArrayList<>(inputRpids);
        final List<PendingReport> temporaryPendingReports = new ArrayList<>(inputPendingReports);
        final List<String> temporaryRpids = new ArrayList<>(inputRpids);
        final boolean[] ReadReportRate = new boolean[inputRpids.size()];

        for( int i = 0; i<ReadReportRate.length; i++ ){
            if( ReportRatesValueEventListeners[i] == null ){
                final int finalI = i;
                ReportRatesValueEventListeners[i] = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for( DataSnapshot child : dataSnapshot.getChildren() ){
                            if( child.getKey().equals(mFirebaseUser.getUid())){
                                temporaryPendingReports.remove(inputPendingReports.get(finalI));
                                temporaryRpids.remove(inputRpids.get(finalI));
                                break;
                            }
                        }
                        ReadReportRate[finalI] = true;
                        if( allReportRatesRead( ReadReportRate ) ){
                            updatePrivilege(temporaryPendingReports.size(), "Oceń");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                mDatabaseReportRatesRef.child(inputRpids.get(i))
                        .addValueEventListener(ReportRatesValueEventListeners[i]);
            }
        }
    }

    private void updatePrivilege(int pendingReports, String brand ) {
        if( privileges != null ){
            Log.i(TAG, "invoke updatePrivilege( "+String.valueOf(pendingReports)+", "+brand+" )");
            for( Privilege current : privileges ){
                if( current.getBrand().equals(brand)){
                    current.setPendingReports(pendingReports);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private boolean allReportRatesRead(boolean[] readReportRate) {
        for( int i=0; i<mRpids.size(); i++ ){
            if( !readReportRate[i] ){
                return false;
            }
        }
        return true;
    }

    public void getPrivileges() {

        privileges = new ArrayList<>();

        String brand = mCurrentUserData.getLabel();

        if( brand == null ){
            return;
        }

        if( brand.equals("professor") || brand.equals("wizzard") ){
            privileges.add(new Privilege(R.mipmap.icon_small_report, "Melduj"));
        }
        if( brand.equals("professor") ){
            privileges.add(new Privilege(R.mipmap.icon_small_mc, "Konkurencja Główna"));
            privileges.add(new Privilege(R.mipmap.icon_small_medal, "Medal"));
            privileges.add(new Privilege(R.mipmap.icon_small_prof, "Opiniuj", true ));
            privileges.add(new Privilege(R.mipmap.icon_small_bet, "Zakład"));
        }
        if ( brand.equals("judge") ){
            privileges.add(new Privilege(R.mipmap.icon_small_judge, "Oceń", true ));
            privileges.add(new Privilege(R.mipmap.icon_small_wizards, "Czarodzieje"));
        }
        if ( brand.equals("professor") || brand.equals("wizzard") || brand.equals("judge") )
        privileges.add(new Privilege(R.mipmap.icon_small_calendar, "Kalendarz"));
        privileges.add(new Privilege(R.mipmap.icon_small_zongler, "Żongler"));
        privileges.add(new Privilege(R.mipmap.icon_small_sm_info, "Misje Poboczne"));

        if( brand.equals("professor") || brand.equals("wizzard") ){
            privileges.add(new Privilege(R.mipmap.icon_small_mc_info, "Konkurencje Główne"));
        }
    }

    private void updateUserDatabaseWith( FirebaseUser firebaseUser ){
        User user = new User(
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                firebaseUser.getPhotoUrl() == null
                        ? "http://i.kafeteria.pl/0991f9c6631ca79a8bb5b5199b2c39df1fc77dc4"
                        : firebaseUser.getPhotoUrl().toString()
        );
        mDatabaseUserRef.setValue(user);

        String instanceId = FirebaseInstanceId.getInstance().getToken();
        if( instanceId != null ){
            mDatabaseUserRef.child("instanceId").setValue(instanceId);
        }
    }
    public void setTeamImagesListeners(){

        mCormeumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isUserAuthorized() ){
                    mBundle.putString("team","cormeum");
                    mListPointsIntent.putExtras(mBundle);
                    startActivity(mListPointsIntent);
                }
            }
        });
        mSensumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isUserAuthorized() ){
                    mBundle.putString("team","sensum");
                    mListPointsIntent.putExtras(mBundle);
                    startActivity(mListPointsIntent);
                }
            }
        });
        mMutiniumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isUserAuthorized() ){
                    mBundle.putString("team","mutinium");
                    mListPointsIntent.putExtras(mBundle);
                    startActivity(mListPointsIntent);
                }
            }
        });
    }

    private boolean isUserAuthorized(){
        if( mCurrentUserData != null
                && mCurrentUserData.getLabel() != null ){
            if( mCurrentUserData.getLabel().equals("professor")
                    || mCurrentUserData.getLabel().equals("wizzard")
                    || mCurrentUserData.getLabel().equals("judge")){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void SetFirebaseData(){
        Log.i(TAG, "invoke SetFirebaseData()");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFirebaseUser = mAuth.getCurrentUser();
        mRootRef = mFirebaseDatabase.getReference().getRoot();
        mDatabaseUserRef = mRootRef.child("users").child(mFirebaseUser.getUid());

        mDatabaseScoresRef = mRootRef.child("SCORES");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mListPointsIntent = new Intent(this, PointsListActivity.class);
        mBundle = new Bundle();
    }
}
