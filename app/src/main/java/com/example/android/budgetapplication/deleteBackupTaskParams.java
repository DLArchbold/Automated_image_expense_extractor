package com.example.android.budgetapplication;

import android.content.Context;

import com.google.api.services.drive.Drive;

public class deleteBackupTaskParams {
    Drive driveService;
    String ms;
    Context mContext;

    deleteBackupTaskParams(Drive driveService, String ms, Context context) {
        this.driveService = driveService;
        this.ms = ms;
        this.mContext = context;
    }


}
