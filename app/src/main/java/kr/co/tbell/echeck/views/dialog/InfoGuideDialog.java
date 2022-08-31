package kr.co.tbell.echeck.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.tbell.echeck.R;

public class InfoGuideDialog {

    private Context context;
    private Button close;
    private TextView textDetail;

    public InfoGuideDialog(Context context) {
        this.context = context;
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.info_guide_dialog);

        textDetail = dialog.findViewById(R.id.info_dialog_detail);
        close = dialog.findViewById(R.id.close_button);

        SpannableString spannableString = new SpannableString("고지서의 전력량계 전월지침과\n사용(부과)기간을 입력해주세요.");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#54279d")), 5, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textDetail.setText(spannableString);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 90 / 100;

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}