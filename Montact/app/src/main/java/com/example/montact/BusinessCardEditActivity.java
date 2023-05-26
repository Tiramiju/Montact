package com.example.montact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class BusinessCardEditActivity extends AppCompatActivity {
    private BizCard bizCard;

    private ImageView scanImg;
    private EditText scannedTv;
    private Button doneBtn;
    private Button cancelBtn;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bizcard);

        Intent intent = getIntent();
        int bizId = intent.getIntExtra("bizCardId", 0);

        scannedTv = findViewById(R.id.scanned_text);
        scanImg = findViewById(R.id.scanned_image);
        doneBtn = findViewById(R.id.scan_done_btn);
        cancelBtn = findViewById(R.id.scan_cancel_btn);

        lottieAnimationView = findViewById(R.id.processing_ani);
        lottieAnimationView.setVisibility(View.GONE);

        doneBtn.setText(R.string.edit);
        cancelBtn.setText(R.string.delete);

        new Thread() {
            @Override
            public void run() {
                bizCard = BizCardDB.getInstance(BusinessCardEditActivity.this).bizCardDao().selectById(bizId);

                String imgPath = getFilesDir() + "/picture/" + bizCard.getImgName() + ".jpg";
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannedTv.setText(bizCard.getText());
                        scanImg.setImageBitmap(bitmap);
                    }
                });

            }
        }.start();

        cancelBtn.setOnClickListener(cancelBtnListener);
        doneBtn.setOnClickListener(doneBtnListener);
    }

    View.OnClickListener cancelBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new Thread() {
                @Override
                public void run() {
                    BizCardDB.getInstance(BusinessCardEditActivity.this).bizCardDao().deleteContact(bizCard);
                }
            }.start();

            finish();
        }
    };

    View.OnClickListener doneBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            bizCard.setText(scannedTv.getText().toString());

            new Thread() {
                @Override
                public void run() {
                    BizCardDB.getInstance(BusinessCardEditActivity.this).bizCardDao().updateContact(bizCard);
                }
            }.start();

            finish();
        }
    };
}
