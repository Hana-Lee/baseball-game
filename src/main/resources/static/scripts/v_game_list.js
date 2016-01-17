/**
 * @author Hana Lee
 * @since 2016-01-17 22:33
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_game_list = (function () {
	'use strict';

	var configMap = {}, getView, initModule,
		view;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = {
			id: 'game-list-container',
			rows: [
				{cols: [
					{template: '<div style="border: 1px solid red;width:100%;height:100%;">게임룸1</div>'},
					{template: '<div style="border: 1px solid red;width:100%;height:100%;"">게임룸2</div>'},
					{template: '<div style="border: 1px solid red;width:100%;height:100%;"">게임룸3</div>'}
				]},
				{cols: [
					{template: '<div style="border: 1px solid red;width:100%;height:100%;"">게임룸4</div>'},
					{template: '<div style="border: 1px solid red;width:100%;height:100%;"">게임룸5</div>'},
					{template: '<div style="border: 1px solid red;width:100%;height:100%;"">게임룸6</div>'}
				]},
				{cols: [
					{template: '<div style="border: 1px solid red;width:100%;height:100%;"">게임룸7</div>'},
					{template: '<div style="border: 1px solid red;width:100%;height:100%;"">게임룸8</div>'},
					{template: '<div style="border: 1px solid red;width:100%;height:100%;"">게임룸9</div>'}
				]}
			]
		};
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());