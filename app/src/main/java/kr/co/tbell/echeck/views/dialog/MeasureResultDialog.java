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

public class MeasureResultDialog {

    private Context context;
    Button reInput;
    Button goManual;
    Button closePopup;

    MeasureResultDialog.DialogListener dialogListener;

    public MeasureResultDialog(Context context, MeasureResultDialog.DialogListener dialogListener) {
        this.context = context;
        this.dialogListener = dialogListener;
    }

    public interface DialogListener {
        void onReShootClicked();
        void onManualClicked();
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.measure_result_dialog);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 40 / 100;

        reInput = dialog.findViewById(R.id.re_input);
        goManual = dialog.findViewById(R.id.manual_input);
        closePopup = dialog.findViewById(R.id.close_button);

        reInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "전월지침 변경을 위해 전월지침 변경 화면으로 이동합니다.", Toast.LENGTH_LONG).show();
                dialogListener.onReShootClicked();
                dialog.dismiss();
            }
        });

        goManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "수기입력란에 지침에 적힌 숫자를 정확히 입력해주세요.", Toast.LENGTH_LONG).show();
                dialogListener.onManualClicked();
                dialog.dismiss();
            }
        });

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
