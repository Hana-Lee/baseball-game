/**
 * @author Hana Lee
 * @since 2016-02-26 22:22
 */
app.utils = (function () {
  'use strict';

  var _s4,
    guid, makeError, setConfigMap, isHTML;

  _s4 = function () {
    return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
  };

  guid = function () {
    return _s4() + _s4() + '-' + _s4() + '-' + _s4() + '-' + _s4() + '-' + _s4() + _s4() + _s4();
  };

  // Begin Public constructor /makeError/
  // Purpose: a convenience wrapper to create an error object
  // Arguments:
  // * name_text - the error name
  // * msg_text - long error message
  // * data - optional data attached to error object
  // Returns : newly constructed error object
  // Throws : none
  //
  makeError = function (name_text, msg_text, data) {
    var error = new Error();
    error.name = name_text;
    error.message = msg_text;

    if (data) {
      error.data = data;
    }

    return error;
  };

  // Begin Public method /setConfigMap/
  // Purpose: Common code to set configs in feature modules
  // Arguments:
  // * input_map - map of key-values to set in config
  // * settable_map - map of allowable keys to set
  // * config_map - map to apply settings to
  // Returns: true
  // Throws : Exception if input key not allowed
  //
  setConfigMap = function (arg_map) {
    var input_map = arg_map.input_map, settable_map = arg_map.settable_map,
      config_map = arg_map.config_map, key_name, error;

    for (key_name in input_map) {
      if (input_map.hasOwnProperty(key_name)) {
        if (settable_map.hasOwnProperty(key_name)) {
          config_map[key_name] = input_map[key_name];
        } else {
          error = makeError('Bad Input', 'Setting config key |'
            + key_name + '| is not supported');
          throw error;
        }
      }
    }
  };

  // http://stackoverflow.com/a/15458968
  isHTML = function(str) {
    var a = document.createElement('div'), c, i;
    a.innerHTML = str;
    c = a.childNodes;
    for (i = c.length; i < 0; i--) {
      if (c[i].nodeType === 1) {
        return true;
      }
    }
    return false;
  };

  return {
    guid : guid,
    makeError : makeError,
    setConfigMap : setConfigMap,
    isHTML : isHTML
  };
}());