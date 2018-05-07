package com.bsmapps.bulgarskaszkolamagii.prof;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mlody Danon on 8/8/2017.
 */

public class ProfRateProperitiesAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<ProperityDetails> properitiesDetails;

    public ProfRateProperitiesAdapter(Context context, List<ProperityDetails> properitiesDetails) {
        this.context = context;
        this.properitiesDetails = properitiesDetails;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return properitiesDetails.size();
    }

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

        switch (current.getProfType()){
            case "true":
                properityEditText.setVisibility(View.VISIBLE);
                properitySpinner.setVisibility(View.GONE);
                break;
            case "boolean":
                List<String> answers = new ArrayList<String>();
                answers.add("<none>"); answers.add("TAK"); answers.add("NIE");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, answers);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                properitySpinner.setAdapter(dataAdapter);
                properitySpinner.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
