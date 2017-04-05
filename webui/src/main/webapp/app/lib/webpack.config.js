const path = require('path');
const webpack = require('webpack');

module.exports = function(env) {
    return {
        entry: {
            vendor: [
                'angular',
                'angular-ui-router',
                'angular-route',
                'angular-resource',
                'angular-aria',
                'angular-animate',
                'angular-material',
                'angular-material-data-table',
                'angular-base64',
                'angular-sanitize',
                'angular-audio',
                'showdown',
                'moment',
                'smoothscroll',
                'jquery-creditcardvalidator',
                'braintree'
            ]
        },
        output: {
            filename: 'vendor.bundle.js',
            path: path.resolve(__dirname, 'dist')
        },
        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery"
            })
        ],
module: {
    noParse: /bullshit/
  }
    }
}
