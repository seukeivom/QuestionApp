package com.seu.qapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seu.qapp.adapter.FolderRecyclerAdapter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FolderRecyclerAdapter.ItemClickListener {

    private FirebaseAuth mAuth;
    private ActionBar actionBar;
    private static final String TAG = "MainActivity";
    private static final int STORAGE_PERMISSION_CODE = 101;
    private File appFolder;
    private FirebaseStorage storage;
    private StorageReference rootRef;
    private final String rootPath = Environment.getExternalStorageDirectory().toString();
    private final String homeFolder = "NEHUQuestion";
    private String rPath = rootPath + "/" + homeFolder;
    private DownloadTask downloadTask;
    private ArrayList<String> folderName;
    private FolderRecyclerAdapter myRecyclerViewAdapter;
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();

        user = mAuth.getCurrentUser();
        if (user == null) {
            sendToStart();
        } else {
            actionBar.setTitle(user.getDisplayName());
            createFolder(homeFolder, rootPath);
            getFolders(rootRef);
            generateList();
        }
    }


    private void sendToStart() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FloatingActionButton fab = findViewById(R.id.fab);
        actionBar = getSupportActionBar();
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("name", "Forum");
            startActivity(intent);
        });

        storage = FirebaseStorage.getInstance();
        rootRef = storage.getReference();
        folderName = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        myRecyclerViewAdapter.notifyDataSetChanged();

    }

    private void generateList() {
        folderName.clear();
        File nehuFolder = new File(rPath);
        File[] listFolder = nehuFolder.listFiles();
        if (listFolder != null) {
            for (File f : listFolder) {
                if (f.isDirectory()) {
                    folderName.add(f.getName());
                } else {
                    f.delete(); // Delete file in root folder
                }
            }
            Log.d(TAG, "generateList: " + folderName.size());
        }
        recyclerViewSetup();
    }

    private void getFolders(StorageReference ref) {
        Log.d(TAG, "getFolders: " + ref.toString());
        ref.listAll()
                .addOnSuccessListener(listResult -> {
                    List<StorageReference> prefixes = listResult.getPrefixes();
                    List<StorageReference> items = listResult.getItems();
                    for (StorageReference s : prefixes) {
                        String folderPath = s.toString();
                        String tempPath = StringUtils.difference(rootRef.toString(), s.toString());
                        createFolder(rPath, tempPath);
                        getFolders(s);
                        Log.d(TAG, "Prefix" + folderPath);
                    }
                    if (!items.isEmpty()) {
                        Log.d(TAG, "Item not empty");
                        for (StorageReference r : items) {
                            Log.d(TAG, "File : " + r.toString());
                            String s = r.toString().substring(33);
                            Log.d(TAG, "PDF location : " + s);
                            downloadTask = new DownloadTask(getApplicationContext());
                            downloadTask.execute(s);
                        }
                    }
                    for (StorageReference s : items) {
                        Log.d(TAG, s.toString());
                    }
                })
                .addOnFailureListener(e -> {
                    // Uh-oh, an error occurred!
                    Log.d(TAG, "Cannot find the  folders: " + e.getMessage());

                });

    }

    private void createFolder(String path, @Nullable String folderName) {
        if (folderName != null) {
            appFolder = new File(path + File.separator + folderName);
//            if (folderName == null) {
//                appFolder = new File(rPath + File.separator + path);
//            }
            if (!appFolder.exists()) {
                if (appFolder.mkdirs()) {
                    Log.d(TAG, "listDirectory: File created successfully");
                } else {
                    Log.d(TAG, "listDirectory: File cannot be created");
                }
            } else {
                Log.d(TAG, "listDirectory: Folder already created");
            }
        }
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
        Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
        intent.putExtra("FolderName", rPath + "/" + folderName.get(position));
        startActivity(intent);
    }


    private void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            MainActivity.this,
                            new String[]{permission},
                            requestCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        return true;
    }


}
