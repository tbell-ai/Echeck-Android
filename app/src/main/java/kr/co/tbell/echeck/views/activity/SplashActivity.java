package kr.co.tbell.echeck.views.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.Manager.VersionCheckManager;

import static java.lang.Thread.sleep;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private long backBtnTime = 0;
    private int permissionCheck = 0;
    private boolean userInfoCheck = false;

    private EcheckDatabaseManager dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkPermission();

        dbHandler = EcheckDatabaseManager.getInstance(getApplicationContext());
        dbHandler.openDataBase();

        /**
         * 유저의 정보 등록 유무 체크
         * 처음 사용자이거나, 정보 등록 과정에서 종료하였거나, 예기치 못하게 로컬 DB에 정보가 삽입 안된 경우를 점검
         * DB에 값이 없을 경우 false 취하고, Intro부터 다시 시작
         */
        if(dbHandler.checkJoin("agree") && dbHandler.checkJoin("user") && dbHandler.checkJoin("house")) {
            userInfoCheck = true;
        } else {
            dbHandler.removeData();
            userInfoCheck = false;
        }
        dbHandler.closeDatabase();


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sleep(3500);
                    Intent intent;

                    SharedPreferences pref = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
                    boolean checkFirst = pref.getBoolean("checkFirst", false);

                    if(!checkFirst) {

                        /**
                         * 최초 실행시 제품 목록을 SharedPreferences에 저장해야함!
                         */
                        HashMap<String, String> productMap = new HashMap<>();
                        productMap.put("TV", "150");
                        productMap.put("컴퓨터 본체", "450");
                        productMap.put("컴퓨터 모니터", "120");
                        productMap.put("세탁기", "500");
                        productMap.put("청소기", "850");
                        productMap.put("정수기", "150");
                        productMap.put("냉장고", "100");
                        productMap.put("전기밥솥(보온)", "100");
                        productMap.put("전기밥솥(취사)", "1000");
                        productMap.put("김치냉장고", "60");
                        productMap.put("공기청정기", "65");
                        productMap.put("전자레인지", "1050");
                        productMap.put("건조기", "350");
                        productMap.put("헤어드라이기", "1000");
                        productMap.put("전기다리미", "1200");
                        productMap.put("선풍기", "60");
                        productMap.put("에어컨", "1800");
                        productMap.put("전기장판", "250");
                        productMap.put("안마의자", "70");
                        productMap.put("스타일러", "500");
                        JSONObject json =  new JSONObject(productMap);
                        String jsonString = json.toString();

                        SharedPreferences.Editor editor = getSharedPreferences("shared_data", MODE_PRIVATE).edit();
                        editor.putString("products", jsonString);
                        editor.commit();
                    }

                    // 1. 정보 등록 유무 체크
                    if (permissionCheck > 0) {
                        intent = new Intent(SplashActivity.this, PermissionActivity.class);
                        startActivity(intent);
                    } else if (permissionCheck == 0 && checkFirst) {
                        intent = new Intent(SplashActivity.this, IntroActivity.class);
                        startActivity(intent);
                    } else if(userInfoCheck) {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(SplashActivity.this, IntroActivity.class);
                        startActivity(intent);
                    }

                    // 2. 앱 버전 체크 - 파이어베이스 RemoteConfig 활용!

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            this.finishAffinity();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "뒤로가기를 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck = 1;
        } else {
            permissionCheck = 0;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck = 1;
        } else {
            permissionCheck = 0;
        }
    }
}