package jp.co.tripodw.iot.facedetection;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class face detection called from JavaScript.
 */
public class faceDetection extends CordovaPlugin {
    private static final String TAG = "faceDetection";

    private static final String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int CAM_REQ_CODE = 1;
    private CallbackContext execCallback;
    private JSONObject execArgs;

    private LivePreviewActivity fragment;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.execCallback = callbackContext;
        if (action.equals("start")) {
            Log.d(TAG, "execute start");
            this.execArgs = args.getJSONObject(0);
            if (this.checkPermissions()) {
                this.startCamera(this.execArgs, this.execCallback);
            } else {
                cordova.requestPermissions(this, CAM_REQ_CODE, permissions);
            }
            return true;
        } else if (action.equals("stop")) {
            Log.d(TAG, "execute stop");
            this.stopCamera(this.execCallback);
            return true;
        }
        return false;
    }

    private boolean checkPermissions() {
        for (String permission : permissions) {
            Log.d(TAG, "checkPermissions:" + permission);
            if (!cordova.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                execCallback.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
                return;
            }
        }

        if (requestCode == CAM_REQ_CODE) {
            this.startCamera(this.execArgs, this.execCallback);
        }
    }

    private void startCamera(JSONObject params, CallbackContext callbackContext) {
        if (this.fragment != null) {
            this.checkCamera(callbackContext);
            return;
        }

        DisplayMetrics metrics = cordova.getActivity().getResources().getDisplayMetrics();

        this.fragment = new LivePreviewActivity();
        fragment.setCameraParams(params, metrics);

        cordova.getActivity().runOnUiThread(() -> {
            //create or update the layout params for the container view
            int containerViewId = 20;
            FrameLayout containerView = (FrameLayout) cordova.getActivity().findViewById(containerViewId);
            if (containerView == null) {
                containerView = new FrameLayout(cordova.getActivity().getApplicationContext());
                containerView.setId(containerViewId);

                FrameLayout.LayoutParams containerLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                cordova.getActivity().addContentView(containerView, containerLayoutParams);
            }

            //set camera back to front
            containerView.setAlpha(1);
            containerView.bringToFront();

            //add the fragment to the container
            FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(containerView.getId(), fragment);
            fragmentTransaction.commit();
        });
    }

    private void stopCamera(CallbackContext callbackContext) {
        if (fragment == null) {
            callbackContext.error("camera no active!");
            return;
        }

        if (fragment.getCameraSource() == null) {
            callbackContext.error("camera stopped!");
            return;
        }

        cordova.getActivity().runOnUiThread(() -> {
            fragment.stopCamera();
        });
    }

    private void checkCamera(CallbackContext callbackContext) {
        if (fragment.getCameraSource() != null) {
            callbackContext.error("camera started!");
            return;
        }
        cordova.getActivity().runOnUiThread(() -> {
            fragment.createCameraSource();
            fragment.startCameraSource();
        });
    }
}
