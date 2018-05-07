package com.bsmapps.bulgarskaszkolamagii.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.sminfo.GoogleDriveLinkAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class CalendarDaysActivity extends AppCompatActivity {

    private static final String TAG = "CalendarDaysActivity";

    @BindView(R.id.simple_recycler)
    RecyclerView DayRecycler;

    private static DatabaseReference mRootRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recycler_list);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        initializeDaysRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initializeDaysRecycler() {
        CalendarDaysAdapter adapter = new CalendarDaysAdapter(this,
                mRootRef.child("CalendarDays").orderByChild("timestamp"));
        DayRecycler.setAdapter(adapter);
        DayRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
