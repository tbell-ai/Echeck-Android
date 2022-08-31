package kr.co.tbell.echeck.views.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.fragment.guide.Guide1Fragment;

public class SavingGuideActivity extends AppCompatActivity {

    private long backBtnTime = 0;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_guide);

        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.guide_frame, Guide1Fragment.newInstance()).commit();
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            Intent intent = new Intent(SavingGuideActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "뒤로가기를 한번 더 누르시면 메인화면으로 이동합니다.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void onInfoPageChange(Fragment fragment, String type) {
        if(type.equals("prev")) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.guide_frame, fragment).commit();
        } else if(type.equals("next")) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.guide_frame, fragment).commit();
        }
    }
}