package com.example.android.budgetapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.UserDictionary;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ImageRecognitionEntryActivity extends AppCompatActivity {

    String[] sumKeywords = new String[]{"total", "subtotal", "sub-total", "sub total",   "due",
            "amount", "totl", "tl", "amt" , "nett", "jumlah", "sum"};
//    String[] sumKeywords = new String[]{"total", "subtotal", "sub-total", "sub total",   "due",
//            "amount", "totl", "tl", "amt" , "nett", "rm"};
    String sumAmount;

    String[] dateSymbols = new String[]{".", "/", "-", "'"};

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
    String[] automatedValues = new String[4];

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
        boolean dateDetected = false;
        String prevWord = null;
        String[] potentialSingleDate = new String[3];
        boolean spacesDate = false;
        boolean furtherProcessDate = true;
        int interestL = 0;

        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    sumDetectedFlag = false;
                    Log.e("MainActivity", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + words.get(l).getText());

                    String currentWord = words.get(l).getText().toString();
                    if(currentWord.equals("Aug")){
                        int test = 0;
                        System.out.println("found Aug");
                    }
                    currentWord = currentWord.toLowerCase();



                    //Check for sum
                    /*Only care there is match, don't matter match which one, since
                    we will sort List of .Words according to lowest coordinates then
                     check again if got matching sum value*/
                    for(String keyWords : sumKeywords){
                        if(currentWord.contains(keyWords) ){
                            if(prevWord !=null && (!prevWord.equals("change") && !prevWord.equals("baki"))){
                                matchedKeywords.add(words.get(l));
                                sumDetectedFlag = true;
                                break;
                            }

                        }
                    }



                    //Check for date, 1. symbols case & 2. no symbol cases
                    if(sumDetectedFlag == false && dateDetected == false){
                        currentWord = currentWord.trim();
                        currentWord = currentWord.replace(" ", "");
                        //Check for symbols case
                        for(String dateSymbol: dateSymbols){

                            if(currentWord.contains(dateSymbol)){

                                //Break only if a date, otherwise might be phone number, address etc.
                                //Check for date, 2 symbols case
                                boolean firstAndLastAreDigits = Character.isDigit(currentWord.charAt(0)) && Character.isDigit(currentWord.charAt(currentWord.length()-1));
                                boolean twoSeparatedSymbols = currentWord.indexOf(dateSymbol) != currentWord.lastIndexOf(dateSymbol);

                                if( twoSeparatedSymbols && firstAndLastAreDigits && checkIfDateFormat(currentWord, dateSymbol)){
                                    //Dates with 2 symbols, but not necessarily in dd/mm/yyyy

                                    potentialDates.add(currentWord);
                                    date = getDate(currentWord, dateSymbol);
                                    Log.e("MainActivity", "!!!!!!!!!!!!!!matchedDate!!!!!!!!!!!!!" + currentWord);
                                    dateDetected = true;
                                    furtherProcessDate = true;
                                    break;
                                }

                            }
                        }

                        //Check for no symbols case
                        if(dateDetected == false){
                            boolean monthNameFound = false;
                            Matcher matcher = Pattern.compile("(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)").matcher(currentWord);
                            int matchStart = 0;
                            int matchEnd = 0;
                            while (matcher.find()){
                                matchStart = matcher.start();
                                matchEnd = matcher.end()-1;
                                monthNameFound = true;
                            }
                            //boolean monthNameFound = Pattern.compile("\\?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?").matcher(currentWord).find();
                            //boolean monthNameFound = Pattern.compile("\\?:jan(\\?:uary)\\?|feb(\\?:ruary)\\?|mar(\\?:ch)\\?|apr(\\?:il)\\?|may|jun(\\?:e)\\?|jul(\\?:y)\\?|aug(\\?:ust)\\?|sep(\\?:tember)\\?|oct(\\?:ober)\\?|nov(\\?:ember)\\?|dec(\\?:ember)\\?").matcher(currentWord).find();
                            if(monthNameFound == true){


                                //CHANGE MONTHNAMEFOUND TO LOOP AND GET START AND END INDEX, THEN CHECK FOR START-1 AND END+1 ARE
                                //NUMBERS FOR NO SPACE CASE

                                if(matchStart-1 >= 0 && matchEnd+1< currentWord.length() && Character.isDigit(currentWord.charAt(matchStart-1)) && Character.isDigit(currentWord.charAt(matchEnd+1))) {

                                    //no spaces case
                                   /* int lPtr = 0;
                                    int rPtr = currentWord.length()-1;
                                    lPtr =0;
                                    rPtr =currentWord.length()-1;

                                    while(Character.isDigit(currentWord.charAt(lPtr))){
                                        lPtr++;
                                    }
                                    while (Character.isDigit((currentWord.charAt(rPtr)))) {
                                        rPtr--;
                                    }
                                    //Get month
                                    potentialSingleDate[1] = currentWord.substring(lPtr, rPtr+1);
                                    //Get Day
                                    potentialSingleDate[0] = currentWord.substring(0, lPtr);
                                    //Get year
                                    potentialSingleDate[2] = currentWord.substring(rPtr+1);*/
                                    //Get month
                                    potentialSingleDate[1] = currentWord.substring(matchStart, matchEnd+1);
                                    //Get Day
                                    potentialSingleDate[0] = currentWord.substring(0, matchStart);
                                    //Get year
                                    potentialSingleDate[2] = currentWord.substring(matchEnd+1);

                                    dateDetected =true;
                                    furtherProcessDate = false;
                                    interestL = l;
                                }else if(matchEnd-matchStart+1 == currentWord.length() && Character.isDigit(prevWord.charAt(0))){
                                    //THEN CHECK IF END-START+1 IS SIZE OF whole string FOR SPACE CASE
                                    //And check if prevWord is numeric
                                    //spaces case
                                    //Got spaces
                                    //Set month
                                    potentialSingleDate[1] = currentWord;
                                    //Set day
                                    potentialSingleDate[0] = prevWord;
                                    spacesDate = true;
                                    dateDetected =true;
                                    furtherProcessDate = false;
                                    interestL = l;
                                }


                            }

                        }
                    }

                    //If known is no symbols case-> got spaces, just set currentWord as year
                    if(sumDetectedFlag == false && dateDetected == true &&  spacesDate == true && Character.isDigit(currentWord.charAt(0))  ){
                        //set year
                        int currentWordLength = currentWord.length();
                        Character curWordFirstChar = currentWord.charAt(0);
                        Character curWordLastChar = currentWord.charAt(currentWordLength-1);
                        boolean isNumber = Character.isDigit(curWordFirstChar) && Character.isDigit(curWordFirstChar);
                        if((currentWordLength == 2 || currentWordLength == 4) && isNumber && l == interestL+1){
                            potentialSingleDate[2] = currentWord;

                        }else{
                            dateDetected = false;
                            interestL = 0;
                        }
                        spacesDate = false;

                    }





                    prevWord = currentWord;
                    CloudTextGraphic cloudDocumentTextGraphic = new CloudTextGraphic(mGraphicOverlay,
                            words.get(l));
                    mGraphicOverlay.add(cloudDocumentTextGraphic);
                }
            }
        }

       if(furtherProcessDate == false){
           date = potentialSingleDate[0] + "/" + potentialSingleDate[1] + "/" + potentialSingleDate[2];
           //System.out.println("this is date: " +date);
       }

       if(date == null){
           Calendar c = Calendar.getInstance();
           date = String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + "/" +  String.valueOf(c.get(Calendar.MONTH)) + "/" + String.valueOf(c.get(Calendar.YEAR));
       }


        sumAmount = getSumAmount(matchedKeywords, blocks);
        automatedValues = formatDate(date, automatedValues);
        automatedValues[3] = sumAmount;

        System.out.println("date: " + date + " sumAmount: " + sumAmount);
        Intent manualEntryActivityIntent = new Intent(this, ManualEntryActivity.class);
        manualEntryActivityIntent.putExtra("automatedValues", automatedValues);
        startActivity(manualEntryActivityIntent);


    }

    public String[] formatDate(String date, String[] automatedValues){
        String[] dateArray = date.split("/");
        String day = dateArray[0];
        String month = dateArray[1];
        String year = dateArray[2];
        int dayInt;
        int monthInt;
        int yearInt;

        dayInt = Integer.parseInt(day);
        automatedValues[0] = String.valueOf(dayInt);

        if(Character.isDigit(month.charAt(0)) == true){
            monthInt = Integer.parseInt(month);
        }else{

           if(month.equals("jan")|| month.equals("january")){
               monthInt = 1;
           }else if(month.equals("feb") || month.equals("february")){
               monthInt = 2;
           }else if(month.equals("mar")|| month.equals("march")){
               monthInt = 3;
           }else if(month.equals("apr")|| month.equals("april")){
               monthInt = 4;
           }else if(month.equals("may")|| month.equals("may")){
               monthInt = 5;
           }else if(month.equals("jun")|| month.equals("june")){
               monthInt = 6;
           }else if(month.equals("jul")|| month.equals("july")){
               monthInt = 7;
           }else if(month.equals("aug")|| month.equals("august")){
               monthInt = 8;
           }else if(month.equals("sep")|| month.equals("september")){
               monthInt = 9;
           }else if(month.equals("oct")|| month.equals("october")){
               monthInt = 10;
           }else if(month.equals("nov")|| month.equals("november")){
               monthInt = 11;
           }else if(month.equals("dec")|| month.equals("december")){
               monthInt = 12;
           }else{
               monthInt =1;
           }
        }
        automatedValues[1] = String.valueOf(monthInt);

        if(year.length() == 2){
            yearInt = Integer.parseInt("20" + year);
        }else{
            yearInt = Integer.parseInt(year);
        }

        automatedValues[2] = String.valueOf(yearInt);

        return automatedValues;
    }
    public boolean checkIfDateFormat(String currentWord, String dateSymbol){
        //Check for 2-2, 2-4, or 4-2 format exists
        String firstPart = currentWord.substring(0, currentWord.indexOf(dateSymbol));
        String lastPart = currentWord.substring(currentWord.lastIndexOf(dateSymbol)+1);
        boolean twoTwo = (firstPart.length() == 2 && lastPart.length() == 2);
        boolean twoFour = (firstPart.length() == 2 && lastPart.length() == 4);
        boolean fourTwo = (firstPart.length() == 4 && lastPart.length() == 2);
        int dateSymbolCounter = 0;
        for(int i = 0 ; i<currentWord.length(); i++){
            if (currentWord.charAt(i) == dateSymbol.charAt(0)){
                dateSymbolCounter++;
            }
        }
        if ((twoTwo || twoFour || fourTwo) && dateSymbolCounter == 2){
            return true;
        }else{
            return false;
        }
    }

    public String getDate(String currentWord, String dateSymbol){

        String[] dateParts = currentWord.split(dateSymbol, 0);
        String day;
        String month;
        String year;

        if(dateParts != null){


        month = dateParts[1];

        //Check if year comes last or first
        if (dateParts[0].length() == 4){
            //year comes first
            year = dateParts[0];
            day = dateParts[2];
        }else{
            //year comes last
            year = dateParts[2];
            day = dateParts[0];
        }



        return (day + "/" + month + "/" + year);
        }else{
            return null;
        }

    }

    public String getSumAmount(Set<FirebaseVisionDocumentText.Word> matchedKeywords, List<FirebaseVisionDocumentText.Block> blocks){


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
                        double thresholdBottom = keyWord.getBoundingBox().height()*0.25 + keyWordBottomCoordinates;
                        double thresholdTop = keyWordTopCoordinates +keyWord.getBoundingBox().height()*0.40 ;
                        //double thresholdTop = keyWordTopCoordinates -keyWord.getBoundingBox().height()*0.05 ;

                        int potentialSumBottom = words.get(l).getBoundingBox().bottom;
                        if(words.get(l).getText().toString().equals("316.0")){
                            System.out.println("found 316.0");
                        }

                        String sumWord = keyWord.getText();
                        String curWord = words.get(l).getText().toString();

                        if(potentialSumBottom>= thresholdTop && potentialSumBottom <=thresholdBottom ){
                            //Check if text is format 2dp
                            String potentialSum = words.get(l).getText().toString();

                            boolean potentialSumIsDecimal =potentialSum.contains(".");

                            if(potentialSumIsDecimal ){
                                boolean is2dp = potentialSum.substring(potentialSum.lastIndexOf(".")).length()>2;
                                boolean potentialSumIsMoreThan0 = !potentialSum.substring(0, potentialSum.indexOf(".")).equals("0");
                                boolean potentialSumDecimalMoreThan0 = !potentialSum.substring(potentialSum.indexOf(".")).equals("0");
                                if(is2dp && (potentialSumIsMoreThan0 || potentialSumDecimalMoreThan0) ){
                                    chosenWord = potentialSum;
                                    Log.e("MainActivity", "!!!!!!!!!!!!found sum!!!!!!!!!!!!!!!!!!" + words.get(l).getText());
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

//        sumAmount = chosenWord;
        return chosenWord;

    }


}
