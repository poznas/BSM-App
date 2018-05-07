package com.bsmapps.bulgarskaszkolamagii.wizzards;

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
 * Created by Mlody Danon on 7/24/2017.
 */

public class PrivilegeWizzardsActivity extends AppCompatActivity {

    private static final String TAG = "PrivilegeWizzardsActivity";

    @BindView(R.id.simple_recycler)
    RecyclerView usersRecycler;

    private static DatabaseReference mRootRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recycler_list);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        initializeUsersRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initializeUsersRecycler() {
        UsersAdapter adapter = new UsersAdapter(this,
                mRootRef.child("users").orderByChild("team").startAt(true));
        usersRecycler.setAdapter(adapter);
        usersRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
