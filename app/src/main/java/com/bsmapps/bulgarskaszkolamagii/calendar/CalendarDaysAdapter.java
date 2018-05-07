package com.bsmapps.bulgarskaszkolamagii.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.CalendarDay;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class CalendarDaysAdapter  extends FirebaseRecyclerAdapter<CalendarDay, CalendarDaysAdapter.CalendarDaysViewHolder> {

    private final Context context;

    public CalendarDaysAdapter(Context context, Query ref) {
        super(CalendarDay.class, R.layout.item_name_and_arrow, CalendarDaysViewHolder.class, ref);
        this.context = context;
    }

    @Override
    public CalendarDaysViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_name_and_arrow, parent, false);

        return new CalendarDaysViewHolder(itemView);
    }

    @Override
    protected void populateViewHolder(CalendarDaysViewHolder viewHolder, CalendarDay day, int position) {
        viewHolder.setDay(day);
    }

    public class CalendarDaysViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.item_name_text_view)
        TextView itemDay;
        @BindView(R.id.item_parent)
        View itemParent;

        private Intent timesIntent;
        private Bundle bundle;

        public CalendarDaysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.startActivity(timesIntent);
        }

        public void setDay(CalendarDay day) {

            Date date = new Date(day.getTimestamp()*1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy / EEEE");
            String formattedDate = sdf.format(date);
            itemDay.setText(formattedDate);

            Long todayUnix = System.currentTimeMillis()/1000;
            Long timeDifference = todayUnix - day.getTimestamp();

            if( 0 < timeDifference && timeDifference < 86400 ){
                itemParent.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
            }

            timesIntent = new Intent(context, CalendarTimesActivity.class);
            bundle = new Bundle();
            bundle.putString("day", day.getDay() );
            timesIntent.putExtras(bundle);
        }
    }
}
