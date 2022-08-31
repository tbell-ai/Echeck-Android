package kr.co.tbell.echeck.views.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.dialog.CameraGuideDialog;
import kr.co.tbell.echeck.views.fragment.camera.CameraFragment;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        String machine = intent.getStringExtra("machine");

        Bundle bundle = new Bundle();
        bundle.putString("machine", machine);
        CameraFragment cameraFragment = CameraFragment.newInstance();
        cameraFragment.setArguments(bundle);

        CameraGuideDialog dialog = new CameraGuideDialog(this);
        dialog.showDialog();

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, cameraFragment)
                    .commit();
        }
    }
}