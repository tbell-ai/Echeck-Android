package kr.co.tbell.echeck.views.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.tbell.echeck.R;

public class LoginActivity extends AppCompatActivity {

    private EditText id;
    private EditText password;
    private CheckBox autoLogin;
    private Button goLogin;
    private TextView searchAccount;
    private TextView goJoin;

    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = findViewById(R.id.id_input);
        password = findViewById(R.id.pw_input);
        autoLogin = findViewById(R.id.auto_login_check);
        goLogin = findViewById(R.id.login_btn);
        searchAccount = findViewById(R.id.search_id_pw);
        goJoin = findViewById(R.id.go_join);
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