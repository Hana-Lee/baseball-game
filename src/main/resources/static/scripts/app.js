/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true, unparam : true
 */
/*global $, app:true, webix */

var app;
app = (function () {
  'use strict';
  var stateMap = {
    container : null
  }, initModule, _registerStompProxy;

  _registerStompProxy = function () {
    webix.proxy.stomp = {
      $proxy : true,
      init : function () {
        this.clientId = this.clientId || webix.uid();
      },
      load : function (view) {
        var selfId = this.clientId, subscribeUrl = '/topic' + this.source,
          headers = {
            id : 'sub-' + this.clientId
          }, subscribeObj;

        subscribeObj = app.v_shell.getStompClient().subscribe(subscribeUrl, function (response) {
          var update = {};
          update = JSON.parse(response.body);
          console.log('update', update);
          console.log(update.clientId, selfId);
          if (update.clientId === selfId.toString()) {
            return;
          }

          webix.dp(view).ignore(function () {
            if (update.operation === 'delete') {
              view.remove(update.data.id);
            } else if (update.operation === 'insert') {
              view.add(update.data);
            } else if (update.operation === 'update') {
              var item = view.getItem(update.data.id);
              if (item) {
                webix.extend(item, update.data, true);
                view.refresh(item.id);
              }
            }
          });
        }, headers);

        view.attachEvent('onDestruct', function () {
          subscribeObj.unsubscribe();
        });
      },
      unload : function () {
        app.v_shell.getStompClient().unsubscribe('sub-' + this.clientId);
      },
      save : function (view, update/*, dp, callback*/) {
        update.clientId = this.clientId;
        update.data.email = app.m_player.getInfo().email;
        var header = {}, sendUrl = "/app" + this.source;
        app.v_shell.getStompClient().send(sendUrl, header, JSON.stringify(update));
      }
    };
  };

  initModule = function (container) {
    stateMap.container = container;

    _registerStompProxy();

    app.v_shell.initModule(stateMap.container);
  };

  return {
    initModule : initModule,
    stateMap : stateMap
  };
}());