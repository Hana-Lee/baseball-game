/**
 * @author Hana Lee
 * @since 2016-01-18 22:03
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

app.v_main_chat = (function () {
  'use strict';

  var stateMap = {
      container : null,
      player : null
    }, webixMap = {},
    _createView,
    initModule;

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
      id : 'main-chat',
      height : 240,
      css : 'main_chat',
      rows : [
        {
          view : 'list', id : 'chat', gravity : 3,
          url : stateMap.proxy, save : stateMap.proxy,
          type : {height : 'auto'},
          on : {
            onAfterAdd : function (id) {
              webix.delay(function () {
                this.showItem(id);
              }, this);
            },
            onDestruct : function () {
              console.log('destruct', arguments);
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
    webixMap.chat = $$('chat');
    webix.dp(webixMap.chat).ignore(function () {
      webixMap.chat.add({
        user : '시스템', value : '숫자 야구 게임에 오신걸 환영합니다 :-)'
      });
      webixMap.chat.add({
        user : '시스템', value : '건전한 채팅 문화는 우리의 미래 입니다 ㅋㅋㅋ'
      });
    });
  };

  initModule = function (container) {
    stateMap.container = container;
    stateMap.proxy = webix.proxy('stomp', '/chat');
    stateMap.proxy.clientId = app.utils.guid();
    stateMap.player = app.m_player.getInfo();
    _createView();
  };

  return {
    initModule : initModule
  };
}());