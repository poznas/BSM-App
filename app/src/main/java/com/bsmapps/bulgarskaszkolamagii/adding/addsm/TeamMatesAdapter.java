package com.bsmapps.bulgarskaszkolamagii.adding.addsm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.User;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mlody Danon on 7/29/2017.
 */

public class TeamMatesAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<User> mTeamMates;
    private List<String> mTeamMatesIds;

    public TeamMatesAdapter(Context context, List<User> mTeamMates, List<String> mTeamMatesIds) {
        this.context = context;
        this.mTeamMates = mTeamMates;
        this.mTeamMatesIds = mTeamMatesIds;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mTeamMates.size();
    }

    @Override
    public Object getItem(int position) {
        return mTeamMatesIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.item_simple_wizzard, parent, false );

        CircleImageView userImage = ( CircleImageView ) convertView.findViewById(R.id.item_user_image_view);
        TextView userDisplayName = (TextView) convertView.findViewById(R.id.item_display_name_text_view);

        userDisplayName.setText(mTeamMates.get(position).getDisplayName());
        String photoUrl = mTeamMates.get(position).getPhotoUrl();
        if( photoUrl != null ){
            Glide.with(context)
                    .load(photoUrl)
                    .into(userImage);
        }
        return convertView;
    }
}
