/**
 * @author Hana Lee
 * @since 2016-01-21 18:21
 */
/*jslint
 browser  : true,
 continue : true,
 devel    : true,
 indent   : 2,
 maxerr   : 100,
 nomen    : true,
 plusplus : true,
 regexp   : true,
 vars     : false,
 white    : true,
 todo     : true
 */
/*global $, app, webix, $$ */

app.v_theme_selector = (function () {
  'use strict';

  var configMap = {
      themes : {
        flat : 'bower_components/webix/codebase/skins/flat.css',
        compact : 'bower_components/webix/codebase/skins/compact.css',
        air : 'bower_components/webix/codebase/skins/air.css',
        aircompact : 'bower_components/webix/codebase/skins/aircompact.css',
        clouds : 'bower_components/webix/codebase/skins/clouds.css',
        glamour : 'bower_components/webix/codebase/skins/glamour.css',
        light : 'bower_components/webix/codebase/skins/light.css',
        metro : 'bower_components/webix/codebase/skins/metro.css',
        terrace : 'bower_components/webix/codebase/skins/terrace.css',
        touch : 'bower_components/webix/codebase/skins/touch.css',
        web : 'bower_components/webix/codebase/skins/web.css'
      }
    }, stateMap = {
      selectedThemeId : 'aircompact'
    }, view,
    initModule, getView;

  getView = function () {
    return view;
  };

  initModule = function () {
    view = {
      id : 'theme-selector',
      view : 'combo',
      label : '테마',
      labelWidth : 60,
      value : stateMap.selectedThemeId,
      width : 200,
      placeholder : '테마선택',
      options : [
        {id : 'flat', value : 'Flat'},
        {id : 'compact', value : 'Compact'},
        {id : 'air', value : 'Air'},
        {id : 'aircompact', value : 'Air Compact'},
        {id : 'clouds', value : 'Clouds'},
        {id : 'glamour', value : 'Glamour'},
        {id : 'light', value : 'Light'},
        {id : 'metro', value : 'Metro'},
        {id : 'terrace', value : 'Terrace'},
        {id : 'touch', value : 'Touch'},
        {id : 'web', value : 'Web'}
      ],
      on : {
        onChange : function (newId, oldId) {
          var oldThemeStyleLinkEl, newThemeStyleLinkEl;
          if (newId !== oldId) {
            stateMap.selectedThemeId = newId;

            oldThemeStyleLinkEl = $('link[rel="stylesheet"]')[0];

            newThemeStyleLinkEl = document.createElement('link');
            newThemeStyleLinkEl.setAttribute('rel', 'stylesheet');
            newThemeStyleLinkEl.setAttribute('type', 'text/css');
            newThemeStyleLinkEl.setAttribute('href', configMap.themes[newId]);

            $('head')[0].replaceChild(newThemeStyleLinkEl, oldThemeStyleLinkEl);
          }
        }
      }
    };
  };

  return {
    initModule : initModule,
    getView : getView
  };
}());