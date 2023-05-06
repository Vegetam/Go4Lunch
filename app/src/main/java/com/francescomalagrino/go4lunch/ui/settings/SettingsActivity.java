package com.francescomalagrino.go4lunch.ui.settings;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.databinding.ActivitySettingsBinding;
import com.francescomalagrino.go4lunch.ui.ViewModelFactory;
import com.francescomalagrino.go4lunch.ui.main.MainActivity;

public class SettingsActivity extends AppCompatActivity  {

    private ActivitySettingsBinding binding ;
    private SettingsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureViewModel();
        setupListeners();
        updateView();
    }

    private void updateView() {
        binding.switchNotif.setChecked(mViewModel.getMyUser().isNotification());
    }

    private void configureViewModel() {

        this.mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(SettingsViewModel.class);
        mViewModel.init();
        Log.e(TAG, "configureViewModel: " + mViewModel.getMyUser() );
    }

    private void setupListeners() {
        binding.btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                        mViewModel.deleteUser(this)
                                .addOnCompleteListener(aVoid -> {
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                })
                )
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show());

        binding.switchNotif.setOnClickListener(v ->{
            mViewModel.notificationChecked(!mViewModel.getMyUser().isNotification());
        });

        binding.backArrow.setOnClickListener(v-> onBackPressed());
    }
}