# cordova plugin for mlkit face detection
<a href="https://badge.fury.io/js/cordova-plugin-camera-preview" target="_blank"><img height="21" style='border:0px;height:21px;' border='0' src="https://badge.fury.io/js/cordova-plugin-camera-preview.svg" alt="NPM Version"></a>
<a href='https://www.npmjs.org/package/cordova-plugin-camera-preview' target='_blank'><img height='21' style='border:0px;height:21px;' src='https://img.shields.io/npm/dt/cordova-plugin-camera-preview.svg?label=NPM+Downloads' border='0' alt='NPM Downloads' /></a>

Cordova plugin that allows face detection from Javascript and HTML
Preferences

**Currently available for Android only. Will support iOS in the future.<br> This plugin is under constant development. Releases are being kept up to date when appropriate.**

## Requirements
<ul>
  <li>Cordova 9.0.0 or higher</li>
  <li>Android Cordova library 8.0.0 or higher</li>
</ul>

## Features

<ul>
  <li>Start the camera preview for face detection from HTML code</li>
  <li>Stop the camera preview</li>
  <li>Take Photos</li>
</ul>

## Installation

```
$ cordova plugin add https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection
```

## Methods

### start(options, [successCallback, errorCallback])
Starts the camera preview instance.

<strong>Options: </strong>
All options stated are optional and will default to values here.

| item | Type | Default | Note |
| --- | --- | --- | --- |
| x | int | 0 | start x position for camera  |
| y | int | 0 | start y position for camera |
| width | int | 0 | Defaults to window.screen.width |
| height | int | 0 | Defaults to window.screen.height |
| front | boolean | true | Defaults to front camera |
| cameraPixel | string | '480x640' | Picture pixel |
| minFaceSize | float | 0.1 | Recognize the proportion of the face |
| landmark | boolean | true | Whether to attempt to identify facial "landmarks": eyes, ears, nose, cheeks, mouth, and so on |
| classification | boolean | true | Whether or not to classify faces into categories such as "smiling", and "eyes open" |
| faceTrack | boolean | false | Whether or not to assign faces an ID, which can be used to track faces across images |
| contour | boolean | false | Whether to detect the contours of facial features. Contours are detected for only the most prominent face in an image |

<br>
<strong>successCallback(result): </strong>Result will default to values here.

| item | Type | Default | Note |
| --- | --- | --- | --- |
| type | string | image/face | Type of live frame information |
| data | Json/List  |  | if result.type is 'image', data type is Json, otherwise，data type is List |

<br>
int
If result.type is 'image', data type is Json, values is follow.

| item | Type | Default | Note |
| --- | --- | --- | --- |
| imageSize | string | '480x640' | Input Picture pixel  |
| framesPerSecond | int | 0 | Image frames Per Second  |
| frameLatency | int | 0 | Image frames Latency(ms)  |
| detectorLatency | int | 0 | Image detector Latency(ms)  |

<br>

If result.type is 'face', data type is List, list item type is Json, json values is follow.

| item | Type | Default | Note |
| --- | --- | --- | --- |
| id | int | 0 | the tracking ID if the tracking is enabled.  Only the start option faceTrack is true  |
| smiling | float | 0 | The probability that the face is smiling(0~1). Only the start option classification is true  |
| leftEyeOpen | float | 0 | The probability that the face's left eye is open(0~1). Only the start option classification is true  |
| rightEyeOpen | float | 0 | The probability that the face's right eye is open(0~1). Only the start option classification is true  |
| eulerX | float | 0 | Rotation of the face about the horizontal axis of the image  |
| eulerY | float | 0 | Rotation of the face about the vertical axis of the image  |
| eulerZ | float | 0 | Rotation of the face about the axis pointing out of the image  |

<br>

```
  let options = {
    x: 30,
    y: 10,
    width: 200,
    height: 400,
    front: false,
    cameraPixel: '480x640',
    minFaceSize: 0.5,
    landmark:false,
    classification:false,
    faceTrack:true,
    contour:false,
    liveFrame:false,
  };

  faceDetection.start(options, function(result) {
  });
```

### stop([successCallback, errorCallback])
Stop the camera preview instance.

```
faceDetection.stop();
```

### takePicture(options, successCallback, [errorCallback])
Take the picture. It will choose a supported photo size that is closest to width and height specified and has closest aspect ratio to the preview.

<strong>Options:</strong>
All options stated are optional and will default to values here.

| item | Type | Default | Note |
| --- | --- | --- | --- |
| width | int | 480 | Taken image width. If width are not specified or are 0 it will use the defaults |
| height | int | 640 | Taken Image height. If height are not specified or are 0 it will use the defaults |
| quality | int | 85 | Specifies the quality/compression value: 0=min compression, 100=max quality

```
  let options = {
    width: 200,
    height: 400,
    quality: 90,
  };

  faceDetection.takePicture(options, function(base64Data) {
    /*
      base64Data is base64 encoded jpeg image. Use this data to store to a file or upload.
      Its up to the you to figure out the best way to save it to disk or whatever for your application.
    */

    // One simple example is if you are going to use it inside an HTML img src attribute
    // then you would do the following:
    imageSrcData = 'data:image/jpeg;base64,' + base64Data;
    $('img#my-img').attr('src', imageSrcData);
  });

  // OR if you want to use the default options.
  faceDetection.takePicture(function(base64Data){
    /* code here */
  });
```

## Sample App
Execute the following command to start app.

```
$ cd /path/cordova-plugin-mlkit-facedetection/
$ cd ./examples\mlkit.facedeteciton.cordova
$ npm install
$ cd ./src-cordova
$ npm install
$ cordova platform add android
$ cd ..
$ npm run cordova-serve-android
```

### Screenshots
<img src="https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection/master/img/android01.png"/>
<img hspace="20" src="https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection/master/img/android02.png"/>
