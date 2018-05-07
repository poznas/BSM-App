package com.bsmapps.bulgarskaszkolamagii.adding.addsm;

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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ReportBasicFirebase;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ReportSingleMedia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bsmapps.bulgarskaszkolamagii.adding.addsm.AddSMDetailsActivity.PICK_FILES_REQUEST;

public class AddSMPostActivity extends AppCompatActivity {

    private static final String TAG = "AddSMPostActivity";
    private Context context;

    @BindView(R.id.title_edit_text)
    EditText titleEditText;
    @BindView(R.id.body_edit_text)
    EditText bodyEditText;
    @BindView(R.id.pick_files_button)
    View pickFilesButton;
    @BindView(R.id.progress_bar_view)
    ProgressBar progressBar;
    @BindView(R.id.ok_image_view)
    ImageView okView;
    @BindView(R.id.send_button)
    View sendButton;

    private Bundle bundle;
    private String bSMName;

    private FirebaseAuth mAuth;
    private static FirebaseUser mFirebaseUser;

    static final int PICK_FILES_REQUEST = 2137;
    static final int PICK_FILES_KITKAT = 1488;
    private List<String> requestedProofTypes;
    private Uri[] mediaUris;
    private String performingUserId;

    UploadStorageWakefulReceiver mReceiver;
    private boolean isProviderRegistered = false;
    public static final String BROADCAST =
            "com.bsmapps.bulgarskaszkolamagii.adding.addsm.android.action.broadcast";

    private String mTitle;
    private String mBody;

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


        setContentView(R.layout.activity_add_sm_post);
        ButterKnife.bind(this);
        context = this;

        initializeFirebaseComponents();
        setPickFileButton();
        setSendButton();
    }

    private void setSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isPostCorrect() ){
                    okView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    setUpUpload();
                }
            }
        });
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
            bundle.putString("body",mBody);
            bundle.putString("title",mTitle);
            if( mediaUris != null ){
                for( int i=0; i<mediaUris.length; i++ ){
                    bundle.putString("mediaUri"+String.valueOf(i),mediaUris[i].toString());
                }
                bundle.putInt("mediaUrisAmount",mediaUris.length);
            } else {
                bundle.putInt("mediaUrisAmount",0);
            }

            intent.putExtras(bundle);
            sendBroadcast(intent);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        finish();
    }


    private boolean isPostCorrect() {
        mTitle = titleEditText.getText().toString();
        mBody = bodyEditText.getText().toString();

        if( mTitle.matches("")){
            Toast.makeText(this, "Kurwa a tytuł nie ma typie?", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( mBody.matches("")){
            Toast.makeText(this, "Jeszcze treść", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setPickFileButton() {
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
                progressBar.setVisibility(View.GONE);
                okView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initializeFirebaseComponents() {
        bundle = this.getIntent().getExtras();
        if( bundle != null ){
            bSMName = bundle.getString("sm_name");
        }
        setTitle(bSMName);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        performingUserId = mFirebaseUser.getUid();
    }
}
