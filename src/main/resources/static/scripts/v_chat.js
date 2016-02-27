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

app.v_chat = (function () {
  'use strict';

  var
    configMap = {
      settableMap : {
        chat_height : 0,
        chat_list_height : 0,
        proxy_name : null,
        data_url : null,
        system_message_list : null,
        player_model : null
      },
      chat_height : 240,
      chat_list_height : 'auto',
      proxy_name : 'stomp',
      data_url : '/chat',
      system_message_list : [
        '숫자 야구 게임에 오신걸 환영합니다 :-)',
        '건전한 채팅 문화는 우리의 미래 입니다 ㅋㅋㅋ'
      ],
      player_model : {},

      system_nickname : '시스템'
    },
    stateMap = {
      container : null
    }, webixMap = {},
    _sendMessage, _chatTemplate, _createView,
    configModule, initModule;

  _sendMessage = function () {
    var text = webixMap.chat_message.getValue();

    if (text) {
      webixMap.chat_list.add({
        user : configMap.player_model.nickname,
        value : text
      });
    }

    webixMap.chat_message.setValue('');

    setTimeout(function () {
      webixMap.chat_message.focus();
    }, 100);
  };

  _chatTemplate = function (obj) {
    var className;

    if (obj.user === configMap.system_nickname) {
      className = 'system';
    } else if (obj.user !== configMap.player_model.nickname) {
      className = 'from';
    } else {
      className = 'to';
    }

    return '<div class="' + className + '"><span>' + obj.user + '</span>&nbsp;:&nbsp;' + obj.value + '</div>';
  };

  _createView = function () {
    var mainView;

    mainView = {
      id : 'chat',
      css : 'chat',
      height : configMap.chat_height,
      rows : [{
        view : 'list', id : 'chat-list', gravity : 3,
        url : stateMap.proxy, save : stateMap.proxy,
        type : {height : configMap.chat_list_height},
        on : {
          onAfterAdd : function (id) {
            webix.delay(function () {
              this.showItem(id);
            }, this);
          }
        },
        template : _chatTemplate
      }, {
        cols : [{
          view : 'text', id : 'chat-message', placeholder : '채팅 메세지를 입력해주세요', gravity : 3,
          on : {
            onAfterRender : function () {
              webix.UIManager.setFocus(this);
            }
          }
        }, {
          view : 'button', value : '전송', click : _sendMessage, hotkey : 'enter'
        }]
      }]
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('chat');
    webixMap.chat_list = $$('chat-list');
    webixMap.chat_message = $$('chat-message');

    webix.dp(webixMap.chat_list).ignore(function () {
      configMap.system_message_list.forEach(function (message) {
        webixMap.chat_list.add({
          user : configMap.system_nickname, value : message
        });
      });
    });
  };

  configModule = function (input_map) {
    app.utils.setConfigMap({
      input_map : input_map,
      settable_map : configMap.settableMap,
      config_map : configMap
    });
  };

  initModule = function (container) {
    stateMap.container = container;

    stateMap.proxy = webix.proxy(configMap.proxy_name, configMap.data_url);
    //stateMap.proxy = webix.proxy('stomp', '/chat/gameroom');
    stateMap.proxy.clientId = app.utils.guid();

    stateMap.player = app.m_player.getInfo();
    _createView();
  };

  return {
    initModule : initModule,
    configModule : configModule
  };
}());