package kr.co.tbell.echeck.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.HomeProduct;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.dialog.ElectDialog;
import kr.co.tbell.echeck.views.fragment.product.HomeProductListFragment;

public class HomeProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private Menu navMenu;
    private Toolbar toolbar;
    private TextView pageTitle;
    private TextView nickname;
    private ImageView myUpdateButton;

    private EcheckDatabaseManager dbHandler;
    private User user;
    private Elect elect;
    private FragmentManager fragmentManager;

    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_product);

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

        user = dbHandler.getUser();
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


        // HomeProductActivity 화면에서 초기에 보여줄 HomeProductListFragment 화면 설정
        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.product_frame, HomeProductListFragment.newInstance()).commit();

    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            Intent intent = new Intent(HomeProductActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "뒤로가기를 한번 더 누르시면 메인화면으로 이동합니다.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    /**
     * 프레그먼트 페이지 이동 함수
     *
     * @param fragment          이동할 Fragment 인스턴스
     * @param type              화면 전환 애니메이션을 결정할 Type
     * @param data              Fragment 이동 간에 전달할 데이터
     */
    public void onProductPageChange(Fragment fragment, String type, HomeProduct data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", data);
        fragment.setArguments(bundle);

        if(type.equals("prev")) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.product_frame, fragment).commit();
        } else if(type.equals("next")) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.product_frame, fragment).commit();
        }
    }

    public Elect getElect() {
        return this.elect;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_cacul :

                ElectDialog dialog = new ElectDialog(HomeProductActivity.this, new ElectDialog.ElectDialogListener() {
                    @Override
                    public void onUpdateClicked() {
                        Intent intent = new Intent(HomeProductActivity.this, BeforeElectActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onNextClicked() {
                        Intent intent = new Intent(HomeProductActivity.this, MachineActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.showDialog();
                break;

            case R.id.nav_uselist :
                intent = new Intent(HomeProductActivity.this, UsageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_mypage :
                intent = new Intent(HomeProductActivity.this, MypageActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_analysis :
                intent = new Intent(HomeProductActivity.this, EnergyActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_guide :
                intent = new Intent(HomeProductActivity.this, SavingGuideActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_appinfo :
                intent = new Intent(HomeProductActivity.this, CompanyInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_faq :
                intent = new Intent(HomeProductActivity.this, FaqActivity.class);
                startActivity(intent);
                break;

        }

        return false;
    }
}