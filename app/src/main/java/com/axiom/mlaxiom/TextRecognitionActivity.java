package com.axiom.mlaxiom;


import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static android.text.TextUtils.isEmpty;

public class TextRecognitionActivity extends AppCompatActivity {

    private static final int CAMERA =1 ;
    private static final int GALLERY = 2;
    TextView textrec,textlive;
    Button cam,opencamlive;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textrecog);
        cam=findViewById(R.id.opencam);
        textrec=findViewById(R.id.textrec);
        textlive=findViewById(R.id.textlive);
        opencamlive=findViewById(R.id.opencamlive);
        opencamlive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TextRecognitionActivity.this,LivePreviewActivity.class);
                startActivity(intent);
            }
        });
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textrec.setText("");
                callcamera("image1");
            }
        });
    }




    private void recognizeText(FirebaseVisionImage image) {

        // [START get_detector_default]
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        // [END get_detector_default]

        // [START run_detector]
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                if(firebaseVisionText.getTextBlocks().size()==0){
                                    Toast.makeText(TextRecognitionActivity.this,"NO TEXT Recognized",Toast.LENGTH_LONG).show();
                                }
                                else {
                                  //  textlive.setText(firebaseVisionText.getText().replace("\n","\t"));
                                    processTextBlock(firebaseVisionText);
                                }
                                }

                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
        // [END run_detector]
    }


    private void processTextBlock(FirebaseVisionText result) {
        // [START mlkit_process_text_block]
        String resultText = result.getText();
        Set<String> array = new LinkedHashSet<>();
        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();
                int count = countMatches(resultText.split("\\n"), lineText.trim());
                array.add(lineText.trim()+"  count="+ count);
            }
            String listString = "";

            for (String s : array)
              {
              listString += s +"\n" ;
               }

            textrec.setText(listString);


        }



    }

    public static int count(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }


    public static int countMatches(String[] str, String sub) {
        if (str.length==0 || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        for(int i=0;i<str.length;i++){
            if(sub.equalsIgnoreCase(str[i]))
            count++;
        }
        return count;
    }



    private void callcamera(String img) {

        PackageManager packageManager = getApplicationContext().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    callPermission();
                }
            } else
                showPictureDialog(img);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void callPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }


        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 3) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                   /* PackageManager packageManager = getApplicationContext().getPackageManager();
                    if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {*/
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        callPermission();
                    }
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        callPermission();
                    }

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        callPermission();
                    }
                    // }

                    //Displaying a toast
                    //
                }  //Displaying another toast if permission is not granted //Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
                /*else {
                  //  Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                }*/
            }

        }
    }

    private void showPictureDialog(String attachhment_section) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    recognizeText(image);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TextRecognitionActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            try {
//                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(thumbnail);
//                recognizeText(image);

                Uri selectedImage = imageUri;
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                bitmap = android.provider.MediaStore.Images.Media
                            .getBitmap(cr, selectedImage);
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    recognizeText(image);


            } catch (Exception e) {
                Log.d("UPLOAD", e.getLocalizedMessage() + "");
                Toast.makeText(TextRecognitionActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
