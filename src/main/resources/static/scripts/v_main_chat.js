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

	var configMap = {}, getView, initModule,
		view;

	getView = function () {
		return view;
	};

	initModule = function () {
		//view = {
		//	id: 'main-chat',
		//	view: 'form',
		//	type: {
		//		margin: 0,
		//		padding: 0
		//	},
		//	height: 200, elements: [
		//		{view: 'textarea', readonly: true},
		//		{view: 'text', placeholder: '내용을 입력해주세요'}
		//	]
		//};

		webix.proxy.faye.client = new Faye.Client('//localhost:8000/');
		webix.proxy.faye.clientId = webix.uid();

		var user_name = '이하나';

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

			setTimeout(function() {
				$$('message').focus();
			}, 100);
		}

		function chat_template(obj) {
			return '<span style="font-weight:bold;">' + obj.user + '</span>: ' + obj.value;
		}

		view = {
			id: 'main-chat',
			rows: [
				//{template: 'Webix Based Chat', type: 'header'},
				{
					view: 'list', id: 'chat', gravity: 3,
					//url: 'faye->/data', save: 'faye->/data',
					type: {height: 'auto'},
					template: chat_template
				},
				{
					cols: [
						{view: 'text', id: 'message', placeholder: '채팅 메세지를 입력해주세요', gravity: 3},
						{view: 'button', value: 'Send', click: send_message, hotkey: 'enter'}
					]
				}
			]
		};

		//webix.dp($$('chat')).ignore(function () {
		//	$$('chat').add({
		//		user: 'System', value: 'Welcome to chat :)'
		//	});
		//	$$('chat').add({
		//		user: 'System', value: 'Uset '/nick Name' to set a name'
		//	});
		//});

		//$$('chat').attachEvent('onAfterAdd', function (id) {
		//	webix.delay(function () {
		//		this.showItem(id);
		//	}, this);
		//});


		//webix.UIManager.addHotKey('Enter', send_message, $$('message'));
		//webix.UIManager.setFocus($$('message'));
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());