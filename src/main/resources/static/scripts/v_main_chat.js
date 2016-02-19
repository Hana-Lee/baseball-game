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
      container: null
    }, webixMap = {},
    _createView,
    initModule;

  _createView = function () {
    webix.proxy.faye.client = new Faye.Client('//localhost:8000/');
    webix.proxy.faye.clientId = webix.uid();

    var user_name = '이하나', mainView;

    function send_message() {
      console.log(arguments);
      var text = $$('message').getValue();

      if (text) {
        if (text.indexOf('/nick ') === 0) {
          user_name = text.substr(6);

        } else {
          $$('chat').add({
            user: user_name,
            value: text
          });
        }
      }

      $$('message').setValue('');

      setTimeout(function () {
        $$('message').focus();
      }, 100);
    }

    function chat_template(obj) {
      return '<span style="font-weight:bold;">' + obj.user + '</span>: ' + obj.value;
    }

    mainView = {
      id: 'main-chat',
      height: 240,
      rows: [
        //{template: 'Webix Based Chat', type: 'header'},
        {
          view: 'list', id: 'chat', gravity: 3,
          //url: 'faye->/data', save: 'faye->/data',
          type: {height: 'auto'},
          on: {
            onAfterAdd: function (id) {
              webix.delay(function () {
                this.showItem(id);
              }, this);
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
  };

  initModule = function (container) {
    stateMap.container = container;
    _createView();
  };

  return {
    initModule: initModule
  };
}());