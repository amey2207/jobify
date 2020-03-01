package com.sheridan.jobpill;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sheridan.jobpill.Models.Job;

public class JobsListFirestoreAdapter extends FirestorePagingAdapter<Job, JobsListFirestoreAdapter.JobsViewHolder> {

    private OnListItemClick onListItemClick;

    public JobsListFirestoreAdapter(@NonNull FirestorePagingOptions<Job> options, OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull JobsViewHolder holder, int position, @NonNull Job model) {

        holder.jobTitle.setText(model.getJobTitle());
        holder.jobDescription.setText(model.getJobDescription());
        holder.jobEstimatedPay.setText(model.getEstimatedPay() + "");
    }

    @NonNull
    @Override
    public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobslist_item_single, parent, false);
        return new JobsViewHolder(view);
    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch(state){
            case LOADING_INITIAL:
                Log.d("PAGING_LOG","Loading Initial Data");
            case LOADING_MORE:
                Log.d("PAGING_LOG","Loading Next Page");
                break;
            case FINISHED:
                Log.d("PAGING_LOG","All Data Loaded");
                break;
            case ERROR:
                Log.d("PAGING_LOG","Error Loading Data: ");
                break;
            case LOADED:
                Log.d("PAGING_LOG","Total Items Loaded : " + getItemCount());
                break;
        }
    }

    public class JobsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView jobTitle;
        private TextView jobDescription;
        private ImageView jobImage;
        private TextView jobEstimatedPay;

        public JobsViewHolder(@NonNull View itemView) {
            super(itemView);

            jobTitle = itemView.findViewById(R.id.txt_jobTitle);
            jobDescription = itemView.findViewById(R.id.txt_jobDescription);
            jobImage = itemView.findViewById(R.id.img_jobPhoto);
            jobEstimatedPay = itemView.findViewById(R.id.txt_jobEstimatedPay);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()),getAdapterPosition());
        }
    }

    public interface OnListItemClick{
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
}
