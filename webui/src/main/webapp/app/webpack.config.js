const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = function(env) {
    return {
        entry: {
            main: [
                'angular',
                './app.js',
                'angular-ui-router',
                'angular-route',
                'angular-base64',
                'angular-resource',
                'angular-material',
                'angular-material-data-table',
                'angular-sanitize',
                'angular-audio',
                'angular-nvd3',
                'jquery-creditcardvalidator'
                /* 'angular-aria',
                'angular-animate',
                'showdown',
                'smoothscroll',*/
            ]
        },
        output: {
            filename: '[name].bundle.js',
            path: path.resolve(__dirname, 'dist'),
            publicPath: 'http://localhost:8181/app/dist/'   /* WATCH THIS FOR PROD */
        },
        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery"
            }),
            new HtmlWebpackPlugin({ title: 'Tree-shaking' }),
            new webpack.optimize.CommonsChunkPlugin({
                name: 'vendor',
                minChunks: function (module) {
                   // this assumes your vendor imports exist in the node_modules directory
                   return module.context && module.context.indexOf('node_modules') !== -1;
                }
            })
        ]
    }
}
