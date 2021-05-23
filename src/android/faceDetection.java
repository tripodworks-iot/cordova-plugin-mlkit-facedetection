package jp.co.tripodw.iot.facedetection;

import android.Manifest;
import android.app.Fragment;
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
public class faceDetection extends CordovaPlugin implements LivePreviewActivity.LivePreviewActivityListener {
    private static final String TAG = "faceDetection";

    private static final String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int CAM_REQ_CODE = 1;
    private JSONObject execArgs;
    String action;

    private LivePreviewActivity fragment;
    private CameraXLivePreviewActivity fragmentx;

    private CallbackContext startCallback;
    private CallbackContext stopCallback;
    private CallbackContext takePictureCallback;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.action = action;
        if (action.equals("start")) {
            Log.d(TAG, "execute start");
            startCallback = callbackContext;

            this.execArgs = args.getJSONObject(0);
            if (this.checkPermissions()) {
                this.startCamera(this.execArgs, this.startCallback);
            } else {
                cordova.requestPermissions(this, CAM_REQ_CODE, permissions);
            }
            return true;
        } else if (action.equals("stop")) {
            Log.d(TAG, "execute stop");
            stopCallback = callbackContext;
            this.stopCamera(this.stopCallback);
            return true;
        } else if (action.equals("startX")) {
            Log.d(TAG, "execute start");
            startCallback = callbackContext;
            this.execArgs = args.getJSONObject(0);
            if (this.checkPermissions()) {
                this.startCameraX(this.execArgs, this.startCallback);
            } else {
                cordova.requestPermissions(this, CAM_REQ_CODE, permissions);
            }
            return true;
        } else if (action.equals("stopX")) {
            Log.d(TAG, "execute stop");
            stopCallback = callbackContext;
            this.stopCameraX(this.stopCallback);
            return true;
        } else if (action.equals("takePicture")) {
            Log.d(TAG, "execute takePicture");
            takePictureCallback = callbackContext;
            this.takePicture(args.getJSONObject(0), this.takePictureCallback);
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
                startCallback.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
                return;
            }
        }

        if (requestCode == CAM_REQ_CODE) {
            if (action.equals("start")) {
                this.startCamera(this.execArgs, this.startCallback);
            } else {
                this.startCameraX(this.execArgs, this.startCallback);
            }
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
        fragment.setEventListener(this);
        this.startCameraThread(fragment);
    }

    private void startCameraThread(Fragment params) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
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
                fragmentTransaction.add(containerView.getId(), params);
                fragmentTransaction.commit();
            }
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

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                fragment.stopCamera();
                FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
                fragment = null;
                callbackContext.success();
            }
        });
    }

    private void checkCamera(CallbackContext callbackContext) {
        if (fragment.getCameraSource() != null) {
            callbackContext.error("camera started!");
            return;
        }
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                fragment.createCameraSource();
                fragment.startCameraSource();
            }
        });
    }

    public void onLiveFrame(JSONObject liveFrame) {
        //Log.d(TAG, "onLiveFrame:" + liveFrame.toString());
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, liveFrame);
        pluginResult.setKeepCallback(true);
        this.startCallback.sendPluginResult(pluginResult);
    }

    private void startCameraX(JSONObject params, CallbackContext callbackContext) {
        if (this.fragmentx != null) {
            return;
        }

        DisplayMetrics metrics = cordova.getActivity().getResources().getDisplayMetrics();
        this.fragmentx = new CameraXLivePreviewActivity();
        fragmentx.setCameraParams(params, metrics);

//        cordova.getActivity().runOnUiThread(new Runnable() {
//            public void run() {
//                Intent intent = new Intent(cordova.getActivity().getApplicationContext(), CameraXLivePreviewActivity.class);
//                cordova.getActivity().startActivity(intent);
//            }
//        });
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
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
//                fragmentTransaction.add(containerView.getId(), fragmentx);
                fragmentTransaction.commit();
            }
        });

    }

    private void stopCameraX(CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                fragmentx.stopCamera();
            }
        });
    }


    private void takePicture(JSONObject params, CallbackContext callbackContext) {
        if (this.fragment == null) {
            callbackContext.error("camera no started!");
            return;
        }
        fragment.takePicture(params);
    }

    public void onPicture(JSONArray data) {
        Log.d(TAG, "returning picture");

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, data);
        pluginResult.setKeepCallback(false);
        takePictureCallback.sendPluginResult(pluginResult);
    }

    public void onPictureError(String message) {
        Log.d(TAG, "CameraPreview onPictureTakenError");
        takePictureCallback.error(message);
    }
}
