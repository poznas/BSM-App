package com.bsmapps.bulgarskaszkolamagii.adding.addsm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.compat.BuildConfig;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.User;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ProofExample;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ReportBasicFirebase;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ReportSingleMedia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.webkit.WebSettings.PluginState.ON;

public class AddSMDetailsActivity extends AppCompatActivity {

    private static final String TAG = "AddSMDetailsActivity";
    private Context context;

    @BindView(R.id.performing_user_spinner)
    Spinner performingUserSpinner;
    @BindView(R.id.proofs_recycler)
    RecyclerView proofsRecycler;
    @BindView(R.id.pick_files_button)
    View pickFilesButton;
    @BindView(R.id.progress_bar_view)
    ProgressBar progressBar;
    @BindView(R.id.send_button)
    View sendButton;

    private Bundle bundle;
    private String bSMName;
    private String bTeam;

    private static DatabaseReference mRootRef;
    private DatabaseReference mDatabaseSMPProofsRef;
    private ValueEventListener mSMPProofsValueEventListener;
    private Query mDatabaseUsersQuery;
    private ValueEventListener mUsersValueEventListener;
    private List<String> usersRestrictions;

    private FirebaseAuth mAuth;
    private static FirebaseUser mFirebaseUser;

    private static List<User> teamMates;
    private static List<String> teamMatesIds;
    private static List<ProofExample> proofExamples;

    static final int PICK_FILES_REQUEST = 2137;
    static final int PICK_FILES_KITKAT = 1488;
    private List<String> requestedProofTypes;
    private List<String> inputMediaTypes;
    private Uri[] mediaUris;
    private String performingUserId;

    UploadStorageWakefulReceiver mReceiver;
    private boolean isProviderRegistered = false;
    public static final String BROADCAST =
            "com.bsmapps.bulgarskaszkolamagii.adding.addsm.android.action.broadcast";


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
    protected void onDestroy() {
        super.onDestroy();
        if( isProviderRegistered ){
            unregisterReceiver(mReceiver);
            isProviderRegistered = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        setContentView(R.layout.activity_add_sm_details);
        ButterKnife.bind(this);
        context = this;


        initializeFirebaseComponents();
        initializeUsersListener();
        initializeProofsListener();
        setPickFilesButton();
    }

    private void setPickFilesButton() {
        final Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("*/*");
        String[] mimetypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 19){
                    startActivityForResult(Intent.createChooser(intent, "Wybierz pliki"),
                            PICK_FILES_REQUEST);
                }else{
                    startActivityForResult(intent, PICK_FILES_KITKAT);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ) {
            pickFilesButton.setOnClickListener(null);
            progressBar.setVisibility(View.VISIBLE);
            if (data.getData() != null) {
                mediaUris = new Uri[1];
                if( requestCode == PICK_FILES_REQUEST ){
                    mediaUris[0] = data.getData();
                }else if( requestCode == PICK_FILES_KITKAT ){
                    mediaUris[0] = data.getData();
                    grantUriPermission(getPackageName(), mediaUris[0],
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(mediaUris[0], takeFlags);
                }
                Log.d(TAG, "mediaUris[0]: " + mediaUris[0]);
                initializeSendButton();
            } else {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    mediaUris = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        if( requestCode == PICK_FILES_REQUEST ){
                            mediaUris[i] = clipData.getItemAt(i).getUri();

                        }else if( requestCode == PICK_FILES_KITKAT ){
                            mediaUris[i] = clipData.getItemAt(i).getUri();
                            grantUriPermission(getPackageName(), mediaUris[i],
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            final int takeFlags = data.getFlags()
                                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            getContentResolver().takePersistableUriPermission(mediaUris[i], takeFlags);

                        }
                        Log.d(TAG, "mediaUris[i]: " + mediaUris[i]);
                    }
                    initializeSendButton();
                }
            }
        }
    }

