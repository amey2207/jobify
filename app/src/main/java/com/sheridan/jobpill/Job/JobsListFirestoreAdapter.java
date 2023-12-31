package com.sheridan.jobpill.Job;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sheridan.jobpill.JobApplication.JobApplicantProfile;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class JobsListFirestoreAdapter extends FirestorePagingAdapter<Job, JobsListFirestoreAdapter.JobsViewHolder> {

    private OnListItemClick onListItemClick;
    private Context  context;

    public JobsListFirestoreAdapter(@NonNull FirestorePagingOptions<Job> options, OnListItemClick onListItemClick, Context context) {
        super(options);
        this.onListItemClick = onListItemClick;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull JobsViewHolder holder, int position, @NonNull Job model) {

        holder.jobTitle.setText(model.getJobTitle());
        holder.jobDescription.setText(model.getJobDescription());
        holder.jobEstimatedPay.setText("$" + model.getEstimatedPay());
        holder.jobLocation.setText(model.getLocation());
        holder.postedBy.setText(model.getCreatedByName());
        holder.createdDate.setText(model.getCreatedDate());


        Glide.with(holder.itemView.getContext())
                .load(model.getPhotoURL())
                .placeholder(R.drawable.profile_default)
                .transition(withCrossFade())
                .into(holder.jobImage);

        Glide.with(holder.itemView.getContext())
                .load(model.getCreatedByPhotoURL())
                .placeholder(R.drawable.profile_default)
                .into(holder.postedByImage);



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
                if(getItemCount() == 0){
                    Log.d("PAGING_LOG","No Items: " + 0);

                    Toast.makeText(this.context,"No Jobs Available",Toast.LENGTH_SHORT).show();
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

    public class JobsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView jobTitle;
        private TextView jobDescription;
        private ImageView jobImage;
        private TextView jobEstimatedPay;
        private TextView jobLocation;
        private TextView postedBy;
        private TextView createdDate;
        private CircleImageView postedByImage;

        public JobsViewHolder(@NonNull View itemView) {
            super(itemView);


            jobTitle = itemView.findViewById(R.id.txt_jobTitle);
            jobDescription = itemView.findViewById(R.id.txt_jobDescription);
            jobImage = itemView.findViewById(R.id.img_jobPhoto);
            jobEstimatedPay = itemView.findViewById(R.id.txt_jobEstimatedPay);
            jobLocation = itemView.findViewById(R.id.txt_jobLocation);
            postedBy = itemView.findViewById(R.id.txtPostedBy);
            postedByImage = itemView.findViewById(R.id.img_postedBy);
            createdDate = itemView.findViewById(R.id.txtCreatedDate);

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
