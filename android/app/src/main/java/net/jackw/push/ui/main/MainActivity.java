package net.jackw.push.ui.main;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.jackw.push.data.device.DeviceDetailsDefaultDataSource;
import net.jackw.push.data.device.DeviceDetailsRepository;
import net.jackw.push.data.device.DeviceDetailsStoreDataSource;
import net.jackw.push.data.registrations.NotificationRegistrationFirebaseDataSource;
import net.jackw.push.data.registrations.NotificationRegistrationRepository;
import net.jackw.push.databinding.ActivityMainBinding;
import net.jackw.push.notifications.NotificationRegistrationManager;
import net.jackw.push.notifications.Notifications;
import net.jackw.push.ui.login.LoginActivity;

import net.jackw.push.R;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.deviceName.setText(DeviceDetailsRepository.getInstance().getDeviceDetails(this).getDeviceName());
        binding.notificationPermissionWarningButton.setOnClickListener(_v -> askNotificationPermission());

        askNotificationPermission();
        NotificationRegistrationManager.getInstance().updateStoredToken(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Directly ask for the permission
                ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        binding.notificationPermissionWarning.setVisibility(View.GONE);
                    } else {
                        binding.notificationPermissionWarning.setVisibility(View.VISIBLE);
                    }
                });
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}