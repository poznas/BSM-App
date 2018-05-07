package com.bsmapps.bulgarskaszkolamagii.judge;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.SideMissionInfo;
import com.bsmapps.bulgarskaszkolamagii.beans.User;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ProperityDetails;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ReportBasicFirebase;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ReportSingleMedia;
import com.bsmapps.bulgarskaszkolamagii.home.MainActivity;
import com.bsmapps.bulgarskaszkolamagii.login.LoginActivity;
import com.bsmapps.bulgarskaszkolamagii.points.result_display.ReportMediaAdapter;
import com.bsmapps.bulgarskaszkolamagii.wizzards.PrivilegeWizzardsActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class JudgeSideMissionActivity extends AppCompatActivity {

    private static final String TAG = "JudgeSideMissionActivity";
    private Context context;

    @BindView(R.id.media_recycler)
    RecyclerView mediaRecycler;
    @BindView(R.id.sm_name_view)
    TextView smNameView;
    @BindView(R.id.google_drive_view)
    ImageView googleDriveView;
    @BindView(R.id.team_name_view)
    TextView teamView;
    @BindView(R.id.info_view)
    ImageView infoView;
    @BindView(R.id.performing_user_image_view)
    CircleImageView performingUserImage;
    @BindView(R.id.performing_user_name_view)
    TextView performingUserName;
    @BindView(R.id.rate_properities_list_view)
    ListView properitiesListView;
    @BindView(R.id.send_button_view)
    View sendButtonView;

    private Bundle bundle;
    private String bSMName;
    private String bUserImageURL;
    private String bUserName;
    private String bRpid;
    private int bPreviousAmountOfReports;

    private DatabaseReference mDatabaseSMDocsRef;
    private ValueEventListener mSMDocsValueEventListener;
    private static SideMissionInfo mSMInfo;
    private Intent googleDriveIntent;

    private Intent wizzardsIntent;

    private static DatabaseReference mRootRef;
    private DatabaseReference mDatabaseInReportsRef;
    private ValueEventListener mInReportValueEventListener;
    private DatabaseReference mDatabaseInReportsMediaRef;
    private ValueEventListener mInReportMediaValueEventListener;
    private DatabaseReference mDatabasePerformingUserRef;
    private ValueEventListener mPerformingUserValueEventListener;
    private DatabaseReference mDatabaseSMPProperitiesRef;
    private ValueEventListener mSMPProperitiesValueEventListener;
    private DatabaseReference mDatabaseSMPProperitiesHintsRef;
    private ValueEventListener mSMPProperitiesHintsValueEventListener;

    private FirebaseAuth mAuth;
    private static FirebaseUser mFirebaseUser;

    private static ReportBasicFirebase mReportBasicFirebase;
    private static List<ReportSingleMedia> mMedia;
    private static User mPerformingUser;

    private List<String> properitiesNames;
    private List<String> properitiesSymbols;
    private List<String> properitiesTypes;
    private List<String> properitiesHintsNames;
    private List<String> properitiesHints;

    private boolean properitiesHintsLoaded = false;
    private boolean properitiesDetailsLoaded = false;

    private List<ProperityDetails> properitiesDetails;
    private List<ValueEventListener> typeListeners;
    private List<DatabaseReference> typeReferences;
    private boolean[] properitiesReady;

    private Map<String, Long> ReportRate;


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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.judge_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.judge_opluj:
                makeReportInvalid();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge_side_mission);
        ButterKnife.bind(this);
        context = this;

        initializeFirebaseComponents();
        InitializeInReportMediaListener();
        InitializeInReportListener();
        InitializeSMPProperitiesListener();
        InitializeSMPProperitiesHintsListener();
    }

    private void makeReportInvalid() {
        if( mFirebaseUser != null ){
            mRootRef.child("InvalidReports").child(bRpid).setValue(mFirebaseUser.getUid());
            mDatabaseInReportsRef.child("valid").setValue(false);
            mRootRef.child("pendingReports").child(bRpid).setValue(null);
            mRootRef.child("requireProfRate").child(bRpid).setValue(null);

            exitJudgeActivity();
        }
    }

    private void createProperitiesDetails() {
        properitiesDetails = new ArrayList<>();
        for( int i=0; i<properitiesNames.size(); i++ ){

            if( !properitiesTypes.get(i).equals("professor_value")
                    && !properitiesNames.get(i).equals("płeć_wykonawcy")){

                for( int j=0; j<properitiesHintsNames.size(); j++ ){
                    if( properitiesNames.get(i).equals(properitiesHintsNames.get(j))){

                        properitiesDetails.add(new ProperityDetails(
                                properitiesHintsNames.get(j),
                                properitiesHints.get(j),
                                properitiesSymbols.get(i),
                                properitiesTypes.get(i)
                        ));
                    }
                }
            }
        }
        completeProperitiesDetails();
    }

    private void completeProperitiesDetails() {

        properitiesReady = new boolean[properitiesDetails.size()];
        typeListeners = new ArrayList<>();
        typeReferences = new ArrayList<>();

        for( int i=0; i<properitiesDetails.size(); i++ ){
            typeListeners.add(null);
            typeReferences.add( mDatabaseSMPProperitiesRef
                    .child(properitiesDetails.get(i).getName())
                    .child("type")
                    .child(properitiesDetails.get(i).getType()));
        }

        for( int i=0; i<properitiesDetails.size(); i++ ){
            if( typeListeners.get(i) == null ){
                final int finalI = i;
                typeListeners.set(i, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        switch (properitiesDetails.get(finalI).getType()){
                            case "spinner":
                                List<String> keys = new ArrayList<String>();
                                List<Long> values = new ArrayList<Long>();
                                for( DataSnapshot data : dataSnapshot.getChildren() ){
                                    keys.add(data.getKey());
                                    values.add(data.getValue(Long.class));
                                }
                                properitiesDetails.get(finalI).setSpinnerKeys(keys);
                                properitiesDetails.get(finalI).setSpinnerValues(values);
                                break;
                            case "limited_value":
                                properitiesDetails.get(finalI).setLimitedValue(dataSnapshot.getValue(Long.class));
                                break;
                            case "professor_value":
                                properitiesDetails.get(finalI).setProfType(dataSnapshot.getValue().toString());
                            default:
                                break;
                        }

                        properitiesReady[finalI] = true;
                        if( readyToLaunch() ){
                            InitializeProperitiesListView();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                typeReferences.get(i).addValueEventListener(typeListeners.get(i));
            }
        }
    }

    private boolean readyToLaunch(){
        for( int j=0; j<properitiesReady.length; j++ ){
            if( !properitiesReady[j] ){
                return false;
            }
        }
        return true;
    }

    private void InitializeProperitiesListView() {
        RateProperitiesAdapter adapter = new RateProperitiesAdapter(this, properitiesDetails);
        properitiesListView.setAdapter(adapter);
        InitializeSendButton();
    }

    private void InitializeSendButton() {
        sendButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( correctJudgeInput() ){
                    sendReportRate();
                }
            }
        });
    }

    private void sendReportRate() {
        ReportRate = new HashMap<>();
        for( int i=0; i<properitiesListView.getChildCount(); i++ ){

            ProperityDetails current = (ProperityDetails) properitiesListView.getAdapter().getItem(i);

            Spinner spinner;
            EditText editText;
            String selectedKey;

            switch (current.getType()){
                case "normal_value":
                    editText = (EditText) properitiesListView
                            .getChildAt(i).findViewById(R.id.item_properity_edit_text);
                    ReportRate.put(current.getSymbol(),
                            Long.valueOf(editText.getText().toString()));
                    break;
                case "limited_value":
                    spinner = (Spinner) properitiesListView
                            .getChildAt(i).findViewById(R.id.item_properity_spinner);
                    ReportRate.put(current.getSymbol(),
                            Long.valueOf(spinner.getSelectedItem().toString()));
                    break;
                case "spinner":
                    spinner = (Spinner) properitiesListView
                            .getChildAt(i).findViewById(R.id.item_properity_spinner);
                    selectedKey = spinner.getSelectedItem().toString();
                    for( int j=0; j<current.getSpinnerKeys().size(); j++ ){
                        if( selectedKey.equals(current.getSpinnerKeys().get(j))){
                            ReportRate.put(current.getSymbol(),
                                    current.getSpinnerValues().get(j));
                        }
                    }
                    break;
                case "boolean_value":
                    spinner = (Spinner) properitiesListView
                            .getChildAt(i).findViewById(R.id.item_properity_spinner);
                    selectedKey = spinner.getSelectedItem().toString();
                    if( selectedKey.equals("TAK") ){
                        ReportRate.put(current.getSymbol(), (long) 1);
                    }else {
                        ReportRate.put(current.getSymbol(), (long) 0);
                    }
                    break;
                default:
                    break;
            }

            mRootRef.child("ReportRates").child(bRpid).child(mFirebaseUser.getUid()).setValue(ReportRate);

            exitJudgeActivity();
        }
    }

    private void exitJudgeActivity(){
        if( bPreviousAmountOfReports < 2 ){
            Intent home = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("previousAmountOfReports",bPreviousAmountOfReports);
            home.putExtras(bundle);
            startActivity(home);
            finish();
        }else {
            finish();
        }
    }

    private boolean correctJudgeInput() {

        for( int i=0; i<properitiesListView.getChildCount(); i++ ){

            ProperityDetails current = (ProperityDetails) properitiesListView.getAdapter().getItem(i);
            switch (current.getType()){
                case "normal_value":
                    EditText editText = (EditText) properitiesListView
                            .getChildAt(i).findViewById(R.id.item_properity_edit_text);
                    if(editText.getText().toString().matches("")){
                        Toast.makeText(this, "Uzupełnij "+current.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
                default:
                    Spinner spinner = (Spinner) properitiesListView
                            .getChildAt(i).findViewById(R.id.item_properity_spinner);
                    if( spinner.getSelectedItem().toString().equals("<none>") ){
                        Toast.makeText(this, "Uzupełnij "+current.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
            }
        }
        return true;
    }

    private void InitializeSMPProperitiesHintsListener() {
        if( mSMPProperitiesHintsValueEventListener == null ){
            mSMPProperitiesHintsValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    properitiesHints = new ArrayList<>();
                    properitiesHintsNames = new ArrayList<>();
                    for( DataSnapshot child : dataSnapshot.getChildren() ){
                        properitiesHintsNames.add(child.getKey());
                        properitiesHints.add(child.getValue().toString());
                    }
                    properitiesHintsLoaded = true;
                    if( properitiesDetailsLoaded ){
                        createProperitiesDetails();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseSMPProperitiesHintsRef.addValueEventListener(mSMPProperitiesHintsValueEventListener);
        }
    }

    private void InitializeSMPProperitiesListener() {
        if( mSMPProperitiesValueEventListener == null ){
            mSMPProperitiesValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    properitiesNames = new ArrayList<>();
                    properitiesTypes = new ArrayList<>();
                    properitiesSymbols = new ArrayList<>();
                    for( DataSnapshot data : dataSnapshot.getChildren() ){
                        properitiesNames.add(data.getKey());
                        properitiesSymbols.add(data.child("symbol").getValue().toString());
                        for( DataSnapshot type : data.child("type").getChildren() ){
                            properitiesTypes.add(type.getKey());
                        }
                    }
                    properitiesDetailsLoaded = true;
                    if( properitiesHintsLoaded ){
                        createProperitiesDetails();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseSMPProperitiesRef.addValueEventListener(mSMPProperitiesValueEventListener);
        }
    }

    private void InitializeInReportListener() {
        if( mInReportValueEventListener == null ){
            mInReportValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mReportBasicFirebase = dataSnapshot.getValue(ReportBasicFirebase.class);
                    mDatabasePerformingUserRef = mRootRef.child("users").child(mReportBasicFirebase.getPerforming_user());

                    InitializePerformingUserListener();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseInReportsRef.addValueEventListener(mInReportValueEventListener);
        }
    }

    private void InitializePerformingUserListener(){
        if( mPerformingUserValueEventListener == null ){
            mPerformingUserValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPerformingUser = dataSnapshot.getValue(User.class);
                    InitializeWizzardTeam();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabasePerformingUserRef.addValueEventListener(mPerformingUserValueEventListener);
        }
    }

    private void InitializeWizzardTeam() {
        teamView.setText(mPerformingUser.getTeam());
        switch (mPerformingUser.getTeam()){
            case "cormeum":
                teamView.setTextColor(ContextCompat.getColor(this, R.color.red));
                break;
            case "sensum":
                teamView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case "mutinium":
                teamView.setTextColor(ContextCompat.getColor(this, R.color.green));
                break;
            default:
                break;
        }
        wizzardsIntent = new Intent(context, PrivilegeWizzardsActivity.class );
        infoView.setVisibility(View.VISIBLE);
        infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( wizzardsIntent != null ){
                    context.startActivity(wizzardsIntent);
                }
            }
        });
    }

    private void initializeFirebaseComponents() {
        bundle = this.getIntent().getExtras();
        if( bundle != null ){
            bSMName = bundle.getString("sm_name");
            bUserImageURL = bundle.getString("user_image_url");
            bUserName = bundle.getString("user_name");
            bRpid = bundle.getString("rpid");
            bPreviousAmountOfReports = bundle.getInt("previousAmountOfReports");
        }
        setTitle(bSMName);
        performingUserName.setText(bUserName);
        Glide.with(context)
                .load(bUserImageURL)
                .into(performingUserImage);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseInReportsRef = mRootRef.child("Reports").child(bRpid);
        mDatabaseInReportsMediaRef = mDatabaseInReportsRef.child("mediaUrls");
        mDatabaseSMDocsRef = mRootRef.child("SideMissionsDocs").child(bSMName);
        mDatabaseSMPProperitiesRef = mRootRef.child("SideMissionsProperities").child(bSMName).child("properities");
        mDatabaseSMPProperitiesHintsRef = mRootRef.child("SideMissionsProperities").child(bSMName).child("properitiesHints");
    }

    private void InitializeInReportMediaListener(){
        if ( mInReportMediaValueEventListener == null ){
            mInReportMediaValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mMedia = new ArrayList<>();
                    for( DataSnapshot snap : dataSnapshot.getChildren()){
                        mMedia.add( snap.getValue(ReportSingleMedia.class));
                    }
                    initializeMediaRecycler();
                    InilializeSMDocsListener();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseInReportsMediaRef.addValueEventListener(mInReportMediaValueEventListener);
        }
    }

    private void initializeMediaRecycler(){
        ReportMediaAdapter adapter = new ReportMediaAdapter(this, mMedia );
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false );
        mediaRecycler.setAdapter(adapter);
        mediaRecycler.setLayoutManager(layoutManager);
    }

    private void InilializeSMDocsListener(){
        if( mSMDocsValueEventListener == null ){
            mSMDocsValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mSMInfo = dataSnapshot.getValue(SideMissionInfo.class);
                    setGoogleDriveButton();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseSMDocsRef.addValueEventListener(mSMDocsValueEventListener);
        }
    }

    private void setGoogleDriveButton(){
        googleDriveIntent = new Intent(Intent.ACTION_VIEW);
        googleDriveIntent.setData(Uri.parse(mSMInfo.getLink()));
        smNameView.setText(bSMName);
        googleDriveView.setVisibility(View.VISIBLE);

        googleDriveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( googleDriveIntent != null ){
                    startActivity(googleDriveIntent);
                }
            }
        });
    }

    private void dettachFirebaseListeners() {
        if( mInReportMediaValueEventListener != null ){
            mDatabaseInReportsMediaRef.removeEventListener(mInReportMediaValueEventListener);
            mInReportMediaValueEventListener = null;
        }
        if( mSMDocsValueEventListener != null ){
            mDatabaseSMDocsRef.removeEventListener(mSMDocsValueEventListener);
            mSMDocsValueEventListener = null;
        }
        if( mInReportValueEventListener != null ){
            mDatabaseInReportsRef.removeEventListener(mInReportValueEventListener);
            mInReportValueEventListener = null;
        }
        if( mPerformingUserValueEventListener != null ){
            mDatabasePerformingUserRef.removeEventListener(mPerformingUserValueEventListener);
            mPerformingUserValueEventListener = null;
        }
        if( mSMPProperitiesValueEventListener != null ){
            mDatabaseSMPProperitiesRef.removeEventListener(mSMPProperitiesValueEventListener);
            mSMPProperitiesValueEventListener = null;
        }
        if( mSMPProperitiesHintsValueEventListener != null ){
            mDatabaseSMPProperitiesHintsRef.removeEventListener(mSMPProperitiesHintsValueEventListener);
            mSMPProperitiesHintsValueEventListener = null;
        }

        for( int i=0; i<typeListeners.size(); i++ ){
            if( typeListeners.get(i) != null ){
                typeReferences.get(i).removeEventListener(typeListeners.get(i));
                typeListeners.set(i, null);
            }
        }
    }
}
