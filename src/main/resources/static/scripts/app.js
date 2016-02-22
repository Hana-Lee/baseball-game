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
/*global $, app:true, webix, SockJS, Stomp */

var app = (function () {
  'use strict';
  var stateMap = {
    container: null
  }, initModule, _registerStompProxy, _initStompClient;

  _registerStompProxy = function () {
    webix.proxy.stomp = {
      $proxy: true,
      init: function () {
        this.clientId = this.clientId || webix.uid();
      },
      load: function (view) {
        var selfId = this.clientId, header = {};

        stateMap.stomp_client.subscribe(this.source, function (response) {
          var update = {};
          update = JSON.parse(response.body);
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
        }, header);
      },
      save: function (view, update, dp, callback) {
        update.clientId = this.clientId;
        var header = {};
        stateMap.stomp_client.send(this.source, header, JSON.stringify(update));
      }
    };
  };

  _initStompClient = function() {
    var api_socket = new SockJS('/bbg/sock'), login = '', passcode = '';
    stateMap.stomp_client = Stomp.over(api_socket);
    stateMap.stomp_client.connect(login, passcode,
      function (frame) {
        // connect 완료 시 error subscribe, global 에러 처리.
        //console.log(frame);
        app.v_shell.initModule(stateMap.container);
      },
      function (error) {
        console.log(error);
      }
    );
    webix.proxy.stomp.client = stateMap.stomp_client;
    webix.proxy.stomp.clientId = webix.uid();
  };

  initModule = function (container) {
    stateMap.container = container;

    _registerStompProxy();
    _initStompClient();

  };

  return {
    initModule: initModule,
    stateMap: stateMap
  };
}());