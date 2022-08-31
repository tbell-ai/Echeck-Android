package kr.co.tbell.echeck.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import kr.co.tbell.echeck.R;

public class InfoPrevDialog {

    private Context context;
    Button cancel;
    Button okay;
    DialogListener dialogListener;

    public InfoPrevDialog(Context context, DialogListener dialogListener) {
        this.context = context;
        this.dialogListener = dialogListener;
    }

    public interface DialogListener {
        void onCheckClicked();
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.info_dialog);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 35 / 100;

        cancel = dialog.findViewById(R.id.cancel_button);
        okay = dialog.findViewById(R.id.okay_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "이전 화면 이동을 취소 했습니다.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "현재까지 입력한 가구별 할인 정보를 초기화합니다.", Toast.LENGTH_LONG).show();
                dialogListener.onCheckClicked();
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
