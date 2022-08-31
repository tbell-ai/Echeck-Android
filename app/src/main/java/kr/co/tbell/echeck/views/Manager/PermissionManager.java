package kr.co.tbell.echeck.views.Manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private Context mContext;
    private Activity mActivity;
    private static PermissionManager instance;

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<String> permissionList;

    private final int MULTI_PERMISSIONS = 101;

    private PermissionManager(Activity activity, Context context) {
        this.mActivity = activity;
        this.mContext = context;
    }

    public static synchronized PermissionManager getInstance(Activity activity, Context context) {
        if (instance == null) {
            instance = new PermissionManager(activity, context);
        }

        return instance;
    }

    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<String>();

        for(String permission : permissions) {
            result = ContextCompat.checkSelfPermission(mContext, permission);
            if(result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if(!permissionList.isEmpty()) {
            return false;
        }
        return true;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(mActivity, permissionList.toArray(new String[0]), MULTI_PERMISSIONS);
    }

    public boolean permissionResult(int requestCode, String[] permissions, int[] grantResults) {

        if(requestCode == MULTI_PERMISSIONS && (grantResults.length > 0)) {
            for(int i=0; i<grantResults.length; i++) {
                if(grantResults[i] == -1) {
                    return false;
                }
            }
        }
        return true;
    }
}
