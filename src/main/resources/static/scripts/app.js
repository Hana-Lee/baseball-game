/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
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
/*global $, app:true, webix */

var app = (function () {
  'use strict';
  var stateMap = {
    container : null
  }, initModule, _registerStompProxy;

  _registerStompProxy = function () {
    webix.proxy.stomp = {
      $proxy : true,
      game_room : null,
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

          if (update.clientId === selfId) {
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
      save : function (view, update/*, dp, callback*/) {
        if (view === undefined || view === null) {
          return;
        }

        if (this.source.indexOf('chat') !== -1) {
          delete update.data.id;
        }

        if (this.game_room) {
          update.data.gameRoom = this.game_room;
        }

        update.clientId = this.clientId;
        update.data.email = app.m_player.getInfo().email;
        var header = {}, sendUrl = '/app' + this.source;
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
    initModule : initModule
  };
}());