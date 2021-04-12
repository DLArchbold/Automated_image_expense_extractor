package com.example.android.budgetapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;
//import android.support.v4.util.Pair;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
//import android.util.Pair;
import com.example.android.budgetapplication.data.ExpenseContract;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.android.budgetapplication.data.ExpenseContract.ExpenseEntry;


/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    public Context mContext;
    String ms;

    public DriveServiceHelper(Drive driveService, Context context) {
        mDriveService = driveService;
        mContext = context;

        Date date = new Date();
        ms = "_" + String.valueOf(date.getTime());
    }


    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    public Task<String> createFile() {
        return Tasks.call(mExecutor, () -> {
            //Toast.makeText(mContext, "Creating backup.....", Toast.LENGTH_LONG).show();
            java.io.File dbPath = mContext.getDatabasePath("expenses.db");
            Uri dbUri = Uri.fromFile(dbPath);
            Log.w("DriveServiceHelper", "dbUri 1 wj: " + dbUri);
//            fileUri = Uri.fromFile(new java.io.File(Environment.getDataDirectory().getPath()
//                    + "/data/com.example.myapp/databases/mydb.db"));
//            dbUri = Uri.fromFile(new java.io.File(Environment.getDataDirectory().getPath()
//                    + dbUri));
//            Log.w("DriveServiceHelper", "dbUri 2 wj: " + dbUri);

            //java.io.File fileContent = new java.io.File(dbUri.getPath());
            FileContent mediaContent = new FileContent("application/vnd.sqlite3", dbPath);
            File body = new com.google.api.services.drive.model.File();

            String fileName = dbPath.getName().substring(0, dbPath.getName().indexOf('.')) + ms + dbPath.getName().substring(dbPath.getName().indexOf('.'));
            File metadata = new File()
                    .setParents(Collections.singletonList("root"))
                    .setMimeType("application/vnd.sqlite3")
                    .setName(fileName);

            File googleFile;
            Log.d("DriveServiceHelper", "wj " + fileName);
            googleFile = mDriveService.files().create(metadata, mediaContent).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }


            return googleFile.getId();
        });
    }


    public static class deleteBackupsTask extends AsyncTask<deleteBackupTaskParams, Void, Void> {
        @Override
        protected Void doInBackground(deleteBackupTaskParams... deleteBackupTaskParam) {

            //File directory = new File("/data/user/0/com.example.android.budgetapplication/databases");

            try {
                FileList fileList = deleteBackupTaskParam[0].driveService.files().list().setSpaces("drive").execute();


                for (File file : fileList.getFiles()) {

//                    String fileId = file.getId();
//                    java.io.File dbfile = new java.io.File("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db");
//                    OutputStream outputStream = new FileOutputStream("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db");
//                    deleteBackupTaskParam[0].driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
//                    SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db", null, SQLiteDatabase.OPEN_READWRITE);
//                    Cursor c = myDataBase.rawQuery("SELECT * from expenses", null);
//                   System.out.println("c.getCount(): " +  c.getCount());
//                    outputStream.close();


                    //builder.append(file.getName()).append("\n");
                    Log.d("doInBackground", " wj com.google.api file name: " + file.getName());
                    if (!file.getName().equals("expenses" + deleteBackupTaskParam[0].ms + ".db")) {
                        System.out.println("wj downloaded com.google.api.File location: " + deleteBackupTaskParam[0].mContext.getDatabasePath("random") + " related com.google.api.File name "
                                + file.getName());
                        try {
                            Log.d("doInBackground", "wj delete success! ");
                            deleteBackupTaskParam[0].driveService.files().delete(file.getId()).execute();
                        } catch (IOException e) {
                            Log.e("doInBackground", "wj delete failed " + e);
                        }
                    }

                }

//                java.io.File directory = new java.io.File("/data/user/0/com.example.android.budgetapplication/databases/");
//                java.io.File[] files = directory.listFiles();
//                for (java.io.File oneFile : files){
//                    System.out.println("wj java.io.File name in local directory: " + oneFile.getName());
//                }

            } catch (IOException e) {

            }
            return null;

        }

    }

    public static class restoreAsyncTask extends AsyncTask<restoreBackupTaskParams, Void, Void> {
        @Override
        protected Void doInBackground(restoreBackupTaskParams... restoreBackupTaskParam) {
            try {
                //File directory = new File("/data/user/0/com.example.android.budgetapplication/databases");
                FileList fileList = restoreBackupTaskParam[0].driveService.files().list().setSpaces("drive").execute();
                String fileId = null;

                for (File file : fileList.getFiles()) {

                    fileId = file.getId();

                }

                //Download from drive to local app database directory
                if (!fileId.isEmpty()) {
                    java.io.File dbfile = new java.io.File("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db");
                    OutputStream outputStream = new FileOutputStream("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db");
                    restoreBackupTaskParam[0].driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
                    //outputStream.close();
                }

                //Check if backup downloaded successfully
                java.io.File directory = new java.io.File("/data/user/0/com.example.android.budgetapplication/databases/");
                java.io.File[] files = directory.listFiles();
                for (java.io.File oneFile : files) {
                    System.out.println("wj java.io.File name in local directory: " + oneFile.getName());
                }

                SQLiteDatabase expenseBackupDatabase = SQLiteDatabase.openDatabase("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db", null, SQLiteDatabase.OPEN_READWRITE);
                Cursor expenseBackupTableCursor = expenseBackupDatabase.rawQuery("SELECT * from expenses", null);
                System.out.println("c.getCount(): " + expenseBackupTableCursor.getCount());
                Log.d("DriveServiceHelper", "test: " + String.valueOf(expenseBackupTableCursor.getCount()));


                //Columns to retrieve from original expense.db during query
                String[] expenseTableCursorProjection = {
                        ExpenseEntry._ID,
                        ExpenseEntry.COLUMN_OPTION,
                        ExpenseEntry.COLUMN_DAY,
                        ExpenseEntry.COLUMN_MONTH,
                        ExpenseEntry.COLUMN_YEAR,
                        ExpenseEntry.COLUMN_AMOUNT,
                        ExpenseEntry.COLUMN_DESCRIPTION,
                        ExpenseEntry.COLUMN_CATEGORY,
                        ExpenseEntry.COLUMN_DATE,
                        ExpenseEntry.COLUMN_COORDINATES
                };


                //Retrieve from expense_backup.db to cross-check with expense.db
                int idColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry._ID);
                int optionColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_OPTION);
                int dayColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DAY);
                int monthColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_MONTH);
                int yearColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_YEAR);
                int amountColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT);
                int descriptionColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DESCRIPTION);
                int categoryColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_CATEGORY);
                int dateColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
                int coordinatesColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_COORDINATES);

                expenseBackupTableCursor.moveToFirst();

                for(int i = 0; i<expenseBackupTableCursor.getCount(); i++) {
                    int cId = expenseBackupTableCursor.getInt(idColIdx);
                    String cOption = expenseBackupTableCursor.getString(optionColIdx);
                    int cDay = expenseBackupTableCursor.getInt(dayColIdx);
                    int cMonth = expenseBackupTableCursor.getInt(monthColIdx);
                    int cYear = expenseBackupTableCursor.getInt(yearColIdx);
                    double cAmount = expenseBackupTableCursor.getDouble(amountColIdx);
                    String cDescription = expenseBackupTableCursor.getString(descriptionColIdx);
                    String cCategory = expenseBackupTableCursor.getString(categoryColIdx);
                    String cDate = expenseBackupTableCursor.getString(dateColIdx);
                    String cCoordinates = expenseBackupTableCursor.getString(coordinatesColIdx);

                    boolean coordinatesSet=true;
                    if(cCoordinates.equals("")){
                        coordinatesSet = false;
                        cCoordinates = "-";
                    }






                    //Define selection arguments
                    String path = "/" + String.valueOf(cId) + "/" + cOption + "/" + String.valueOf(cDay) + "/" + String.valueOf(cMonth) + "/"
                            + String.valueOf(cYear) + "/" + String.valueOf(cAmount) + "/" + cDescription + "/" + cCategory + "/"
                            + cDate + "/" + cCoordinates;

                    //get id/option/day/month/year.....
                    String strUri = path.substring(path.indexOf("/") + 1);
                    // {date, category, option}
                    String[] selectionArgs = strUri.split("/");

                    if(coordinatesSet == false){
                        selectionArgs[9] = "";
                    }

                    //Define selection
                    String selection = ExpenseContract.ExpenseEntry._ID + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_OPTION + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_DAY + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_MONTH + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_YEAR + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION+ "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_CATEGORY + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_DATE + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_COORDINATES + "=?";



                    // This will perform a query on the expenses table where the {date, expense/income category and
                    // option} equals the selectionArgs to return a Cursor containing rows of the table.
                    SQLiteDatabase expensesDatabase = SQLiteDatabase.openDatabase("/data/user/0/com.example.android.budgetapplication/databases/expenses.db", null, SQLiteDatabase.OPEN_READWRITE);
                   Cursor expenseTableCursor = expensesDatabase.query(ExpenseContract.ExpenseEntry.TABLE_NAME, expenseTableCursorProjection, selection, selectionArgs,
                            null, null, null);



//                    Cursor expenseTableCursor = restoreBackupTaskParam[0].mContext.getContentResolver().query(
//                            Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
//                            expenseTableCursorProjection,
//                            null,
//                            null
//
//                    );
                    System.out.println("expenseTableCursor count: " + expenseTableCursor.getCount());
                    System.out.println("path: " + path);

                    //Row from backup does not exist in local db
                    if(expenseTableCursor.getCount() == 0){
                        ContentValues values = new ContentValues();
                        values.put(ExpenseEntry._ID, cId);
                        values.put(ExpenseEntry.COLUMN_OPTION, cOption);
                        values.put(ExpenseEntry.COLUMN_DAY, cDay);
                        values.put(ExpenseEntry.COLUMN_MONTH, cMonth);
                        values.put(ExpenseEntry.COLUMN_YEAR, cYear);
                        values.put(ExpenseEntry.COLUMN_AMOUNT, cAmount);
                        values.put(ExpenseEntry.COLUMN_DESCRIPTION, cDescription);
                        values.put(ExpenseEntry.COLUMN_CATEGORY, cCategory);
                        values.put(ExpenseEntry.COLUMN_DATE, cDate);
                        values.put(ExpenseEntry.COLUMN_COORDINATES, selectionArgs[9]);


                       long insertedId =  expensesDatabase.insert(ExpenseEntry.TABLE_NAME, null, values);
                       System.out.println("insertedId: " + insertedId);
                    }



                    /*Debug code
                    expenseTableCursor.moveToFirst();

                    for(int j = 0; j<expenseTableCursor.getCount(); j++){
                        String row = + expenseTableCursor.getInt(expenseTableCursor.getColumnIndex(ExpenseEntry._ID))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_OPTION))
                                + expenseTableCursor.getInt(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DAY))
                                + expenseTableCursor.getInt(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_MONTH))
                                + expenseTableCursor.getInt(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_YEAR))
                                + expenseTableCursor.getDouble(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DESCRIPTION))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_CATEGORY))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DATE))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_COORDINATES));
                        System.out.println("restore: "+ row);
                        expenseTableCursor.moveToNext();
                    }
                    */



                    expenseBackupTableCursor.moveToNext();
                }
            } catch (IOException e) {

            }


            return null;

        }

    }


    public Task<String> restoreTask(restoreBackupTaskParams taskParams) {
        return Tasks.call(mExecutor, () -> {
            try {
                //File directory = new File("/data/user/0/com.example.android.budgetapplication/databases");
                FileList fileList = taskParams.driveService.files().list().setSpaces("drive").execute();
                String fileId = null;

                for (File file : fileList.getFiles()) {

                    fileId = file.getId();

                }

                //Download from drive to local app database directory
                if (!fileId.isEmpty()) {
                    java.io.File dbfile = new java.io.File("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db");
                    OutputStream outputStream = new FileOutputStream("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db");
                    taskParams.driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
                    //outputStream.close();
                }

                //Check if backup downloaded successfully
                java.io.File directory = new java.io.File("/data/user/0/com.example.android.budgetapplication/databases/");
                java.io.File[] files = directory.listFiles();
                for (java.io.File oneFile : files) {
                    System.out.println("wj java.io.File name in local directory: " + oneFile.getName());
                }

                SQLiteDatabase expenseBackupDatabase = SQLiteDatabase.openDatabase("/data/user/0/com.example.android.budgetapplication/databases/expenses_backup.db", null, SQLiteDatabase.OPEN_READWRITE);
                Cursor expenseBackupTableCursor = expenseBackupDatabase.rawQuery("SELECT * from expenses", null);
                System.out.println("c.getCount(): " + expenseBackupTableCursor.getCount());
                Log.d("DriveServiceHelper", "test: " + String.valueOf(expenseBackupTableCursor.getCount()));


                //Columns to retrieve from original expense.db during query
                String[] expenseTableCursorProjection = {
                        ExpenseEntry._ID,
                        ExpenseEntry.COLUMN_OPTION,
                        ExpenseEntry.COLUMN_DAY,
                        ExpenseEntry.COLUMN_MONTH,
                        ExpenseEntry.COLUMN_YEAR,
                        ExpenseEntry.COLUMN_AMOUNT,
                        ExpenseEntry.COLUMN_DESCRIPTION,
                        ExpenseEntry.COLUMN_CATEGORY,
                        ExpenseEntry.COLUMN_DATE,
                        ExpenseEntry.COLUMN_COORDINATES
                };


                //Retrieve from expense_backup.db to cross-check with expense.db
                int idColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry._ID);
                int optionColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_OPTION);
                int dayColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DAY);
                int monthColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_MONTH);
                int yearColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_YEAR);
                int amountColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT);
                int descriptionColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DESCRIPTION);
                int categoryColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_CATEGORY);
                int dateColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
                int coordinatesColIdx = expenseBackupTableCursor.getColumnIndex(ExpenseEntry.COLUMN_COORDINATES);

                expenseBackupTableCursor.moveToFirst();

                for(int i = 0; i<expenseBackupTableCursor.getCount(); i++) {
                    int cId = expenseBackupTableCursor.getInt(idColIdx);
                    String cOption = expenseBackupTableCursor.getString(optionColIdx);
                    int cDay = expenseBackupTableCursor.getInt(dayColIdx);
                    int cMonth = expenseBackupTableCursor.getInt(monthColIdx);
                    int cYear = expenseBackupTableCursor.getInt(yearColIdx);
                    double cAmount = expenseBackupTableCursor.getDouble(amountColIdx);
                    String cDescription = expenseBackupTableCursor.getString(descriptionColIdx);
                    String cCategory = expenseBackupTableCursor.getString(categoryColIdx);
                    String cDate = expenseBackupTableCursor.getString(dateColIdx);
                    String cCoordinates = expenseBackupTableCursor.getString(coordinatesColIdx);

                    boolean coordinatesSet=true;
                    if(cCoordinates.equals("")){
                        coordinatesSet = false;
                        cCoordinates = "-";
                    }






                    //Define selection arguments
                    String path = "/" + String.valueOf(cId) + "/" + cOption + "/" + String.valueOf(cDay) + "/" + String.valueOf(cMonth) + "/"
                            + String.valueOf(cYear) + "/" + String.valueOf(cAmount) + "/" + cDescription + "/" + cCategory + "/"
                            + cDate + "/" + cCoordinates;

                    //get id/option/day/month/year.....
                    String strUri = path.substring(path.indexOf("/") + 1);
                    // {date, category, option}
                    String[] selectionArgs = strUri.split("/");

                    if(coordinatesSet == false){
                        selectionArgs[9] = "";
                    }

                    //Define selection
                    String selection = ExpenseContract.ExpenseEntry._ID + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_OPTION + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_DAY + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_MONTH + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_YEAR + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION+ "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_CATEGORY + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_DATE + "=? " + "AND " +
                            ExpenseContract.ExpenseEntry.COLUMN_COORDINATES + "=?";



                    // This will perform a query on the expenses table where the {date, expense/income category and
                    // option} equals the selectionArgs to return a Cursor containing rows of the table.
                    SQLiteDatabase expensesDatabase = SQLiteDatabase.openDatabase("/data/user/0/com.example.android.budgetapplication/databases/expenses.db", null, SQLiteDatabase.OPEN_READWRITE);
                    Cursor expenseTableCursor = expensesDatabase.query(ExpenseContract.ExpenseEntry.TABLE_NAME, expenseTableCursorProjection, selection, selectionArgs,
                            null, null, null);



//                    Cursor expenseTableCursor = restoreBackupTaskParam[0].mContext.getContentResolver().query(
//                            Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
//                            expenseTableCursorProjection,
//                            null,
//                            null
//
//                    );
                    System.out.println("expenseTableCursor count: " + expenseTableCursor.getCount());
                    System.out.println("path: " + path);

                    //Row from backup does not exist in local db
                    if(expenseTableCursor.getCount() == 0){
                        ContentValues values = new ContentValues();
                        values.put(ExpenseEntry._ID, cId);
                        values.put(ExpenseEntry.COLUMN_OPTION, cOption);
                        values.put(ExpenseEntry.COLUMN_DAY, cDay);
                        values.put(ExpenseEntry.COLUMN_MONTH, cMonth);
                        values.put(ExpenseEntry.COLUMN_YEAR, cYear);
                        values.put(ExpenseEntry.COLUMN_AMOUNT, cAmount);
                        values.put(ExpenseEntry.COLUMN_DESCRIPTION, cDescription);
                        values.put(ExpenseEntry.COLUMN_CATEGORY, cCategory);
                        values.put(ExpenseEntry.COLUMN_DATE, cDate);
                        values.put(ExpenseEntry.COLUMN_COORDINATES, selectionArgs[9]);


                        long insertedId =  expensesDatabase.insert(ExpenseEntry.TABLE_NAME, null, values);
                        System.out.println("insertedId: " + insertedId);
                    }



                    /*Debug code
                    expenseTableCursor.moveToFirst();

                    for(int j = 0; j<expenseTableCursor.getCount(); j++){
                        String row = + expenseTableCursor.getInt(expenseTableCursor.getColumnIndex(ExpenseEntry._ID))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_OPTION))
                                + expenseTableCursor.getInt(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DAY))
                                + expenseTableCursor.getInt(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_MONTH))
                                + expenseTableCursor.getInt(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_YEAR))
                                + expenseTableCursor.getDouble(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DESCRIPTION))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_CATEGORY))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_DATE))
                                + expenseTableCursor.getString(expenseTableCursor.getColumnIndex(ExpenseEntry.COLUMN_COORDINATES));
                        System.out.println("restore: "+ row);
                        expenseTableCursor.moveToNext();
                    }
                    */



                    expenseBackupTableCursor.moveToNext();
                }
            } catch (IOException e) {

            }

            return "";
        });
    }


    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<Pair<String, String>> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the metadata as a File object.
            File metadata = mDriveService.files().get(fileId).execute();
            String name = metadata.getName();

            // Stream the file contents to a String.
            try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String contents = stringBuilder.toString();

                return Pair.create(name, contents);
            }
        });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> saveFile(String fileId, String name, String content) {
        return Tasks.call(mExecutor, () -> {
            // Create a File containing any metadata changes.
            File metadata = new File().setName(name);

            // Convert content to an AbstractInputStreamContent instance.
            ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, contentStream).execute();

            return null;
        });
    }

    /**
     * Returns a {@link FileList} containing all the visible files in the user's My Drive.
     *
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     */
    public Task<FileList> queryFiles() {
        return Tasks.call(mExecutor, () ->
                mDriveService.files().list().setSpaces("drive").execute());
    }

    public Task<Boolean> query() {
        return Tasks.call(mExecutor, () -> {
            Log.d("DriveServiceHelper", " wj Querying for files.");

            this.queryFiles()
                    .addOnSuccessListener(fileList -> {
                        StringBuilder builder = new StringBuilder();
                        //fileList only contains files from Google Drive created by this app
                        for (File file : fileList.getFiles()) {
                            //builder.append(file.getName()).append("\n");
                            Log.d("DriveServiceHelper", " wj fileNames: " + file.getName());
                            if (!file.getName().equals("expenses" + ms + ".db")) {

                                try {
                                    Log.d("DriveServiceHelper", "wj delete success! ");
                                    mDriveService.files().delete(file.getId()).execute();
                                } catch (IOException e) {
                                    Log.e("DriveServiceHelper", "wj delete failed " + e);
                                }
                            }

                        }
                    })
                    .addOnFailureListener(exception -> Log.e("MainActivity", "Unable to query files.", exception));
            return true;
        });


    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.sqlite3");

        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Pair<String, String>> openFileUsingStorageAccessFramework(
            ContentResolver contentResolver, Uri uri) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the document's display name from its metadata.
            String name;
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    name = cursor.getString(nameIndex);
                } else {
                    throw new IOException("Empty cursor returned for file.");
                }
            }

            // Read the document's contents as a String.
            String content;
            try (InputStream is = contentResolver.openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                content = stringBuilder.toString();
            }

            return Pair.create(name, content);
        });
    }


}