package kr.co.tbell.echeck.views.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.HomeProduct;
import kr.co.tbell.echeck.util.DateUtil;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.dialog.AnalysisDetailDialog;
import kr.co.tbell.echeck.views.dialog.ElectDialog;
import kr.co.tbell.echeck.views.dialog.ProductDialog;

public class EnergyResultActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private Menu navMenu;
    private Toolbar toolbar;
    private TextView pageTitle;
    private TextView nickname;
    private ImageView myUpdateButton;

    private TextView goDetail;
    private Button goProduct;
    private Button goMain;

    private EcheckDatabaseManager dbHandler;
    private User user;
    private Elect elect;
    private PieChart pieChart;
    int[] colorArray = new int[]{ Color.rgb(199, 80, 141), Color.rgb(247, 183, 163), Color.rgb(234, 95, 137),
                                Color.rgb(155, 49, 146), Color.rgb(87, 22, 126), Color.rgb(43, 11, 63)};

    private List<HomeProduct> products;
    private HashMap<String, Integer> chart = new HashMap<>();

    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_result);

        dbHandler = EcheckDatabaseManager.getInstance(getApplicationContext());
        dbHandler.openDataBase();

        Intent intent = getIntent();
        elect = (Elect)intent.getSerializableExtra("target");

        // Navigation And ActionBar Find View By ID
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        navMenu = navigationView.getMenu();
        nickname = navHeader.findViewById(R.id.nav_nickname);
        toolbar = findViewById(R.id.toolbar);
        pageTitle = findViewById(R.id.toolbarTitle);
        myUpdateButton = findViewById(R.id.mypage_update);

        goDetail = findViewById(R.id.go_detail);
        goProduct = findViewById(R.id.analy_home_elect);
        goMain = findViewById(R.id.analy_go_main);
        pieChart = findViewById(R.id.pie_chart);

        user = dbHandler.getUser();
        products = dbHandler.getProduct();
        dbHandler.closeDatabase();

        // Navigation And ActionBar Settings
        nickname.setText(user.getNickname() + " 님");
        navMenu.findItem(R.id.nav_analysis).setVisible(false);
        pageTitle.setText("에너지 소비패턴 분석");
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


        int[] splitDate = DateUtil.splitStringDate(elect.getCreatedAt().substring(0, 10));
        int monthCount = DateUtil.getLastDay(splitDate[0], splitDate[1], splitDate[2]);

        if(products.size() > 0) {

            int totalUsage = 0;

            for(int i=0; i<products.size(); i++) {
                String name = products.get(i).getProduct();
                int dayHour = Integer.parseInt(products.get(i).getDayHour());
                int usage = calcHomeProductUsage(name, dayHour, monthCount);
                totalUsage = totalUsage + usage;
                chart.put(name, usage);
            }

            for(int i=0; i<products.size(); i++) {
                String name = products.get(i).getProduct();
                int dayHour = Integer.parseInt(products.get(i).getDayHour());
                int usage = calcHomeProductUsage(name, dayHour, monthCount);
                double percentage = ((int)((usage * (100.0 / totalUsage)) * 100) / 100.0);
                products.get(i).setDayHour("일 "+ products.get(i).getDayHour() + "시간");
                products.get(i).setPersentage("전체에서 " + percentage + "%");
            }


            PieDataSet pieDataSet = new PieDataSet(splitPie(), "");
            pieDataSet.setColors(colorArray);
            PieData pieData = new PieData(pieDataSet);
            pieChart.setDrawEntryLabels(true);
            pieChart.setUsePercentValues(true);
            pieData.setValueTextSize(10);
            pieData.setValueTextColor(Color.BLACK);
//            pieChart.setCenterText("");
            Description des = new Description();
            des.setText("");
            pieChart.setDescription(des);
//            pieChart.setCenterTextSize(6);
//            pieChart.setCenterTextColor(Color.BLACK);
//            pieChart.setHoleRadius(30);
            pieChart.setRotationEnabled(true);
            pieChart.setDragDecelerationFrictionCoef(0.9f);
            pieChart.setRotationAngle(0);
            pieChart.setData(pieData);
            pieChart.invalidate();

        } else {
            ProductDialog dialog = new ProductDialog(this, elect);
            dialog.showDialog();
        }

        goDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalysisDetailDialog dialog = new AnalysisDetailDialog(EnergyResultActivity.this, products);
                dialog.showDialog();
            }
        });

        goProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnergyResultActivity.this, HomeProductActivity.class);
                intent.putExtra("target", elect);
                startActivity(intent);
            }
        });

        goMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnergyResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private ArrayList<PieEntry> splitPie() {

        ArrayList<PieEntry> dataValue = new ArrayList<>();

        for(String key : chart.keySet()) {
            dataValue.add(new PieEntry(chart.get(key), key));
        }

        return dataValue;
    }

    private int calcHomeProductUsage(String productName, int dayHour, int monthDay) {

        int result = 0;

        SharedPreferences sp = getSharedPreferences("shared_data", MODE_PRIVATE);
        String productJson = sp.getString("products","");

        if (productJson != null) {
            try {
                JSONObject products = new JSONObject(productJson);
                String productUsage = products.getString(productName);

                if(productUsage == null) {
                    Log.e("sp_error", "존재하지 않는 가전제품 입니다.");
                } else {
                    result = (Integer.parseInt(productUsage) * dayHour * monthDay) / 1000;
                }

            } catch (JSONException e) {
                Log.e("json_error", e.toString());
            }
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            Intent intent = new Intent(EnergyResultActivity.this, MainActivity.class);
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

                ElectDialog dialog = new ElectDialog(EnergyResultActivity.this, new ElectDialog.ElectDialogListener() {
                    @Override
                    public void onUpdateClicked() {
                        Intent intent = new Intent(EnergyResultActivity.this, BeforeElectActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onNextClicked() {
                        Intent intent = new Intent(EnergyResultActivity.this, MachineActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.showDialog();
                break;

            case R.id.nav_uselist :
                intent = new Intent(EnergyResultActivity.this, UsageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_mypage :
                intent = new Intent(EnergyResultActivity.this, MypageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_analysis :
                intent = new Intent(EnergyResultActivity.this, EnergyActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_guide :
                intent = new Intent(EnergyResultActivity.this, SavingGuideActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_appinfo :
                intent = new Intent(EnergyResultActivity.this, CompanyInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_faq :
                intent = new Intent(EnergyResultActivity.this, FaqActivity.class);
                startActivity(intent);
                break;

        }

        return false;
    }
}