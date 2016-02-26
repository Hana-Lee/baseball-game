/**
 * @author Hana Lee
 * @since 2016-02-14 15:42
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
/*global $, app, webix, $$, Faye */

app.v_game_room_chat = (function () {
  'use strict';

  var stateMap = {
      container : null,
      player : null,
      game_room_info : null
    }, webixMap = {}, _createView,
    setGameRoom, initModule;

  _createView = function () {
    var user_name = stateMap.player.nickname, mainView;

    function send_message() {
      var text = $$('message').getValue();

      if (text) {
        webixMap.chat.add({
          user : user_name,
          value : text
        });
      }

      $$('message').setValue('');

      setTimeout(function () {
        $$('message').focus();
      }, 100);
    }

    function chat_template(obj) {
      var className;

      if (obj.user === '시스템') {
        className = 'system';
      } else if (obj.user !== user_name) {
        className = 'from';
      } else {
        className = 'to';
      }

      return '<div class="' + className + '"><span>' + obj.user + '</span>&nbsp;:&nbsp;' + obj.value + '</div>';
    }

    mainView = {
      id : 'game-room-chat',
      css : 'game_room_chat',
      height : 450,
      rows : [
        {
          view : 'list', id : 'game-room-chat-list', gravity : 3,
          url : stateMap.proxy, save : stateMap.proxy,
          type : {height : 'auto'},
          on : {
            onAfterAdd : function (id) {
              webix.delay(function () {
                this.showItem(id);
              }, this);
            }
          },
          template : chat_template
        },
        {
          cols : [
            {
              view : 'text', id : 'message', placeholder : '채팅 메세지를 입력해주세요', gravity : 3,
              on : {
                onAfterRender : function () {
                  webix.UIManager.setFocus(this);
                }
              }
            },
            {view : 'button', value : 'Send', click : send_message, hotkey : 'enter'}
          ]
        }
      ]
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.chat = $$('game-room-chat-list');
    webix.dp(webixMap.chat).ignore(function () {
      webixMap.chat.add({
        user : '시스템', value : '[' + stateMap.game_room_info.id + '번 ' + stateMap.game_room_info.name + '] 방에 입장 하셨습니다'
      });
      webixMap.chat.add({
        user : '시스템', value : '즐거운 게임 즐기시기 바랍니다'
      });
    });
  };

  setGameRoom = function (gameRoomInfo) {
    stateMap.game_room_info = gameRoomInfo;
  };

  initModule = function (container) {
    stateMap.container = container;

    stateMap.proxy = webix.proxy('stomp', '/chat/gameroom');
    stateMap.proxy.clientId = app.utils.guid();

    stateMap.player = app.m_player.getInfo();
    _createView();
  };

  return {
    initModule : initModule,
    setGameRoom : setGameRoom
  };
}());