    private void initializeSendButton() {

        getInputMediaTypes();
        progressBar.setVisibility(View.GONE);
        sendButton.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (performingUserSpinner.getSelectedItem() != null) {

                    performingUserId = performingUserSpinner.getSelectedItem().toString();

                    if( mediaTypesCorrect() ){
                        setUpUpload();
                    } else {
                        setPickFilesButton();
                    }
                } else {
                    Toast.makeText(context, "Wybierz Czarodzieja", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getInputMediaTypes() {
        inputMediaTypes = new ArrayList<>();
        ContentResolver cR = this.getContentResolver();
        String mime;

        for (int i = 0; i < mediaUris.length; i++) {
            mime = cR.getType(mediaUris[i]);
            if (mime.startsWith("image")) {
                inputMediaTypes.add("photo");
            } else {
                inputMediaTypes.add("video");
            }
        }
    }


    private boolean mediaTypesCorrect() {
        if( requestedProofTypes == null ){ return false; }

        List<String> temporaryRequestedTypes
                = new ArrayList<>(requestedProofTypes);

        int basicMatches = 0;

        for( String input : inputMediaTypes ){
            for( String requested : requestedProofTypes){
                if( input.equals(requested) ){
                    int index = temporaryRequestedTypes.lastIndexOf(requested);
                    if( index > -1 ){
                        temporaryRequestedTypes.remove( index );
                        basicMatches += 1;
                    }
                    break;
                }
            }
        }
        if( temporaryRequestedTypes.contains("photo")
                || temporaryRequestedTypes.contains("video")){
            Toast.makeText(context, "nieprawidłowe typy dowodów", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( inputMediaTypes.size() - basicMatches < temporaryRequestedTypes.size() ){
            Toast.makeText(context, "za mało dowodów", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setUpUpload() {

        if( mReceiver == null ){
            mReceiver = new UploadStorageWakefulReceiver();
            IntentFilter intentFilter = new IntentFilter(BROADCAST);
            registerReceiver( mReceiver , intentFilter);
            isProviderRegistered = true;

            Intent intent = new Intent(BROADCAST);
            Bundle bundle = new Bundle();
            bundle.putString("sm_name",bSMName);
            bundle.putString("performingUser",performingUserId);
            bundle.putString("recordingUser",mFirebaseUser.getUid());

            for( int i=0; i<mediaUris.length; i++ ){
                bundle.putString("mediaUri"+String.valueOf(i),mediaUris[i].toString());
            }
            bundle.putInt("mediaUrisAmount",mediaUris.length);

            intent.putExtras(bundle);
            sendBroadcast(intent);
        }


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        finish();
    }

    private void initializeFirebaseComponents() {

        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            bSMName = bundle.getString("sm_name");
            bTeam = bundle.getString("team");
        }
        setTitle(bSMName);
        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseSMPProofsRef = mRootRef
                .child("SideMissionsProperities")
                .child(bSMName)
                .child("proofs");

        usersRestrictions = new ArrayList<>();
        switch (bTeam) {
            case "cormeum":
                usersRestrictions.add("c");
                usersRestrictions.add("c\uf8ff");
                break;
            case "sensum":
                usersRestrictions.add("s");
                usersRestrictions.add("s\uf8ff");
                break;
            case "mutinium":
                usersRestrictions.add("m");
                usersRestrictions.add("m\uf8ff");
                break;
            default:
                break;
        }
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabaseUsersQuery = mRootRef.child("users")
                .orderByChild("team")
                .startAt(usersRestrictions.get(0))
                .endAt(usersRestrictions.get(1));
    }

    private void initializeUsersListener() {
        if (mUsersValueEventListener == null) {
            mUsersValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User noneUser = new User();
                    noneUser.setDisplayName("none");
                    noneUser.setPhotoUrl(null);

                    teamMates = new ArrayList<>();
                    teamMatesIds = new ArrayList<>();

                    teamMates.add(noneUser);
                    teamMatesIds.add(null);

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        teamMates.add(child.getValue(User.class));
                        teamMatesIds.add(child.getKey());
                    }
                    initializePerformingUserSpinner();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseUsersQuery.addValueEventListener(mUsersValueEventListener);
        }
    }

    private void initializeProofsListener() {
        if (mSMPProofsValueEventListener == null) {
            mSMPProofsValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    requestedProofTypes = new ArrayList<>();
                    proofExamples = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        proofExamples.add(child.getValue(ProofExample.class));
                        requestedProofTypes.add(child.getValue(ProofExample.class).getType());
                    }
                    initializeProofExamplesRecycler();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseSMPProofsRef.addValueEventListener(mSMPProofsValueEventListener);
        }
    }

    private void initializeProofExamplesRecycler() {
        ProofExamplesAdapter adapter = new ProofExamplesAdapter(this, proofExamples);
        proofsRecycler.setAdapter(adapter);
        proofsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void dettachFirebaseListeners() {
        if (mUsersValueEventListener != null) {
            mDatabaseUsersQuery.removeEventListener(mUsersValueEventListener);
            mUsersValueEventListener = null;
        }
        if (mSMPProofsValueEventListener != null) {
            mDatabaseSMPProofsRef.removeEventListener(mSMPProofsValueEventListener);
            mSMPProofsValueEventListener = null;
        }
    }

    private void initializePerformingUserSpinner() {
        TeamMatesAdapter adapter = new TeamMatesAdapter(this, teamMates, teamMatesIds);
        performingUserSpinner.setAdapter(adapter);
    }
}


