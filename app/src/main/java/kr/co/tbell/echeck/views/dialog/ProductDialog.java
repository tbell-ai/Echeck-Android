package kr.co.tbell.echeck.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.views.activity.HomeProductActivity;

public class ProductDialog {

    private Context context;
    private Button okButton;
    private Elect elect;

    public ProductDialog(Context context, Elect elect) {
        this.context = context;
        this.elect = elect;
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_dialog);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 30 / 100;

        okButton = dialog.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "사용 가전제품을 확인하시고 등록해주세요.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, HomeProductActivity.class);
                intent.putExtra("target", elect);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
