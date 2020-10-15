package com.sheridan.jobpill.Profile;

import android.content.Context;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.sheridan.jobpill.Models.Rating;
import com.sheridan.jobpill.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

class RatingsListFirestoreAdapter extends FirestorePagingAdapter<Rating, RatingsListFirestoreAdapter.RatingsViewHolder> {

    private Context context;

    //Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}
    public RatingsListFirestoreAdapter(@NonNull FirestorePagingOptions<Rating> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RatingsViewHolder holder, int position, @NonNull Rating model) {

        holder.ratingBar.setRating(model.getRatingScore());
        holder.ratingScore.setText(Float.toString(model.getRatingScore()));
        holder.jobTitle.setText(model.getJobTitle());
        holder.reviewDate.setText(model.getPostedDate());

        holder.reviewTxt.setText(model.getReview());
        holder.setReviewSize(true, holder.reviewTxt);
        holder.ExpandCollapse(holder.reviewTxt);

        Glide.with(holder.itemView.getContext())
                .load(model.getReviewerPhotoUrl())
                .placeholder(R.drawable.profile_default)
                .dontAnimate()
                .into(holder.profileImg);
    }

    @NonNull
    @Override
    public RatingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ratingslist_item_single, parent, false);
        return new RatingsViewHolder(view);
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
                    Toast.makeText(this.context,"No Ratings Available",Toast.LENGTH_SHORT).show();
                }
                break;
            case ERROR:
                Toast.makeText(this.context,"Error Occurred",Toast.LENGTH_SHORT).show();
                break;
            case LOADED:
                Log.d("PAGING_LOG","Total Items Loaded : " + getItemCount());
                break;
        }
    }

    public class RatingsViewHolder extends RecyclerView.ViewHolder{

        private RatingBar ratingBar;
        private TextView ratingScore;
        private TextView jobTitle;
        private TextView reviewDate;
        private TextView reviewTxt;
        private CircleImageView profileImg;

        private boolean isExpanded = false;

        public RatingsViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.rating_bar_rating_item);
            ratingScore = itemView.findViewById(R.id.rating_score_rating_item);
            jobTitle = itemView.findViewById(R.id.job_title_lbl_rating_item);
            reviewDate = itemView.findViewById(R.id.date_lbl_rating_item);
            reviewTxt = itemView.findViewById(R.id.review_rating_item);
            profileImg = itemView.findViewById(R.id.img_profile_rating_item);
        }

        private void setReviewSize(boolean active, TextView textView){
            if(active){
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
            else{
                textView.setMaxLines(Integer.MAX_VALUE);
                textView.setEllipsize(null);
            }
        }

        private void ExpandCollapse(final TextView textView){
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isExpanded){
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        setReviewSize(false, textView);
                    }
                    else{
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        setReviewSize(true, textView);
                    }

                    isExpanded = !isExpanded;
                }
            });
        }

    }
}
