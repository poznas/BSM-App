package com.bsmapps.bulgarskaszkolamagii.calendar;

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

public class CalendarTimesActivity extends AppCompatActivity {

    private static final String TAG = "CalendarTimesActivity";

    @BindView(R.id.simple_recycler)
    RecyclerView TimeRecycler;

    private static DatabaseReference mRootRef;

    private String dayId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recycler_list);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = this.getIntent().getExtras();
        if( bundle != null ){
            dayId = bundle.getString("day");
        }

        initializeTimeRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initializeTimeRecycler() {
        CalendarTimesAdapter adapter = new CalendarTimesAdapter(this,
                mRootRef.child("CalendarTimes").child(dayId).orderByChild("time"));
        TimeRecycler.setAdapter(adapter);
        TimeRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}