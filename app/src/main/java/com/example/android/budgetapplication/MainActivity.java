
package com.example.android.budgetapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.budgetapplication.adapters.ExpandableListAdapter;
import com.example.android.budgetapplication.adapters.OneLevelExpenseAdapter;
import com.example.android.budgetapplication.adapters.SliderAdapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.budgetapplication.data.ExpenseContract.ExpenseEntry;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.squareup.picasso.Picasso;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveResourceClient;
import com.example.android.budgetapplication.DriveServiceHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager mSlideViewPager;
    private TextView mDotLayout;
    private SliderAdapter sliderAdapter;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    Button signOutButton;
    View navHeader;
    View navMenu;
    TextView email_field;
    TextView name_field;
    TextView iconTxt;
    ImageView iconImg;
    private static final int RC_SIGN_IN = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 3;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    Uri dbUri;
    private DriveServiceHelper mDriveServiceHelper;
    Drive googleDriveService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
//                Intent wayIntent = new Intent(MainActivity.this, WayActivity.class);
//
//                startActivity(wayIntent);
//
//            }
//        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //Display all database records
        displayDatabaseInfo();


        //Deal with Google sign in and header
        navHeader = navigationView.getHeaderView(0);
        signInButton = (SignInButton) navHeader.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signOutButton = (Button) navHeader.findViewById(R.id.sign_out_button);


        email_field = navHeader.findViewById(R.id.email);
        name_field = navHeader.findViewById(R.id.name);
        iconTxt = navHeader.findViewById(R.id.title);
        iconImg = navHeader.findViewById(R.id.imageView);

        View.OnClickListener signInClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        };
        signInButton.setOnClickListener(signInClickListener);
        View.OnClickListener signOutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        };
        signOutButton.setOnClickListener(signOutClickListener);


        //signIn();

//        java.io.File directory = new java.io.File("/data/user/0/com.example.android.budgetapplication/databases");
//        java.io.File[] files = directory.listFiles();
//        for (java.io.File oneFile : files){
//            System.out.println("wj file name: " + oneFile.getName());
//        }


    }

    private void updateSignIn(GoogleSignInAccount account) {
        if (account == null) {
            //Display sign in button
            //Hide email/name/sign out button
            signOutButton.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            email_field.setVisibility(View.GONE);
            name_field.setVisibility(View.GONE);
            iconImg.setVisibility(View.GONE);
            iconTxt.setVisibility(View.GONE);
        } else {
            //Hide sign in button
            //Display email/name/sign out button
            signOutButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            email_field.setVisibility(View.VISIBLE);
            name_field.setVisibility(View.VISIBLE);
            email_field.setText(account.getEmail());
            String displayName = account.getDisplayName();
            name_field.setText(displayName);
            if (account.getPhotoUrl() != null) {
                iconImg.setVisibility(View.VISIBLE);
                iconTxt.setVisibility(View.GONE);
                String photoUri = account.getPhotoUrl().toString();
                Picasso.with(getApplicationContext()).load(photoUri).resize(72, 0).transform(new CircleTransform()).into(iconImg);
            } else {
                iconTxt.setVisibility(View.VISIBLE);
                iconImg.setVisibility(View.GONE);
                System.out.println(displayName);
                String firstAlphabetFirstName = String.valueOf(displayName.toUpperCase().charAt(0));
                //displayName.toUpperCase().charAt(0);
                iconTxt.setText(firstAlphabetFirstName);
                // icon.setBackground(R.mipmap.icon_color);

            }

//            mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
//            // Build a drive resource client.
//            mDriveResourceClient =
//                    Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));

            // Excel Sheet path from SD card
            final String filePath = "/data/user/0/com.example.android.budgetapplication/databases/expenses.db";
            //saveFileToDrive(filePath);

        }
    }
