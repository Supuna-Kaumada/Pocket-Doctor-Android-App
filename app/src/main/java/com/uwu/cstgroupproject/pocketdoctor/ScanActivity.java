package com.uwu.cstgroupproject.pocketdoctor;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity
{
    private Toolbar mToolbar;

    private SurfaceView cameraView;
    private TextView Scan_result;
    private CameraSource cameraSource;

    final int RequestCameraPermissionID = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case RequestCameraPermissionID:
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }

                    try
                    {
                        cameraSource.start(cameraView.getHolder());
                    }
                    catch (IOException e)
                    {
                        String message = e.getMessage().toString();
                        Toast.makeText(this, "Error "+ message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mToolbar = (Toolbar)findViewById(R.id.scan_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Scan Report");


        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        Scan_result = (TextView) findViewById(R.id.text_scan_result);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational())
        {
            Toast.makeText(this, "Detector dependensies are not yet available", Toast.LENGTH_SHORT).show();
        }
        else
        {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @SuppressLint("MissingPermission")
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    try
                    {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {

                            ActivityCompat.requestPermissions(ScanActivity.this,
                                    new String[]{android.Manifest.permission.CAMERA},RequestCameraPermissionID);

                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e)
                    {
                        String message = e.getMessage().toString();
                        Toast.makeText(ScanActivity.this, "Error "+ message, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder)
                {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release()
                {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections)
                {
                    final SparseArray<TextBlock> scan_items = detections.getDetectedItems();

                    if(scan_items.size() != 0)
                    {
                        Scan_result.post(new Runnable() {
                            @Override
                            public void run()
                            {
                                StringBuilder stringBuilder = new StringBuilder();

                                for(int i = 0; i<scan_items.size(); ++i)
                                {
                                    TextBlock item = scan_items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }

                                Scan_result.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }

    }
}
