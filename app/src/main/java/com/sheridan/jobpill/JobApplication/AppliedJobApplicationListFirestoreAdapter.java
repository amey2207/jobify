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
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AppliedJobApplicationListFirestoreAdapter extends FirestorePagingAdapter<JobApplication, AppliedJobApplicationListFirestoreAdapter.JobApplicationViewHolder> {
    private OnListItemClick onListItemClick;

    public AppliedJobApplicationListFirestoreAdapter(@NonNull FirestorePagingOptions<JobApplication> options, OnListItemClick onListItemClick){
        super(options);
        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull AppliedJobApplicationListFirestoreAdapter.JobApplicationViewHolder holder, int position, @NonNull JobApplication model) {
        holder.jobTitle.setText(model.getJobTitle());
        holder.applicationDate.setText(model.getApplicationDate());
        Glide.with(holder.itemView.getContext())
                .load(model.getJobPhotoURL())
                .placeholder(R.drawable.profile_default)
                .transition(withCrossFade())
                .into(holder.jobImage);
    }

    @NonNull
    @Override
    public AppliedJobApplicationListFirestoreAdapter.JobApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applied_jobapplications_item_single,parent,false);
        return new JobApplicationViewHolder(view);
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

   public class JobApplicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView jobTitle;
        private TextView applicationDate;
        private ImageView jobImage;

        JobApplicationViewHolder(@NonNull View itemView){
            super(itemView);

            jobTitle = itemView.findViewById(R.id.txt_job_title);
            applicationDate = itemView.findViewById(R.id.txt_date_applied);
            jobImage = itemView.findViewById(R.id.img_jobImage);

            itemView.setOnClickListener(this);
        }

       @Override
       public void onClick(View v) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()),getAdapterPosition());
       }
   }

   public interface OnListItemClick{
        void onItemClick(DocumentSnapshot snapshot, int position);
   }
}
