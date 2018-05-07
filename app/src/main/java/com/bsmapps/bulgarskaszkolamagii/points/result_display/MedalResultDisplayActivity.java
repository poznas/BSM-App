package com.bsmapps.bulgarskaszkolamagii.points.result_display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/26/2017.
 */

public class MedalResultDisplayActivity extends AppCompatActivity {

    private static final String TAG = "MCResultDisplayActivity";

    @BindView(R.id.points_view)
    TextView pointsView;
    @BindView(R.id.info_view)
    TextView infoView;
    @BindView(R.id.time_text_view)
    TextView timeView;
    @BindView(R.id.date_text_view)
    TextView dateView;
    @BindView(R.id.medal_view)
    ImageView medalView;

    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal_result_display);
        ButterKnife.bind(this);

        bundle = this.getIntent().getExtras();
        setMedalResultInfo();
    }

    private void setMedalResultInfo() {
        if(bundle != null) {

            Long points = bundle.getLong("points");

            if( points >= 0 ){
                medalView.setImageResource(R.mipmap.merito);
            }else{
                medalView.setImageResource(R.mipmap.infamia);
            }

            pointsView.setText(points.toString());
            infoView.setText(bundle.getString("info"));
            timeView.setText(bundle.getString("time"));
            dateView.setText(bundle.getString("date"));

            switch (bundle.getString("team")){
                case "cormeum":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.red));
                    setTitle("Cormeum");
                    break;
                case "sensum":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    setTitle("Sensum");
                    break;
                case "mutinium":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.green));
                    setTitle("Mutinium");
                    break;
                default:
                    break;
            }
        }
    }
}
