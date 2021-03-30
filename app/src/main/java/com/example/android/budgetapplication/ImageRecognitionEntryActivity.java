package com.example.android.budgetapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImageRecognitionEntryActivity extends AppCompatActivity {

    String[] sumKeywords = new String[]{"total", "subtotal", "sub-total", "sub total",   "due",
    "amount", "totl", "tl" };

    String sumAmount;

    String[] dateSymbols = new String[]{".", "/", "-", "'", ","};

    private GraphicOverlay mGraphicOverlay;

    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_image_recognition_entry);
        mGraphicOverlay = (GraphicOverlay)findViewById(R.id.graphic_overlay);
        TextView camOpen = (TextView) findViewById(R.id.cam_open);


        //Start camera immediately w/o prompt
        dispatchTakePictureIntent();

        //Ask whether to start camera when at prompt page after pressing back button
        camOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();

            }
        });

        //Display prompt after done with image
        ImageView imgView = (ImageView) findViewById(R.id.display_image);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_image_recognition_entry);
            }
        });

    }


    //Check if there's an app to handle intent
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Check if got activity to handle this intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch(IOException ex)
            {
                //Error whle creating the file
            }

            if(photoFile !=null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                //In .putExtra because photoURI is defined, the extra in onActivityResult will be null
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }




        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
        }
    }


    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @TargetApi(29)
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);

        try {

            ExifInterface exif = new ExifInterface(f.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            Bitmap rotatedBitMap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
            ImageView imgView = (ImageView) findViewById(R.id.display_image);
            imgView.setImageBitmap(rotatedBitMap);

            TextView camOpen = (TextView) findViewById(R.id.cam_open);
            camOpen.setText("");
            runCloudTextRecognition(rotatedBitMap);



        }catch (IOException ex)
        {

        }

        Log.e("CurrentPhotoPath", currentPhotoPath);



        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);


    }







    private void runCloudTextRecognition(Bitmap mSelectedImage) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionDocumentText>() {
                            @Override
                            public void onSuccess(FirebaseVisionDocumentText texts) {

                                processCloudTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception

                                e.printStackTrace();
                            }
                        });
    }

    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully
        if (text == null) {
            Log.e("MainActivity", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!FAIL");
            return;
        }
        mGraphicOverlay.clear();

        boolean sumDetectedFlag = false;

        Set<FirebaseVisionDocumentText.Word> matchedKeywords = new HashSet<>();
        List<String> potentialDates = new ArrayList<>();
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();


        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    sumDetectedFlag = false;

                    Log.e("MainActivity", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + words.get(l).getText());

                    String currentWord = words.get(l).getText().toString();
                    currentWord = currentWord.toLowerCase();



                    //Check for sum
                    /*Only care there is match, don't matter match which one, since
                    we will sort List of .Words according to lowest coordinates then
                     check again if got matching sum value*/
                    for(String keyWords : sumKeywords){
                        if(currentWord.contains(keyWords)){
                            matchedKeywords.add(words.get(l));
                            sumDetectedFlag = true;
                            break;
                        }
                    }



                    //Check for date
                    if(sumDetectedFlag == false){
                        for(String dateSymbol: dateSymbols){

                            if(currentWord.contains(dateSymbol)){
                                currentWord = currentWord.trim();
                                potentialDates.add(currentWord);
                                //Break only if a date, otherwise might be phone number, address etc.
                                if(currentWord.indexOf(dateSymbol) != currentWord.lastIndexOf(dateSymbol)){
                                    date = currentWord;
                                    date = date.replace(" ", "");
                                    Log.e("MainActivity", "!!!!!!!!!!!!!!matchedDate!!!!!!!!!!!!!" + currentWord);
                                    break;
                                }

                            }


                        }
                    }


                    CloudTextGraphic cloudDocumentTextGraphic = new CloudTextGraphic(mGraphicOverlay,
                            words.get(l));
                    mGraphicOverlay.add(cloudDocumentTextGraphic);
                }
            }
        }

        if(date == null){
            date = getDate(potentialDates);
        }

        getSumAmount(matchedKeywords, blocks);






    }

    public String getDate(List<String> potentialDates){

        // . / and - who have 1 occurence in string are not
        //dates, but ' and , are possibly dates
        for(String possibleDate: potentialDates){
            if(possibleDate.contains(",")){

            }



        }



        return null;
    }

    public void getSumAmount(Set<FirebaseVisionDocumentText.Word> matchedKeywords, List<FirebaseVisionDocumentText.Block> blocks){


        //After checked all blocks..words
        List<FirebaseVisionDocumentText.Word> wordList = new ArrayList<>();
        for(FirebaseVisionDocumentText.Word word: matchedKeywords){
            wordList.add(word);
        }



        //Sort hash set
        Collections.sort( wordList, new Comparator<FirebaseVisionDocumentText.Word>(){
            @Override
            public int compare(FirebaseVisionDocumentText.Word o1, FirebaseVisionDocumentText.Word o2){

                //Return box with highest y value as lowest keyword in receipt, likeliest to be sum/Total
                //Sort in decreasing order
                return Double.compare(o1.getBoundingBox().exactCenterY(), o2.getBoundingBox().exactCenterY())*-1;

            }


        });



        //
//        FirebaseVisionDocumentText.Word chosenWord;
//        for(FirebaseVisionDocumentText.Word word: wordList){
//            //Check for (non) white space for number bboxes in range
//            if(word.getText().toString().trim().length()>0){
//                chosenWord = word;
//            }
//        }

        String chosenWord = new String();
        boolean breakFlag = false;
        for(FirebaseVisionDocumentText.Word keyWord: wordList){

            for (int i = 0; i < blocks.size(); i++) {
                List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
                for (int j = 0; j < paragraphs.size(); j++) {
                    List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                    for (int l = 0; l < words.size(); l++) {


                        Log.e("MainActivity", "!!!!!!!!!!!!Check for values in y range!!!!!!!!!!!!!!!!!!" + words.get(l).getText());
                        int keyWordTopCoordinates = keyWord.getBoundingBox().top;
                        int keyWordBottomCoordinates = keyWord.getBoundingBox().bottom;
                        double threshold = keyWord.getBoundingBox().height()*0.15 + keyWordBottomCoordinates;

                        int wordBottom = words.get(l).getBoundingBox().bottom;
                        if(wordBottom>= keyWordTopCoordinates && wordBottom <=threshold ){
                            //Check if text is format 2dp
                            String wordOfInterest = words.get(l).getText().toString();

                            if(wordOfInterest.contains(".") ){
                                boolean is2dp = wordOfInterest.substring(wordOfInterest.lastIndexOf(".")).length()>2;
                                if(is2dp){
                                    chosenWord = wordOfInterest;
                                    breakFlag = true;
                                    break;
                                }

                            }

                        }
                    }

                }
                if(breakFlag == true)
                    break;
            }
            if(breakFlag == true)
                break;
        }


        //Extract chosenWord sum value:
        chosenWord = chosenWord.toLowerCase();
        if(chosenWord.contains("rm")){
            chosenWord = chosenWord.replace("rm", "");
        }
        chosenWord = chosenWord.trim();

        sumAmount = chosenWord;

    }


}
