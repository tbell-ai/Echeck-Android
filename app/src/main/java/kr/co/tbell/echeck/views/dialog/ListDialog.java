package kr.co.tbell.echeck.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.Elect;

public class ListDialog {

    private Context mContext;
    private Button okay;
    private TextView date;
    private TextView usage;
    private TextView charge;
    private TextView measure;
    private Elect elect;

    public ListDialog(Context context, Elect elect) {
        this.mContext = context;
        this.elect = elect;
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_detail_dialog);

        measure = dialog.findViewById(R.id.detail_measure);
        date = dialog.findViewById(R.id.mesure_date);
        usage = dialog.findViewById(R.id.mesure_usage);
        charge = dialog.findViewById(R.id.mesure_charge);

        measure.setText(elect.getElectMeasure());
        date.setText(elect.getCreatedAt());
        usage.setText(elect.getElectAmount() + " kWh");
        charge.setText(elect.getElectCharge() + " 원");

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 70 / 100;

        okay = dialog.findViewById(R.id.close_button);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
