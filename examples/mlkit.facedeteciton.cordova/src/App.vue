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
        <v-ons-button modifier="large" @click="goStartFaceDetection()">start</v-ons-button>
      </section>
      <section style="margin-top: 5px; padding:5px;text-align: left;">
        <v-ons-button modifier="large" @click="goStopFaceDetection()">stop</v-ons-button>
      </section>

      <div v-show="isCameraStart" style="margin-top: 10px; text-align: left;">
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
              <li>id: {{face.id}}, smiling: {{face.smiling}}</li>
              <li>leftEyeOpen: {{face.leftEyeOpen}}, rightEyeOpen: {{face.rightEyeOpen}}</li>
              <li>eulerX: {{face.eulerX}}, eulerY: {{face.eulerY}}, eulerZ: {{face.eulerZ}}</li>
          </div>
        </div>

      </div>

    </v-ons-page>
  </div>
</template>

<script>

const bgImageStyle = {
  'background-color': 'black',
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
  methods: {
    goStartFaceDetection(){
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
        faceTrack:false,
      };
      window.faceDetection.start(options, this.successCallback, this.errorCallback);
    },

    successCallback(result){
      //this.$log.debug('[face]successCallback result=' + JSON.stringify(result));
      this.isCameraStart = true;
      if(result.type === 'frame'){
        this.liveFrame = result.data;
      }
      if(result.type === 'face'){
        this.faceList = result.data;
      }
    },

    errorCallback(result){
      this.$log.debug('[face]errorCallback result=' + result);
      alert('error:' + result);
    },

    goStopFaceDetection(){
      this.$log.debug('[face]goStopFaceDetection call');
      window.faceDetection.stop(this.successCallback, this.errorCallback);
      const me = this;
      setTimeout(function(){
              me.isCameraStart = false;
      }, 300);
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
