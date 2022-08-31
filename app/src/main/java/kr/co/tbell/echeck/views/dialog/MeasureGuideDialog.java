package kr.co.tbell.echeck.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import kr.co.tbell.echeck.R;

public class MeasureGuideDialog {

    private Context context;
    private Button close;
    private MeasureGuideDialog.DialogListener dialogListener;

    public MeasureGuideDialog(Context context) {
        this.context = context;
    }

    public MeasureGuideDialog(Context context, MeasureGuideDialog.DialogListener dialogListener) {
        this.context = context;
        this.dialogListener = dialogListener;
    }

    public interface DialogListener {
        void onCloseClicked();
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.measure_guide_dialog);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 70 / 100;

        close = dialog.findViewById(R.id.close_button);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.onCloseClicked();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