//
//    /**
//     * Create a new file and save it to Drive.
//     */
//    private void saveFileToDrive(final String filePath) {
//        // Start by creating a new contents, and setting a callback.
//        Log.i("MainActivity", "Creating new contents. wj ");
//
//        mDriveResourceClient
//                .createContents()
//                .continueWithTask(
//                        new Continuation<DriveContents, Task<Void>>() {
//                            @Override
//                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
//                                return createFileIntentSender(task.getResult(), new File(filePath));
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w("MainActivity", "Failed to create new contents. wj", e);
//                            }
//                        });
//    }
//
//    /**
//     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
//     * CreateFileActivityOptions} for user to create a new photo in Drive.
//     */
//    private Task<Void> createFileIntentSender(DriveContents driveContents, File file) throws Exception {
//        Log.i("MainActivity", "New contents created. wj");
//
//        OutputStream outputStream = driveContents.getOutputStream();
//        InputStream in = new FileInputStream(file);
//        try {
//            try {
//                // Transfer bytes from in to out
//                byte[] buf = new byte[1024];
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    outputStream.write(buf, 0, len);
//                }
//            } finally {
//                outputStream.close();
//            }
//        } finally {
//            in.close();
//        }
//
//
//        // Create the initial metadata - MIME type and title.
//        // Note that the user will be able to change the title later.
//        MetadataChangeSet metadataChangeSet =
//                new MetadataChangeSet.Builder()
//                        .setMimeType(getMimeType( dbUri))
//                        .setTitle("expenses.db")
//                        .build();
//        // Set up options to configure and display the create file activity.
//        CreateFileActivityOptions createFileActivityOptions =
//                new CreateFileActivityOptions.Builder()
//                        .setInitialMetadata(metadataChangeSet)
//                        .setInitialDriveContents(driveContents)
//                        .build();
//
//        return mDriveClient
//                .newCreateFileActivityIntentSender(createFileActivityOptions)
//                .continueWith(
//                        new Continuation<IntentSender, Void>() {
//                            @Override
//                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
//                                startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0);
//                                return null;
//                            }
//                        });
//    }


    /**
     * Opens a file from its {@code uri} returned from the Storage Access Framework file picker
     * initiated by pressing R.id.menu_open_gdrive navigation menu item.
     */
    private void openFileFromFilePicker(Uri uri) {
        if (mDriveServiceHelper != null) {
            Log.d("MainActivity", "Opening wj " + uri.getPath());

            mDriveServiceHelper.openFileUsingStorageAccessFramework(getContentResolver(), uri)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
//                        String content = nameAndContent.second;
//
//                        mFileTitleEditText.setText(name);
//                        mDocContentEditText.setText(content);

                        // Files opened through SAF cannot be modified.
                        //setReadOnlyMode();
                    })
                    .addOnFailureListener(exception ->
                            Log.e("MainActivity", "Unable to open file from picker. wj", exception));
        }
    }

    private void createFile() {
        if (mDriveServiceHelper != null) {
            Log.d("MainActivity", "Creating a file.");

//            mDriveServiceHelper.createFile()
//                    .addOnSuccessListener(fileId -> readFile(fileId))
//                    .addOnFailureListener(exception ->
//                            Log.e("MainActivity", "Couldn't create file.", exception));

            //Delete old backup first
            Date date = new Date();
            mDriveServiceHelper.ms = "_" + String.valueOf(date.getTime());
            deleteBackupTaskParams taskParams = new deleteBackupTaskParams(googleDriveService, mDriveServiceHelper.ms, getApplicationContext());
            new DriveServiceHelper.deleteBackupsTask().execute(taskParams);
            Toast.makeText(getApplicationContext(), "Creating backup.....", Toast.LENGTH_LONG).show();

            //Create new backup
            mDriveServiceHelper.createFile()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Toast.makeText(getApplicationContext(), "Backup created!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(exception ->
                            Log.e("MainActivity", "Couldn't create file.", exception));


        }
    }

    private void restoreDB() {
        if (mDriveServiceHelper != null) {
            restoreExpenseTable(mDriveServiceHelper);
            restoreBudgetTable(mDriveServiceHelper);
        }

    }

    public void restoreExpenseTable(DriveServiceHelper mDriveServiceHelper){
        Date date = new Date();
        mDriveServiceHelper.ms = "_" + String.valueOf(date.getTime());
        restoreBackupTaskParams taskParams = new restoreBackupTaskParams(googleDriveService, mDriveServiceHelper.ms, getApplicationContext());
        OnSuccessListener<String> restoreTaskOnSuccessListener = new OnSuccessListener<String>(){
            @Override
            public void onSuccess(String s) {
                displayDatabaseInfo();
                Toast.makeText(getApplicationContext(), "Expense data restore done!", Toast.LENGTH_LONG).show();
            }
        };

        OnFailureListener restoreTaskOnFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No expense backup data exists!",  Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Failed to restore expense data!", Toast.LENGTH_LONG).show();
            }
        };
        mDriveServiceHelper.restoreExpensesTask(taskParams).addOnSuccessListener(restoreTaskOnSuccessListener).addOnFailureListener(restoreTaskOnFailureListener);
        //new DriveServiceHelper.restoreAsyncTask().execute(taskParams);

        Toast.makeText(getApplicationContext(), "Restoring.....", Toast.LENGTH_LONG).show();
    }

    public void restoreBudgetTable(DriveServiceHelper mDriveServiceHelper){
        Date date = new Date();
        mDriveServiceHelper.ms = "_" + String.valueOf(date.getTime());
        restoreBackupTaskParams taskParams = new restoreBackupTaskParams(googleDriveService, mDriveServiceHelper.ms, getApplicationContext());
        OnSuccessListener<String> restoreTaskOnSuccessListener = new OnSuccessListener<String>(){
            @Override
            public void onSuccess(String s) {
                displayDatabaseInfo();
                Toast.makeText(getApplicationContext(), "Budget data restore done!", Toast.LENGTH_LONG).show();
            }
        };

        OnFailureListener restoreTaskOnFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No budget backup data exists!",  Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Failed to restore budget data!", Toast.LENGTH_LONG).show();
            }
        };
        mDriveServiceHelper.restoreBudgetTask(taskParams).addOnSuccessListener(restoreTaskOnSuccessListener).addOnFailureListener(restoreTaskOnFailureListener);
        //new DriveServiceHelper.restoreAsyncTask().execute(taskParams);

        Toast.makeText(getApplicationContext(), "Restoring.....", Toast.LENGTH_LONG).show();
    }

    private void readFile(String fileId) {
        if (mDriveServiceHelper != null) {
            Log.d("MainActivity", "wj Reading file " + fileId);

            mDriveServiceHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

//                        mFileTitleEditText.setText(name);
//                        mDocContentEditText.setText(content);
//
//                        setReadWriteMode(fileId);
                    })
                    .addOnFailureListener(exception ->
                            Log.e("MainActivity", "WJ Couldn't read file.", exception));
        }
    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signOutButton.setVisibility(View.GONE);
                        signInButton.setVisibility(View.VISIBLE);
                        email_field.setVisibility(View.GONE);
                        name_field.setVisibility(View.GONE);
                        iconImg.setVisibility(View.GONE);
                        iconTxt.setVisibility(View.GONE);
                        revokeAccess();
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        googleDriveService = null;
                        mDriveServiceHelper = null;

                    }
                });
    }

    private void signIn() {
        mGoogleSignInClient = buildGoogleSignInClient();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //Request code number for sign in when onActivityResult() returned

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Build a Google SignIn client.
     */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        return GoogleSignIn.getClient(getApplicationContext(), gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);


        switch (requestCode) {
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            case RC_SIGN_IN:
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(resultData);

                handleSignInResult(task);
                break;
            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri != null) {
                        openFileFromFilePicker(uri);
                    }
                }
                break;
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            completedTask.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount googleAccount) {
                    Log.d("MainActivity", "wj Signed in as " + googleAccount.getEmail());
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    getApplicationContext(), Collections.singleton(DriveScopes.DRIVE_FILE));

                    credential.setSelectedAccount(googleAccount.getAccount());
                    googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                            .setApplicationName("Drive API Migration")
                            .build();
                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService, getApplicationContext());

                }
            }).addOnFailureListener(exception -> Log.e("MainActivity", "wj Unable to sign in.", exception));
            // Signed in successfully, show authenticated UI.
            updateSignIn(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("MainActivity", "signInResult:failed code=" + e.getStatusCode());
            updateSignIn(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //signOut();
        updateSignIn(account);
    }


    private void displayDatabaseInfo() {

        ExpandableListAdapter listAdapter;
//        List<String> listDataHeader = new ArrayList<>();
//        HashMap<String, List<String>> listDataChild = new HashMap<>();

//      No need to get ExpenseDbHelper when using ContentResolver and ContentProvider
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//        ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);
//
//        // Create and/or open a database to read from it
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Columns to retrieve during query
        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.COLUMN_OPTION,
                ExpenseEntry.COLUMN_DAY,
                ExpenseEntry.COLUMN_MONTH,
                ExpenseEntry.COLUMN_YEAR,
                ExpenseEntry.COLUMN_AMOUNT,
                ExpenseEntry.COLUMN_DESCRIPTION,
                ExpenseEntry.COLUMN_CATEGORY,
                ExpenseEntry.COLUMN_DATE
        };


        /*
        Perform query on content provider using content resolver using URI.
        Use the {@link
         */
/*
        //To delete row entries
//        int  id = 1;
//        for(int i = 1; i<7; i++){
//            int cursor = getContentResolver().delete(
//                    Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, String.valueOf(i)),
//                    null,
//                    null
//                    );
//        }


////        Cursor cursor = getContentResolver().query(
////                ExpenseEntry.CONTENT_URI,
////                projection,
////                null,
////                null,
////                ExpenseEntry.COLUMN_YEAR + " DESC,  " + ExpenseEntry.COLUMN_MONTH + " DESC, " + ExpenseEntry.COLUMN_DAY + " DESC"
////
////        );
//
//        Cursor cursor = getContentResolver().query(
//                Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, "Income"),
//                projection,
//                null,
//                null,
//                ExpenseEntry.COLUMN_YEAR + " ASC,  " + ExpenseEntry.COLUMN_MONTH + " ASC, " + ExpenseEntry.COLUMN_DAY + " ASC"
//
//        );
//
//        //Extract all unique dates from expenses table for data header for "Day" view
//        //Map each list header to a list of child data (expense/income categories)
//        List<String> expenseIncomeCategory = Arrays.asList(getResources().getStringArray(R.array.array_expense_income_category));
//        LinkedHashSet<String> uniqueDates = new LinkedHashSet<>();
//        while (cursor.moveToNext()) {
//            int dateColIdx = cursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
//            String curCursorDate = cursor.getString(dateColIdx);
//
//            if (!uniqueDates.contains(curCursorDate)) {
//
//                listDataHeader.add(curCursorDate);
//                listDataChild.put(curCursorDate, expenseIncomeCategory);
//            }
//            uniqueDates.add(curCursorDate);
//        }
//
//
//        //ExpandableListView to populate, use ExapandableListAdapter
////        ExpandableListView list = (ExpandableListView)findViewById(R.id.list);
////        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
////        list.setAdapter(listAdapter);
//
//
////        //List view to populate, use CursorAdapter
////        ListView list = (ListView)findViewById(R.id.list);
////        //Adapter to create list item view for each row of data in cursor
////        ExpenseCursorAdapter adapter = new ExpenseCursorAdapter(this, cursor);
////
////        //Attach adapter to list view
////        list.setAdapter(adapter);
//
//
//
////        //2 level ExpandableListView to populate, use ParentLevelAdapter and SecondLevelAddapter
////        Map<String, Map<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection);
////
////        // Init top level data
////        //Top levl data is uniqueDate
////        ExpandableListView mExpandableListView = (ExpandableListView) findViewById(R.id.list);
////        if (mExpandableListView != null) {
////            //pass list of unique dates as listDataHeader
////            ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(this, listDataHeader, dateCatExpense);
////            mExpandableListView.setAdapter(parentLevelAdapter);
////        }
//
//
//
//        /////////////////////////Adding ViewPager//////////////////////////////////////
//
//
//
//        //Array of OneLevelExpenseAdapters to store categories->expenses for 1 date and page for each array element
//        OneLevelExpenseAdapter [] pageAdapters = new OneLevelExpenseAdapter[uniqueDates.size()];
//        //dateCatExpense contains Dates->Categories->Cursors
//        LinkedHashMap<String, LinkedHashMap<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection);
//        //Create an array of OneLevelExpenseAdapter objects for all dateCatExpense entries for each page's ExpandableListView
//        Iterator<LinkedHashMap.Entry<String, LinkedHashMap<String, Cursor>>> iterator = dateCatExpense.entrySet().iterator();
//        int ctr =0;
//        while(iterator.hasNext()){
//            //Move to next date->category->expenses map entry then get the categories->expenses
//            Map<String, Cursor> catsExpenses = iterator.next().getValue();
//            Object[] validCatArr = catsExpenses.keySet().toArray();
//
//            List<String> validCategories = new ArrayList<String>();
//            String valCatArray [] = Arrays.copyOf(validCatArr, validCatArr.length, String[].class);
//            List<String> validCatList = Arrays.asList(valCatArray);
//
//
//            pageAdapters[ctr] = new OneLevelExpenseAdapter(this, validCatList, catsExpenses);
//            ctr++;
//        }
*/

        //Unique dates and their would be idx on 2d array that contains income/expense Adapters
        HashMap<String, Integer> datePosition = mapDatesToIdx();
        HashMap<Integer, String> idxDate = mapIdxToDates(datePosition);

        //Obtain expense and income adapters corresponding to their day
        OneLevelExpenseAdapter[][] expenseIncomePageAdapters = new OneLevelExpenseAdapter[2][mapDatesToIdx().keySet().size()];
        expenseIncomePageAdapters = getIncomeOrExpenseAdapter("Expense", datePosition, expenseIncomePageAdapters);
        expenseIncomePageAdapters = getIncomeOrExpenseAdapter("Income", datePosition, expenseIncomePageAdapters);

        //Split expense and income adapters
        OneLevelExpenseAdapter[] expensePageAdapters = expenseIncomePageAdapters[0];
        OneLevelExpenseAdapter[] incomePageAdapters = expenseIncomePageAdapters[1];

        //Fill array for date balances for each page
        double[] balanceForEachDate = dateBalances(datePosition);

        //Assign SliderAdapter to ViewPager after readying data in SliderAdapter
        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        sliderAdapter = new SliderAdapter(this, expensePageAdapters, incomePageAdapters, idxDate, balanceForEachDate);
        mSlideViewPager.setAdapter(sliderAdapter);
        //view pager start on last page for
        mSlideViewPager.setCurrentItem(Math.max(expensePageAdapters.length, incomePageAdapters.length));

    }

    private HashMap<Integer, String> mapIdxToDates(HashMap<String, Integer> mapDatesToIdx) {
        HashMap<Integer, String> idxToDates = new HashMap<>();
        for (String date : mapDatesToIdx.keySet()) {
            idxToDates.put(mapDatesToIdx.get(date), date);
        }
        return idxToDates;
    }

    private HashMap<String, Integer> mapDatesToIdx() {

        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.COLUMN_OPTION,
                ExpenseEntry.COLUMN_DAY,
                ExpenseEntry.COLUMN_MONTH,
                ExpenseEntry.COLUMN_YEAR,
                ExpenseEntry.COLUMN_AMOUNT,
                ExpenseEntry.COLUMN_DESCRIPTION,
                ExpenseEntry.COLUMN_CATEGORY,
                ExpenseEntry.COLUMN_DATE
        };

        Cursor cursor = getContentResolver().query(
                ExpenseEntry.CONTENT_URI,
                projection,
                null,
                null,
                ExpenseEntry.COLUMN_YEAR + " ASC,  " + ExpenseEntry.COLUMN_MONTH + " ASC, " + ExpenseEntry.COLUMN_DAY + " ASC"

        );

        //Extract all unique dates from expenses table for data header for "Day" view
        //Map each list header to a list of child data (expense/income categories)
        int pos = 0;
        HashMap<String, Integer> datePos = new HashMap<String, Integer>();
        while (cursor.moveToNext()) {
            int dateColIdx = cursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
            String curCursorDate = cursor.getString(dateColIdx);

            if (!datePos.containsKey(curCursorDate)) {
                datePos.put(curCursorDate, pos++);
            }

        }

        return datePos;

    }

    private OneLevelExpenseAdapter[][] getIncomeOrExpenseAdapter(String expenseOrIncome, HashMap<String, Integer> datePosition, OneLevelExpenseAdapter[][] expenseIncomePageAdapters) {

        List<String> listDataHeader = new ArrayList<>();
        HashMap<String, List<String>> listDataChild = new HashMap<>();
//        Cursor cursor = getContentResolver().query(
//                ExpenseEntry.CONTENT_URI,
//                projection,
//                null,
//                null,
//                ExpenseEntry.COLUMN_YEAR + " DESC,  " + ExpenseEntry.COLUMN_MONTH + " DESC, " + ExpenseEntry.COLUMN_DAY + " DESC"
//
//        );

        //Columns to retrieve during query
        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.COLUMN_OPTION,
                ExpenseEntry.COLUMN_DAY,
                ExpenseEntry.COLUMN_MONTH,
                ExpenseEntry.COLUMN_YEAR,
                ExpenseEntry.COLUMN_AMOUNT,
                ExpenseEntry.COLUMN_DESCRIPTION,
                ExpenseEntry.COLUMN_CATEGORY,
                ExpenseEntry.COLUMN_DATE
        };

        Cursor cursor = getContentResolver().query(
                Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, expenseOrIncome),
                projection,
                null,
                null,
                ExpenseEntry.COLUMN_YEAR + " ASC,  " + ExpenseEntry.COLUMN_MONTH + " ASC, " + ExpenseEntry.COLUMN_DAY + " ASC"

        );

        //Extract all unique dates from expenses table for data header for "Day" view
        //Map each list header to a list of child data (expense/income categories)
        List<String> expenseIncomeCategory = Arrays.asList(getResources().getStringArray(R.array.array_expense_income_category));
        LinkedHashSet<String> uniqueDates = new LinkedHashSet<>();
        while (cursor.moveToNext()) {
            int dateColIdx = cursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
            String curCursorDate = cursor.getString(dateColIdx);

            if (!uniqueDates.contains(curCursorDate)) {

                listDataHeader.add(curCursorDate);
                listDataChild.put(curCursorDate, expenseIncomeCategory);
            }
            uniqueDates.add(curCursorDate);
        }


        //ExpandableListView to populate, use ExapandableListAdapter
