package com.example.uploadmultiplefilestofirebaseandsendurltofirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_FILE =1 ;
    Button chooseBtn,uploadBtn;
    ArrayList<Uri> FileList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chooseBtn=findViewById(R.id.chooseBtn);
        uploadBtn=findViewById(R.id.uploadBtn);
        FileList=new ArrayList<Uri>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Processing, Please Wait.......");

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,PICK_FILE);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                Toast.makeText(MainActivity.this, "If takes time, You can press again", Toast.LENGTH_SHORT).show();


                for(int j=0; j<FileList.size(); j++){
                    Uri PerFile=FileList.get(j);
                    StorageReference folder= FirebaseStorage.getInstance().getReference().child("Files");
                    final StorageReference fileName=folder.child("file"+PerFile.getLastPathSegment());
                    fileName.putFile(PerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("User");
                                    HashMap<String, String> hashMap=new HashMap<>();
                                    hashMap.put("link",String.valueOf(uri));

                                    databaseReference.push().setValue(hashMap);
                                    progressDialog.dismiss();
                                    FileList.clear();


                                }
                            });
                        }
                    });




                }
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==PICK_FILE){
            if (resultCode==RESULT_OK){
               if (data.getClipData()!=null){

                   int count=data.getClipData().getItemCount();

                    int i=0;

                    while(i<count){
                        Uri File= data.getClipData().getItemAt(i).getUri();
                        FileList.add(File);
                        i++;


                    }

                   Toast.makeText(this, "You have selected "+FileList.size(), Toast.LENGTH_LONG).show();


               }

            }

        }
    }
}

