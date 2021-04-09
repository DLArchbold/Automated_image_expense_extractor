package com.example.android.budgetapplication;

import com.google.api.services.drive.Drive;

public class deleteBackupTaskParams {
    Drive driveService;
    String ms;

    deleteBackupTaskParams(Drive driveService, String ms) {
        this.driveService = driveService;
        this.ms = ms;
    }


}
