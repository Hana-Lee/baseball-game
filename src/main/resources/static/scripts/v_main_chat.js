/**
 * @author Hana Lee
 * @since 2016-01-18 22:03
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix, $$, Faye */

app.v_main_chat = (function () {
  'use strict';

  var stateMap = {
      container: null,
      player: null
    }, webixMap = {},
    _createView,
    initModule;

  _createView = function () {
    var user_name = stateMap.player.nickname, mainView;

    function send_message() {
      var text = $$('message').getValue();

      if (text) {
        webixMap.chat.add({
          user: user_name,
          value: text
        });
      }

      $$('message').setValue('');

      setTimeout(function () {
        $$('message').focus();
      }, 100);
    }

    function chat_template(obj) {
      console.log(obj);
      var template;
      if (obj.user !== user_name) {
        template = '<div class="from"><span>' + obj.user + '</span>: ' + obj.value + '</div>';
      } else {
        template = '<div class="to"><span>' + obj.user + '</span>: ' + obj.value + '</div>';
      }

      return template;
    }

    mainView = {
      id: 'main-chat',
      height: 240,
      css: 'main_chat',
      rows: [
        {
          view: 'list', id: 'chat', gravity: 3,
          url: 'stomp->/chat', save: 'stomp->/chat',
          type: {height: 'auto'},
          on: {
            onAfterAdd: function (id) {
              webix.delay(function () {
                this.showItem(id);
              }, this);
            },
            onDestruct: function() {
              console.log('destruct', arguments);
            }
          },
          template: chat_template
        },
        {
          cols: [
            {
              view: 'text', id: 'message', placeholder: '채팅 메세지를 입력해주세요', gravity: 3,
              on: {
                onAfterRender: function () {
                  webix.UIManager.setFocus(this);
                }
              }
            },
            {view: 'button', value: 'Send', click: send_message, hotkey: 'enter'}
          ]
        }
      ]
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.chat = $$('chat');
    webix.dp(webixMap.chat).ignore(function(){
      webixMap.chat.add({
        user:"System", value:"Welcome to chat :)"
      });
      webixMap.chat.add({
        user:"System", value:"Use '/nick Name' to set a name"
      });
    });
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