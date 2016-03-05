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
      settable_map : {
        height : null,
        button_width : null
      },
      height : 45,
      button_width : 200
    }, stateMap = {
      container : null
    }, webixMap = {}, _createView,
    configModule, initModule;

  _createView = function () {
    var mainView = [{
      id : 'game-room-menu', height : configMap.height, cols : [
        {
          id : 'exit-room', view : 'button', label : '방나가기', type : 'danger', width : configMap.button_width,
          on : {
            onItemClick : function () {
              var gameRoomModel = app.v_game_room.getGameRoomModel();
              if (gameRoomModel.players.length === 1) {
                if (gameRoomModel.players[0].email === app.m_player.getInfo().email) {
                  webix.callEvent('onLeaveAndGameRoomDelete', []);
                }
              } else {
                webix.callEvent('onLeaveGameRoom', []);
              }
            }
          }
        },
        {id : 'room-setting', view : 'button', label : '설정', width : configMap.button_width}
      ]
    }];

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('game-room-menu');
  };

  configModule = function (input_map) {
    app.utils.setConfigMap({
      input_map : input_map,
      settable_map : configMap.settable_map,
      config_map : configMap
    });
  };

  initModule = function (container) {
    stateMap.container = container;
    _createView();
  };

  return {
    initModule : initModule,
    configModule : configModule
  };
}());