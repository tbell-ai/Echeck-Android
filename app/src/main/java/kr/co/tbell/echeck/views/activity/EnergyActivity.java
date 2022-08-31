package kr.co.tbell.echeck.views.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.util.DateUtil;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.dialog.AnalysisTargetDialog;
import kr.co.tbell.echeck.views.dialog.ElectDialog;

public class EnergyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private Menu navMenu;
    private Toolbar toolbar;
    private TextView pageTitle;
    private TextView nickname;
    private ImageView myUpdateButton;

    private Button analysis;
    private Button showElect;
    private Button goCamera;

    private TextView period;
    private TextView usage;
    private TextView charge;

    private EcheckDatabaseManager dbHandler;
    private User user;
    private Elect elect;

    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy);

        dbHandler = EcheckDatabaseManager.getInstance(getApplicationContext());
        dbHandler.openDataBase();

        // Navigation And ActionBar Find View By ID
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        navMenu = navigationView.getMenu();
        nickname = navHeader.findViewById(R.id.nav_nickname);
        toolbar = findViewById(R.id.toolbar);
        pageTitle = findViewById(R.id.toolbarTitle);
        myUpdateButton = findViewById(R.id.mypage_update);

        analysis = findViewById(R.id.go_analysis);
        showElect = findViewById(R.id.elect_pop_list);
        goCamera = findViewById(R.id.go_camera);
        period = findViewById(R.id.elect_period);
        usage = findViewById(R.id.target_elect_usage);
        charge = findViewById(R.id.target_charge);

        user = dbHandler.getUser();
        dbHandler.closeDatabase();

        // Navigation And ActionBar Settings
        nickname.setText(user.getNickname() + " 님");
        navMenu.findItem(R.id.nav_analysis).setVisible(false);
        pageTitle.setText("에너지 소비 패턴 분석");
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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        navigationView.setNavigationItemSelectedListener(this);

        // 1. 전력 측정내역 팝업 노출
        AnalysisTargetDialog dialog = new AnalysisTargetDialog(EnergyActivity.this, new AnalysisTargetDialog.AnalysisDialogListener() {
            @Override
            public void onClicked(Elect target) {
                elect = target;
                int[] splitDate = DateUtil
                        .splitStringDate(elect.getCreatedAt().substring(0, 10));
                String date = splitDate[0] + "년 " + splitDate[1] + "월";

                period.setText(date);
                usage.setText(elect.getElectAmount() + " kWh");
                charge.setText(elect.getElectCharge() + " 원");
            }
        });
        dialog.showDialog();

        showElect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 전력 측정내역 다시 보여줌
                AnalysisTargetDialog dialog = new AnalysisTargetDialog(EnergyActivity.this, new AnalysisTargetDialog.AnalysisDialogListener() {
                    @Override
                    public void onClicked(Elect target) {
                        elect = target;

                        // 화면 다시 갱신해줌
                        int[] splitDate = DateUtil.splitStringDate(elect.getCreatedAt().substring(0, 10));
                        String date = splitDate[0] + "년 " + splitDate[1] + "월";

                        period.setText(date);
                        usage.setText(elect.getElectAmount() + " kWh");
                        charge.setText(elect.getElectCharge() + " 원");
                    }
                });
                dialog.showDialog();
            }
        });


        goCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnergyActivity.this, MachineActivity.class);
                startActivity(intent);
            }
        });

        analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnergyActivity.this, EnergyResultActivity.class);

                if(elect == null) {

                    Toast toast = Toast.makeText(getApplicationContext(), "소비패턴 분석을 위한 기간을 선택해주세요!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                    // 전력 측정내역 다시 보여줌
                    AnalysisTargetDialog dialog = new AnalysisTargetDialog(EnergyActivity.this, new AnalysisTargetDialog.AnalysisDialogListener() {
                        @Override
                        public void onClicked(Elect target) {
                            elect = target;

                            // 화면 다시 갱신해줌
                            int[] splitDate = DateUtil.splitStringDate(elect.getCreatedAt().substring(0, 10));
                            String date = splitDate[0] + "년 " + splitDate[1] + "월";

                            period.setText(date);
                            usage.setText(elect.getElectAmount() + " kWh");
                            charge.setText(elect.getElectCharge() + " 원");
                        }
                    });
                    dialog.showDialog();
                } else {
                    intent.putExtra("target", elect);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            Intent intent = new Intent(EnergyActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "뒤로가기를 한번 더 누르시면 메인으로 이동합니다.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_cacul :

                ElectDialog dialog = new ElectDialog(EnergyActivity.this, new ElectDialog.ElectDialogListener() {
                    @Override
                    public void onUpdateClicked() {
                        Intent intent = new Intent(EnergyActivity.this, BeforeElectActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onNextClicked() {
                        Intent intent = new Intent(EnergyActivity.this, MachineActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.showDialog();
                break;

            case R.id.nav_uselist :
                intent = new Intent(EnergyActivity.this, UsageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_mypage :
                intent = new Intent(EnergyActivity.this, MypageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_analysis :
                intent = new Intent(EnergyActivity.this, EnergyActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_guide :
                intent = new Intent(EnergyActivity.this, SavingGuideActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_appinfo :
                intent = new Intent(EnergyActivity.this, CompanyInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_faq :
                intent = new Intent(EnergyActivity.this, FaqActivity.class);
                startActivity(intent);
                break;

        }

        return false;
    }
}