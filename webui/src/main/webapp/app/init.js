// Stuff that can't wait
loadCSS('app/node_modules/angular-material/angular-material.min.css');
loadCSS('app/node_modules/angular-material-data-table/dist/md-data-table.min.css');
loadCSS('app/node_modules/tour/dist/tour.css')
loadCSS('/static/assets/css/main.css');
loadCSS('/static/assets/css/editor.css');
loadCSS('app/node_modules/roboto-fontface/css/roboto/roboto-fontface.css');
loadCSS('/static/assets/fonts/fonts.css');
loadCSS('https://fonts.googleapis.com/icon?family=Material+Icons');

// Just enough timeout to avoid the jitter
setTimeout(function(){
    angular.bootstrap(document, ['fonoster']);
}, 500);



