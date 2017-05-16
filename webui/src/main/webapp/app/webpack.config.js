const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ChunkManifestPlugin = require("chunk-manifest-webpack-plugin");
const WebpackChunkHash = require("webpack-chunk-hash");
const InlineManifestWebpackPlugin = require("inline-manifest-webpack-plugin");

module.exports = function(env) {
    return {
        entry: {
            app: ['./app.js'],
            vendor: [
                'angular',
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
            ],
            init: './init.js'
        },
        module: {
            rules: [
                {
                    test: /\.css$/,
                    use: [ 'style-loader', 'css-loader' ]
                }
            ]
        },
        output: {
            filename: "[name].[chunkhash].js",
            chunkFilename: "[name].[chunkhash].js",
            path: path.resolve(__dirname, 'dist'),
            publicPath: '/app/dist/'
        },
        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery"
            }),
            new webpack.optimize.CommonsChunkPlugin({
                name: ["vendor", "manifest"], // vendor libs + extracted manifest
                minChunks: Infinity,
            }),
            new webpack.HashedModuleIdsPlugin(),
            new webpack.optimize.UglifyJsPlugin({
                compress: {
                    warnings: false,
                    screw_ie8: true,
                    conditionals: true,
                    unused: true,
                    comparisons: true,
                    sequences: true,
                    dead_code: true,
                    evaluate: true,
                    if_return: true,
                    join_vars: true,
                },
                output: {
                  comments: false,
                }
            }),
            new InlineManifestWebpackPlugin({
                name: 'webpackManifest'
            }),
            new HtmlWebpackPlugin({
                template: './index.ejs',
                filename: '../../index.html',
                inject: 'body'
            }),
            new HtmlWebpackPlugin({
                template: './app.ejs',
                filename: '../../app.html',
                inject: 'body'
            })
        ],
    }
}
