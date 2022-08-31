package kr.co.tbell.echeck.views.activity;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.Toast;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.adapter.FragmentIntroAdapter;

public class IntroActivity extends FragmentActivity {

    private long backBtnTime = 0;
    private ViewPager2 introViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introViewPager = findViewById(R.id.intro_viewpager2);
        introViewPager.setAdapter(new FragmentIntroAdapter(this));
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
}