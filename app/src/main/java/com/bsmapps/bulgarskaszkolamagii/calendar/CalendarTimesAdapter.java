package com.bsmapps.bulgarskaszkolamagii.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.CalendarTime;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class CalendarTimesAdapter extends FirebaseRecyclerAdapter<CalendarTime, CalendarTimesAdapter.CalendarTimesViewHolder> {

    private final Context context;

    public CalendarTimesAdapter(Context context, Query ref) {
        super(CalendarTime.class, R.layout.item_calendar_time, CalendarTimesViewHolder.class, ref);
        this.context = context;
    }

    @Override
    public CalendarTimesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_time, parent, false);

        return new CalendarTimesViewHolder(itemView);
    }

    @Override
    protected void populateViewHolder(CalendarTimesViewHolder viewHolder, CalendarTime calendarTime, int position) {
        viewHolder.setCalendarTime(calendarTime);
    }

    public class CalendarTimesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_time_text_view)
        TextView itemTimeView;
        @BindView(R.id.item_info_text_view)
        TextView itemInfoView;

        public CalendarTimesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setCalendarTime(CalendarTime calendarTime) {
            itemTimeView.setText(calendarTime.getTime());
            itemInfoView.setText(calendarTime.getInfo());
        }
    }
}
