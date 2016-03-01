/**
 * @author Hana Lee
 * @since 2016-01-16 19:35
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
/*global $, app, webix, $$, btoa */

app.v_login = (function () {
  'use strict';

  var configMap = {
      width : 300
    },
    stateMap = {
      container : '',
      loginCompleted : false,
      logoutCompleted : true
    },
    initModule;

  initModule = function (container) {
    stateMap.container = container;

    webix.ui({
      id : 'login-container',
      type : 'space',
      css : 'login_container',
      container : stateMap.container,
      borderless : true,
      rows : [
        {
          type : 'header',
          template : '로그인'
        },
        {
          id : 'login-form',
          view : 'form',
          width : configMap.width,
          hidden : false,
          scroll : false,
          elements : [
            {
              id : 'email', view : 'text', type : 'email', label : '이메일', name : 'email', required : true,
              invalidMessage : '',
              on : {
                onAfterRender : function () {
                  $$('email').focus();
                }
              }
            },
            {
              id : 'password',
              view : 'text',
              type : 'password',
              label : '비밀번호',
              name : 'password',
              invalidMessage : '',
              required : true
            },
            {
              margin : 5,
              cols : [
                {
                  view : 'button', value : '로그인', type : 'form', hotkey : 'enter',
                  click : function () {
                    if ($$('login-form').validate()) {
                      webix.ajax().headers({
                        //'Authorization': 'Basic ' + btoa($$('email').getValue() + ':' + $$('password').getValue())
                      }).post('login',
                        $$('login-form').getValues(),
                        {
                          error : function (text/*, data, XmlHttpRequest */) {
                            var textJson = JSON.parse(text);
                            webix.alert({
                              title : '오류',
                              ok : '확인',
                              text : textJson.message
                            });
                          },
                          success : function (/*text, data, XmlHttpRequest */) {
                            app.v_shell.showMainBoard('login-container', $$('email').getValue());
                          }
                        }
                      );
                    }
                  }
                },
                {
                  view : 'button', value : '가입',
                  click : function () {
                    app.v_shell.showSignUp();
                  }
                }
              ]
            }
          ],
          rules : {
            $obj : function (data) {
              var emailKey = 'email', passwordKey = 'password', message;
              if (!webix.rules.isNotEmpty(data[emailKey])) {
                message = '이메일 주소가 비어있습니다';
                $$(emailKey).config.invalidMessage = message;
              } else if (!webix.rules.isEmail(data[emailKey])) {
                message = '이메일 주소가 잘못 입력되었습니다';
                $$(emailKey).config.invalidMessage = message;
              } else if (!webix.rules.isNotEmpty(data[passwordKey])) {
                message = '비밀번호가 비어있습니다';
                $$(passwordKey).config.invalidMessage = message;
              }

              if (message) {
                webix.message(message);
                return false;
              }
              return true;
            }
          }
        }
      ]
    });
  };

  return {
    initModule : initModule
  };
}());