//        ExpandableListView list = (ExpandableListView)findViewById(R.id.list);
//        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//        list.setAdapter(listAdapter);


//        //List view to populate, use CursorAdapter
//        ListView list = (ListView)findViewById(R.id.list);
//        //Adapter to create list item view for each row of data in cursor
//        ExpenseCursorAdapter adapter = new ExpenseCursorAdapter(this, cursor);
//
//        //Attach adapter to list view
//        list.setAdapter(adapter);


//        //2 level ExpandableListView to populate, use ParentLevelAdapter and SecondLevelAddapter
//        Map<String, Map<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection);
//
//        // Init top level data
//        //Top levl data is uniqueDate
//        ExpandableListView mExpandableListView = (ExpandableListView) findViewById(R.id.list);
//        if (mExpandableListView != null) {
//            //pass list of unique dates as listDataHeader
//            ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(this, listDataHeader, dateCatExpense);
//            mExpandableListView.setAdapter(parentLevelAdapter);
//        }


        /////////////////////////Adding ViewPager//////////////////////////////////////


        //Array of OneLevelExpenseAdapters to store categories->expenses for 1 date and page for each array element
        OneLevelExpenseAdapter[] pageAdapters = new OneLevelExpenseAdapter[uniqueDates.size()];
        //dateCatExpense contains Dates->Categories->Cursors
        Object[] dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection, expenseOrIncome);
        LinkedHashMap<String, LinkedHashMap<String, Cursor>> catExpense = (LinkedHashMap<String, LinkedHashMap<String, Cursor>>) dateCatExpense[0];
        LinkedHashMap<String, LinkedHashMap<String, Cursor>> catSum = (LinkedHashMap<String, LinkedHashMap<String, Cursor>>) dateCatExpense[1];
        //LinkedHashMap<String, LinkedHashMap<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection, expenseOrIncome);
        //Create an array of OneLevelExpenseAdapter objects for all dateCatExpense entries for each page's ExpandableListView
        Iterator<LinkedHashMap.Entry<String, LinkedHashMap<String, Cursor>>> iteratorCatExpense = catExpense.entrySet().iterator();
        Iterator<LinkedHashMap.Entry<String, LinkedHashMap<String, Cursor>>> iteratorCatSum = catSum.entrySet().iterator();
        int ctr = 0;
        while (iteratorCatExpense.hasNext() && iteratorCatSum.hasNext()) {
            //Move to next date->category->expenses map entry then get the categories->expenses
            String curDate = iteratorCatExpense.next().getKey();
            LinkedHashMap<String, Cursor> catsExpenses = catExpense.get(curDate);
            Object[] validCatArr = catsExpenses.keySet().toArray();
            LinkedHashMap<String, Cursor> catsSum = catSum.get(curDate);

            //Create 1 adapter for 1 page which rep. 1 day's expense/income
            List<String> validCategories = new ArrayList<String>();
            String valCatArray[] = Arrays.copyOf(validCatArr, validCatArr.length, String[].class);
            List<String> validCatList = Arrays.asList(valCatArray);
            pageAdapters[ctr] = new OneLevelExpenseAdapter(this, validCatList, catsExpenses, catsSum, expenseOrIncome);

            //Expense pageAdapters fill column 0, while income page adapters fill column 1. Each row represents a day.
            //which may have either or both expenses/income
            int curDatePosition = datePosition.get(curDate);
            if (expenseOrIncome.equals("Expense")) {
                expenseIncomePageAdapters[0][curDatePosition] = pageAdapters[ctr];
            } else { //if it's income
                expenseIncomePageAdapters[1][curDatePosition] = pageAdapters[ctr];
            }

            ctr++;
        }
        return expenseIncomePageAdapters;
    }

    private double[] dateBalances(HashMap<String, Integer> datePosition) {
        String[] projection = {ExpenseEntry.COLUMN_DATE, ExpenseEntry.COLUMN_AMOUNT};
        Cursor balancesCursor;
        balancesCursor = getContentResolver().query(
                ExpenseEntry.CONTENT_URI,
                projection,
                null,
                null,
                null

        );

        double[] balanceForEachDate = new double[datePosition.size()];


        balancesCursor.moveToFirst();
        int dateColIdx = balancesCursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
        int amtColIdx = balancesCursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT);
        for (int i = 0; i < balancesCursor.getCount(); i++) {

            String date = balancesCursor.getString(dateColIdx);
            double amt = balancesCursor.getDouble(amtColIdx);
            System.out.println("date print: " + date);
            if (date.equals("20-2-2021")) {
                System.out.println("amt when 20-2-2021 " + amt);
            }
            balanceForEachDate[datePosition.get(date)] += amt;
            balancesCursor.moveToNext();
        }

        return balanceForEachDate;

    }


    private Object[]
    getDateCategoryData(Set<String> uniqueDates, List<String> expenseIncomeCategory,
                        Cursor cursor, String[] projection, String expenseOrIncome) {

        //Get Expenses for each category for each date
        /*
        22/10/2020->map{Bills ----> Bill expense/income 1 for 22/10/2020
                            ----> Bill expense/income 2 for 22/10/2020
                            ----> Bill expense/income 3 for 22/10/2020
                        Food ----> Food expense/income 1 for 22/10/2020
                            ----> Food expense/income 2 for 22/10/2020
                            ----> Food expense/income 3 for 22/10/2020
                            .
                            .
                            .
                        //end map}

            30/10/2020->map{Bills ----> Bill expense/income 1 for 30/10/2020
                                ----> Bill expense/income 2 for 30/10/2020
                                ----> Bill expense/income 3 for 30/10/2020
                            Food ----> Food expense/income 1 for 30/10/2020
                                ----> Food expense/income 2 for 30/10/2020
                                ----> Food expense/income 3 for 30/10/2020
                                .
                                .
                                .
                            //end map}
         */

//        String[] projection = {
//                ExpenseEntry._ID,
//                ExpenseEntry.COLUMN_OPTION,
//                ExpenseEntry.COLUMN_DAY,
//                ExpenseEntry.COLUMN_MONTH,
//                ExpenseEntry.COLUMN_YEAR,
//                ExpenseEntry.COLUMN_AMOUNT,
//                ExpenseEntry.COLUMN_DESCRIPTION,
//                ExpenseEntry.COLUMN_CATEGORY,
//                ExpenseEntry.COLUMN_DATE
//        };

        String[] sumProjection = {"sum(" + ExpenseEntry.COLUMN_AMOUNT + ")"};
        Cursor sumCursor;
        LinkedHashMap<String, LinkedHashMap<String, Cursor>> dateCatExpense = new LinkedHashMap<String, LinkedHashMap<String, Cursor>>();
        LinkedHashMap<String, LinkedHashMap<String, Cursor>> dateCatOptionSum = new LinkedHashMap<String, LinkedHashMap<String, Cursor>>();
        //For all unique dates
        for (String uniqueDate : uniqueDates) {


            //For each expense/income category, get data from db query
            LinkedHashMap<String, Cursor> oneDateCategoriesExpenses = new LinkedHashMap<>();
            LinkedHashMap<String, Cursor> oneDateCategoriesOptionSum = new LinkedHashMap<>();
            for (String category : expenseIncomeCategory) {
                String path = "/" + uniqueDate + "/" + category + "/" + expenseOrIncome;
                cursor = getContentResolver().query(
                        Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
                        projection,
                        null,
                        null,
                        ExpenseEntry.COLUMN_YEAR + " DESC,  " + ExpenseEntry.COLUMN_MONTH + " DESC, " + ExpenseEntry.COLUMN_DAY + " DESC"

                );

                //Get sum for individual category and option
                sumCursor = getContentResolver().query(
                        Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
                        sumProjection,
                        null,
                        null,
                        null

                );
//                sumCursor.moveToNext();
//                int sumColumnIndex = sumCursor.getColumnIndex(sumCursor.getColumnName(0));
//                System.out.println(sumCursor.getCount() + " "+ sumColumnIndex);
//                String sum = sumCursor.getString(sumColumnIndex);
//                System.out.println("sumCursor print: " +" sum: " + sum + " uniqueDate: " + uniqueDate + " category: " + category + " expenseOrIncome: "
//                + expenseOrIncome);


                if (cursor.getCount() > 0 && sumCursor.getCount() > 0) {
                    Log.e("MainActvity", String.valueOf("num of rows" + cursor.getCount()) + " " + uniqueDate + " " + category);
                    //For 1 date
                    //1st loop
                    // Bills ----> Bill expense/income 1 for 30/10/2020
//                            ----> Bill expense/income 2 for 30/10/2020
//                            ----> Bill expense/income 3 for 30/10/2020
                    //2nd loop
                    //    Food ----> Food expense/income 1 for 30/10/2020
                    //                                ----> Food expense/income 2 for 30/10/2020
                    //                                ----> Food expense/income 3 for 30/10/2020
                    //
                    oneDateCategoriesExpenses.put(category, cursor);

                    //For 1 date
                    //Bills(expenses/income) -> sum
                    //Transport (expenses/income)->sum
                    oneDateCategoriesOptionSum.put(category, sumCursor);
                }


            }

            //map date to list of categories and their respective expense/income data
            dateCatExpense.put(uniqueDate, oneDateCategoriesExpenses);
            //For each date
            //Date 1-> Bills(expenses/income)->sum
            //         ->Transport (expenses/income) ->sum)
            //Date 2-> Bills(expenses/income)->sum
            //         ->Transport (expenses/income) ->sum)
            dateCatOptionSum.put(uniqueDate, oneDateCategoriesOptionSum);
        }
        Object[] expenseAndSumMaps = new Object[2];
        expenseAndSumMaps[0] = dateCatExpense;
        expenseAndSumMaps[1] = dateCatOptionSum;
        return expenseAndSumMaps;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_manual) {

            Intent ManualEntryIntent = new Intent(MainActivity.this, ManualEntryActivity.class);
            startActivity(ManualEntryIntent);
        } else if (id == R.id.action_image_recognition) {

            Intent ImageRecognitionEntryIntent = new Intent(MainActivity.this, ImageRecognitionEntryActivity.class);
            startActivity(ImageRecognitionEntryIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_action_manual) {

            Intent ManualEntryIntent = new Intent(MainActivity.this, ManualEntryActivity.class);
            startActivity(ManualEntryIntent);
        } else if (id == R.id.menu_action_image_recognition) {
            //Open activity with expandable list view of set budgets, allow addition and selection of budget with form
            //In form set start/end date, category, spend and save amount, then sum from available dates
            //Then if still have days then figure out difference needed to overcome deficit if any.
            //

            Intent ImageRecognitionEntryIntent = new Intent(MainActivity.this, ImageRecognitionEntryActivity.class);
            startActivity(ImageRecognitionEntryIntent);


        } else if (id == R.id.menu_supposed_spending_rate) {
            Intent budgetListIntent = new Intent(MainActivity.this, BudgetListActivity.class);
            startActivity(budgetListIntent);
        } else if (id == R.id.menu_open_gdrive) {
            if (mDriveServiceHelper != null) {
                Log.d("MainActivity", "Opening file picker. wj");

                Intent pickerIntent = mDriveServiceHelper.createFilePickerIntent();

                // The result of the SAF Intent is handled in onActivityResult.
                startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT);
            }else {
                Toast.makeText(getApplicationContext(), "Please login first!", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.menu_backup) {
            if (mDriveServiceHelper != null) {
                Log.d("MainActivity", "Backing up. wj");
                createFile();
            } else {
                Toast.makeText(getApplicationContext(), "Please login first!", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.menu_restore) {
            if (mDriveServiceHelper != null) {
                Log.d("MainActivity", "Restoring wj");
                restoreDB();
            }else {
                Toast.makeText(getApplicationContext(), "Please login first!", Toast.LENGTH_LONG).show();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
