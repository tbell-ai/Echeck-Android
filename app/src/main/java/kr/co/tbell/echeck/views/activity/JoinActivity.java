package kr.co.tbell.echeck.views.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.tbell.echeck.R;

public class JoinActivity extends AppCompatActivity {

    private Toolbar actionbar;
    private TextView pageTitle;
    private ImageView myUpdateButton;

    private EditText idInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;
    private EditText nicknameInput;
    private EditText emailInput;
    private Button idCheck;
    private Button goJoin;

    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        actionbar = findViewById(R.id.toolbar);
        pageTitle = findViewById(R.id.toolbarTitle);
        myUpdateButton = findViewById(R.id.mypage_update);

        idInput = findViewById(R.id.echeck_id_input);
        passwordInput = findViewById(R.id.password_input);
        passwordConfirmInput = findViewById(R.id.password_confirm_input);
        nicknameInput = findViewById(R.id.nickname_input);
        emailInput = findViewById(R.id.email_input);
        idCheck = findViewById(R.id.id_check);
        goJoin = findViewById(R.id.go_signup);

        // Toolbar 설정
        actionbar.setNavigationIcon(null);
        pageTitle.setText("회원가입");
        myUpdateButton.setEnabled(false);
        myUpdateButton.setVisibility(View.GONE);
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