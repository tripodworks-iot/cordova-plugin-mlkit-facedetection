# cordova plugin for mlkit face detection
<a href="https://badge.fury.io/js/cordova-plugin-mlkit-facedetection" target="_blank"><img height="21" style='border:0px;height:21px;' border='0' src="https://badge.fury.io/js/cordova-plugin-mlkit-facedetection.svg" alt="NPM Version"></a>
<a href='https://www.npmjs.org/package/cordova-plugin-mlkit-facedetection' target='_blank'><img height='21' style='border:0px;height:21px;' src='https://img.shields.io/npm/dt/cordova-plugin-mlkit-facedetection.svg?label=NPM+Downloads' border='0' alt='NPM Downloads' /></a>

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
Start the camera preview instance for face detection.

<strong>Options: </strong>
All options stated are optional and will default to values here.

| Item | Type | Default | Note |
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

| Item | Type | Default | Note |
| --- | --- | --- | --- |
| type | string | image/face | Type of live frame information |
| data | Json/List  |  | if result.type is 'image', data type is Json, otherwise，data type is List |

<br>
If result.type is 'image', data type is Json, values as follows.

| Item | Type | Default | Note |
| --- | --- | --- | --- |
| imageSize | string | '480x640' | Input Picture pixel  |
| framesPerSecond | int | 0 | Image frames Per Second  |
| frameLatency | int | 0 | Image frames Latency(ms)  |
| detectorLatency | int | 0 | Image detector Latency(ms)  |

<br>

If result.type is 'face', data type is List. The type of the list element is Json, element values as follow.

| Item | Type | Default | Note |
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
    const data = result.data;
    if(result.type == 'image') {
      // get live frame information
       console.log(JSON.stringify(data));
    }else {
      // get face frame information
      data.forEach(function(face)){
        console.log(JSON.stringify(face));
      });
    }
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

| Item | Type | Default | Note |
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
Sample cordova application <a href="https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection/tree/main/examples/mlkit.facedeteciton.cordova">mlkit.facedeteciton.cordova</a>


### Screenshots
<img width="200" src="https://user-images.githubusercontent.com/50347751/119295757-91a9bf00-bc92-11eb-8b78-7e0f56322900.png"/><img hspace="30" width="200" src="https://user-images.githubusercontent.com/50347751/119295761-93738280-bc92-11eb-8940-aa3c80987121.png"/>
