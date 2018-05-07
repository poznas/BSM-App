package com.bsmapps.bulgarskaszkolamagii.adding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.shipment.MCShipment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/26/2017.
 */

public class AddMCActivity extends AppCompatActivity {

    private static final String TAG = "AddMCActivity";

    @BindView(R.id.name_edit_text)
    EditText nameEditText;
    @BindView(R.id.team_spinner)
    Spinner teamSpinner;
    @BindView(R.id.points_edit_text)
    EditText pointsEditText;
    @BindView(R.id.info_edit_text)
    EditText infoEditText;
    @BindView(R.id.button_view)
    View buttonView;

    private static DatabaseReference mRootRef;
    private DatabaseReference mDatabaseMCPointsRef;
    private String mPushId;

    private String mName;
    private String mTeam;
    private Long mPoints;
    private String mInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mc);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseMCPointsRef = mRootRef.child("MainCompetitionPoints");

        handleTeamSpinner();
        setOnClickListenerSendButton();
    }

    private void setOnClickListenerSendButton() {
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( checkCorrectness() ){
                    MCShipment shipment = new MCShipment( mPoints, mTeam, mName, mInfo );
                    mPushId = mDatabaseMCPointsRef.push().getKey();
                    mDatabaseMCPointsRef.child(mPushId).setValue(shipment);
                    mDatabaseMCPointsRef.child(mPushId).child("timestamp").setValue(ServerValue.TIMESTAMP);

                    finish();
                }
            }
        });
    }

    private boolean checkCorrectness() {
        mName = nameEditText.getText().toString();
        mTeam = teamSpinner.getSelectedItem().toString();
        mInfo = infoEditText.getText().toString();
        String pointsString = pointsEditText.getText().toString();

        if( mName.matches("")){
            Toast.makeText(this, "Kurwa nazwy nie ma typie", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( mTeam.equals("<none>")){
            Toast.makeText(this, "Ale dom to byś wybrał", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( pointsString.matches("")){
            Toast.makeText(this, "Kurwa punkciki typie", Toast.LENGTH_SHORT).show();
            return false;
        }
        mPoints = Long.parseLong(pointsString);
        if( mPoints <= 0 ){
            Toast.makeText(this, "Punkty muszą być dodatnie", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( mInfo.matches("")){
            Toast.makeText(this, "Opis dziubnij", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void handleTeamSpinner(){

        List<String> teams = new ArrayList<String>();
        teams.add("<none>"); teams.add("cormeum"); teams.add("sensum"); teams.add("mutinium");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teams);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(dataAdapter);
    }
}

