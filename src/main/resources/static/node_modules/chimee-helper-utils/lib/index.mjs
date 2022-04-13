import _toConsumableArray from 'babel-runtime/helpers/toConsumableArray';
import _Promise from 'babel-runtime/core-js/promise';
import _Object$keys from 'babel-runtime/core-js/object/keys';
import _Array$from from 'babel-runtime/core-js/array/from';
import { isArray, isFunction } from 'toxic-predicate-functions';

// **********************  judgement   ************************
/**
 * check if the code running in browser environment (not include worker env)
 * @returns {Boolean}
 */
var inBrowser = typeof window !== 'undefined' && Object.prototype.toString.call(window) !== '[object Object]';

// **********************  对象操作  ************************
/**
 * 转变一个类数组对象为数组
 */
function makeArray(obj) {
  return _Array$from(obj);
}

/**
 * sort Object attributes by function
 * and transfer them into array
 * @param  {Object} obj Object form from numric
 * @param  {Function} fn sort function
 * @return {Array} the sorted attirbutes array
 */
function transObjectAttrIntoArray(obj) {
  var fn = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : function (a, b) {
    return +a - +b;
  };

  return _Object$keys(obj).sort(fn).reduce(function (order, key) {
    return order.concat(obj[key]);
  }, []);
}
/**
 * run a queue one by one.If include function reject or return false it will stop
 * @param  {Array} queue the queue which we want to run one by one
 * @return {Promise}    tell us whether a queue run finished
 */
function runRejectableQueue(queue) {
  for (var _len = arguments.length, args = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
    args[_key - 1] = arguments[_key];
  }

  return new _Promise(function (resolve, reject) {
    var step = function step(index) {
      if (index >= queue.length) {
        resolve();
        return;
      }
      var result = isFunction(queue[index]) ? queue[index].apply(queue, _toConsumableArray(args)) : queue[index];
      if (result === false) return reject('stop');
      return _Promise.resolve(result).then(function () {
        return step(index + 1);
      }).catch(function (err) {
        return reject(err || 'stop');
      });
    };
    step(0);
  });
}
/**
 * run a queue one by one.If include function return false it will stop
 * @param  {Array} queue the queue which we want to run one by one
 * @return {boolean} tell the user if the queue run finished
 */
function runStoppableQueue(queue) {
  for (var _len2 = arguments.length, args = Array(_len2 > 1 ? _len2 - 1 : 0), _key2 = 1; _key2 < _len2; _key2++) {
    args[_key2 - 1] = arguments[_key2];
  }

  var step = function step(index) {
    if (index >= queue.length) {
      return true;
    }
    var result = isFunction(queue[index]) ? queue[index].apply(queue, _toConsumableArray(args)) : queue[index];
    if (result === false) return false;
    return step(++index);
  };
  return step(0);
}

function checkContinuation(uint8array, start, checkLength) {
  var array = uint8array;
  if (start + checkLength < array.length) {
    while (checkLength--) {
      if ((array[++start] & 0xC0) !== 0x80) {
        return false;
      }
    }
    return true;
  }
  return false;
}

// decodeUTF8
function decodeUTF8(uint8array) {
  var out = [];
  var input = uint8array;
  var i = 0;
  var length = uint8array.length;

  while (i < length) {
    if (input[i] < 0x80) {
      out.push(String.fromCharCode(input[i]));
      ++i;
      continue;
    } else if (input[i] < 0xC0) {
      // fallthrough
    } else if (input[i] < 0xE0) {
      if (checkContinuation(input, i, 1)) {
        var ucs4 = (input[i] & 0x1F) << 6 | input[i + 1] & 0x3F;
        if (ucs4 >= 0x80) {
          out.push(String.fromCharCode(ucs4 & 0xFFFF));
          i += 2;
          continue;
        }
      }
    } else if (input[i] < 0xF0) {
      if (checkContinuation(input, i, 2)) {
        var _ucs = (input[i] & 0xF) << 12 | (input[i + 1] & 0x3F) << 6 | input[i + 2] & 0x3F;
        if (_ucs >= 0x800 && (_ucs & 0xF800) !== 0xD800) {
          out.push(String.fromCharCode(_ucs & 0xFFFF));
          i += 3;
          continue;
        }
      }
    } else if (input[i] < 0xF8) {
      if (checkContinuation(input, i, 3)) {
        var _ucs2 = (input[i] & 0x7) << 18 | (input[i + 1] & 0x3F) << 12 | (input[i + 2] & 0x3F) << 6 | input[i + 3] & 0x3F;
        if (_ucs2 > 0x10000 && _ucs2 < 0x110000) {
          _ucs2 -= 0x10000;
          out.push(String.fromCharCode(_ucs2 >>> 10 | 0xD800));
          out.push(String.fromCharCode(_ucs2 & 0x3FF | 0xDC00));
          i += 4;
          continue;
        }
      }
    }
    out.push(String.fromCharCode(0xFFFD));
    ++i;
  }
  return out.join('');
}

