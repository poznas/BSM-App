package com.bsmapps.bulgarskaszkolamagii.zongler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZonglerActivity extends AppCompatActivity {

    private static final String TAG = "ZonglerActivity";

    @BindView(R.id.simple_recycler)
    RecyclerView PostRecycler;

    private static DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recycler_list);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        initializePostRecycler();
    }

    private void initializePostRecycler() {
        PostAdapter adapter = new PostAdapter(this,
                mRootRef.child("Zongler").orderByChild("timestamp"));
        PostRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        PostRecycler.setLayoutManager(layoutManager);
    }
}
