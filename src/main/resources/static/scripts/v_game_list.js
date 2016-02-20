/**
 * @author Hana Lee
 * @since 2016-01-17 22:33
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_game_list = (function () {
  'use strict';

  var stateMap = {
      container: null
    }, webixMap = {
      gameRoomList: []
    },
    _createView, _getCreatedGameRoomList,
    _createTemplate, _createTitleTemplate, _createJoinButtonTemplate, _createSettingTemplate,
    initModule;

  _createView = function () {
    /**
     * data array
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
    console.log('create view', stateMap.gameRoomList);
    var mainView = {
      id: 'game-room-list',
      view: 'dataview',
      select: true,
      css: 'game_room_list',
      type: {
        height: 128,
        //width: 215,
        templateStart: '<div class="custom_item">',
        template: _createTemplate,
        templateEnd: '</div>'
      },
      data: stateMap.gameRoomList,
      ready: function () {
        $('.join_room').click(function (evt) {
          var target = evt.target, roomId;
          roomId = target.getAttribute('data-room-id');
          app.v_shell.showGameRoom(roomId);
        });
      }
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
  };

  _getCreatedGameRoomList = function (callback) {
    webix.ajax().get('gameroom/all', {
      error: function (text) {
        console.log(text);
        stateMap.gameRoomList = [];
        callback();
      },
      success: function (text) {
        var serverResponse = JSON.parse(text);
        stateMap.gameRoomList = serverResponse.content;
        console.log('success', stateMap.gameRoomList);
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
    return '<div class="webix_strong title ' + status + '">[' + obj.id + '] ' + obj.name + ' (' + userCount + '/5)</div>';
  };

  _createJoinButtonTemplate = function (obj) {
    var disabledValue = '', disabledViewValue = '', disabledBoxValue = '', disabledElem = '';
    if (obj.status.toLowerCase() === 'running') {
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
    var guessNum = obj.setting.limitGuessInputCount, genNum = obj.setting.generationNumberCount, wrongNum = obj.setting.limitWrongInputCount;
    return '<fieldset class="setting"><legend>설정</legend>' +
      '<div>입력: ' + guessNum + ', 갯수: ' + genNum + ', 오류: ' + wrongNum + '</div>' +
      '</fieldset>';
  };

  initModule = function (container) {
    stateMap.container = container;
    _getCreatedGameRoomList(_createView);
  };

  return {
    initModule: initModule
  };
}());