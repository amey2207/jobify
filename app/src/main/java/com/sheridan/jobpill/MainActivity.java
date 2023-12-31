package com.sheridan.jobpill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.Job.JobDetailsActivity;
import com.sheridan.jobpill.Job.JobDetailsPoster;
import com.sheridan.jobpill.Job.JobPostingActivity;
import com.sheridan.jobpill.Job.JobsListFirestoreAdapter;
import com.sheridan.jobpill.Job.MyJobsActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Profile.EditProfileActivity;
import com.sheridan.jobpill.Profile.ProfileActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements JobsListFirestoreAdapter.OnListItemClick, FilterAlertDialog.FilterDialogListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private Toolbar toolbar;
    private RecyclerView jobsListView;
    private JobsListFirestoreAdapter adapter;
    private ImageButton btn_plus;
    private TextView txtGreeting;
    private BottomNavigationView bottomNavigationView;

    private Boolean filtersApplied = false;
    String locFilter = "";
    String catFilter = "";
    String payFilter = "";

    int catFilterPosition = 0;
    int payFilterPosition = 0;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.top_toolbar_main);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupWidgets();
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jobIntent = new Intent(MainActivity.this, JobPostingActivity.class);
                startActivity(jobIntent);
                finish();
            }
        });


        bottomNavigationView.setSelectedItemId(R.id.bottom_action_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_jobs:
                        sendToMyJobs();
                        return true;
                    case R.id.bottom_action_messages:
                        sendToMessages();
                        return true;
                    case R.id.bottom_action_account:
                        sendToProfile();
                        return true;

                    default:
                        return false;
                }
            }
        });

        currentUser = firebaseAuth.getCurrentUser();

        //Query
        Query query = firebaseFirestore
                .collection("jobs")
                .whereEqualTo("jobStatus","available")
                .whereNotEqualTo("createdBy",currentUser.getEmail())
                .orderBy("createdBy")
                .orderBy("createdDate", Query.Direction.DESCENDING);

        //RecyclerOptions

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Job> options = new FirestorePagingOptions.Builder<Job>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Job.class)
                .build();

        adapter = new JobsListFirestoreAdapter(options, this, this);


        jobsListView.setHasFixedSize(true);
        jobsListView.setLayoutManager(new LinearLayoutManager(this));
        jobsListView.setAdapter(adapter);

        //View Holder


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Get the menu item and determine what action to take.
        switch (item.getItemId()) {
            case R.id.filter:
                filterAlertDialog();
                break;
            default:
                // Otherwise, do nothing.
                break;
        }

        // Call the super version of this method.
        return super.onOptionsItemSelected(item);
    }


    public void setupWidgets() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        btn_plus = findViewById(R.id.plus_btn);
        txtGreeting = findViewById(R.id.txt_main_title);
        bottomNavigationView = findViewById(R.id.mainBottomNav);
        jobsListView = findViewById(R.id.jobs_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        } else {
            current_user_id = firebaseAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String name = task.getResult().getString("name");
                            String intro = task.getResult().getString("intro");
                            String phone = task.getResult().getString("phone");
                            String city = task.getResult().getString("city");
                            String image = task.getResult().getString("photoURL");
                            String dateOfBirth = task.getResult().getString("dateOfBirth");

                            SharedPreferences settings = getSharedPreferences("myprofile",
                                    Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("name", name);
                            editor.putString("intro", intro);
                            editor.putString("phone", phone);
                            editor.putString("city", city);
                            editor.putString("image", image);
                            editor.putString("dateOfBirth",dateOfBirth);

                            editor.commit();

                            Log.d("SHARED_PREFERENCES", "PROFILE SAVED AS: " + name + ", " + intro +
                                    ", " + phone + ", " + city + ", " + image + ", " + dateOfBirth);

                        }
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });


