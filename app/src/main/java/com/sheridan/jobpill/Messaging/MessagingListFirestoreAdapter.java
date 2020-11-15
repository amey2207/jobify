package com.sheridan.jobpill.Messaging;

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
import com.sheridan.jobpill.Models.Messaging;
import com.sheridan.jobpill.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MessagingListFirestoreAdapter extends FirestorePagingAdapter<Messaging, MessagingListFirestoreAdapter.MessagesViewHolder> {

    private MessagingListFirestoreAdapter.OnListItemClick onListItemClick;

    public MessagingListFirestoreAdapter(@NonNull FirestorePagingOptions<Messaging> options, MessagingListFirestoreAdapter.OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessagingListFirestoreAdapter.MessagesViewHolder holder, int position, @NonNull Messaging model) {

        holder.contactName.setText(model.getContactName() + " from " + model.getChatJobName());
        //holder.applicationDate.setText(model.getApplicationDate());

        Glide.with(holder.itemView.getContext())
                .load(model.getContactPhotoURL())
                .placeholder(R.drawable.profile_default)
                .transition(withCrossFade())
                .into(holder.contactPhoto);
    }

    @NonNull
    @Override
    public MessagingListFirestoreAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelist_item_single, parent, false);
        return new MessagingListFirestoreAdapter.MessagesViewHolder(view);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state) {
            case LOADING_INITIAL:
                Log.d("PAGING_LOG", "Loading Initial Data");
            case LOADING_MORE:
                Log.d("PAGING_LOG", "Loading Next Page");
                break;
            case FINISHED:
                Log.d("PAGING_LOG", "All Data Loaded");
                if (getItemCount() == 0) {
                    Log.d("PAGING_LOG", "No Items: " + 0);
                }
                break;
            case ERROR:
                Log.d("PAGING_LOG", "Error Loading Data: ");
                break;
            case LOADED:
                Log.d("PAGING_LOG", "Total Items Loaded : " + getItemCount());
                break;
        }
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView contactName;
        private ImageView contactPhoto;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.name_applicant);
            contactPhoto = itemView.findViewById(R.id.img_applicant);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemClick {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
}