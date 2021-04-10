package com.example.android.budgetapplication;

import android.content.Context;

import com.google.api.services.drive.Drive;

public class restoreBackupTaskParams {
    Drive driveService;
    String ms;
    Context mContext;

    restoreBackupTaskParams(Drive driveService, String ms, Context context) {
        this.driveService = driveService;
        this.ms = ms;
        this.mContext = context;
    }
}
