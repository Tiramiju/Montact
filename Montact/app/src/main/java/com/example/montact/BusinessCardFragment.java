package com.example.montact;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class BusinessCardFragment extends Fragment {
    private ExtendedFloatingActionButton floatingActionButton;

    private RecyclerView bizCardRv;
    private BusinessCardAdapter businessCardAdapter;
    private List<BizCard> bizCardList;

    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission() , isGranted -> {
            if(isGranted) {
                //
            } else {
                new AlertDialog.Builder(getActivity())
                        .setTitle("미디어 접근 권한")
                        .setPositiveButton("확인", (dialogInterface, i) -> {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .create()
                        .show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_card_list,container,false);

        floatingActionButton = view.findViewById(R.id.fab_camera);
        floatingActionButton.setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(getContext(), BusinessCardAddActivity.class));
            }
            permissionLauncher.launch(Manifest.permission.CAMERA);

        });

        bizCardRv = view.findViewById(R.id.business_card_rv);
        businessCardAdapter = new BusinessCardAdapter(bizCardList);
        bizCardRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        bizCardRv.setAdapter(businessCardAdapter);

        BizCardDB.getInstance(this.getContext()).bizCardDao().getAll().observe(getViewLifecycleOwner(), bizCards -> businessCardAdapter.setList(bizCards));

        return view;
    }
}
