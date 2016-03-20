/**
 * @author Hana Lee
 * @since 2016-01-17 21:37
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

app.v_main_menu = (function () {
  'use strict';

  var
    configMap = {
      height : 45,
      button_width : 200,
      player_model : {
        admin : false
      },
      game_room_list : []
    }, stateMap = {
      container : null
    }, webixMap = {},
    _createView, _getCreatedGameRoomList,
    _resetConfigMap, _resetStateMap, _resetWebixMap,
    showCreateGameRoomDialog, disableQuickBtn, enableQuickBtn, initModule;

  _resetConfigMap = function () {
    configMap.height = 45;
    configMap.button_width = 200;
    configMap.player_model = {};
    configMap.game_room_list = [];
  };

  _resetStateMap = function () {
    stateMap.container = null;
    stateMap = {};
  };

  _resetWebixMap = function () {
    webixMap = {};
  };

  _createView = function () {
    var isNotAdmin = function () {
      return configMap.player_model.admin !== true;
    }, getDynamicWidth = function () {
      return isNotAdmin() ? 609 : 409;
    }, isGameRoomsNotExist = function () {
      return configMap.game_room_list.length === 0;
    }, mainView;

    mainView = {
      id : 'main-menu', height : configMap.height,
      on : {
        onDestruct : function () {
          _resetConfigMap();
          _resetStateMap();
          _resetWebixMap();
        }
      },
      cols : [{
        id : 'make-room', view : 'button', label : '방만들기', type : 'danger', width : configMap.button_width,
        click : function () {
          showCreateGameRoomDialog();
        }
      }, {
        id : 'quick-join',
        view : 'button',
        label : '빠른입장',
        disabled : isGameRoomsNotExist(),
        width : configMap.button_width,
        click : function () {
          app.v_shell.joinRandomGameRoom(configMap.game_room_list);
        }
      }, {
        id : 'game-room-admin',
        hidden : isNotAdmin(),
        view : 'button',
        label : '관리',
        type : 'form',
        width : configMap.button_width
      }, {
        width : getDynamicWidth() - configMap.button_width
      }, {
        id : 'logout', view : 'button', label : '로그아웃', type : 'danger', width : configMap.button_width,
        click : function () {
          app.v_shell.logout();
        }
      }]
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('main-menu');
    webixMap.quick_join_btn = $$('quick-join');
  };

  showCreateGameRoomDialog = function () {
    webix.ui({
      view : 'window',
      id : 'create-game-room-window',
      head : '게임룸 생성',
      height : 400,
      width : 300,
      position : 'center',
      modal : true,
      body : {
        id : 'create-game-room-form',
        view : 'form',
        borderless : true,
        elements : [{
          id : 'game-room-name-field', view : 'text', label : '이름', name : 'name', invalidMessage : '이름을 입력 오류입니다',
          required : true,
          attributes : {
            minlength : 2
          },
          on : {
            onAfterRender : function () {
              webix.delay(function () {
                webixMap.game_room_name_field.focus();
              }, this);
            }
          }
        }, {
          view : 'richselect', label : '역할', name : 'gameRole', invalidMessage : '역할을 선택해주세요',
          value : app.const.gameRole.ATTACKER,
          options : [
            {id : app.const.gameRole.ATTACKER, value : '공격'},
            {id : app.const.gameRole.DEFENDER, value : '수비'}
          ]
        }, {
          view : 'fieldset', label : '게임 설정', name : 'setting',
          body : {
            rows : [{
              view : 'richselect',
              label : '숫자갯수',
              name : 'generationNumberCount',
              invalidMessage : '숫자 갯수를 정해주세요',
              value : 3,
              options : [
                {id : 2, value : '2 개'},
                {id : 3, value : '3 개'},
                {id : 4, value : '4 개'},
                {id : 5, value : '5 개'}
              ]
            }, {
              view : 'richselect',
              label : '입력횟수',
              name : 'limitGuessInputCount',
              invalidMessage : '입력 횟수를 정해주세요',
              value : 10,
              options : [
                {id : 1, value : '1 회'},
                {id : 5, value : '5 회'},
                {id : 10, value : '10 회'},
                {id : 15, value : '15 회'},
                {id : 20, value : '20 회'}
              ]
            }, {
              view : 'richselect',
              label : '입력오류횟수',
              name : 'limitWrongInputCount',
              invalidMessage : '입력 오류 횟수를 정해주세요',
              value : 5,
              options : [
                {id : 5, value : '5 회'},
                {id : 10, value : '10 회'},
                {id : 15, value : '15 회'},
                {id : 20, value : '20 회'}
              ]
            }]
          }
        }, {
          cols : [{
            view : 'button', value : '생성', type : 'form', hotkey : 'enter',
            click : function () {
              if ($$('create-game-room-form').validate()) {
                app.v_shell.createGameRoom($$('create-game-room-form').getValues());

                $$('create-game-room-window').close();
              } else {
                webix.message({type : 'error', text : '오류가 있습니다.'});
                webixMap.game_room_name_field.focus();
              }
            }
          }, {
            view : 'button', value : '닫기', type : 'danger', hotkey : 'esc',
            click : function () {
              this.getTopParentView().close();
            }
          }]
        }],
        rules : {
          name : function (value) {
            var result = false, invalidMessage;
            if (webix.rules.isEmpty(value)) {
              invalidMessage = '이름이 비었습니다';
            } else if (value.length < 2) {
              invalidMessage = '이름은 2글자 이상이어야 합니다';
            } else {
              result = true;
            }

            if (invalidMessage && !result) {
              webixMap.game_room_name_field.config.invalidMessage = invalidMessage;
            }

            return result;
          }
        },
        elementsConfig : {
          labelPosition : 'left'
        }
      }
    }).show();

    webixMap.game_room_name_field = $$('game-room-name-field');
  };

  _getCreatedGameRoomList = function (callback) {
    webix.ajax().get('gameroom/all', {
      error : function (text) {
        console.log(text);
        configMap.game_room_list = [];
        callback();
      },
      success : function (text) {
        var serverResponse = JSON.parse(text);
        configMap.game_room_list = serverResponse.content;
        callback();
      }
    });
  };

  disableQuickBtn = function () {
    webixMap.quick_join_btn.disable();
  };

  enableQuickBtn = function () {
    webixMap.quick_join_btn.enable();
  };

  initModule = function (container) {
    stateMap.container = container;
    configMap.player_model = app.m_player.getInfo();
    _getCreatedGameRoomList(_createView);
  };

  return {
    initModule : initModule,
    disableQuickBtn : disableQuickBtn,
    enableQuickBtn : enableQuickBtn
  };
}());