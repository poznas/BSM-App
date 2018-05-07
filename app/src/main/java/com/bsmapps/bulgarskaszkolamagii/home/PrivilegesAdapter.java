package com.bsmapps.bulgarskaszkolamagii.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.adding.AddBetActivity;
import com.bsmapps.bulgarskaszkolamagii.adding.AddMCActivity;
import com.bsmapps.bulgarskaszkolamagii.adding.AddMedalActivity;
import com.bsmapps.bulgarskaszkolamagii.adding.addsm.AddSMListActivity;
import com.bsmapps.bulgarskaszkolamagii.beans.Privilege;
import com.bsmapps.bulgarskaszkolamagii.calendar.CalendarDaysActivity;
import com.bsmapps.bulgarskaszkolamagii.judge.JudgeSMListActivity;
import com.bsmapps.bulgarskaszkolamagii.mcinfo.MainCompetitionInfoActivity;
import com.bsmapps.bulgarskaszkolamagii.prof.ProfRateSMListActivity;
import com.bsmapps.bulgarskaszkolamagii.sminfo.SideMissionsInfoActivity;
import com.bsmapps.bulgarskaszkolamagii.wizzards.PrivilegeWizzardsActivity;
import com.bsmapps.bulgarskaszkolamagii.zongler.ZonglerActivity;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/21/2017.
 */

public class PrivilegesAdapter extends RecyclerView.Adapter<PrivilegesAdapter.PrivilegeViewHolder>
{
    private LayoutInflater mInflater;
    List<Privilege> privilegesList = Collections.emptyList();
    private String mTeam;

    public PrivilegesAdapter(Context context, List<Privilege> privileges, String team ){
        mInflater = LayoutInflater.from(context);
        this.privilegesList = privileges;
        this.mTeam = team;
    }

    @Override
    public PrivilegeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_privilege, parent, false );
        PrivilegeViewHolder holder = new PrivilegeViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(PrivilegeViewHolder holder, int position) {
        Privilege current = privilegesList.get(position);

        holder.setPrivilegeData( current );

    }

    @Override
    public int getItemCount() {
        return privilegesList.size();
    }

    class PrivilegeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.privilegeIcon)
        ImageView privilegeIcon;
        @BindView(R.id.privilegeBrand)
        TextView privilegeBrand;
        @BindView(R.id.privilegeParent)
        View privilegeParent;

        @BindView(R.id.progress_bar_view)
        ProgressBar progressBar;
        @BindView(R.id.pending_reports_view)
        TextView pendingReportsView;

        private final Context context;

        private Intent intent;
        private Bundle bundle;

        public PrivilegeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            privilegeParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String brand = String.valueOf(privilegeBrand.getText());
            switch ( brand )
            {
                case "Czarodzieje" :
                    intent = new Intent( context, PrivilegeWizzardsActivity.class );
                    break;
                case "Misje Poboczne" :
                    intent = new Intent(context, SideMissionsInfoActivity.class);
                    break;
                case "Konkurencje Główne" :
                    intent = new Intent(context, MainCompetitionInfoActivity.class);
                    break;
                case "Kalendarz" :
                    intent = new Intent(context, CalendarDaysActivity.class);
                    break;
                case "Konkurencja Główna":
                    intent = new Intent(context, AddMCActivity.class);
                    break;
                case "Zakład":
                    intent = new Intent(context, AddBetActivity.class);
                    break;
                case "Medal":
                    intent = new Intent(context, AddMedalActivity.class);
                    break;
                case "Żongler":
                    intent = new Intent(context, ZonglerActivity.class);
                    break;
                case "Melduj":
                    intent = new Intent(context, AddSMListActivity.class);
                    bundle = new Bundle();
                    bundle.putString("team",mTeam);
                    intent.putExtras(bundle);
                    break;
                case "Oceń":
                    break;
                case "Opiniuj":
                    break;
                default:
                    intent = new Intent( context, MainActivity.class );
                    break;
            }
            context.startActivity(intent);
        }

        public void setPrivilegeData(Privilege current) {
            privilegeBrand.setText(current.getBrand());
            privilegeIcon.setImageResource(current.getIconId());

            if( current.isCheckIfContain() ){
                switch (current.getPendingReports()){
                    case -1 :
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case 0 :
                        progressBar.setVisibility(View.GONE);
                        break;
                    default:
                        progressBar.setVisibility(View.GONE);
                        pendingReportsView.setVisibility(View.VISIBLE);
                        pendingReportsView.setText(String.valueOf(current.getPendingReports()));
                        break;
                }
            }

            if( current.getBrand().equals("Oceń")){
                intent = new Intent(context, JudgeSMListActivity.class);
                bundle = new Bundle();
                bundle.putInt("previousAmountOfReports",current.getPendingReports());
                intent.putExtras(bundle);
            }

            if( current.getBrand().equals("Opiniuj")){
                intent = new Intent(context, ProfRateSMListActivity.class);
                bundle = new Bundle();
                bundle.putInt("previousAmountOfReports",current.getPendingReports());
                intent.putExtras(bundle);
            }
        }
    }
}
