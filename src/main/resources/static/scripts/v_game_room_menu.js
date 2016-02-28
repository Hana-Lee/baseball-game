/**
 * @author Hana Lee
 * @since 2016-01-19 19:56
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

app.v_game_room_menu = (function () {
  'use strict';

  var configMap = {
      height : 45,
      button_width : 200
    }, stateMap = {
      container : null
    }, webixMap = {}, _createView,
    initModule;

  _createView = function () {
    var mainView = [{
      id : 'game-room-menu', height : configMap.height, cols : [
        {
          id : 'exit-room', view : 'button', label : '방나가기', type : 'danger', width : configMap.button_width,
          on : {
            onItemClick : function () {
              webix.callEvent('onLeaveGameRoom', []);
            }
          }
        },
        {id : 'room-setting', view : 'button', label : '설정', width : configMap.button_width}
      ]
    }];

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('game-room-menu');
  };

  initModule = function (container) {
    stateMap.container = container;
    _createView();
  };

  return {
    initModule : initModule
  };
}());