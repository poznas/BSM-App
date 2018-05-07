package com.bsmapps.bulgarskaszkolamagii.adding.addsm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddSMListActivity extends AppCompatActivity {

    @BindView(R.id.simple_recycler)
    RecyclerView listSMRecycler;

    private static DatabaseReference mRootRef;
    private Bundle bundle;

    private String mTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recycler_list);
        ButterKnife.bind(this);

        bundle = this.getIntent().getExtras();
        if( bundle != null ){
            mTeam = bundle.getString("team");
        }

        setTitle("Melduj");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        initializeSMRecycler();
    }

    private void initializeSMRecycler(){
        SMListAdapter adapter = new SMListAdapter(this,
                mRootRef.child("SideMissionsDocs").orderByChild("name"), mTeam );
        listSMRecycler.setAdapter(adapter);
        listSMRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
