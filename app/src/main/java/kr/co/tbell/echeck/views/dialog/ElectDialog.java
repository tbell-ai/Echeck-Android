package kr.co.tbell.echeck.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class ElectDialog {

    private Context context;
    private Button update;
    private Button next;
    private TextView electView;

    private User user;
    private EcheckDatabaseManager dbHandler;

    private ElectDialogListener electDialogListener;

    public ElectDialog(Context context, ElectDialogListener electDialogListener) {
        this.context = context;
        this.electDialogListener = electDialogListener;
    }

    public interface ElectDialogListener {
        void onUpdateClicked();
        void onNextClicked();
    }

    public void showDialog() {
        dbHandler = EcheckDatabaseManager.getInstance(context);
        dbHandler.openDataBase();
        user = dbHandler.getUser();

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.elect_dialog);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 50 / 100;

        electView = dialog.findViewById(R.id.elect_area);
        update = dialog.findViewById(R.id.update_button);
        next = dialog.findViewById(R.id.next_button);

        electView.setText(user.getElectBefore());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "전월지침을 수정 화면으로 이동합니다.", Toast.LENGTH_LONG).show();
                electDialogListener.onUpdateClicked();
                dialog.dismiss();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "올바른 전월지침을 입력받은 상태입니다.", Toast.LENGTH_LONG).show();
                electDialogListener.onNextClicked();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}