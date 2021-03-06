package com.explame.testreactnative;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.explame.testreactnative.constants.FileConstant;
import com.explame.testreactnative.hotupdate.HotUpdate;
import com.explame.testreactnative.http.ApiService;
import com.explame.testreactnative.http.RetrofitServiceManager;
import com.explame.testreactnative.preloadreact.ReactNativePreLoader;
import com.explame.testreactnative.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Activity mactivity;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mactivity = MainActivity.this;
//        requestExternalStoragePermission();
        doPermissionApplication(mactivity, permissions[CALL_PHONE], CALL_PHONE);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToJump(mactivity, HelloReactActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToJump(mactivity, TestViewActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.getReactPackage().mModule.nativeCallRn("我是海贼王路飞");
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ReactNativePreLoader.preLoad(MainActivity.this, "helloreactnative");
            ReactNativePreLoader.preLoad(MainActivity.this, "testview");
        }
    }

    public void intentToJump(Context context, Class<?> cls, int flag) {
        Intent intent = new Intent(context, cls);
        intent.setFlags(flag);
        context.startActivity(intent);
    }


    /**
     * 检查版本号
     */
    private void checkVersion() {
        // 默认有最新版本
        Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
        downLoadBundle();
    }

    /**
     * 下载最新Bundle
     */
    private void downLoadBundle() {
        // 1.下载前检查SD卡是否存在更新包文件夹
//        HotUpdate.checkPackage(getApplicationContext(), FileConstant.LOCAL_FOLDER);

        downloadConfig("http://192.168.123.178/wan.zip");
    }

    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1000;

    /**
     * 申请权限
     */
    public String[] permissions = {Manifest.permission.CALL_PHONE};
    public static final int CALL_PHONE = 0;

    public boolean doPermissionApplication(Activity mactivity, String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(mactivity, permission)
                != PackageManager.PERMISSION_GRANTED) {//　没有该权限
            ActivityCompat.requestPermissions(mactivity, new String[]{permission}, REQUEST_CODE);
            return false;
        }
        return true;
    }


    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("local external storage patch is invalid as not read external storage permission");
                }
                break;
            case CALL_PHONE:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 通过
                    LogUtils.i("----->权限被通过了");
                } else {
                    // Permission Denied   被拒绝
                    LogUtils.i("----->权限被拒绝了");
                }
                break;
        }
    }

    private void downloadConfig(String url) {
        final String fileNametemporary = url.substring(url.lastIndexOf("/") + 1);
        final String[] fileNameArray = fileNametemporary.split("\\?");
        final String fileName = fileNameArray[0];


        ApiService apiService = RetrofitServiceManager.getInstance().getApiService();
        Call<ResponseBody> call = apiService.downloadFileWithDynamicUrlSync(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    write2Data(response.body(), fileName);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.i("onFailure----->" + t);
            }
        });
    }

    private boolean write2Data(ResponseBody body, String fileName) {
        try {
            File futureStudioIconFile = new File(FileConstant.JS_PATCH_LOCAL_FOLDER + fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
                HotUpdate.handleZIP(getApplicationContext());
            }
        } catch (IOException e) {
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                System.exit(0);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}


