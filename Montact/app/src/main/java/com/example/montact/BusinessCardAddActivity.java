package com.example.montact;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BusinessCardAddActivity extends AppCompatActivity {
    private TessBaseAPI m_Tess;
    private String mDataPath = "";
    private String mCurrentPhotoPath;
    private Bitmap image;
    private MessageHandler messageHandler;
    private final String[] mLanguageList = {"eng","kor"};
    private String imageFileName;

    private ImageView scanImg;
    private EditText scannedTv;
    private Button doneBtn;
    private Button cancelBtn;

    private ScrollView scrollView;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bizcard);

        scannedTv = findViewById(R.id.scanned_text);
        scanImg = findViewById(R.id.scanned_image);
        doneBtn = findViewById(R.id.scan_done_btn);
        cancelBtn = findViewById(R.id.scan_cancel_btn);

        scrollView = findViewById(R.id.scanned_text_layout);
        lottieAnimationView = findViewById(R.id.processing_ani);

        cancelBtn.setOnClickListener(cancelBtnListener);
        doneBtn.setOnClickListener(doneBtnListener);

        messageHandler = new MessageHandler();
        dispatchTakePictureIntent();
        Tesseract();
    }

    View.OnClickListener cancelBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    View.OnClickListener doneBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new Thread() {
                @Override
                public void run() {
                    saveImg();

                    BizCard bizCard = new BizCard();
                    bizCard.setImgName(imageFileName);
                    bizCard.setText(scannedTv.getText().toString());

                    BizCardDB.getInstance(BusinessCardAddActivity.this).bizCardDao().insertAll(bizCard);

                    finish();
                }
            }.start();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ConstantDefine.PERMISSION_CODE:
                break;
            case ConstantDefine.ACT_TAKE_PIC:
                if ((resultCode == RESULT_OK)) {

                    try {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap rotatedBitmap;
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                FileProvider.getUriForFile(BusinessCardAddActivity.this,
                                        getApplicationContext().getPackageName() + ".fileprovider", file));

                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }
                            OCRThread ocrThread = new OCRThread(rotatedBitmap);
                            ocrThread.setDaemon(true);
                            ocrThread.start();
                            scanImg.setImageBitmap(rotatedBitmap);
                            scrollView.setVisibility(View.INVISIBLE);
                            scannedTv.setText(getResources().getString(R.string.LoadingMessage));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public class MessageHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ConstantDefine.RESULT_OCR) {
                lottieAnimationView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                TextView OCRTextView = findViewById(R.id.scanned_text);
                OCRTextView.setText(String.valueOf(msg.obj));
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void Tesseract() {
        mDataPath = getFilesDir() + "/tesseract/";

        StringBuilder lang = new StringBuilder();
        for (String Language : mLanguageList) {
            checkFile(new File(mDataPath + "tessdata/"), Language);
            lang.append(Language).append("+");
        }
        m_Tess = new TessBaseAPI();
        m_Tess.init(mDataPath, lang.toString());
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName()+".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ConstantDefine.ACT_TAKE_PIC);
            }
        }
    }

    private void copyFiles(String Language) {
        try {
            String filepath = mDataPath + "/tessdata/" + Language + ".traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/"+Language+".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir, String Language) {
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(Language);
        }
        if (dir.exists()) {
            String datafilepath = mDataPath + "tessdata/" + Language + ".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(Language);
            }
        }
    }

    public class OCRThread extends Thread
    {
        private Bitmap rotatedImage;
        OCRThread(Bitmap rotatedImage)
        {
            this.rotatedImage = rotatedImage;;
        }
        @Override
        public void run() {
            super.run();
            String OCRresult = null;
            m_Tess.setImage(rotatedImage);
            OCRresult = m_Tess.getUTF8Text();

            Message message = Message.obtain();
            message.what = ConstantDefine.RESULT_OCR;
            message.obj = OCRresult;
            messageHandler.sendMessage(message);
        }
    }

    private void saveImg() {
        try {
            File storageDir = new File(getFilesDir() + "/picture");
            if (!storageDir.exists()) storageDir.mkdir();

            String filename = imageFileName + ".jpg";

            File file = new File(storageDir, filename);
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(file);
                BitmapDrawable drawable = (BitmapDrawable) scanImg.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert output != null;
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private String postProcessing(String str) {
//        str.replaceAll()
//    }
}
