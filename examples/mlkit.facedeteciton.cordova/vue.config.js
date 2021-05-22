const path = require('path');

module.exports = {
  publicPath: '',
  pluginOptions: {
    cordovaPath: 'src-cordova'
  },

  configureWebpack: {
    resolve: {
      alias: {
        '@src': path.join(__dirname, '/src') // @の参照先の変更
      }
    }
  }
}
