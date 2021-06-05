package com.example.flute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView p_listView = findViewById(R.id.listView);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();


                        ArrayList<File> songsList = getSongs(Environment.getExternalStorageDirectory());

                        String[] songNames = new String[songsList.size()];

//                        System.out.println(songsList.size());
                        for (int i=0; i<songsList.size(); i++) {
                            songNames[i] = songsList.get(i).getName().replace(".mp3", "");
//                            System.out.println(i);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, songNames);
                        p_listView.setAdapter(adapter);

                        p_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this,SongActivity.class);

                                intent.putExtra("songsList", songsList);
                                intent.putExtra("index", position);

                                startActivity(intent);

                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();



    }


    public ArrayList<File> getSongs(File path) {
        ArrayList<File> songsList = new ArrayList<File>();

        File[] allChilds = path.listFiles();

        if (allChilds != null ){
            for (File child : allChilds) {
                if (child != null && !child.isHidden() && child.isDirectory()){
                    songsList.addAll(getSongs(child));
                }
                else{
                    if (child != null && child.getName().endsWith(".mp3") && !child.getName().startsWith(".")){
                        songsList.add(child);
                    }
                }

            }
        }
        return songsList;
    }
}