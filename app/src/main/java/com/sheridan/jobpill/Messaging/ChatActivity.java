package com.sheridan.jobpill.Messaging;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.sheridan.jobpill.Models.MessageText;
import com.sheridan.jobpill.Models.User;
import com.sheridan.jobpill.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String contactID;
    private String contactImage = "";
    private String contactName;
    private TextView userName;
    private CircleImageView userImage;
    private Toolbar chatToolbar;
    private ImageView backButton;
    private ImageButton sendButton;
    Map<String, Object> chatMap;
    private EditText message;
    private final List<MessageText> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private BottomNavigationView bottomNavigationView;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    CollectionReference jobsRef;
    CollectionReference userRef;
    CollectionReference messages;
    private FirebaseFirestore RootRef;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        RootRef = FirebaseFirestore.getInstance();
        chatToolbar = findViewById(R.id.chat_toolbar);
        chatToolbar.setTitle("");
        setSupportActionBar(chatToolbar);
        SetupWidget();
        contactID = getIntent().getExtras().get("contactID").toString();
        if (getIntent().getExtras().get("contactPhoto").toString() != "") {
            contactImage = (String) getIntent().getExtras().get("contactPhoto");
        }
        contactName = getIntent().getExtras().get("contactName").toString();
        userName.setText(contactName);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void SendMessage() {
        String messageText = message.getText().toString();
        String chatIdSender = currentUser.getUid() + contactID;
        String chatIdReceiver = contactID + currentUser.getUid();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please type you message", Toast.LENGTH_SHORT).show();
        } else {
            final CollectionReference newChatRefSender = db.collection("Messages")
                    .document(chatIdSender)
                    .collection("History");
            chatMap = new HashMap<>();
            chatMap.put("from", currentUser.getUid());
            chatMap.put("to", contactID);
            chatMap.put("text", messageText);
            chatMap.put("type", "text");
            newChatRefSender.add(chatMap);

            final CollectionReference newChatRefReceiver = db.collection("Messages")
                    .document(chatIdReceiver)
                    .collection("History");
            chatMap = new HashMap<>();
            chatMap.put("from", currentUser.getUid());
            chatMap.put("to", contactID);
            chatMap.put("text", messageText);
            chatMap.put("type", "text");
            newChatRefReceiver.add(chatMap);


        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        String chatIdSender = currentUser.getUid() + contactID;
        String chatIdReceiver = contactID + currentUser.getUid();
        final CollectionReference newChatRefSender = db.collection("Messages")
                .document(chatIdSender)
                .collection("History");
        newChatRefSender.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }
                    for (DocumentChange dc : snapshot.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:

                                MessageText messages = new MessageText();
                                messages.setFrom(dc.getDocument().getString("from"));
                                messages.setTo(dc.getDocument().getString("to"));
                                messages.setMessage(dc.getDocument().getString("text"));
                                if (dc.getDocument().getString("type") != null) {
                                    messages.setType(dc.getDocument().getString("type"));
                                } else {
                                    messages.setType("text");
                                }
                                messagesList.add(messages);

                                messageAdapter.notifyDataSetChanged();

                                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                                message.getText().clear();
                                break;
                            case MODIFIED:
                                // handle modified documents...
                                break;
                            case REMOVED:
                                // handle removed documents...
                                break;
                        }
                    }
                }
        );
    }

    private void SetupWidget() {
        userImage = findViewById(R.id.custom_profile_image);
        userName = findViewById(R.id.textView_chat);
        backButton = findViewById(R.id.chat_backbutton);
        sendButton = findViewById(R.id.send_message_btn);
        message = findViewById(R.id.input_message);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        jobsRef = db.collection("jobs");
        userRef = db.collection("Users");
        messages = db.collection("Messages");
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        currentUser = firebaseAuth.getCurrentUser();

    }
}
