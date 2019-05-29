package com.example.tinderapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private cards cards_data[];
    private arrayAdapter arrayAdapter;

    private ArrayList<String> al;
    //private ArrayList<String> aR;


    //private ArrayAdapter<String> arrayAdapter;
    private int i;
    //Sender s= new Sender();
    //private int contor = 0;

    private FirebaseAuth mAuth;

    private String currentUId;

    private DatabaseReference usersDb;

    ListView listView;
    List<cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDb = FirebaseDatabase.getInstance().getReference().child("users");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserType();

        rowItems = new ArrayList<cards>();


        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        /*
        aR = new ArrayList<>();
        aR.add("php");
        aR.add("c");
        aR.add("python");
        aR.add("java");
        aR.add("html");
        aR.add("c++");
        aR.add("css");
        aR.add("javascript");
        */



        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //contor++;
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(oppositeUserType).child(userId).child("connections").child("nope").child(currentUId).setValue(true);

                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                //contor++;
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                //abc

                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(oppositeUserType).child(userId).child("connections").child("yup").child(currentUId).setValue(true);

                isConnectionMatch(userId);
                
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                //String str = arrayAdapter.toString();
               // ArrayList str = new ArrayList();
                //String str = al.get(contor);
                //s.Send_data(aR.get(contor));
                //contor++;
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                //aR.add("XML ".concat(String.valueOf(i)));
                //al.add("XML ".concat(String.valueOf(i)));
                //arrayAdapter.notifyDataSetChanged();
                //Log.d("LIST", "notified");
                //i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(userType).child(currentUId).child("connections").child("yup").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();
                    usersDb.child(oppositeUserType).child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).setValue(true);
                    usersDb.child(userType).child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String userType;
    private String oppositeUserType;
    public void checkUserType(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference candDb = FirebaseDatabase.getInstance().getReference().child("users").child("Candidate");
        candDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.getKey().equals(user.getUid())){
                    userType = "Candidate";
                    oppositeUserType = "Company";
                    getOppositeTypeUser();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference compDb = FirebaseDatabase.getInstance().getReference().child("users").child("Company");
        compDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.getKey().equals(user.getUid())){
                    userType = "Company";
                    oppositeUserType = "Candidate";
                    getOppositeTypeUser();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeTypeUser(){
        DatabaseReference oppositeTypeDb = FirebaseDatabase.getInstance().getReference().child("users").child(oppositeUserType);
        oppositeTypeDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yup").hasChild(currentUId)){
                    //al.add(dataSnapshot.child("name").getValue().toString());
                    cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString());
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}