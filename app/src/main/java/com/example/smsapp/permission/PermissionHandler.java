package com.example.smsapp.permission;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionHandler {
    private Activity activity;
    private int requestCode;
    private String[] permissions;

    public PermissionHandler(Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.permissions = new String[] {
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_CONTACTS
        };
    }

    public void checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[0]), requestCode);
        } else {
            allPermissionsGranted(); // Wywołaj metodę, jeśli wszystkie uprawnienia są już przyznane.
        }
    }

    public void handlePermissionsResult(int requestCode, int[] grantResults, Runnable onPermissionsGranted) {
        if (this.requestCode == requestCode) {
            if (grantResults.length > 0) {
                boolean allPermissionsGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    onPermissionsGranted.run();
                } else {
                    permissionDenied();
                }
            }
        }
    }

    private void allPermissionsGranted() {
        // Można tutaj wywołać jakąś akcję lub wysłać powiadomienie do użytkownika.
        Toast.makeText(activity, "All permissions granted.", Toast.LENGTH_SHORT).show();
    }

    private void permissionDenied() {
        Toast.makeText(activity, "Permissions denied. Some features may not work.", Toast.LENGTH_SHORT).show();
    }
}