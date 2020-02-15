package com.seu.qapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;

class DownloadTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "DownloadTask";
    private FirebaseStorage storage;
    Context context;
    private final String rootPath = Environment.getExternalStorageDirectory().toString() + "/NEHUQuestion";

    DownloadTask(Context context) {
        super();
        storage = FirebaseStorage.getInstance();
        this.context = context;
    }

    @Override
    protected Void doInBackground(final String... strings) {
        StorageReference reference = storage.getReference().child(strings[0]);
        String fileName = strings[0].substring(strings[0].lastIndexOf("/") + 1, strings[0].lastIndexOf("."));
        File tempFile = new File(rootPath + "/" + strings[0]);

        if (!tempFile.exists()) {
            try {
                final File localFile = File.createTempFile(fileName, ".pdf");
                reference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    FileInputStream instream = null;
                    FileOutputStream outstream = null;

                    try {
                        File infile = new File(localFile.getAbsolutePath());
                        File outfile = new File(rootPath + "/" + strings[0]);

                        instream = new FileInputStream(infile);
                        outstream = new FileOutputStream(outfile);

                        byte[] buffer = new byte[1024];

                        int length;

                        while ((length = instream.read(buffer)) > 0) {
                            outstream.write(buffer, 0, length);
                        }

                        instream.close();
                        outstream.close();


                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                }).addOnFailureListener(e -> {

                });
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: Cannot create temporary file");
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
