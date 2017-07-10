/**
 * @author Hana Lee
 * @since 2016-02-14 15:42
 */
app.v_chat = (function () {
  'use strict';

  var
    configMap = {
      settableMap : {
        chat_height : 0,
        chat_list_height : 0,
        proxy_name : null,
        data_url : null,
        system_message_list : null
      },
      chat_height : 240,
      chat_list_height : 'auto',
      proxy_name : 'stomp',
      data_url : '/chat',
      system_message_list : [
        '숫자 야구 게임에 오신걸 환영합니다 :-)',
        '건전한 채팅 문화는 우리의 미래 입니다 ㅋㅋㅋ'
      ],

      system_nickname : '시스템'
    },
    stateMap = {
      container : null,
      proxy : null
    }, webixMap = {},
    _sendMessage, _chatTemplate, _createView,
    _resetWebixMap, _resetConfigMap, _resetStateMap,
    configModule, initModule;

  _resetWebixMap = function () {
    webixMap = {};
  };

  _resetConfigMap = function () {
    configMap.chat_height = 200;
    configMap.chat_list_height = 'auto';
    configMap.proxy_name = 'stomp';
    configMap.data_url = '/chat';
    configMap.system_message_list = [
      '숫자 야구 게임에 오신걸 환영합니다 :-)',
      '건전한 채팅 문화는 우리의 미래 입니다 ㅋㅋㅋ'
    ];
    configMap.system_nickname = '시스템';
  };

  _resetStateMap = function () {
    stateMap.proxy.game_room = undefined;

    stateMap = {};
    stateMap.container = null;
    stateMap.proxy = null;
  };

  _sendMessage = function () {
    var text = webixMap.chat_message.getValue();

    if (text) {
      webixMap.chat_list.add({
        player : app.model.getPlayer(),
        message : text
      });
    }

    webixMap.chat_message.setValue('');

    setTimeout(function () {
      if (webixMap.chat_message && webixMap.chat_message.focus) {
        webixMap.chat_message.focus();
      }
    }, 100);
  };

  _chatTemplate = function (obj) {
    var className;

    if (obj.nickname === configMap.system_nickname) {
      className = 'system';
    } else if (obj.player.nickname !== app.model.getPlayer().nickname) {
      className = 'from';
    } else {
      className = 'to';
    }

    return '<div class="' + className + '"><span>' + obj.player.nickname + '</span>&nbsp;:&nbsp;' + obj.message + '</div>';
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
          view : 'text',
          id : 'chat-message',
          placeholder : '채팅 메세지를 입력해주세요',
          gravity : 3,
          on : {
            onAfterRender : function () {
              webix.UIManager.setFocus(this);
            }
          }
        }, {
          view : 'button', value : '전송', click : _sendMessage, hotkey : 'enter'
        }]
      }],
      on : {
        onDestruct : function () {
          _resetWebixMap();
          _resetConfigMap();
          _resetStateMap();
        }
      }
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('chat');
    webixMap.chat_list = $$('chat-list');
    webixMap.chat_message = $$('chat-message');

    webix.dp(webixMap.chat_list).ignore(function () {
      configMap.system_message_list.forEach(function (message) {
        webixMap.chat_list.add({
          player : {nickname : configMap.system_nickname}, message : message
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
    stateMap.proxy.clientId = app.utils.guid();

    if (app.v_game_room && app.model.getGameRoom()) {
      stateMap.proxy.game_room = app.model.getGameRoom();
    }

    _createView();
  };

  return {
    initModule : initModule,
    configModule : configModule
  };
}());