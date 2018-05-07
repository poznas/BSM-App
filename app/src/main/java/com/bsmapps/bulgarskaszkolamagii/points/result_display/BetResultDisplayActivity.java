package com.bsmapps.bulgarskaszkolamagii.points.result_display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/26/2017.
 */

public class BetResultDisplayActivity extends AppCompatActivity {

    private static final String TAG = "BetResultDisplayActivity";

    @BindView(R.id.winner_view)
    TextView winnerView;
    @BindView(R.id.losser_view)
    TextView loserView;
    @BindView(R.id.points_view)
    TextView pointsView;
    @BindView(R.id.info_view)
    TextView infoView;
    @BindView(R.id.time_text_view)
    TextView timeView;
    @BindView(R.id.date_text_view)
    TextView dateView;

    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_result_display);
        ButterKnife.bind(this);

        bundle = this.getIntent().getExtras();
        setBetResultInfo();
    }

    private void setBetResultInfo() {
        if(bundle != null){

            pointsView.setText(bundle.getString("points"));
            infoView.setText(bundle.getString("info"));
            timeView.setText(bundle.getString("time"));
            dateView.setText(bundle.getString("date"));
            winnerView.setText(bundle.getString("winner"));
            loserView.setText(bundle.getString("loser"));

            switch (bundle.getString("winner")){
                case "cormeum":
                    winnerView.setTextColor(ContextCompat.getColor(this, R.color.red));
                    setTitle("Cormeum");
                    break;
                case "sensum":
                    winnerView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    setTitle("Sensum");
                    break;
                case "mutinium":
                    winnerView.setTextColor(ContextCompat.getColor(this, R.color.green));
                    setTitle("Mutinium");
                    break;
                default:
                    break;
            }
            switch (bundle.getString("loser")){
                case "cormeum":
                    loserView.setTextColor(ContextCompat.getColor(this, R.color.red));
                    break;
                case "sensum":
                    loserView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    break;
                case "mutinium":
                    loserView.setTextColor(ContextCompat.getColor(this, R.color.green));
                    break;
                default:
                    break;
            }
            switch (bundle.getString("team")){
                case "cormeum":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.red));
                    break;
                case "sensum":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    break;
                case "mutinium":
                    pointsView.setTextColor(ContextCompat.getColor(this, R.color.green));
                    break;
                default:
                    break;
            }
        }
    }
}
