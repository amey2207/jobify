package com.sheridan.jobpill.JobApplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sheridan.jobpill.Job.JobsListFirestoreAdapter;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class JobApplicationListFirestoreAdapter extends FirestorePagingAdapter<JobApplication,JobApplicationListFirestoreAdapter.JobApplicantsViewHolder> {

    private OnListItemClick onListItemClick;


    public JobApplicationListFirestoreAdapter(@NonNull FirestorePagingOptions<JobApplication> options, OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick = onListItemClick;
    }


    @Override
    protected void onBindViewHolder(@NonNull JobApplicationListFirestoreAdapter.JobApplicantsViewHolder holder, int position, @NonNull JobApplication model) {

        holder.applicantName.setText(model.getApplicantName() + " from " + model.getApplicantCity());
        holder.applicationDate.setText(model.getApplicationDate());

        Glide.with(holder.itemView.getContext())
                .load(model.getApplicantPhoto())
                .placeholder(R.drawable.profile_default)
                .transition(withCrossFade())
                .into(holder.applicantImg);

    }

    @NonNull
    @Override
    public JobApplicationListFirestoreAdapter.JobApplicantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobapplicants_item_single, parent, false);
        return new JobApplicantsViewHolder(view);
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
                if(getItemCount() == 0){
                    Log.d("PAGING_LOG","No Items: " + 0);
                }
                break;
            case ERROR:
                Log.d("PAGING_LOG","Error Loading Data: ");
                break;
            case LOADED:
                Log.d("PAGING_LOG","Total Items Loaded : " + getItemCount());
                break;
        }
    }

    public class JobApplicantsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView applicantName;
        private ImageView applicantImg;
        private TextView applicationDate;

        public JobApplicantsViewHolder(@NonNull View itemView) {
            super(itemView);

            applicantName = itemView.findViewById(R.id.name_applicant);
            applicantImg = itemView.findViewById(R.id.img_applicant);
            applicationDate = itemView.findViewById(R.id.application_date);

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

