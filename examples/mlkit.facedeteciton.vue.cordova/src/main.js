import Vue from 'vue'
import App from './App.vue'
import 'onsenui/css/onsenui.css';
import 'onsenui/css/onsen-css-components.css';
import VueOnsen from 'vue-onsenui'
import VueLogger from 'vuejs-logger';

const options = {
  isEnabled: true,
  logLevel : process.env.NODE_ENV === 'production' ? 'info' : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : true,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);

Vue.config.productionTip = false

Vue.use(VueOnsen);

function startUp() {
  new Vue({
    render: h => h(App)
  }).$mount('#app');
}

document.addEventListener('deviceReady', startUp);
