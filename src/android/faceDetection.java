package jp.co.tripodw.iot.facedetection;

import android.Manifest;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.FrameLayout;

import android.util.DisplayMetrics;
import android.content.pm.PackageManager;

import android.util.TypedValue;
import android.util.Log;
import java.util.List;
import java.util.Arrays;

import jp.co.tripodw.iot.facedetection.LivePreviewActivity;

/**
 * This class face detection called from JavaScript.
 */
public class faceDetection extends CordovaPlugin {
    private static final String TAG = "faceDetection";

    private static final String [] permissions = {
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
        this.execArgs = args.getJSONObject(0);

        if (action.equals("start")) {
            Log.d(TAG, "execute start");
            if(this.checkPermissions()) {
                this.startCamera(this.execArgs, this.execCallback);
            } else {
                cordova.requestPermissions(this, CAM_REQ_CODE, permissions);
            }
            return true;
        }
        return false;
    }

    private boolean checkPermissions() {
        for(int i = 0; i  < permissions.length; i++) {
            Log.d(TAG, "checkPermissions:" + permissions[i]);
            if(!cordova.hasPermission(permissions[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
      for(int r:grantResults){
        if(r == PackageManager.PERMISSION_DENIED){
          execCallback.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
          return;
        }
      }

      if(requestCode == CAM_REQ_CODE){
        this.startCamera(this.execArgs, this.execCallback);
      }
    }

    private void startCamera(JSONObject params, CallbackContext callbackContext) {

        if (this.fragment != null) {
            callbackContext.error("Camera already started");
            return;
        }

        this.fragment = new LivePreviewActivity();
        this.fragment.setCamera(params.optBoolean("front", false));

        DisplayMetrics metrics = cordova.getActivity().getResources().getDisplayMetrics();

        // offset
        int x = params.optInt("x", 0);
        int y = params.optInt("y", 0);
        int computedX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, metrics);
        int computedY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y, metrics);

        // size
        int width = params.optInt("width", 0);
        int height = params.optInt("height", 0);
        int computedWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, metrics);
        int computedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, metrics);

        fragment.setRect(computedX, computedY, computedWidth, computedHeight);


        cordova.getActivity().runOnUiThread(() -> {

            // if (message != null && message.length() > 0) {
            //     callbackContext.success(message);
            // } else {
            //     callbackContext.error("Expected one non-empty string argument.");
            // }

            //create or update the layout params for the container view
            FrameLayout containerView = (FrameLayout)cordova.getActivity().findViewById(20);
            if(containerView == null){
                containerView = new FrameLayout(cordova.getActivity().getApplicationContext());
                 containerView.setId(20);

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
}
