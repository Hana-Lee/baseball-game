/**
 * @author Hana Lee
 * @since 2016-01-17 21:37
 */
/*jslint        browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true, todo    : true
 */
/*global $, app, webix, $$ */

app.v_main_menu = (function () {
  'use strict';

  var configMap = {
    height: 45,
    button_width: 200
  }, stateMap = {
    container: null,
    player: {
      admin: false
    }
  }, webixMap = {
    player: null
  },
    _createView, _sendGameRoomDataToServer,
    showCreateGameRoomDialog, getCreateFormView, initModule;

  _createView = function () {
    console.log(stateMap.player);
    var isNotAdmin = function () {
      return stateMap.player.admin !== true;
    }, getDynamicWidth = function () {
      return isNotAdmin() ? 609 : 409;
    }, mainView;

    mainView = {
      id: 'main-menu', height: configMap.height, cols: [
        {
          id: 'make-room', view: 'button', label: '방만들기', type: 'danger', width: configMap.button_width,
          click: function () {
            showCreateGameRoomDialog();
          }
        },
        {id: 'quick-join', view: 'button', label: '빠른입장', disabled: true, width: configMap.button_width},
        {
          id: 'game-room-admin',
          hidden: isNotAdmin(),
          view: 'button',
          label: '관리',
          type: 'form',
          width: configMap.button_width
        },
        {
          width: getDynamicWidth() - configMap.button_width
        },
        {
          id: 'logout', view: 'button', label: '로그아웃', type: 'danger', width: configMap.button_width,
          click: function () {
            app.v_shell.logout();
          }
        }
        //app.v_theme_selector.getView()
      ]
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
  };

  _sendGameRoomDataToServer = function (data) {
    var sendData = {
      name: data.name,
      gameRole: data.gameRole,
      setting: {
        generationNumberCount: data.generationNumberCount,
        limitGuessInputCount: data.limitGuessInputCount,
        limitWrongInputCount: data.limitWrongInputCount
      }
    };

    sendData = JSON.stringify(sendData);

    webix.ajax().headers({
      'Content-Type': 'application/json'
    }).post('gameroom', sendData, {
      error: function(text) {
        console.log(text);
        var textJson = JSON.parse(text);
        webix.alert({
          title: '오류',
          ok: '확인',
          text: textJson.message
        });
      },
      success: function(text) {
        console.log(text);
      }
    });
  };

  showCreateGameRoomDialog = function () {
    webix.ui({
      view: 'window',
      id: 'create-game-room-window',
      head: '게임룸 생성',
      height: 400,
      width: 300,
      position: 'center',
      modal: true,
      body: getCreateFormView()
    }).show();
  };

  getCreateFormView = function () {
    return {
      id: 'create-game-room-form',
      view: 'form',
      borderless: true,
      elements: [
        {view: 'text', label: '이름', name: 'name', invalidMessage: '이름을 입력해주세요'},
        {
          view: 'richselect', label: '역할', name: 'gameRole', invalidMessage: '역할을 선택해주세요', value: 'ATTACKER',
          options: [
            {id: 'ATTACKER', value: '공격'},
            {id: 'DEFENDER', value: '수비'}
          ]
        },
        {
          view: 'fieldset', label: '게임 설정', name: 'setting',
          body: {
            rows: [
              {
                view: 'richselect',
                label: '숫자갯수',
                name: 'generationNumberCount',
                invalidMessage: '숫자 갯수를 정해주세요',
                value: 3,
                options: [
                  {id: 2, value: '2 개'},
                  {id: 3, value: '3 개'},
                  {id: 4, value: '4 개'},
                  {id: 5, value: '5 개'}
                ]
              },
              {
                view: 'richselect',
                label: '입력횟수',
                name: 'limitGuessInputCount',
                invalidMessage: '입력 횟수를 정해주세요',
                value: 10,
                options: [
                  {id: 1, value: '1 회'},
                  {id: 5, value: '5 회'},
                  {id: 10, value: '10 회'},
                  {id: 15, value: '15 회'},
                  {id: 20, value: '20 회'}
                ]
              },
              {
                view: 'richselect',
                label: '입력오류횟수',
                name: 'limitWrongInputCount',
                invalidMessage: '입력 오류 횟수를 정해주세요',
                value: 5,
                options: [
                  {id: 5, value: '5 회'},
                  {id: 10, value: '10 회'},
                  {id: 15, value: '15 회'},
                  {id: 20, value: '20 회'}
                ]
              }
            ]
          }
        },
        {
          cols: [
            {
              view: 'button', value: '생성', type: 'form', hotkey: 'enter',
              click: function () {
                if ($$('create-game-room-form').validate()) { //validate form
                  _sendGameRoomDataToServer($$('create-game-room-form').getValues());
                  this.getTopParentView().close();
                  app.v_shell.showGameRoom();
                } else {
                  webix.message({type: 'error', text: 'Form data is invalid'});
                }
              }
            },
            {
              view: 'button', value: '닫기', type: 'danger', hotkey: 'esc',
              click: function () {
                this.getTopParentView().close();
              }
            }
          ]
        }
      ],
      rules: {
        name: webix.rules.isNotEmpty
      },
      elementsConfig: {
        labelPosition: 'left'
      }
    };
  };

  initModule = function (container) {
    stateMap.container = container;
    stateMap.player = app.m_player.getInfo();
    _createView();
  };

  return {
    initModule: initModule
  };
}());