package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.R;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class JobsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private LinearLayoutManager linearLayoutManager;

    private boolean isScrolling;
    private boolean isLastItemReached;

    private JobAdapter jobAdapter;


    private List<Job> list;
    private DocumentSnapshot lastVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.jobsList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        list = new ArrayList<>();

        Query query = firebaseFirestore
                .collection("jobs")
                .orderBy("createdDate", Query.Direction.ASCENDING)
                .limit(5);

        getJobs(query);






    }

    public void getJobs(final Query query){
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document: task.getResult()){
                        Job job = document.toObject(Job.class);
                        job.setItemId(document.getId());

                        if(!job.getCreatedBy().equals(currentUser.getEmail())){
                            list.add(job);
                        }
                    }

                    jobAdapter = new JobAdapter(list);
                    recyclerView.setAdapter(jobAdapter);

                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                    Toast.makeText(getApplicationContext(),"First page loaded",Toast.LENGTH_SHORT).show();

                    if(task.getResult().size() < 10){
                        isLastItemReached = true;
                    }

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if(isScrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemReached){
                                isScrolling = false;

                               Query nextQuery = query.startAfter(lastVisible);



                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for(DocumentSnapshot document: task.getResult()){
                                            Job job = document.toObject(Job.class);
                                            job.setItemId(document.getId());

                                            if(!job.getCreatedBy().equals(currentUser.getEmail())){
                                                list.add(job);
                                            }
                                        }

                                        jobAdapter.notifyDataSetChanged();
                                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                        Toast.makeText(getApplicationContext(),"Next page loaded",Toast.LENGTH_SHORT).show();

                                        if(task.getResult().size() < 10){
                                            isLastItemReached = true;
                                        }
                                    }
                                });

                            }
                        }
                    };

                    recyclerView.addOnScrollListener(onScrollListener);
                }

                if(isLastItemReached){
                    Toast.makeText(getApplicationContext(),"All Documents loaded",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private class JobAdapter extends RecyclerView.Adapter<JobsViewHolder2> {
        private List<Job> list;

        JobAdapter(List<Job> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public JobsViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobslist_item_single, parent, false);
            return new JobsViewHolder2(view);
        }

        @Override
        public void onBindViewHolder(@NonNull JobsViewHolder2 jobsViewHolder, int position) {
            String jobTitle = list.get(position).getJobTitle();
            jobsViewHolder.jobTitle.setText(jobTitle);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class JobsViewHolder2 extends RecyclerView.ViewHolder {
        private View view;
        private TextView jobTitle;


        public JobsViewHolder2(@NonNull View itemView) {
            super(itemView);

            jobTitle = itemView.findViewById(R.id.txt_jobTitle);

        }


    }
}
