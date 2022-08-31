package kr.co.tbell.echeck.views.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.dto.InfoData;
import kr.co.tbell.echeck.views.fragment.info.Info1Fragment;

public class InfoActivity extends AppCompatActivity {

    private Toolbar actionbar;
    private TextView pageTitle;
    private ImageView myUpdateButton;

    private long backBtnTime = 0;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        actionbar = findViewById(R.id.toolbar);
        pageTitle = findViewById(R.id.toolbarTitle);
        myUpdateButton = findViewById(R.id.mypage_update);

        // Toolbar 설정
        actionbar.setNavigationIcon(null);
        pageTitle.setText("거주지 정보 입력");
        myUpdateButton.setEnabled(false);
        myUpdateButton.setVisibility(View.GONE);

        // InfoActivity 화면에서 초기에 보여줄 InfoFragment 화면 설정
        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.info_frame, Info1Fragment.newInstance()).commit();
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

    /**
     * 프레그먼트 페이지 이동 함수, InfoActivity와 연관된 Fragment들이 사용함
     *
     * @param fragment          이동할 Fragment 인스턴스
     * @param type              화면 전환 애니메이션을 결정할 Type
     * @param infoData          Fragment 이동 간에 전달할 데이터
     */
    public void onInfoPageChange(Fragment fragment, String type, InfoData infoData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoData", infoData);
        fragment.setArguments(bundle);

        if(type.equals("prev")) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.info_frame, fragment).commit();
        } else if(type.equals("next")) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.info_frame, fragment).commit();
        }
    }
}