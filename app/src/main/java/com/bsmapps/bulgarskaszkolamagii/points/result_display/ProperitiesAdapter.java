package com.bsmapps.bulgarskaszkolamagii.points.result_display;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ProperityToDisplay;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ReportSingleMedia;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/29/2017.
 */

public class ProperitiesAdapter extends RecyclerView.Adapter<ProperitiesAdapter.ProperitiesViewHolder>{

    private LayoutInflater mInflater;
    private Context context;
    List<ProperityToDisplay> mProperitiesList;

    public ProperitiesAdapter(Context context, List<ProperityToDisplay> List ) {
        mInflater = LayoutInflater.from(context);
        this.mProperitiesList = List;
        this.context = context;
    }

    @Override
    public ProperitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_properities_component, parent, false );
        ProperitiesViewHolder holder = new ProperitiesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ProperitiesViewHolder holder, int position) {
        ProperityToDisplay current = mProperitiesList.get(position);
        holder.setProperityData(current);

    }

    @Override
    public int getItemCount() {
        return mProperitiesList.size();
    }

    public class ProperitiesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_properity_name_view)
        TextView nameView;
        @BindView(R.id.item_properity_grade_view)
        TextView gradeView;


        public ProperitiesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setProperityData(ProperityToDisplay current) {

            nameView.setText(current.getName());
            Double rounded = (double) Math.round(current.getGrade()*100)/100;
            gradeView.setText(String.valueOf(rounded));
        }
    }
}