function debounce(func, wait, immediate) {
  // immediate默认为false
  var timeout = void 0,
      args = void 0,
      context = void 0,
      timestamp = void 0,
      result = void 0;

  var later = function later() {
    // 当wait指定的时间间隔期间多次调用_.debounce返回的函数，则会不断更新timestamp的值，导致last < wait && last >= 0一直为true，从而不断启动新的计时器延时执行func
    var last = new Date() - timestamp;

    if (last < wait && last >= 0) {
      timeout = setTimeout(later, wait - last);
    } else {
      timeout = null;
      if (!immediate) {
        result = func.apply(context, args);
        if (!timeout) context = args = null;
      }
    }
  };

  return function () {
    context = this;
    args = arguments;
    timestamp = new Date();
    // 第一次调用该方法时，且immediate为true，则调用func函数
    var callNow = immediate && !timeout;
    // 在wait指定的时间间隔内首次调用该方法，则启动计时器定时调用func函数
    if (!timeout) timeout = setTimeout(later, wait);
    if (callNow) {
      result = func.apply(context, args);
      context = args = null;
    }

    return result;
  };
}

/**
 * 函数节流（控制函数执行频率）
 * @param  {Function} func 要节流控制的函数，必填
 * @return {Number}   wait 等待时长
 * @return {Object}   options {
 *                      leading<是否首次调用立即执行，否：则按wait设定等待到期后调用才执行>:false,
 *                      trailing<是否在调用并未到期时启用定时器，以保证一定执行>:true
 *                    }
 * @return {Object}   cxt 上下文对象
 * @return {Function}
 */
function throttle(func, wait, options, cxt) {
  /* options的默认值
   *  表示首次调用返回值方法时，会马上调用func；否则仅会记录当前时刻，当第二次调用的时间间隔超过wait时，才调用func。
   *  options.leading = true;
   * 表示当调用方法时，未到达wait指定的时间间隔，则启动计时器延迟调用func函数，若后续在既未达到wait指定的时间间隔和func函数又未被调用的情况下调用返回值方法，则被调用请求将被丢弃。
   *  options.trailing = true;
   * 注意：当options.trailing = false时，效果与上面的简单实现效果相同
   */
  var context = void 0,
      args = void 0,
      result = void 0;
  var timeout = null;
  var previous = 0;
  if (!options) options = {};
  var later = function later() {
    previous = options.leading === false ? 0 : new Date() - 0;
    timeout = null;
    result = func.apply(context, args);
    if (!timeout) context = args = null;
  };
  wait = wait || 0;
  return function () {
    var now = new Date();
    if (!previous && options.leading === false) previous = now;
    // 计算剩余时间
    var remaining = wait - (now - previous);
    if (cxt) {
      context = cxt;
    } else {
      context = this;
    }

    args = arguments;
    // 当到达wait指定的时间间隔，则调用func函数
    // 精彩之处：按理来说remaining <= 0已经足够证明已经到达wait的时间间隔，但这里还考虑到假如客户端修改了系统时间则马上执行func函数。
    if (remaining <= 0 || remaining > wait) {
      // 由于setTimeout存在最小时间精度问题，因此会存在到达wait的时间间隔，但之前设置的setTimeout操作还没被执行，因此为保险起见，这里先清理setTimeout操作
      if (timeout) {
        clearTimeout(timeout);
        timeout = null;
      }
      previous = now;
      result = func.apply(context, args);
      if (!timeout) context = args = null;
    } else if (!timeout && options.trailing !== false) {
      // options.trailing=true时，延时执行func函数
      timeout = setTimeout(later, remaining);
    }
    return result;
  };
}

