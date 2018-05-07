package com.bsmapps.bulgarskaszkolamagii.points;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.PointsInfo;
import com.bsmapps.bulgarskaszkolamagii.points.result_display.BetResultDisplayActivity;
import com.bsmapps.bulgarskaszkolamagii.points.result_display.MCResultDisplayActivity;
import com.bsmapps.bulgarskaszkolamagii.points.result_display.MedalResultDisplayActivity;
import com.bsmapps.bulgarskaszkolamagii.points.result_display.SMPostResultDisplayActivity;
import com.bsmapps.bulgarskaszkolamagii.points.result_display.SMResultDisplayActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.label;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class PointsAdapter extends FirebaseRecyclerAdapter<PointsInfo, PointsAdapter.PointsViewHolder> {

    private final Context context;
    private final String team;

    public PointsAdapter(Context context, Query ref, String team ) {
        super(PointsInfo.class, R.layout.item_points, PointsViewHolder.class, ref);
        this.context = context;
        this.team = team;
    }

    @Override
    public PointsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_points, parent, false);

        return new PointsViewHolder(itemView);
    }

    @Override
    protected void populateViewHolder(PointsViewHolder viewHolder, PointsInfo points, int position) {

        DatabaseReference keyRef = this.getRef(position);
        viewHolder.setPointsInfo(points, keyRef.getKey());
    }

    public class PointsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.item_parent)
        View itemParent;
        @BindView(R.id.item_time_text_view)
        TextView itemTime;
        @BindView(R.id.item_date_text_view)
        TextView itemDate;
        @BindView(R.id.item_user_image_view)
        CircleImageView itemUserImage;
        @BindView(R.id.item_description_text_view)
        TextView itemDescription;
        @BindView(R.id.item_label_text_view)
        TextView itemLabel;
        @BindView(R.id.item_points_text_view)
        TextView itemPoints;

        private Intent resultDisplayIntent;
        private Bundle bundle;


        public PointsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if( resultDisplayIntent != null ){
                context.startActivity(resultDisplayIntent);
            }
        }

        public void setPointsInfo(PointsInfo points, String key) {

            Date dateObject = new Date(points.getTimestamp());
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

            String date = sdfDate.format(dateObject);
            String time = sdfTime.format(dateObject);

            itemDate.setText(date);
            itemTime.setText(time);

            if( points.getPoints() != null ){
                itemPoints.setText(points.getPoints().toString());
            }

            String label = points.getLabel();
            itemLabel.setText(label);

            if ( label.equals("B") || label.equals("S") ){

                itemUserImage.setVisibility(View.GONE);
                itemDescription.setText(points.getInfo());
                itemDescription.setPadding(0,0,0,0);

            }else if ( label.equals("MC")){
                itemUserImage.setVisibility(View.GONE);
                itemDescription.setText(points.getName());
                itemDescription.setTypeface(null, Typeface.BOLD);
                itemDescription.setPadding(0,0,0,0);
            }else {
                itemDescription.setText(points.getUser_name());
                itemDescription.setTypeface(null, Typeface.BOLD);

                Glide.with(context)
                        .load(points.getUser_photo())
                        .into(itemUserImage);
            }

            switch (team){
                case "cormeum":
                    itemPoints.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;
                case "sensum":
                    itemPoints.setTextColor(ContextCompat.getColor(context, R.color.blue));
                    break;
                case "mutinium":
                    itemPoints.setTextColor(ContextCompat.getColor(context, R.color.green));
                    break;
                default:
                    break;
            }

            setIntentandBundle(points, date, time, key );
        }

        private void setIntentandBundle(PointsInfo points, String date, String time, String key ) {

            switch (points.getLabel()){

                case "MC":
                    resultDisplayIntent = new Intent(context, MCResultDisplayActivity.class);
                    bundle = new Bundle();
                    bundle.putString("points",points.getPoints().toString());
                    bundle.putString("team",team);
                    bundle.putString("name",points.getName());
                    bundle.putString("info",points.getInfo());
                    bundle.putString("date",date);
                    bundle.putString("time",time);
                    resultDisplayIntent.putExtras(bundle);
                    break;
                case "B":
                    resultDisplayIntent = new Intent(context, BetResultDisplayActivity.class);
                    bundle = new Bundle();
                    bundle.putString("points",points.getPoints().toString());
                    bundle.putString("team",team);
                    bundle.putString("info",points.getInfo());
                    bundle.putString("date",date);
                    bundle.putString("time",time);
                    bundle.putString("loser",points.getLosser());
                    bundle.putString("winner",points.getWinner());
                    resultDisplayIntent.putExtras(bundle);
                    break;
                case "S":
                    resultDisplayIntent = new Intent(context, MedalResultDisplayActivity.class);
                    bundle = new Bundle();
                    bundle.putLong("points",points.getPoints());
                    bundle.putString("team",team);
                    bundle.putString("info",points.getInfo());
                    bundle.putString("date",date);
                    bundle.putString("time",time);
                    resultDisplayIntent.putExtras(bundle);
                    break;
                case "SM":
                    if( points.getIsPost() ){
                        resultDisplayIntent = new Intent(context, SMPostResultDisplayActivity.class);
                    } else {
                        resultDisplayIntent = new Intent(context, SMResultDisplayActivity.class);
                    }
                    bundle = new Bundle();
                    bundle.putString("date",date);
                    bundle.putString("time",time);
                    bundle.putLong("points",points.getPoints());
                    bundle.putString("rpid",key);
                    bundle.putString("team",points.getTeam());
                    resultDisplayIntent.putExtras(bundle);
                    break;
                default:
                    break;
            }
        }
    }
}
