package kr.co.tbell.echeck.views.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.views.Manager.VersionCheckManager;
import kr.co.tbell.echeck.views.dialog.ElectDialog;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private Toolbar toolbar;
    private TextView textView;
    private TextView nickname;
    private TextView userInfo;
    private TextView userUpdate;
    private ImageView myUpdateButton;
    private LinearLayout useArea;
    private LinearLayout cacul;
    private LinearLayout guideArea;
    private LinearLayout mypage;

    private long backBtnTime = 0;
    private EcheckDatabaseManager dbHandler;

    private String deviceVersion;
    private String storeVersion;
    private BackgroundThread mBackgroundThread;
    private final DeviceVersionCheckHandler deviceVersionCheckHandler = new DeviceVersionCheckHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBackgroundThread = new MainActivity.BackgroundThread();
        mBackgroundThread.start();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.toolbarTitle);
        useArea = findViewById(R.id.useArea);
        cacul = findViewById(R.id.cacul);
        guideArea = findViewById(R.id.guideArea);
        mypage = findViewById(R.id.mypage);
        nickname = navHeader.findViewById(R.id.nav_nickname);
        myUpdateButton = findViewById(R.id.mypage_update);
        userInfo = findViewById(R.id.user_info);
        userUpdate = findViewById(R.id.update_info);

        // ????????????
        dbHandler = EcheckDatabaseManager.getInstance(getApplicationContext());
        dbHandler.openDataBase();
        User user = dbHandler.getUser();
        List<Elect> elects = dbHandler.getElect();
        dbHandler.closeDatabase();

        int listSize = elects.size();
        nickname.setText(user.getNickname() + " ???");
        userInfo.setText(user.getNickname() + " ???");
        userUpdate.setText("???????????? " + user.getCreatedAt().substring(0, 10));

        if(listSize > 0) {
            Elect elect = elects.get(listSize - 1);

            // - ???????????? : ????????? ?????? ??????, ????????? ??????
            // ??????1 - ????????? ?????? ??????(2021-01-10) / ????????? ????????? ??????(2020-12-03) / ????????? ????????? ?????? ?????? ?????? ?????????(2020-12-04 ~ 2021-01-03)
            // ??????1??? ?????? ?????? ????????? ?????????, ?????? ???????????? ???????????? "?????? ??? ?????? ??????????????? ??????????????????" ?????? ??????

            // ??????2 - ????????? ?????? ??????(2021-01-10) / ????????? ????????? ?????? ?????? ?????? ?????????(2020-12-25 ~ 2021-01-24)
            // ??????2??? ?????? ?????? ????????? ????????? ?????? ???????????? ??????????????? "????????? ?????? ??????????????? 0,000????????????." ?????? ??????

            // ??????3 - ?????? ?????? ?????????
            // ??????3??? ?????? ?????? ????????? ???????????? ??????, "?????? ??????????????? ??????????????????." ?????? ??????

        }

        // Toolbar(Actionbar)
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        textView.setText("");
        myUpdateButton.setEnabled(false);
        myUpdateButton.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        // Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // custom navigation drawer menu icon
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.main_menu_ic);

        navigationView.setNavigationItemSelectedListener(this);
        cacul.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ElectDialog dialog = new ElectDialog(MainActivity.this, new ElectDialog.ElectDialogListener() {
                    @Override
                    public void onUpdateClicked() {
                        Intent intent = new Intent(MainActivity.this, BeforeElectActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onNextClicked() {
                        Intent intent = new Intent(MainActivity.this, MachineActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.showDialog();
            }
        });

        useArea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsageActivity.class);
                startActivity(intent);
            }
        });

        mypage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EnergyActivity.class);
                startActivity(intent);
            }
        });

        guideArea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SavingGuideActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(0 <= gapTime && 2000 >= gapTime) {
                this.finishAffinity();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                backBtnTime = curTime;
                Toast.makeText(this, "??????????????? ?????? ??? ???????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_cacul :

                ElectDialog dialog = new ElectDialog(MainActivity.this, new ElectDialog.ElectDialogListener() {
                    @Override
                    public void onUpdateClicked() {
                        Intent intent = new Intent(MainActivity.this, BeforeElectActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onNextClicked() {
                        Intent intent = new Intent(MainActivity.this, MachineActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.showDialog();
                break;

            case R.id.nav_uselist :
                intent = new Intent(MainActivity.this, UsageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_mypage :
                intent = new Intent(MainActivity.this, MypageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_analysis :
                intent = new Intent(MainActivity.this, EnergyActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_guide :
                intent = new Intent(MainActivity.this, SavingGuideActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_appinfo :
                intent = new Intent(MainActivity.this, CompanyInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_faq :
                intent = new Intent(MainActivity.this, FaqActivity.class);
                startActivity(intent);
                break;

        }

        return false;
    }

    // ??? ?????? ??????(Elect DB ??????)
    public void guideTextSetting() {

    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            // ????????? ?????? ?????? ??? ????????? ?????? ?????????
            storeVersion = VersionCheckManager.getMarketVersion(getPackageName());

            // ???????????? ?????? ?????????
            try {
                deviceVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            // ???????????? ????????? ??????
            deviceVersionCheckHandler.sendMessage(deviceVersionCheckHandler.obtainMessage());
        }
    }

    // ????????? ?????? ?????????
    private static class DeviceVersionCheckHandler extends Handler {
        private final WeakReference<MainActivity> mainActivityWeakReference;
        public DeviceVersionCheckHandler(MainActivity mainActivity) {
            mainActivityWeakReference = new WeakReference<MainActivity>(mainActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mainActivityWeakReference.get();
            if (activity != null) {
                // ?????????????????? ????????? ??????
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        //??????????????? ????????? ??? ??????
        if (storeVersion.compareTo(deviceVersion) > 0) {
            // ???????????? ??????
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("????????????");alertDialogBuilder
                    .setMessage("????????? ????????? ????????????.\n????????? ????????? ?????? ??????????????? ??????????????????.")
                    .setPositiveButton("???????????? ????????????", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // ??????????????? ???????????? ??????
                            Intent intent = new Intent(Intent.ACTION_VIEW); intent.setData(Uri.parse("market://details?id=" + getPackageName())); startActivity(intent);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

        } else {
            // ???????????? ?????????

        }
    }
}