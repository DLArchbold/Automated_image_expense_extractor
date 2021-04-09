package com.example.android.budgetapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
//import android.support.v4.util.Pair;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
//import android.util.Pair;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

            try {
              FileList fileList =   deleteBackupTaskParam[0].driveService.files().list().setSpaces("drive").execute();
                for (File file : fileList.getFiles()) {
                    //builder.append(file.getName()).append("\n");
                    Log.d("doInBackground", " wj fileNames: " + file.getName());
                    if (!file.getName().equals("expenses" + deleteBackupTaskParam[0].ms + ".db")) {

                        try {
                            Log.d("doInBackground", "wj delete success! ");
                            deleteBackupTaskParam[0].driveService.files().delete(file.getId()).execute();
                        } catch (IOException e) {
                            Log.e("doInBackground", "wj delete failed " + e);
                        }
                    }

                }

            } catch (IOException e) {

            }
            return null;

        }

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