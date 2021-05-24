var app = {
  startCamera: function(){
    const cameraDiv = document.getElementById('originalPicture');
    const cDivRect = cameraDiv.getBoundingClientRect();
    const x = cDivRect.left;
    const y = cDivRect.top;
    const height = cDivRect.height;
    const width = cDivRect.width;

    const options = {
      x: x,
      y: y,
      width: width,
      height: height,
      front: false,
      cameraPixel:'640x480',
      minFaceSize: 0.5,
      landmark:true,
      classification:true,
      contour: false,
      faceTrack:true,
    };
    window.faceDetection.start(options, function(result){
      //console.log('start' +JSON.stringify(result));
      const data = result.data;
      let htmlText = '';
      if(result.type == 'image'){
        document.getElementById('imageFrameTitle').innerHTML = 'Live frame information';

        htmlText += '<li>InputImage size: ' + data.imageSize; +' </li>';
        htmlText += '<li>FPS: ' + data.framesPerSecond +', Frame latency: ' + data.frameLatency +' </li>';
        htmlText += '<li>Detector latency: ' + data.detectorLatency +' </li>';
        document.getElementById('imageFrame').innerHTML = htmlText;
        return;
      }

      for(let i= 0; i < data.length; i++) {
        //console.log('face=' + JSON.stringify(data[i]));
        document.getElementById('faceFrameTitle').innerHTML = 'Detected faces information';

        htmlText += '<li>id:'+ data[i].id + ' , smiling:' + data[i].smiling +' </li>';
        htmlText += '<li>[EyeOpen]left: ' + data[i].leftEyeOpen +', right: ' + data[i].rightEyeOpen +'</li>';
        htmlText += '<li>[euler]X: ' + data[i].eulerX + ', Y: ' + data[i].eulerY + ', Z: ' + data[i].eulerZ +'</li>';
        htmlText += '<br>';
      }
      document.getElementById('faceFrame').innerHTML = htmlText;

    });
  },

  takePicture: function(){
    const options = {
      width: 360,
      height: 480,
      quality: 85,
    };
    window.faceDetection.takePicture(options, function(imgData){
      //console.log('takePicture callback' +JSON.stringify(imgData));
      document.getElementById('originalPicture').style.backgroundColor = 'white';
      document.getElementById('originalPicture').style.backgroundImage = 'url(data:image/jpeg;base64,' + imgData + ')';
      window.faceDetection.stop();

      document.getElementById('imageFrameTitle').innerHTML = '';
      document.getElementById('faceFrameTitle').innerHTML = '';

      document.getElementById('imageFrame').innerHTML = '';
      document.getElementById('faceFrame').innerHTML = '';
    });
  },

  init: function(){
    document.getElementById('startCameraButton').addEventListener('click', this.startCamera, false);
    document.getElementById('takePictureButton').addEventListener('click', this.takePicture, false);
  }
};

document.addEventListener('deviceready', function(){
  app.init();
}, false);
