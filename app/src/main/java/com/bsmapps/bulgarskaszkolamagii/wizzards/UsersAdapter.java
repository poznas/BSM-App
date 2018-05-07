package com.bsmapps.bulgarskaszkolamagii.wizzards;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.User;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mlody Danon on 7/24/2017.
 */

public class UsersAdapter extends FirebaseRecyclerAdapter<User, UsersAdapter.UserViewHolder>{

    private final Context context;

    public UsersAdapter(Context context, Query ref) {
        super(User.class, R.layout.item_wizzard, UserViewHolder.class, ref);
        this.context = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wizzard, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    protected void populateViewHolder(UsersAdapter.UserViewHolder viewHolder, User user, int position) {
        viewHolder.setUser(user);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_user_image_view)
        CircleImageView itemUserImage;
        @BindView(R.id.item_display_name_text_view)
        TextView itemUserDisplayName;
        @BindView(R.id.item_team_text_view)
        TextView itemUserTeam;
        @BindView(R.id.item_user_parent)
        View itemUserParent;

        private Intent facebookIntent;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemUserParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.startActivity(facebookIntent);
        }

        void setUser( User user ){
            itemUserDisplayName.setText(user.getDisplayName());
            itemUserTeam.setText(user.getTeam());

            switch (user.getTeam()){
                case "cormeum":
                    itemUserTeam.setTextColor(context.getResources().getColor(R.color.red));
                    break;
                case "sensum":
                    itemUserTeam.setTextColor(context.getResources().getColor(R.color.blue));
                    break;
                case "mutinium":
                    itemUserTeam.setTextColor(context.getResources().getColor(R.color.green));
                    break;
                default:
                    break;
            }

            Glide.with(context)
                    .load(user.getPhotoUrl())
                    .into(itemUserImage);

            facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(getFacebookPageURL(user)));
        }


        public String getFacebookPageURL( User user ){
           PackageManager packageManager = context.getPackageManager();
           try{
               int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0 ).versionCode;
               if( versionCode >= 3002850 ){
                   return "fb://facewebmodal/f?href=" + user.getFacebook();
               } else {
                   return "fb://page/" + user.getFacebook();
               }
           } catch (PackageManager.NameNotFoundException e) {
               return user.getFacebook();
           }
       }
    }
}
