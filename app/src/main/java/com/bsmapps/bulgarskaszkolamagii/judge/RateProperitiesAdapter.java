package com.bsmapps.bulgarskaszkolamagii.judge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ProperityDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mlody Danon on 8/8/2017.
 */

public class RateProperitiesAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<ProperityDetails> properitiesDetails;

    public RateProperitiesAdapter(Context context, List<ProperityDetails> properitiesDetails) {
        this.context = context;
        this.properitiesDetails = properitiesDetails;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {return properitiesDetails.size();}

    @Override
    public Object getItem(int position) {
        return properitiesDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.item_rate_sm_properity, parent, false );

        TextView properityName = ( TextView ) convertView.findViewById(R.id.item_properity_name);
        TextView properityHint = ( TextView ) convertView.findViewById(R.id.item_properity_hint);
        EditText properityEditText = ( EditText ) convertView.findViewById(R.id.item_properity_edit_text);
        Spinner properitySpinner = ( Spinner ) convertView.findViewById(R.id.item_properity_spinner);

        ProperityDetails current = properitiesDetails.get(position);

        properityName.setText(current.getName());
        properityHint.setText(current.getHint());

        if( current.getType().equals("normal_value") ){
            properityEditText.setVisibility(View.VISIBLE);
            properitySpinner.setVisibility(View.GONE);
        }else{
            InitializeSpinner(current, position, properitySpinner);
            properitySpinner.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private void InitializeSpinner( ProperityDetails current, int position, Spinner properitySpinner ) {

        switch (current.getType()){
            case "boolean_value":
                InitializeBooleanSpinner(current, position, properitySpinner);
                break;
            case "limited_value":
                InitializeLimitedSpinner(current, position, properitySpinner);
                break;
            case "spinner":
                InitializeClassicSpinner(current, position, properitySpinner);
            default:
                break;
        }
    }

    private void InitializeClassicSpinner(ProperityDetails current, int position, Spinner properitySpinner) {
        List<String> keys = new ArrayList<String>();
        keys.add("<none>");
        for( int i=0; i<current.getSpinnerKeys().size(); i++ ){
            keys.add(current.getSpinnerKeys().get(i));
        }
        setProperitySpinner( properitySpinner, keys);
    }

    private void InitializeLimitedSpinner(ProperityDetails current, int position, Spinner properitySpinner) {

        List<String> values = new ArrayList<String>();
        values.add("<none>");
        for( int i=1; i<=current.getLimitedValue(); i++ ){
            values.add(String.valueOf(i));
        }
        setProperitySpinner( properitySpinner, values);
    }

    private void InitializeBooleanSpinner( ProperityDetails current, int position, Spinner properitySpinner) {

        List<String> answers = new ArrayList<String>();
        answers.add("<none>"); answers.add("TAK"); answers.add("NIE");
        setProperitySpinner( properitySpinner, answers);
    }

    private void setProperitySpinner(Spinner properitySpinner, List<String> values ){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        properitySpinner.setAdapter(dataAdapter);
    }
}
