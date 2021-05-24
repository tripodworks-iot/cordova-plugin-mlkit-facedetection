cordova-plugin-mlkit-facedetection Sample Cordova App
=======================

This is a sample cordova application for <a href="https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection">cordova-plugin-mlkit-facedetection</a>

## Install
```
$cordova platform add android
$cordova build android
```

## Reset / Update Plugin to Latest

If you last installed an older version of the plugin and want to ensure the sample app is up to date again just do the following to reset.

```
$rm -rf platforms/ plugins/

$cordova platform add android
$cordova build android
```

## IOS Quirks

It is not possible to use your computers webcam during testing in the simulator, you must device test
