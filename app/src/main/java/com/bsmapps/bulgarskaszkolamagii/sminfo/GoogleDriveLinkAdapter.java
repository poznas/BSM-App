package com.bsmapps.bulgarskaszkolamagii.sminfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.SideMissionInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class GoogleDriveLinkAdapter extends FirebaseRecyclerAdapter<SideMissionInfo, GoogleDriveLinkAdapter.SideMissionsInfoViewHolder> {

    private final Context context;

    public GoogleDriveLinkAdapter(Context context, Query ref) {
        super(SideMissionInfo.class, R.layout.item_google_drive_link, SideMissionsInfoViewHolder.class, ref);
        this.context = context;
    }

    @Override
    public SideMissionsInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_google_drive_link, parent, false);

        return new SideMissionsInfoViewHolder(itemView);
    }

    @Override
    protected void populateViewHolder(SideMissionsInfoViewHolder viewHolder, SideMissionInfo sminfo, int position) {
        viewHolder.setSideMissionInfo(sminfo);
    }

    public class SideMissionsInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.item_name_text_view)
        TextView sideMissionName;
        @BindView(R.id.item_parent)
        View sideMissionParent;

        private Intent googleDriveIntent;

        public SideMissionsInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sideMissionParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.startActivity(googleDriveIntent);
        }

        void setSideMissionInfo( SideMissionInfo sminfo )
        {
            sideMissionName.setText(sminfo.getName());
            googleDriveIntent = new Intent(Intent.ACTION_VIEW);
            googleDriveIntent.setData(Uri.parse(sminfo.getLink()));
        }
    }
}
