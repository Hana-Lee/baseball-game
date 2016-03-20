/**
 * @author Hana Lee
 * @since 2016-01-17 22:33
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

app.v_game_list = (function () {
  'use strict';

  var
    configMap = {
      height : 128
    },
    stateMap = {
      container : null
    }, webixMap = {
    },
    _createView, _getCreatedGameRoomList,
    _createTemplate, _createTitleTemplate, _createJoinButtonTemplate, _createSettingTemplate,
    _showGameRoleSelectWindow, _resetConfigMap, _resetStateMap, _resetWebixMap,
    initModule;

  _resetConfigMap = function () {
    configMap.height = 128;
  };

  _resetStateMap = function () {
    stateMap.game_room_list = null;
    stateMap.proxy = null;
    stateMap.container = null;
    stateMap = {};
  };

  _resetWebixMap = function () {
    webixMap = {};
  };

  _createView = function () {
    /**
     * data structure
     * {
     *  id: 1,
     *  name: '루비',
     *  state: 'normal or running',
     *  owner: { id: 1, nickname: '이하나', email: 'i@leehana.co.kr'....},
     *  setting: {
     *    limitGuessInputCount: 10,
     *    generationNumberCount: 5,
     *    limitWrongInputCount: 5
     *  }, ......
     * }
     */
    var mainView = {
      id : 'game-room-list',
      view : 'dataview',
      select : false,
      css : 'game_room_list',
      type : {
        height : configMap.height,
        templateStart : '<div class="custom_item">',
        template : _createTemplate,
        templateEnd : '</div>'
      },
      data : stateMap.game_room_list,
      url : stateMap.proxy,
      on : {
        onAfterAdd : function (id) {
          if (!id) {
            return;
          }

          app.v_main_menu.enableQuickBtn();
        },
        onAfterDelete : function (id) {
          if (!id) {
            return;
          }

          if (this.data.count() === 0) {
            app.v_main_menu.disableQuickBtn();
          }
        },
        onAfterRender : function () {
          $('.join_room').click(function (evt) {
            evt.preventDefault();
            var target = evt.target, roomId, selectedRoom;
            roomId = target.getAttribute('data-room-id');
            selectedRoom = webixMap.main_view.getItem(roomId);
            _showGameRoleSelectWindow(selectedRoom);
          });
        },
        onDestruct : function () {
          _resetConfigMap();
          _resetStateMap();
          _resetWebixMap();
        }
      }
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('game-room-list');
  };

  _showGameRoleSelectWindow = function (selectedGameRoom) {
    webix.ui({
      id : 'game-role-select-window',
      view : 'window',
      head : '게임 역할 선택',
      width : 200,
      position : 'center',
      modal : true,
      body : {
        id : 'game-role-select-form',
        view : 'form',
        elements : [{
          id : 'role-selector',
          view : 'richselect', label : '역할', value : app.const.gameRole.ATTACKER,
          labelAlign : 'center',
          options : [{
            id : app.const.gameRole.ATTACKER, value : '공격'
          }, {
            id : app.const.gameRole.DEFENDER, value : '수비'
          }]
        }, {
          cols : [{
            view : 'button', value : '확인', hotkey : 'enter', type : 'form',
            click : function () {
              app.v_shell.joinGameRoom(selectedGameRoom, $$('role-selector').getValue());
              this.getTopParentView().close();
            }
          }, {
            view : 'button', value : '취소', hotkey : 'esc', type : 'danger',
            click : function () {
              this.getTopParentView().close();
            }
          }]
        }]
      }
    }).show();
  };

  _getCreatedGameRoomList = function (callback) {
    webix.ajax().get('gameroom/all', {
      error : function (text) {
        console.log(text);
        stateMap.game_room_list = [];
        callback();
      },
      success : function (text) {
        var serverResponse = JSON.parse(text);
        stateMap.game_room_list = serverResponse.content;
        callback();
      }
    });
  };

  _createTemplate = function (obj) {
    return _createTitleTemplate(obj) +
      '<div class="owner">방장: ' + obj.owner.nickname +
      _createJoinButtonTemplate(obj) +
      _createSettingTemplate(obj);
  };

  _createTitleTemplate = function (obj) {
    var userCount = obj.players.length, status = obj.status.toLowerCase();
    return '<div class="webix_strong title ' + status + '">[' + obj.roomNumber + '] ' + obj.name + ' (' + userCount + '/5)</div>';
  };

  _createJoinButtonTemplate = function (obj) {
    var disabledValue = '', disabledViewValue = '', disabledBoxValue = '', disabledElem = '';
    if (obj.status.toUpperCase() === app.const.status.RUNNING) {
      disabledViewValue = ' webix_disabled_view';
      disabledBoxValue = ' webix_disabled_box';
      disabledElem = '<div class="webix_disabled"></div>';
      disabledValue = ' disabled';
    }

    return '<div class="webix_view webix_control webix_el_button join_button' + disabledViewValue + '" style="width: 40px;">' +
      '<div class="webix_el_box' + disabledBoxValue + '">' +
      '<button type="button" class="join_room webixtype_base" data-room-id="' + obj.id + '"' + disabledValue + '>입장</button>' +
      '</div>' +
      disabledElem +
      '</div>' +
      '</div>';
  };

  _createSettingTemplate = function (obj) {
    var guessNum = obj.setting.limitGuessInputCount, genNum = obj.setting.generationNumberCount,
      wrongNum = obj.setting.limitWrongInputCount;
    return '<fieldset class="setting"><legend>설정</legend>' +
      '<div>입력: ' + guessNum + ', 갯수: ' + genNum + ', 오류: ' + wrongNum + '</div>' +
      '</fieldset>';
  };

  initModule = function (container) {
    stateMap.container = container;

    stateMap.proxy = webix.proxy('stomp', '/gameroom/list/updated');
    stateMap.proxy.clientId = app.utils.guid();

    _getCreatedGameRoomList(_createView);
  };

  return {
    initModule : initModule
  };
}());