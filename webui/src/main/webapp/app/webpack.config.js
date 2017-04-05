const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = function(env) {
    return {
        entry: {
            main: './app.js',
            base: [
                'angular',
                'angular-ui-router',
                'angular-route',
                'angular-base64',
                'angular-resource',
                'angular-material',
                'angular-material-data-table',
                'angular-sanitize',
                'angular-audio',
                'angular-nvd3'
            ],
            extra: [
                'showdown',
                'smoothscroll',
                'angular-aria',
                'angular-animate',
                'jquery-creditcardvalidator'
            ]
        },
        output: {
            filename: '[name].bundle.js',
            path: path.resolve(__dirname, 'dist')
        },
        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery"
            }),
            new HtmlWebpackPlugin({ title: 'Tree-shaking' })
        ],
        /*module: {
            rules: [
                {
                    test: /\.js$/,
                    loader: 'babel-loader',
                    options: {
                        presets: [
                            [ 'es2015', { modules: false } ]
                        ]
                    }
                }
            ]
        }*/
    }
}
