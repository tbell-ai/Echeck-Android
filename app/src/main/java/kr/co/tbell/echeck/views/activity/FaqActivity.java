package kr.co.tbell.echeck.views.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.FaqItem;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.adapter.RecyclerFaqAdapter;
import kr.co.tbell.echeck.views.dialog.ElectDialog;

public class FaqActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private Menu navMenu;
    private Toolbar toolbar;
    private TextView pageTitle;
    private TextView nickname;
    private ImageView myUpdateButton;

    private RecyclerView recyclerView;
    private List<FaqItem> faqList;

    private EcheckDatabaseManager dbHandler;
    private User user;

    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

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
        recyclerView = findViewById(R.id.faqRecyclerView);

        user = dbHandler.getUser();
        dbHandler.closeDatabase();

        // Navigation And ActionBar Settings
        nickname.setText(user.getNickname() + " 님");
        navMenu.findItem(R.id.nav_faq).setVisible(false);
        pageTitle.setText("FAQ");
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

        initData();
        setRecyclerView();
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            Intent intent = new Intent(FaqActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "뒤로가기를 한번 더 누르시면 메인화면으로 이동합니다.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void setRecyclerView() {
        RecyclerFaqAdapter faqAdapter = new RecyclerFaqAdapter(faqList);
        recyclerView.setAdapter(faqAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {

        Gson gson = new Gson();
        faqList = new ArrayList<FaqItem>();

        try {

            InputStream is = getAssets().open("echeck_faq.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("faqList");

            for(int i=0; i<jsonArray.length(); i++) {
                FaqItem faq = gson.fromJson(jsonArray.get(i).toString(), FaqItem.class);
                faqList.add(faq);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_cacul :

                ElectDialog dialog = new ElectDialog(FaqActivity.this, new ElectDialog.ElectDialogListener() {
                    @Override
                    public void onUpdateClicked() {
                        Intent intent = new Intent(FaqActivity.this, BeforeElectActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onNextClicked() {
                        Intent intent = new Intent(FaqActivity.this, MachineActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.showDialog();
                break;

            case R.id.nav_uselist :
                intent = new Intent(FaqActivity.this, UsageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_mypage :
                intent = new Intent(FaqActivity.this, MypageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_analysis :
                intent = new Intent(FaqActivity.this, EnergyActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_guide :
                intent = new Intent(FaqActivity.this, SavingGuideActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_appinfo :
                intent = new Intent(FaqActivity.this, CompanyInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_faq :
                intent = new Intent(FaqActivity.this, FaqActivity.class);
                startActivity(intent);
                break;

        }

        return false;
    }
}