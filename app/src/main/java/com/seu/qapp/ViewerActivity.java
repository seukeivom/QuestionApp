package com.seu.qapp;

import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;


public class ViewerActivity extends AppCompatActivity {

    private PDFView pdfView;
    private String path;
    private String page;
    File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        setTitle("Viewer Title");
        pdfView = findViewById(R.id.pdfView);
        path = getIntent().getStringExtra("path");
        page = getIntent().getStringExtra("page");
        if (path != null) {
            File file = new File(path);
            Objects.requireNonNull(getSupportActionBar()).setTitle(file.getName());

            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .defaultPage(Integer.parseInt(page))
                    .spacing(4)
                    .load();
        } else {
            Toast.makeText(this, "Cannot open the file", Toast.LENGTH_SHORT).show();
        }

    }

}
