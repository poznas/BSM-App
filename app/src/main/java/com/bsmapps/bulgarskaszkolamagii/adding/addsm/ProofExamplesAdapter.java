package com.bsmapps.bulgarskaszkolamagii.adding.addsm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.beans.sidemission.ProofExample;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/29/2017.
 */

public class ProofExamplesAdapter extends RecyclerView.Adapter<ProofExamplesAdapter.ProofExamplesViewHolder>{

    private Context context;
    private LayoutInflater mInflater;
    List<ProofExample> proofExamples;

    public ProofExamplesAdapter(Context context, List<ProofExample> proofExamples) {
        this.context = context;
        this.proofExamples = proofExamples;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ProofExamplesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_report_proof_example, parent, false );
        ProofExamplesViewHolder holder = new ProofExamplesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ProofExamplesViewHolder holder, int position) {
        ProofExample current = proofExamples.get(position);
        holder.setExampleData(current);
    }

    @Override
    public int getItemCount() {
        return proofExamples.size();
    }

    public class ProofExamplesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_proof_type_view)
        TextView nameView;
        @BindView(R.id.item_example_view)
        ImageView exampleView;
        @BindView(R.id.item_hint_view)
        TextView hintView;

        public ProofExamplesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setExampleData(ProofExample example) {

            nameView.setText(example.getDisplayName());
            hintView.setText(example.getHint());

            Glide.with(context)
                    .load(example.getExampleUrl())
                    .into(exampleView);
        }
    }
}
