package kr.co.tbell.echeck.views.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class AgreeActivity extends AppCompatActivity {

    private CheckBox allCheck;
    private CheckBox mandatoryCheck;
    private ScrollView agreeDetail;
    private LinearLayout agreeList;
    private ImageView arrow;
    private Button agreeBtn;

    private EcheckDatabaseManager dbHandler;
    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree);

        allCheck = findViewById(R.id.agree_all_check);
        mandatoryCheck = findViewById(R.id.mandatory_check);
        agreeList = findViewById(R.id.agree_list);
        arrow = findViewById(R.id.agree_view_btn);
        agreeDetail = findViewById(R.id.agree_detail);
        agreeBtn = findViewById(R.id.agree_btn);

        dbHandler = EcheckDatabaseManager.getInstance(getApplicationContext());
        dbHandler.openDataBase();

        /**
         * 약관 동의 펼쳐서 상세보기 / 접기 이벤트
         */
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(agreeDetail.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(agreeList, new AutoTransition());
                    agreeDetail.setVisibility(View.VISIBLE);
                    arrow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                } else {
                    TransitionManager.beginDelayedTransition(agreeList, new AutoTransition());
                    agreeDetail.setVisibility(View.GONE);
                    arrow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

            }
        });

        /**
         * 전체 동의 체크박스 클릭 이벤트
         */
        allCheck.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allCheck.isChecked()) {
                    mandatoryCheck.setChecked(true);
                    agreeBtn.setEnabled(true);
                    agreeBtn.setBackgroundResource(R.color.activeColor);
                } else {
                    mandatoryCheck.setChecked(false);
                    agreeBtn.setEnabled(false);
                    agreeBtn.setBackgroundResource(R.color.inactiveColor);
                }
            }
        });

        /**
         * 필수 동의 체크박스 클릭 이벤트
         */
        mandatoryCheck.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mandatoryCheck.isChecked()) {
                    allCheck.setChecked(true);
                    agreeBtn.setEnabled(true);
                    agreeBtn.setBackgroundResource(R.color.activeColor);
                } else {
                    allCheck.setChecked(false);
                    agreeBtn.setEnabled(false);
                    agreeBtn.setBackgroundResource(R.color.inactiveColor);
                }
            }
        });

        /**
         * [다음] 버튼 클릭 이벤트
         * 이용약관에 동의해야지만 활성화 됨
         * 이용약관 동의 값을 로컬 DB에 저장하고 다음 화면으로 이동
         */
        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date = df.format(Calendar.getInstance().getTime());

                values.put("agree_name", "이용약관");
                values.put("agree_type", "필수");
                values.put("agree_yn", "Y");
                values.put("created_at", date);

                long newRowId = dbHandler.putData("agree", values);
                dbHandler.closeDatabase();

                if (newRowId > 0) {
                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                    startActivity(intent);
                } else {
                    Log.d("db_error", "agree insert error");
                }
            }
        });
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