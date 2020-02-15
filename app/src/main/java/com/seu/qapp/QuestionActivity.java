package com.seu.qapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ferfalk.simplesearchview.SimpleSearchView;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.seu.qapp.adapter.CustomAdapter;
import com.seu.qapp.adapter.FolderRecyclerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class QuestionActivity extends AppCompatActivity implements FolderRecyclerAdapter.ItemClickListener, AsyncResponse, CustomAdapter.ItemClickListener {

    private static final String TAG = "QuestionActivity";
    private FolderRecyclerAdapter myRecyclerViewAdapter;
    private ArrayList<String> mList;
    private ArrayList<String> mListAddr;
    private ArrayList<String> tempAddr;
    private ArrayList<String> tempPage;
    private DividerItemDecoration itemDecoration;
    private File[] mFileList;
    private SimpleSearchView searchView;
    private ProgressDialog progressDialog;
    private String path;
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private SearchText searchText;
    private boolean found = false;
    private boolean dialogShow;
    private ArrayList<String> res;
    private String folderName;
    String s;
    private String folderNameAddr;
    private static final String mypreference = "mypref1";
    private SharedPreferences sharedpreferences;
    private static final String Name = "foldername1";
    private SharedPreferences.Editor editor;

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedpreferences.contains(Name)) {
            folderNameAddr = sharedpreferences.getString(Name, "");
            Log.d(TAG, "onStart: " + folderNameAddr);
            initialized(folderNameAddr);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = sharedpreferences.edit();
        editor.putString(Name, folderNameAddr);
        Log.d(TAG, "onPause: " + folderNameAddr);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        searchView = findViewById(R.id.searchView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        folderNameAddr = getIntent().getStringExtra("FolderName");// path of the pdf file
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
        initialized(folderNameAddr);
        Log.d(TAG, "onCreate: Question" + folderNameAddr);

    }

    private void initialized(String folderNameAddr) {
        try {
            folderName = folderNameAddr.substring(folderNameAddr.lastIndexOf("/") + 1);
            Log.d(TAG, "initialized: " + folderNameAddr);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mList = new ArrayList<>();
        mListAddr = new ArrayList<>();
        createRootDirectory();
        recyclerViewSetup();
        searchViewAction();
        Objects.requireNonNull(getSupportActionBar()).setTitle(folderName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void searchViewAction() {
        searchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialogShow = false;
                searchText = new SearchText(output -> {
                    res = output;
                    if (res.size() > 0) {
                        createDialogBox();
                    } else {
                        Toast.makeText(QuestionActivity.this, "Query not Found!!", Toast.LENGTH_SHORT).show();
                    }
                });
                searchText.execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                return false;
            }
        });
    }

    private void createDialogBox() {

        tempAddr = new ArrayList<>();
        tempPage = new ArrayList<>();
        boolean t = false;
        for (String s : res) {
            if (!t) {
                tempPage.add(s);
            } else {
                tempAddr.add(s);
            }
            t = !t;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View convertView = LayoutInflater.from(this).inflate(R.layout.dialog_box, null);
        builder.setView(convertView);
        Dialog dialog = builder.create();
        RecyclerView rv = convertView.findViewById(R.id.rvDialog);
        LinearLayoutManager layoutManager = new LinearLayoutManager(QuestionActivity.this);
        rv.setLayoutManager(layoutManager);
        CustomAdapter customAdapter = new CustomAdapter(this, tempAddr, tempPage);
        customAdapter.setClickListener(this);
        itemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(itemDecoration);
        rv.setAdapter(customAdapter);
        dialogShow = true;
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ViewerActivity.class);

        String page = "0";
        try {
            if (dialogShow) {
                path = tempAddr.get(position);
                page = tempPage.get(position);
                //dialogShow = !dialogShow;
            } else {
                path = mFileList[position].getAbsolutePath();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        intent.putExtra("path", path);
        intent.putExtra("page", page);
        startActivity(intent);
    }

    @Override
    public void processFinish(ArrayList<String> output) {
        Log.d(TAG, "processFinish: ");
        res = output;
    }

    private class SearchText extends AsyncTask<String, Void, ArrayList<String>> {
        private static final String TAG = "SearchText";
        AsyncResponse delegate;

        SearchText(AsyncResponse asyncResponse) {
            super();
            delegate = asyncResponse;
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> result = new ArrayList<>();
            try {
                if (mListAddr != null) {
                    for (String f : mListAddr) {
                        PdfReader pdfReader = new PdfReader(f);
                        String temp;
                        int n = pdfReader.getNumberOfPages();
                        Log.d(TAG, "doInBackground: number of page" + n);
                        for (int i = 0; i < n; i++) {
                            temp = (PdfTextExtractor.getTextFromPage(pdfReader, i + 1).trim()).toLowerCase();
                            if (temp.contains(strings[0].toLowerCase())) {
                                found = true;
                                result.add(String.valueOf(i + 1));
                                result.add(f);
                            }
                        }
                        if (!found) {
                            Log.d(TAG, "doInBackground: Word not found");
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(QuestionActivity.this);
            progressDialog.setMessage("Please wait...It is searching");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            progressDialog.hide();
            delegate.processFinish(strings);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.hide();
        }
    }

    private void createRootDirectory() {
        File directory = new File(folderNameAddr);
        mFileList = directory.listFiles();
        Log.d(TAG, "createRootDirectory: mfilelist size " + directory);
        if (mFileList != null) {
            for (File f : mFileList) {
                if (f.isFile()) {
                    mList.add(f.getName());
                    mListAddr.add(f.getAbsolutePath());
                }
            }
            Log.d(TAG, "createRootDirectory: mlist" + mList.size());
        } else {
            Log.d(TAG, "createRootDirectory: mlist empty");
        }
    }

    private void recyclerViewSetup() {
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myRecyclerViewAdapter = new FolderRecyclerAdapter(this, mList);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(myRecyclerViewAdapter);
        Log.d(TAG, "recyclerViewSetup: called");
    }
}