// requestAnimationFrame
var raf = inBrowser && (window.requestAnimationFrame || window.mozRequestAnimationFrame || window.webkitRequestAnimationFrame || window.msRequestAnimationFrame || window.oRequestAnimationFrame) || function (cb) {
  return setTimeout(cb, 17);
};

// cancelAnimationFrame
var caf = inBrowser && (window.cancelAnimationFrame || window.mozCancelAnimationFrame || window.webkitCancelAnimationFrame || window.webkitCancelRequestAnimationFrame || window.msCancelAnimationFrame || window.oCancelAnimationFrame) || function (id) {
  clearTimeout(id);
};

// 根据要求的位数，将9格式化为 09\009\0009...
function strRepeat(num, bit) {
  var pBit = bit;
  num = '' + (num || '');
  var numLen = num.length;
  bit = (bit || numLen) - numLen;
  var paddingStr = bit > 0 ? num.repeat ? '0'.repeat(bit) : new Array(bit + 1).join('0') : '';
  return (paddingStr + num).slice(0, pBit);
}

// video 时间格式化
function formatTime(time) {
  var hh = Math.floor(time / 3600);
  time = Math.floor(time % 3600);
  var mm = strRepeat(Math.floor(time / 60), 2);
  time = Math.floor(time % 60);
  var ss = strRepeat(time, 2);
  return hh >= 1 ? hh + ':' + mm + ':' + ss : mm + ':' + ss;
}

/**
 * 追加样式代码到head的style标签，不存在则创建
 * @param {String} cssText 样式文本
 * @return {HTMLElement}
 */
function appendCSS(cssText) {
  var doc = document;
  var styleEl = doc.querySelector('style');
  if (!styleEl) {
    styleEl = doc.createElement('style');
    var header = doc.querySelector('head');
    header && header.appendChild(styleEl);
  }
  styleEl.appendChild(doc.createTextNode(cssText));
  return styleEl;
}

/**
 * 格式化日期对象为：年-月-日 时:分:秒.毫秒
 * @param {Date} date Date日期对象
 * @param {String} pattern 要输出的日期格式，默认：`yyyy-MM-dd hh:mm:ss.i`
 * @return {String}
 */
function formatDate() {
  var date = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : new Date();
  var pattern = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 'yyyy-MM-dd hh:mm:ss.i';

  var year = date.getFullYear().toString();
  var fields = {
    M: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    m: date.getMinutes(),
    s: date.getSeconds(),
    i: date.getMilliseconds()
  };
  pattern = pattern.replace(/(y+)/ig, function (_, yearPattern) {
    return year.substr(4 - Math.min(4, yearPattern.length));
  });

  var _loop = function _loop(i) {
    pattern = pattern.replace(new RegExp('(' + i + '+)', 'g'), function (_, pattStr) {
      return (fields[i] < 10 && pattStr.length > 1 ? '0' : '') + fields[i];
    });
  };

  for (var i in fields) {
    _loop(i);
  }
  return pattern;
}

/**
 * 读取本地存储的值（不支持localStorage则降级到cookie）
 * @param {String} key 目标数据key
 * @return {String}
 */
function getLocalStorage(key) {
  try {
    return window.localStorage.getItem(key);
  } catch (e) {
    try {
      var regRt = document.cookie.match(new RegExp('(^| )' + key + '=([^;]*)(;|$)'));
      return isArray(regRt) ? unescape(regRt[2]) : '';
    } catch (e) {
      return '';
    }
  }
}
/**
 * 将指定key对应值写入本地存储（不支持localStorage则降级到cookie）
 * @param {String} key
 * @param {String} val
 * @return {String}
 */
function setLocalStorage(key, val) {
  try {
    window.localStorage.setItem(key, val);
  } catch (e) {
    var expires = new Date();
    // 默认存储300天
    expires.setTime(expires.getTime() + 24 * 3600 * 1000 * 300);
    try {
      document.cookie = key + '=' + escape(val) + ';expires=' + expires.toUTCString() + ';path=/;';
    } catch (e) {
      console.error(e);
    }
  }
}

export { inBrowser, makeArray, transObjectAttrIntoArray, runRejectableQueue, runStoppableQueue, decodeUTF8, debounce, throttle, raf, caf, strRepeat, formatTime, appendCSS, formatDate, getLocalStorage, setLocalStorage };
