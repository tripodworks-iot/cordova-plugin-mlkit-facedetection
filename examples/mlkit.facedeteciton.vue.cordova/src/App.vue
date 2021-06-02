<template>
  <div id="app">
    <v-ons-page>
      <v-ons-toolbar>
        <div class="center">Face Detection Mock</div>
      </v-ons-toolbar>

      <div class="camera-content">
        <div id="cameraDivID" :style="imageStyle">
        </div>
      </div>

      <section style="margin-top: 10px; padding:5px; text-align: left;">
        <v-ons-button modifier="large" @click="goFaceDetection()">{{startBunText}}</v-ons-button>
      </section>
      <section style="margin-top: 5px; padding:5px;text-align: left;">
        <v-ons-button modifier="large" @click="goStopFaceDetection()">Stop</v-ons-button>
      </section>

      <div v-show="isCameraStart && liveFrame.imageSize != ''" style="margin-top: 10px; text-align: left;">
        <div style="padding-left:5px;">
            <b>live frame information</b>
        </div>
        <div style="padding-left:10px;">
            <li>InputImage size: {{liveFrame.imageSize}}</li>
            <li>FPS: {{liveFrame.framesPerSecond}}, Frame latency: {{liveFrame.frameLatency}}</li>
            <li>Detector latency: {{liveFrame.detectorLatency}}</li>
        </div>
      </div>

      <div v-show="isCameraStart" style="margin-top: 10px; text-align: left;">
        <div style="padding-left:5px;">
            <b>detected faces information</b>
        </div>

        <!--eslint-disable-next-line-->
        <div v-for="(face, index) in faceList">
          <div style="padding-left:10px;" :key="index">
              <li>id: {{face.id}}</li>
              <li>smiling: {{Number.parseFloat(face.smiling).toFixed(2)}}</li>
              <li>leftEyeOpen: {{Number.parseFloat(face.leftEyeOpen).toFixed(2)}}</li>
              <li>rightEyeOpen: {{Number.parseFloat(face.rightEyeOpen).toFixed(2)}}</li>
              <li>eulerX/Y/Z: {{Number.parseFloat(face.eulerX).toFixed(2)}} / {{Number.parseFloat(face.eulerY).toFixed(2)}} / {{Number.parseFloat(face.eulerZ).toFixed(2)}}</li>
          </div>
        </div>

      </div>

    </v-ons-page>
  </div>
</template>

<script>
const bgImage = require('@src/assets/camera.png');
const bgImageStyle = {
  backgroundImage: 'url(' + bgImage + ')',
  backgroundSize: 'contain',
  backgroundRepeat: 'no-repeat',
  backgroundPosition: 'center',
  display: 'table',
  'text-align': 'center',
  width: '100%',
  height: window.parent.screen.height * 0.4 + 'px'
};

export default {
  name: 'App',
  components: {
  },
  data() {
    return {
      imageStyle: bgImageStyle,
      isCameraStart: false,
      liveFrame: {
        imageSize:"",
        framesPerSecond:"",
        frameLatency:"",
        detectorLatency:"",
      },
      faceList: [],
    }
  },

  computed: {
    startBunText: function () {
      if (this.isCameraStart && this.liveFrame.imageSize != '') {
        return 'Take picture';
      } else {
        return 'Start';
      }
    },
  },

  methods: {
    goFaceDetection(){
      const text = this.startBunText;
      this.$log.debug('[face]goFaceDetection text=' + text);

      if(text == 'Start'){
        this.startFaceDetection();
      }else{
        this.takePicture();
      }
    },

    takePicture(){
      this.$log.debug('[face]takePicture call');
      const options = {
        width: 360,
        height: 480,
        quality: 85,
      };
      window.faceDetection.takePicture(options, this.takeSuccessCallback, this.errorCallback);
      this.isCameraStart = true;
    },

    takeSuccessCallback(result){
      this.$log.debug('[face]takeSuccessCallback result=' + JSON.stringify(result));
      const image = 'data:image/png;base64,' + result;
      this.goStopFaceDetection(image);
    },

    startFaceDetection(){
      this.$log.debug('[face]goStartFaceDetection call');

      const cameraDiv = document.getElementById('cameraDivID');
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
        minFaceSize: 0.5,
        landmark:true,
        classification:true,
        contour: false,
        faceTrack:true,
      };
      window.faceDetection.start(options, this.successCallback, this.errorCallback);
      this.isCameraStart = true;
    },

    successCallback(result){
      //this.$log.debug('[face]successCallback result=' + JSON.stringify(result));
      if(result.type === 'image'){
        this.liveFrame = result.data;
      }
      if(result.type === 'face'){
        this.faceList = result.data;
      }
    },

    errorCallback(result){
      this.$log.debug('[face]errorCallback result=' + result);
      this.isCameraStart = false;
      alert('error:' + result);
    },

    goStopFaceDetection(image=null){
      this.$log.debug('[face]goStopFaceDetection call');
      this.isCameraStart = false;
      let data = bgImage;
      if(image){
        data = image;
      }
      this.imageStyle.backgroundImage = 'url(' + data + ')';
      window.faceDetection.stop(this.successCallback, this.errorCallback);
    }
  },
  mounted: function() {
    this.$log.debug('[face]mounted call');
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

.camera-content {
  margin: 5px;
}
</style>