//            SharedPreferences sharedPreferences = getSharedPreferences("tokenSettings", Context.MODE_PRIVATE);
//            final String token = sharedPreferences.getString("deviceToken","null");
//            Log.d("SHARED_PREF_TOKEN: " , token);




            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if(!task.isSuccessful()){
                                Log.d("FCM:","Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            final String token = task.getResult();
                            Log.d("FCM INSTANCE:", token);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("UID",current_user_id);


                            firebaseFirestore.collection("Users").document(currentUser.getUid()).collection("deviceTokens").document(token).set(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Log.d("FCM:", "The Refreshed token: " + token + "is saved to user collection.");
                                            }else{
                                                Log.d("FCM:", "Firebase Error: token not saved");
                                            }
                                        }
                                    });
                        }
                    });


        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyJobs() {
        Intent intent = new Intent(MainActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMessages() {
        Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + "and ID: " + snapshot.getId());

        Job job = snapshot.toObject(Job.class);
        job.setItemId(snapshot.getId());

        if (currentUser.getEmail().equals(job.getCreatedBy())) {
            Intent intent = new Intent(this, JobDetailsPoster.class);
            intent.putExtra("JobSnapshot", job);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, JobDetailsActivity.class);
            intent.putExtra("JobSnapshot", job);
            startActivity(intent);
        }
    }

    public void filterAlertDialog() {
        FilterAlertDialog filterAlertDialog = new FilterAlertDialog();
        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(locFilter)) {
            bundle.putString("location", locFilter);
        }

        if (!TextUtils.isEmpty(catFilter)) {
            bundle.putInt("category", catFilterPosition);
        }

        if (!TextUtils.isEmpty(payFilter)) {
            bundle.putInt("estimatedPay", payFilterPosition);
        }

        filterAlertDialog.setArguments(bundle);
        filterAlertDialog.show(getSupportFragmentManager(), "filter_dialog");
    }

    //On Filter alert dialog "filter" click
    //apply the options on the query, update the paging adapter with the new query
    //save the selected options in bundle and pass to activity
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        locFilter = "";
        payFilter = "";
        catFilter = "";

        catFilterPosition = 0;
        payFilterPosition = 0;

        EditText location = dialog.getDialog().findViewById(R.id.txt_location_filter);
        Spinner category_spinner = dialog.getDialog().findViewById(R.id.spinner_category_filter);
        Spinner pay_spinner = dialog.getDialog().findViewById(R.id.spinner_pay_filter);

        Log.d("POSITIVE_CLICK", "location: " + location.getText().toString());
        Log.d("POSITIVE_CLICK", "category: " + category_spinner.getSelectedItem().toString());
        Log.d("POSITIVE_CLICK", "pay: " + pay_spinner.getSelectedItemPosition());


        Query query = firebaseFirestore
                .collection("jobs")
                .whereEqualTo("jobStatus","available")
                .whereNotEqualTo("createdBy",currentUser.getEmail())
                .orderBy("createdBy")
                .orderBy("createdDate", Query.Direction.DESCENDING);


        if (!TextUtils.isEmpty(location.getText()) && category_spinner.getSelectedItemPosition() > 0 && pay_spinner.getSelectedItemPosition() > 0) {
            locFilter = location.getText().toString();
            catFilter = category_spinner.getSelectedItem().toString();
            catFilterPosition = category_spinner.getSelectedItemPosition();

            payFilter = pay_spinner.getSelectedItem().toString();

            payFilterPosition = pay_spinner.getSelectedItemPosition();

            if (payFilterPosition == 1) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("location", locFilter)
                        .whereEqualTo("jobCategory", catFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 0)
                        .whereLessThanOrEqualTo("estimatedPay", 100)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            } else if (payFilterPosition == 2) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("location", locFilter)
                        .whereEqualTo("jobCategory", catFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 100)
                        .whereLessThanOrEqualTo("estimatedPay", 500)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            } else if (payFilterPosition == 3) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("location", locFilter)
                        .whereEqualTo("jobCategory", catFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 500)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            }


        } else if (!TextUtils.isEmpty(location.getText()) && category_spinner.getSelectedItemPosition() > 0 && pay_spinner.getSelectedItemPosition() == 0) {
            catFilter = category_spinner.getSelectedItem().toString();
            catFilterPosition = category_spinner.getSelectedItemPosition();

            locFilter = location.getText().toString();

            query = firebaseFirestore.collection("jobs")
                    .whereEqualTo("location", locFilter)
                    .whereEqualTo("jobCategory", catFilter)
                    .orderBy("createdDate", Query.Direction.DESCENDING);
        } else if (!TextUtils.isEmpty(location.getText()) && category_spinner.getSelectedItemPosition() == 0 && pay_spinner.getSelectedItemPosition() > 0) {


            locFilter = location.getText().toString();

            payFilter = pay_spinner.getSelectedItem().toString();
            payFilterPosition = pay_spinner.getSelectedItemPosition();

            if (payFilterPosition == 1) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("location", locFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 0)
                        .whereLessThanOrEqualTo("estimatedPay", 100)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            } else if (payFilterPosition == 2) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("location", locFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 100)
                        .whereLessThanOrEqualTo("estimatedPay", 500)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            } else if (payFilterPosition == 3) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("location", locFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 500)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            }

            Log.d("IAMHERE", locFilter + "-" + payFilterPosition);


        } else if (!TextUtils.isEmpty(location.getText()) && category_spinner.getSelectedItemPosition() == 0 && pay_spinner.getSelectedItemPosition() == 0) {

            locFilter = location.getText().toString();
            query = firebaseFirestore.collection("jobs")
                    .whereEqualTo("location", locFilter)
                    .orderBy("createdDate", Query.Direction.DESCENDING);

        } else if (TextUtils.isEmpty(location.getText()) && category_spinner.getSelectedItemPosition() > 0 && pay_spinner.getSelectedItemPosition() > 0) {


            catFilter = category_spinner.getSelectedItem().toString();
            catFilterPosition = category_spinner.getSelectedItemPosition();

            payFilter = pay_spinner.getSelectedItem().toString();

            payFilterPosition = pay_spinner.getSelectedItemPosition();

            if (payFilterPosition == 1) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("jobCategory", catFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 0)
                        .whereLessThanOrEqualTo("estimatedPay", 100)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            } else if (payFilterPosition == 2) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("jobCategory", catFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 100)
                        .whereLessThanOrEqualTo("estimatedPay", 500)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            } else if (payFilterPosition == 3) {
                query = firebaseFirestore.collection("jobs")
                        .whereEqualTo("jobCategory", catFilter)
                        .whereGreaterThanOrEqualTo("estimatedPay", 500)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            }


        } else if (TextUtils.isEmpty(location.getText()) && category_spinner.getSelectedItemPosition() > 0 && pay_spinner.getSelectedItemPosition() == 0) {
            catFilter = category_spinner.getSelectedItem().toString();
            catFilterPosition = category_spinner.getSelectedItemPosition();

            query = firebaseFirestore.collection("jobs")
                    .whereEqualTo("jobCategory", catFilter)
                    .orderBy("createdDate", Query.Direction.DESCENDING);


        } else if (TextUtils.isEmpty(location.getText()) && category_spinner.getSelectedItemPosition() == 0 && pay_spinner.getSelectedItemPosition() > 0) {

            payFilter = pay_spinner.getSelectedItem().toString();
            payFilterPosition = pay_spinner.getSelectedItemPosition();

            if (payFilterPosition == 1) {
                query = firebaseFirestore.collection("jobs")
                        .whereGreaterThanOrEqualTo("estimatedPay", 0)
                        .whereLessThanOrEqualTo("estimatedPay", 100)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            } else if (payFilterPosition == 2) {
                query = firebaseFirestore.collection("jobs")
                        .whereGreaterThanOrEqualTo("estimatedPay", 100)
                        .whereLessThanOrEqualTo("estimatedPay", 500)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            } else if (payFilterPosition == 3) {
                query = firebaseFirestore.collection("jobs")
                        .whereGreaterThanOrEqualTo("estimatedPay", 500)
                        .orderBy("estimatedPay", Query.Direction.DESCENDING)
                        .orderBy("createdDate", Query.Direction.DESCENDING);
            }
        } else if (TextUtils.isEmpty(location.getText()) && category_spinner.getSelectedItemPosition() == 0 && pay_spinner.getSelectedItemPosition() == 0) {

        }


        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Job> options = new FirestorePagingOptions.Builder<Job>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Job.class)
                .build();

        adapter.updateOptions(options);

        Log.d("QUERY", locFilter + " - " + catFilter);

        this.filtersApplied = true;

    }

    //Clear filters on query and update adapter
    // and reinitialize the filters to empty
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        Query query = firebaseFirestore
                .collection("jobs")
                .whereEqualTo("jobStatus","available")
                .whereNotEqualTo("createdBy",currentUser.getEmail())
                .orderBy("createdBy")
                .orderBy("createdDate", Query.Direction.DESCENDING);

        //RecyclerOptions

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Job> options = new FirestorePagingOptions.Builder<Job>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Job.class)
                .build();

        adapter.updateOptions(options);

        locFilter = "";
        catFilter = "";
        payFilter = "";
        catFilterPosition = 0;
        payFilterPosition = 0;

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
