package com.seu.qapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.seu.qapp.adapter.FolderRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectActivity extends AppCompatActivity implements FolderRecyclerAdapter.ItemClickListener {

    private ArrayList<String> folderName;
    private static final String TAG = "SubjectActivity";
    private String folderNameAddr;
    private FolderRecyclerAdapter myRecyclerViewAdapter;
    private static final String mypreference = "mypref";
    private SharedPreferences sharedpreferences;
    private static final String Name = "foldername";
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        folderNameAddr = getIntent().getStringExtra("FolderName");
        Log.d(TAG, "onCreate: " + folderNameAddr);


        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        if (folderNameAddr != null) {
            editor.putString(Name, folderNameAddr);
            editor.apply();
        }
        if (sharedpreferences.contains(Name)) {
            folderNameAddr = sharedpreferences.getString(Name, "");
            initialized(folderNameAddr);
            editor.clear();
        } else {
            initialized(folderNameAddr);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedpreferences.contains(Name)) {
            folderNameAddr = sharedpreferences.getString(Name, "");
            Log.d(TAG, "onStart: " + folderNameAddr);
            initialized(folderNameAddr);
        }
    }


    private void initialized(String g) {
        Log.d(TAG, "initialized: " + g);
        String f = folderNameAddr.substring(folderNameAddr.lastIndexOf("/") + 1);
        setTitle(f);
        createList();
    }

    private void createList() {
        folderName = new ArrayList<>();
        File f = new File(folderNameAddr);
        Log.d(TAG, "createList: " + f.getAbsolutePath());
        File[] fileList = f.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (f.isDirectory()) {
                    folderName.add(file.getName());
                } else {
                    file.delete();
                }
            }
        }
        recyclerViewSetup();
    }

    private void recyclerViewSetup() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myRecyclerViewAdapter = new FolderRecyclerAdapter(this, folderName);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(SubjectActivity.this, QuestionActivity.class);
        intent.putExtra("FolderName", folderNameAddr + "/" + folderName.get(position));
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = sharedpreferences.edit();
        editor.putString(Name, folderNameAddr);
        Log.d(TAG, "onPause: " + folderNameAddr);
        editor.apply();
    }
}
