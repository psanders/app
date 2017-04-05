(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.Tour = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/**
 * TinyAnimate
 *  version 0.3.0
 *
 * Source:  https://github.com/branneman/TinyAnimate
 * Author:  Bran van der Meer <branmovic@gmail.com> (http://bran.name/)
 * License: MIT
 *
 * Functions:
 *  TinyAnimate.animate(from, to, duration, update, easing, done)
 *  TinyAnimate.animateCSS(element, property, unit, from, to, duration, easing, done)
 *
 * Parameters:
 *  element   HTMLElement        A dom node
 *  property  string             Property name, as available in element.style, i.e. 'borderRadius', not 'border-radius'
 *  unit      string             Property unit, like 'px'
 *  from      int                Property value to animate from
 *  to        int                Property value to animate to
 *  duration  int                Duration in milliseconds
 *  update    function           Function to implement updating the DOM, get's called with a value between `from` and `to`
 *  easing    string | function  Optional: A string when the easing function is available in TinyAnimate.easings,
 *                                or a function with the signature: function(t, b, c, d) {...}
 *  done      function           Optional: To be executed when the animation has completed.
 */

/**
 * Universal Module Dance
 *  config: CommonJS Strict, exports Global, supports circular dependencies
 *  https://github.com/umdjs/umd/
 */
(function(root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(['exports'], function(exports) {
            factory((root.TinyAnimate = exports));
        });
    } else if (typeof exports === 'object') {
        factory(exports);
    } else {
        factory((root.TinyAnimate = {}));
    }
}(this, function(exports) {

    /**
     * TinyAnimate.animate()
     */
    exports.animate = function(from, to, duration, update, easing, done) {

        // Early bail out if called incorrectly
        if (typeof from !== 'number' ||
            typeof to !== 'number' ||
            typeof duration !== 'number' ||
            typeof update !== 'function')
            return;

        // Determine easing
        if (typeof easing === 'string' && easings[easing]) {
            easing = easings[easing];
        }
        if (typeof easing !== 'function') {
            easing = easings.linear;
        }

        // Create mock done() function if necessary
        if (typeof done !== 'function') {
            done = function() {};
        }

        // Pick implementation (requestAnimationFrame | setTimeout)
        var rAF = window.requestAnimationFrame || function(callback) {
            window.setTimeout(callback, 1000 / 60);
        };

        // Animation loop
        var change = to - from;
        function loop(timestamp) {
            var time = (timestamp || +new Date()) - start;
            update(easing(time, from, change, duration));
            if (time >= duration) {
                update(to);
                done();
            } else {
                rAF(loop);
            }
        }
        update(from);

        // Start animation loop
        var start = window.performance && window.performance.now ? window.performance.now() : +new Date();

        rAF(loop);
    };

    /**
     * TinyAnimate.animateCSS()
     *  Shortcut method for animating css properties
     */
    exports.animateCSS = function(element, property, unit, from, to, duration, easing, done) {

        var update = function(value) {
            element.style[property] = value + unit;
        };
        exports.animate(from, to, duration, update, easing, done);
    };

    /**
     * TinyAnimate.easings
     *  Adapted from jQuery Easing
     */
    var easings = exports.easings = {};
    easings.linear = function(t, b, c, d) {
        return c * t / d + b;
    };
    easings.easeInQuad = function(t, b, c, d) {
        return c * (t /= d) * t + b;
    };
    easings.easeOutQuad = function(t, b, c, d) {
        return -c * (t /= d) * (t - 2) + b;
    };
    easings.easeInOutQuad = function(t, b, c, d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t + b;
        return -c / 2 * ((--t) * (t - 2) - 1) + b;
    };
    easings.easeInCubic = function(t, b, c, d) {
        return c * (t /= d) * t * t + b;
    };
    easings.easeOutCubic = function(t, b, c, d) {
        return c * ((t = t / d - 1) * t * t + 1) + b;
    };
    easings.easeInOutCubic = function(t, b, c, d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t + 2) + b;
    };
    easings.easeInQuart = function(t, b, c, d) {
        return c * (t /= d) * t * t * t + b;
    };
    easings.easeOutQuart = function(t, b, c, d) {
        return -c * ((t = t / d - 1) * t * t * t - 1) + b;
    };
    easings.easeInOutQuart = function(t, b, c, d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
        return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
    };
    easings.easeInQuint = function(t, b, c, d) {
        return c * (t /= d) * t * t * t * t + b;
    };
    easings.easeOutQuint = function(t, b, c, d) {
        return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
    };
    easings.easeInOutQuint = function(t, b, c, d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
    };
    easings.easeInSine = function(t, b, c, d) {
        return -c * Math.cos(t / d * (Math.PI / 2)) + c + b;
    };
    easings.easeOutSine = function(t, b, c, d) {
        return c * Math.sin(t / d * (Math.PI / 2)) + b;
    };
    easings.easeInOutSine = function(t, b, c, d) {
        return -c / 2 * (Math.cos(Math.PI * t / d) - 1) + b;
    };
    easings.easeInExpo = function(t, b, c, d) {
        return (t == 0) ? b : c * Math.pow(2, 10 * (t / d - 1)) + b;
    };
    easings.easeOutExpo = function(t, b, c, d) {
        return (t == d) ? b + c : c * (-Math.pow(2, -10 * t / d) + 1) + b;
    };
    easings.easeInOutExpo = function(t, b, c, d) {
        if (t == 0) return b;
        if (t == d) return b + c;
        if ((t /= d / 2) < 1) return c / 2 * Math.pow(2, 10 * (t - 1)) + b;
        return c / 2 * (-Math.pow(2, -10 * --t) + 2) + b;
    };
    easings.easeInCirc = function(t, b, c, d) {
        return -c * (Math.sqrt(1 - (t /= d) * t) - 1) + b;
    };
    easings.easeOutCirc = function(t, b, c, d) {
        return c * Math.sqrt(1 - (t = t / d - 1) * t) + b;
    };
    easings.easeInOutCirc = function(t, b, c, d) {
        if ((t /= d / 2) < 1) return -c / 2 * (Math.sqrt(1 - t * t) - 1) + b;
        return c / 2 * (Math.sqrt(1 - (t -= 2) * t) + 1) + b;
    };
    easings.easeInElastic = function(t, b, c, d) {
        var p = 0;
        var a = c;
        if (t == 0) return b;
        if ((t /= d) == 1) return b + c;
        if (!p) p = d * .3;
        if (a < Math.abs(c)) {
            a = c;
            var s = p / 4;
        }
        else var s = p / (2 * Math.PI) * Math.asin(c / a);
        return -(a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
    };
    easings.easeOutElastic = function(t, b, c, d) {
        var p = 0;
        var a = c;
        if (t == 0) return b;
        if ((t /= d) == 1) return b + c;
        if (!p) p = d * .3;
        if (a < Math.abs(c)) {
            a = c;
            var s = p / 4;
        }
        else var s = p / (2 * Math.PI) * Math.asin(c / a);
        return a * Math.pow(2, -10 * t) * Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
    };
    easings.easeInOutElastic = function(t, b, c, d) {
        var p = 0;
        var a = c;
        if (t == 0) return b;
        if ((t /= d / 2) == 2) return b + c;
        if (!p) p = d * (.3 * 1.5);
        if (a < Math.abs(c)) {
            a = c;
            var s = p / 4;
        }
        else var s = p / (2 * Math.PI) * Math.asin(c / a);
        if (t < 1) return -.5 * (a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
        return a * Math.pow(2, -10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p) * .5 + c + b;
    };
    easings.easeInBack = function(t, b, c, d, s) {
        if (s == undefined) s = 1.70158;
        return c * (t /= d) * t * ((s + 1) * t - s) + b;
    };
    easings.easeOutBack = function(t, b, c, d, s) {
        if (s == undefined) s = 1.70158;
        return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
    };
    easings.easeInOutBack = function(t, b, c, d, s) {
        if (s == undefined) s = 1.70158;
        if ((t /= d / 2) < 1) return c / 2 * (t * t * (((s *= (1.525)) + 1) * t - s)) + b;
        return c / 2 * ((t -= 2) * t * (((s *= (1.525)) + 1) * t + s) + 2) + b;
    };
    easings.easeInBounce = function(t, b, c, d) {
        return c - easings.easeOutBounce(d - t, 0, c, d) + b;
    };
    easings.easeOutBounce = function(t, b, c, d) {
        if ((t /= d) < (1 / 2.75)) {
            return c * (7.5625 * t * t) + b;
        } else if (t < (2 / 2.75)) {
            return c * (7.5625 * (t -= (1.5 / 2.75)) * t + .75) + b;
        } else if (t < (2.5 / 2.75)) {
            return c * (7.5625 * (t -= (2.25 / 2.75)) * t + .9375) + b;
        } else {
            return c * (7.5625 * (t -= (2.625 / 2.75)) * t + .984375) + b;
        }
    };
    easings.easeInOutBounce = function(t, b, c, d) {
        if (t < d / 2) return easings.easeInBounce(t * 2, 0, c, d) * .5 + b;
        return easings.easeOutBounce(t * 2 - d, 0, c, d) * .5 + c * .5 + b;
    };

}));

},{}],2:[function(require,module,exports){
"use strict";

/*! cash-dom 1.3.4, https://github.com/kenwheeler/cash @license MIT */
(function (root, factory) {
  if (typeof define === "function" && define.amd) {
    define(factory);
  } else if (typeof exports !== "undefined") {
    module.exports = factory();
  } else {
    root.cash = root.$ = factory();
  }
})(this, function () {
  var doc = document, win = window, ArrayProto = Array.prototype, slice = ArrayProto.slice, filter = ArrayProto.filter, push = ArrayProto.push;

  var noop = function () {}, isFunction = function (item) {
    return typeof item === typeof noop;
  }, isString = function (item) {
    return typeof item === typeof "";
  };

  var idMatch = /^#[\w-]*$/, classMatch = /^\.[\w-]*$/, htmlMatch = /<.+>/, singlet = /^\w+$/;

  function find(selector, context) {
    context = context || doc;
    var elems = (classMatch.test(selector) ? context.getElementsByClassName(selector.slice(1)) : singlet.test(selector) ? context.getElementsByTagName(selector) : context.querySelectorAll(selector));
    return elems;
  }

  var frag, tmp;
  function parseHTML(str) {
    frag = frag || doc.createDocumentFragment();
    tmp = tmp || frag.appendChild(doc.createElement("div"));
    tmp.innerHTML = str;
    return tmp.childNodes;
  }

  function onReady(fn) {
    if (doc.readyState !== "loading") {
      fn();
    } else {
      doc.addEventListener("DOMContentLoaded", fn);
    }
  }

  function Init(selector, context) {
    if (!selector) {
      return this;
    }

    // If already a cash collection, don't do any further processing
    if (selector.cash && selector !== win) {
      return selector;
    }

    var elems = selector, i = 0, length;

    if (isString(selector)) {
      elems = (idMatch.test(selector) ?
      // If an ID use the faster getElementById check
      doc.getElementById(selector.slice(1)) : htmlMatch.test(selector) ?
      // If HTML, parse it into real elements
      parseHTML(selector) :
      // else use `find`
      find(selector, context));

      // If function, use as shortcut for DOM ready
    } else if (isFunction(selector)) {
      onReady(selector);return this;
    }

    if (!elems) {
      return this;
    }

    // If a single DOM element is passed in or received via ID, return the single element
    if (elems.nodeType || elems === win) {
      this[0] = elems;
      this.length = 1;
    } else {
      // Treat like an array and loop through each item.
      length = this.length = elems.length;
      for (; i < length; i++) {
        this[i] = elems[i];
      }
    }

    return this;
  }

  function cash(selector, context) {
    return new Init(selector, context);
  }

  var fn = cash.fn = cash.prototype = Init.prototype = { // jshint ignore:line
    constructor: cash,
    cash: true,
    length: 0,
    push: push,
    splice: ArrayProto.splice,
    map: ArrayProto.map,
    init: Init
  };

  cash.parseHTML = parseHTML;
  cash.noop = noop;
  cash.isFunction = isFunction;
  cash.isString = isString;

  cash.extend = fn.extend = function (target) {
    target = target || {};

    var args = slice.call(arguments), length = args.length, i = 1;

    if (args.length === 1) {
      target = this;
      i = 0;
    }

    for (; i < length; i++) {
      if (!args[i]) {
        continue;
      }
      for (var key in args[i]) {
        if (args[i].hasOwnProperty(key)) {
          target[key] = args[i][key];
        }
      }
    }

    return target;
  };

  function each(collection, callback) {
    var l = collection.length, i = 0;

    for (; i < l; i++) {
      if (callback.call(collection[i], collection[i], i, collection) === false) {
        break;
      }
    }
  }

  function matches(el, selector) {
    var m = el && (el.matches || el.webkitMatchesSelector || el.mozMatchesSelector || el.msMatchesSelector || el.oMatchesSelector);
    return !!m && m.call(el, selector);
  }

  function unique(collection) {
    return cash(slice.call(collection).filter(function (item, index, self) {
      return self.indexOf(item) === index;
    }));
  }

  cash.extend({
    merge: function (first, second) {
      var len = +second.length, i = first.length, j = 0;

      for (; j < len; i++, j++) {
        first[i] = second[j];
      }

      first.length = i;
      return first;
    },

    each: each,
    matches: matches,
    unique: unique,
    isArray: Array.isArray,
    isNumeric: function (n) {
      return !isNaN(parseFloat(n)) && isFinite(n);
    }

  });

  var uid = cash.uid = "_cash" + Date.now();

  function getDataCache(node) {
    return (node[uid] = node[uid] || {});
  }

  function setData(node, key, value) {
    return (getDataCache(node)[key] = value);
  }

  function getData(node, key) {
    var c = getDataCache(node);
    if (c[key] === undefined) {
      c[key] = node.dataset ? node.dataset[key] : cash(node).attr("data-" + key);
    }
    return c[key];
  }

  function removeData(node, key) {
    var c = getDataCache(node);
    if (c) {
      delete c[key];
    } else if (node.dataset) {
      delete node.dataset[key];
    } else {
      cash(node).removeAttr("data-" + name);
    }
  }

  fn.extend({
    data: function (name, value) {
      if (isString(name)) {
        return (value === undefined ? getData(this[0], name) : this.each(function (v) {
          return setData(v, name, value);
        }));
      }

      for (var key in name) {
        this.data(key, name[key]);
      }

      return this;
    },

    removeData: function (key) {
      return this.each(function (v) {
        return removeData(v, key);
      });
    }

  });

  var notWhiteMatch = /\S+/g;

  function getClasses(c) {
    return isString(c) && c.match(notWhiteMatch);
  }

  function hasClass(v, c) {
    return (v.classList ? v.classList.contains(c) : new RegExp("(^| )" + c + "( |$)", "gi").test(v.className));
  }

  function addClass(v, c, spacedName) {
    if (v.classList) {
      v.classList.add(c);
    } else if (spacedName.indexOf(" " + c + " ")) {
      v.className += " " + c;
    }
  }

  function removeClass(v, c) {
    if (v.classList) {
      v.classList.remove(c);
    } else {
      v.className = v.className.replace(c, "");
    }
  }

  fn.extend({
    addClass: function (c) {
      var classes = getClasses(c);

      return (classes ? this.each(function (v) {
        var spacedName = " " + v.className + " ";
        each(classes, function (c) {
          addClass(v, c, spacedName);
        });
      }) : this);
    },

    attr: function (name, value) {
      if (!name) {
        return undefined;
      }

      if (isString(name)) {
        if (value === undefined) {
          return this[0] ? this[0].getAttribute ? this[0].getAttribute(name) : this[0][name] : undefined;
        }

        return this.each(function (v) {
          if (v.setAttribute) {
            v.setAttribute(name, value);
          } else {
            v[name] = value;
          }
        });
      }

      for (var key in name) {
        this.attr(key, name[key]);
      }

      return this;
    },

    hasClass: function (c) {
      var check = false, classes = getClasses(c);
      if (classes && classes.length) {
        this.each(function (v) {
          check = hasClass(v, classes[0]);
          return !check;
        });
      }
      return check;
    },

    prop: function (name, value) {
      if (isString(name)) {
        return (value === undefined ? this[0][name] : this.each(function (v) {
          v[name] = value;
        }));
      }

      for (var key in name) {
        this.prop(key, name[key]);
      }

      return this;
    },

    removeAttr: function (name) {
      return this.each(function (v) {
        if (v.removeAttribute) {
          v.removeAttribute(name);
        } else {
          delete v[name];
        }
      });
    },

    removeClass: function (c) {
      if (!arguments.length) {
        return this.attr("class", "");
      }
      var classes = getClasses(c);
      return (classes ? this.each(function (v) {
        each(classes, function (c) {
          removeClass(v, c);
        });
      }) : this);
    },

    removeProp: function (name) {
      return this.each(function (v) {
        delete v[name];
      });
    },

    toggleClass: function (c, state) {
      if (state !== undefined) {
        return this[state ? "addClass" : "removeClass"](c);
      }
      var classes = getClasses(c);
      return (classes ? this.each(function (v) {
        var spacedName = " " + v.className + " ";
        each(classes, function (c) {
          if (hasClass(v, c)) {
            removeClass(v, c);
          } else {
            addClass(v, c, spacedName);
          }
        });
      }) : this);
    } });

  fn.extend({
    add: function (selector, context) {
      return unique(cash.merge(this, cash(selector, context)));
    },

    each: function (callback) {
      each(this, callback);
      return this;
    },

    eq: function (index) {
      return cash(this.get(index));
    },

    filter: function (selector) {
      return cash(filter.call(this, (isString(selector) ? function (e) {
        return matches(e, selector);
      } : selector)));
    },

    first: function () {
      return this.eq(0);
    },

    get: function (index) {
      if (index === undefined) {
        return slice.call(this);
      }
      return (index < 0 ? this[index + this.length] : this[index]);
    },

    index: function (elem) {
      var child = elem ? cash(elem)[0] : this[0], collection = elem ? this : cash(child).parent().children();
      return slice.call(collection).indexOf(child);
    },

    last: function () {
      return this.eq(-1);
    }

  });

  var camelCase = (function () {
    var camelRegex = /(?:^\w|[A-Z]|\b\w)/g, whiteSpace = /[\s-_]+/g;
    return function (str) {
      return str.replace(camelRegex, function (letter, index) {
        return letter[index === 0 ? "toLowerCase" : "toUpperCase"]();
      }).replace(whiteSpace, "");
    };
  }());

  var getPrefixedProp = (function () {
    var cache = {}, doc = document, div = doc.createElement("div"), style = div.style;

    return function (prop) {
      prop = camelCase(prop);
      if (cache[prop]) {
        return cache[prop];
      }

      var ucProp = prop.charAt(0).toUpperCase() + prop.slice(1), prefixes = ["webkit", "moz", "ms", "o"], props = (prop + " " + (prefixes).join(ucProp + " ") + ucProp).split(" ");

      each(props, function (p) {
        if (p in style) {
          cache[p] = prop = cache[prop] = p;
          return false;
        }
      });

      return cache[prop];
    };
  }());

  cash.prefixedProp = getPrefixedProp;
  cash.camelCase = camelCase;

  fn.extend({
    css: function (prop, value) {
      if (isString(prop)) {
        prop = getPrefixedProp(prop);
        return (arguments.length > 1 ? this.each(function (v) {
          return v.style[prop] = value;
        }) : win.getComputedStyle(this[0])[prop]);
      }

      for (var key in prop) {
        this.css(key, prop[key]);
      }

      return this;
    }

  });

  function compute(el, prop) {
    return parseInt(win.getComputedStyle(el[0], null)[prop], 10) || 0;
  }

  each(["Width", "Height"], function (v) {
    var lower = v.toLowerCase();

    fn[lower] = function () {
      return this[0].getBoundingClientRect()[lower];
    };

    fn["inner" + v] = function () {
      return this[0]["client" + v];
    };

    fn["outer" + v] = function (margins) {
      return this[0]["offset" + v] + (margins ? compute(this, "margin" + (v === "Width" ? "Left" : "Top")) + compute(this, "margin" + (v === "Width" ? "Right" : "Bottom")) : 0);
    };
  });

  function registerEvent(node, eventName, callback) {
    var eventCache = getData(node, "_cashEvents") || setData(node, "_cashEvents", {});
    eventCache[eventName] = eventCache[eventName] || [];
    eventCache[eventName].push(callback);
    node.addEventListener(eventName, callback);
  }

  function removeEvent(node, eventName, callback) {
    var eventCache = getData(node, "_cashEvents")[eventName];
    if (callback) {
      node.removeEventListener(eventName, callback);
    } else {
      each(eventCache, function (event) {
        node.removeEventListener(eventName, event);
      });
      eventCache = [];
    }
  }

  fn.extend({
    off: function (eventName, callback) {
      return this.each(function (v) {
        return removeEvent(v, eventName, callback);
      });
    },

    on: function (eventName, delegate, callback, runOnce) {
      // jshint ignore:line

      var originalCallback;

      if (!isString(eventName)) {
        for (var key in eventName) {
          this.on(key, delegate, eventName[key]);
        }
        return this;
      }

      if (isFunction(delegate)) {
        callback = delegate;
        delegate = null;
      }

      if (eventName === "ready") {
        onReady(callback);
        return this;
      }

      if (delegate) {
        originalCallback = callback;
        callback = function (e) {
          var t = e.target;

          while (!matches(t, delegate)) {
            if (t === this) {
              return (t = false);
            }
            t = t.parentNode;
          }

          if (t) {
            originalCallback.call(t, e);
          }
        };
      }

      return this.each(function (v) {
        var finalCallback = callback;
        if (runOnce) {
          finalCallback = function () {
            callback.apply(this, arguments);
            removeEvent(v, eventName, finalCallback);
          };
        }
        registerEvent(v, eventName, finalCallback);
      });
    },

    one: function (eventName, delegate, callback) {
      return this.on(eventName, delegate, callback, true);
    },

    ready: onReady,

    trigger: function (eventName, data) {
      var evt = doc.createEvent("HTMLEvents");
      evt.data = data;
      evt.initEvent(eventName, true, false);
      return this.each(function (v) {
        return v.dispatchEvent(evt);
      });
    }

  });

  function encode(name, value) {
    return "&" + encodeURIComponent(name) + "=" + encodeURIComponent(value).replace(/%20/g, "+");
  }
  function isCheckable(field) {
    return field.type === "radio" || field.type === "checkbox";
  }

  var formExcludes = ["file", "reset", "submit", "button"];

  fn.extend({
    serialize: function () {
      var formEl = this[0].elements, query = "";

      each(formEl, function (field) {
        if (field.name && formExcludes.indexOf(field.type) < 0) {
          if (field.type === "select-multiple") {
            each(field.options, function (o) {
              if (o.selected) {
                query += encode(field.name, o.value);
              }
            });
          } else if (!isCheckable(field) || (isCheckable(field) && field.checked)) {
            query += encode(field.name, field.value);
          }
        }
      });

      return query.substr(1);
    },

    val: function (value) {
      if (value === undefined) {
        return this[0].value;
      } else {
        return this.each(function (v) {
          return v.value = value;
        });
      }
    }

  });

  function insertElement(el, child, prepend) {
    if (prepend) {
      var first = el.childNodes[0];
      el.insertBefore(child, first);
    } else {
      el.appendChild(child);
    }
  }

  function insertContent(parent, child, prepend) {
    var str = isString(child);

    if (!str && child.length) {
      each(child, function (v) {
        return insertContent(parent, v, prepend);
      });
      return;
    }

    each(parent, str ? function (v) {
      return v.insertAdjacentHTML(prepend ? "afterbegin" : "beforeend", child);
    } : function (v, i) {
      return insertElement(v, (i === 0 ? child : child.cloneNode(true)), prepend);
    });
  }

  fn.extend({
    after: function (selector) {
      cash(selector).insertAfter(this);
      return this;
    },

    append: function (content) {
      insertContent(this, content);
      return this;
    },

    appendTo: function (parent) {
      insertContent(cash(parent), this);
      return this;
    },

    before: function (selector) {
      cash(selector).insertBefore(this);
      return this;
    },

    clone: function () {
      return cash(this.map(function (v) {
        return v.cloneNode(true);
      }));
    },

    empty: function () {
      this.html("");
      return this;
    },

    html: function (content) {
      if (content === undefined) {
        return this[0].innerHTML;
      }
      var source = (content.nodeType ? content[0].outerHTML : content);
      return this.each(function (v) {
        return v.innerHTML = source;
      });
    },

    insertAfter: function (selector) {
      var _this = this;


      cash(selector).each(function (el, i) {
        var parent = el.parentNode, sibling = el.nextSibling;
        _this.each(function (v) {
          parent.insertBefore((i === 0 ? v : v.cloneNode(true)), sibling);
        });
      });

      return this;
    },

    insertBefore: function (selector) {
      var _this2 = this;
      cash(selector).each(function (el, i) {
        var parent = el.parentNode;
        _this2.each(function (v) {
          parent.insertBefore((i === 0 ? v : v.cloneNode(true)), el);
        });
      });
      return this;
    },

    prepend: function (content) {
      insertContent(this, content, true);
      return this;
    },

    prependTo: function (parent) {
      insertContent(cash(parent), this, true);
      return this;
    },

    remove: function () {
      return this.each(function (v) {
        return v.parentNode.removeChild(v);
      });
    },

    text: function (content) {
      if (content === undefined) {
        return this[0].textContent;
      }
      return this.each(function (v) {
        return v.textContent = content;
      });
    }

  });

  var docEl = doc.documentElement;

  fn.extend({
    position: function () {
      var el = this[0];
      return {
        left: el.offsetLeft,
        top: el.offsetTop
      };
    },

    offset: function () {
      var rect = this[0].getBoundingClientRect();
      return {
        top: rect.top + win.pageYOffset - docEl.clientTop,
        left: rect.left + win.pageXOffset - docEl.clientLeft
      };
    },

    offsetParent: function () {
      return cash(this[0].offsetParent);
    }

  });

  function directCompare(el, selector) {
    return el === selector;
  }

  fn.extend({
    children: function (selector) {
      var elems = [];
      this.each(function (el) {
        push.apply(elems, el.children);
      });
      elems = unique(elems);

      return (!selector ? elems : elems.filter(function (v) {
        return matches(v, selector);
      }));
    },

    closest: function (selector) {
      if (!selector || matches(this[0], selector)) {
        return this;
      }
      return this.parent().closest(selector);
    },

    is: function (selector) {
      if (!selector) {
        return false;
      }

      var match = false, comparator = (isString(selector) ? matches : selector.cash ? function (el) {
        return selector.is(el);
      } : directCompare);

      this.each(function (el, i) {
        match = comparator(el, selector, i);
        return !match;
      });

      return match;
    },

    find: function (selector) {
      if (!selector) {
        return cash();
      }

      var elems = [];
      this.each(function (el) {
        push.apply(elems, find(selector, el));
      });

      return unique(elems);
    },

    has: function (selector) {
      return filter.call(this, function (el) {
        return cash(el).find(selector).length !== 0;
      });
    },

    next: function () {
      return cash(this[0].nextElementSibling);
    },

    not: function (selector) {
      return filter.call(this, function (el) {
        return !matches(el, selector);
      });
    },

    parent: function () {
      var result = this.map(function (item) {
        return item.parentElement || doc.body.parentNode;
      });

      return unique(result);
    },

    parents: function (selector) {
      var last, result = [];

      this.each(function (item) {
        last = item;

        while (last !== doc.body.parentNode) {
          last = last.parentElement;

          if (!selector || (selector && matches(last, selector))) {
            result.push(last);
          }
        }
      });

      return unique(result);
    },

    prev: function () {
      return cash(this[0].previousElementSibling);
    },

    siblings: function () {
      var collection = this.parent().children(), el = this[0];

      return filter.call(collection, function (i) {
        return i !== el;
      });
    }

  });


  return cash;
});
},{}],3:[function(require,module,exports){
"use strict";function _interopRequireDefault(t){return t&&t.__esModule?t:{"default":t}}function start(t){if(initialized||(init(),initialized=!0),!t)return Promise.reject("No Tour Specified!");if(!t.steps.length)return Promise.reject("No steps were found in that tour!");if(service.current)return stop(),start(t);var e=void 0,o=void 0,s=void 0;return e=new Promise(function(t,e){o=t,s=e}),service.current=Object.assign({},defaults,t,{index:0,promise:e,resolve:o,reject:s}),prepView(),updateView(),service.current.promise}function stop(){cleanup(),service.current.reject(errors.interrupted),service.current=null}function next(){return stepExists(service.current.index+1)?doAfter(service.current.index).then(function(){service.current.index++}).then(function(t){return service.current.index===service.current.steps.length?Promise.resolve(!0):doBefore(service.current.index)}).then(function(t){return t===!0?doAfter(service.current.index).then(function(t){return finish()}):void updateView()}):finish()}function previous(){if(stepExists(service.current.index-1))return doAfter(service.current.index).then(function(t){return service.current.index--,doBefore(service.current.index)}).then(function(t){updateView()})}function goto(t){return stepExists(t)?doAfter(service.current.index).then(function(e){return service.current.index=t,doBefore(service.current.index)}).then(function(t){updateView()}):Promise.reject(errors.notFound)}function init(){injectTemplate(),resolveEventSystem()}function prepView(){var t=service.current;validPriorities(service.current.placement)||(console.warn("Tour - Invalid placement setting found in tour config. Must be an array eg: ['bottom', 'right', 'top', 'left']",service.current),service.current.placement=defaults.placement),service.current.steps.forEach(function(t){t.placement&&(validPriorities(t.placement)||console.warn("Tour - Invalid placement setting found in step. Must be an array eg: ['bottom', 'right', 'top', 'left']",t))}),Object.assign(els,{window:(0,_cashDom2["default"])(window),tour:(0,_cashDom2["default"])("#Tour"),wrap:(0,_cashDom2["default"])("#Tour-box-wrap"),box:(0,_cashDom2["default"])("#Tour-box"),tip:(0,_cashDom2["default"])("#Tour-tip"),step:(0,_cashDom2["default"])("#Tour-step"),length:(0,_cashDom2["default"])("#Tour-length"),close:(0,_cashDom2["default"])("#Tour-close"),content:(0,_cashDom2["default"])("#Tour-content"),innerContent:(0,_cashDom2["default"])("#Tour-inner-content"),actions:(0,_cashDom2["default"])("#Tour-actions"),previous:(0,_cashDom2["default"])("#Tour-previous"),next:(0,_cashDom2["default"])("#Tour-next"),masks_wrap:(0,_cashDom2["default"])("#Tour-masks"),masks_top:(0,_cashDom2["default"])("#Tour-masks .top"),masks_right:(0,_cashDom2["default"])("#Tour-masks .right"),masks_bottom:(0,_cashDom2["default"])("#Tour-masks .bottom"),masks_left:(0,_cashDom2["default"])("#Tour-masks .left"),masks_center:(0,_cashDom2["default"])("#Tour-masks .center"),canvas:(0,_cashDom2["default"])("#Tour-canvas"),ctx:(0,_cashDom2["default"])("#Tour-canvas")[0].getContext("2d"),scroll:(0,_cashDom2["default"])(t.scrollBox),target:!1}),Object.assign(dims,{window:{},scroll:{},target:{},canvas:{left:0,top:0,right:0,bottom:0}}),t.disableHotkeys||(els.previous.on("click",previous),els.next.on("click",next),els.close.on("click",clickStop),els.window.on("keydown",keyDown),els.window.on("resize",eventUtils.onWindowScrollDebounced),els.window.on("scroll",eventUtils.onWindowScrollDebounced),eventUtils.addWheelListener(window,eventUtils.onWindowScrollDebounced),els.content.on("scroll",onBoxScroll),eventUtils.addWheelListener(els.content[0],onBoxScroll),t.maskScrollThrough===!1&&eventUtils.addWheelListener(els.masks_wrap[0],stopMaskScroll)),els.tour.removeClass("hidden")}function doBefore(t){return service.current.steps[t]&&service.current.steps[t].before?service.current.steps[t].before():Promise.resolve()}function doAfter(t){return service.current.steps[t]&&service.current.steps[t].after?service.current.steps[t].after():Promise.resolve()}function finish(){cleanup(),service.current.resolve(),service.current=null}function cleanup(){els.tour.addClass("hidden"),els.canvas.css("opacity",null),els.masks_wrap.css("pointer-events",null),els.previous.off("click",previous),els.next.off("click",next),els.close.off("click",clickStop),els.window.off("keydown",keyDown),els.window.off("resize",eventUtils.onWindowScrollDebounced),els.window.off("scroll",eventUtils.onWindowScrollDebounced),eventUtils.removeWheelListener(window,eventUtils.onWindowScrollDebounced),els.content.off("scroll",onBoxScroll),eventUtils.removeWheelListener(els.content[0],onBoxScroll),service.current.maskScrollThrough===!1&&eventUtils.removeWheelListener(els.masks_wrap[0],stopMaskScroll)}function updateView(){var t=service.current,e=t.steps,o=t.index,s=e[o];return els.masks_wrap.css("pointer-events",(void 0!==s.maskClickThrough?s.maskClickThrough:t.maskClickThrough)?"none":"all"),(void 0!==s.dark?s.dark:t.dark)?els.box.addClass("dark-box"):els.box.removeClass("dark-box"),els.step.html(o+1),els.length.html(e.length),els.innerContent.html(s.content),els.previous.html(s.previousText||t.previousText),els.next.html(s.nextText||(o==e.length-1?t.finishText:t.nextText)),(void 0===s.showNext?t.showNext:s.showNext)?els.next.css({display:null}):els.next.css({display:"none"}),o>0&&(void 0===s.showPrevious?t.showPrevious:s.showPrevious)?els.previous.css({display:null}):els.previous.css({display:"none"}),(void 0===s.canExit?t.canExit:s.canExit)?els.close.css({display:null}):els.close.css({display:"none"}),els.content[0].scrollTop=0,findTarget(),getDimensions(),scrollToTarget().then(function(){return getDimensions(),!dims.first&&(dims.first=!0)&&moveToTarget(),moveToTarget()})}function findTarget(){var t=(0,_cashDom2["default"])(service.current.steps[service.current.index].target);els.target=t.length?t:null}function getDimensions(){var t=service.current;t&&(dims.window={width:els.window[0].innerWidth,height:els.window[0].innerHeight},dims.scroll={width:els.scroll.outerWidth(),height:els.scroll.outerHeight(),offset:els.scroll.offset(),scrollTop:els.scroll[0].scrollTop,scrollLeft:els.scroll[0].scrollLeft},Object.keys(dims.scroll.offset).forEach(function(t){dims.scroll.offset[t]=Math.ceil(dims.scroll.offset[t])}),dims.scroll.height=dims.scroll.height+dims.scroll.offset.top>dims.window.height?dims.window.height:dims.scroll.height,dims.scroll.width=dims.scroll.width+dims.scroll.offset.left>dims.window.width?dims.window.width:dims.scroll.width,dims.scroll.offset.toBottom=dims.scroll.height+dims.scroll.offset.top,dims.scroll.offset.toRight=dims.scroll.width+dims.scroll.offset.left,dims.scroll.offset.fromBottom=dims.window.height-dims.scroll.offset.top-dims.scroll.height,dims.scroll.offset.fromRight=dims.window.width-dims.scroll.offset.left-dims.scroll.width,dims.target={width:els.target.outerWidth(),height:els.target.outerHeight(),offset:els.target.offset()},"body"!=t.scrollBox&&"html"!=t.scrollBox||(dims.target.offset.top-=dims.scroll.scrollTop),Object.keys(dims.target.offset).forEach(function(t){dims.target.offset[t]=Math.ceil(dims.target.offset[t])}),dims.target.offset.toBottom=dims.target.offset.top+dims.target.height,dims.target.offset.toRight=dims.target.offset.left+dims.target.width,dims.target.offset.fromBottom=dims.window.height-dims.target.offset.top-dims.target.height,dims.target.offset.fromRight=dims.window.width-dims.target.offset.left-dims.target.width,dims.target.margins={offset:{top:dims.target.offset.top-t.padding,left:dims.target.offset.left-t.padding,toBottom:dims.target.offset.toBottom+t.padding,toRight:dims.target.offset.toRight+t.padding,fromBottom:dims.target.offset.fromBottom-t.padding,fromRight:dims.target.offset.fromRight-t.padding},height:dims.target.height+2*t.padding,right:dims.target.offset.fromRight+2*t.padding})}function scrollToTarget(){els.seeking=!0;var t=findScrollTop();return new Promise(function(e,o){t?_TinyAnimate2["default"].animate(els.scroll[0].scrollTop,t,service.current.animationDuration,function(t){els.scroll[0].scrollTop=t},"easeOutQuad",function(){els.seeking=!1,e()}):(e(),els.seeking=!1)})}function findScrollTop(){var t=service.current.maxHeight;{if(!(dims.target.margins.height>dims.scroll.height))return dims.target.margins.offset.top<dims.scroll.offset.top?dims.scroll.scrollTop-(dims.scroll.offset.top-dims.target.margins.offset.top):dims.target.margins.offset.toBottom>dims.scroll.offset.toBottom?dims.scroll.scrollTop+(dims.target.margins.offset.toBottom-dims.scroll.offset.toBottom):void 0;if(dims.target.offset.toBottom-t<dims.scroll.offset.top)return dims.scroll.scrollTop-(dims.scroll.offset.top-(dims.target.offset.toBottom-t));if(dims.target.offset.top+t>dims.scroll.offset.toBottom)return dims.scroll.scrollTop+(dims.target.offset.top+t-dims.scroll.offset.toBottom)}}function moveToTarget(){return moveBox(),moveMasks()}function injectTemplate(){var t=document.createElement("div");document.body.appendChild(t),t.outerHTML=template}function clickStop(){service.current.canExit&&stop()}function keyDown(t){switch(t.which){case 37:return previous(),void prevent(t);case 39:return next(),void prevent(t);case 27:if(!service.current.disableEscExit)return stop(),void prevent(t);case 38:case 40:return void onWindowScroll()}}function stopMaskScroll(t){return t.stopPropagation(t),t.preventDefault(t),t.returnValue=!1,!1}function onBoxScroll(t){var e=void 0;e="DOMMouseScroll"==t.type?t.detail*-40:t.wheelDelta;var o=e>0,s=els.content[0].scrollTop;return o&&!s?prevent(t):o||els.innerContent.height()-els.content.height()!=s?void 0:prevent(t)}function prevent(t){return t.stopPropagation(t),t.preventDefault(t),t.returnValue=!1,!1}function onWindowScroll(){if(!els.seeking)return findTarget(),getDimensions(),scrollToTarget().then(function(){return getDimensions(),moveToTarget()})}function stepExists(t){return t>=0&&t<service.current.steps.length}function moveBox(){function t(){if(dims.target.margins.offset.fromBottom>c)return dims.target.width>f?(i("bottom","center"),!0):dims.target.offset.fromRight+dims.target.width>f?(i("bottom","left"),!0):(i("bottom","right"),!0)}function e(){if(dims.target.margins.offset.fromRight>f)return dims.target.margins.height>dims.scroll.height?dims.target.offset.top>dims.window.height/2?(r("right","top"),!0):dims.target.offset.fromBottom>dims.window.height/2?(r("right","bottom"),!0):(r("right","center",!0),!0):dims.target.height>c?(r("right","center"),!0):dims.target.offset.fromBottom+dims.target.height>c?(r("right","top"),!0):(r("right","bottom"),!0)}function o(){if(dims.target.margins.offset.left>f)return dims.target.margins.height>dims.scroll.height?(r("left","center",!0),!0):dims.target.height>c?(r("left","center"),!0):dims.target.offset.fromBottom+dims.target.height>c?(r("left","top"),!0):(r("left","bottom"),!0)}function s(){if(dims.target.margins.offset.top>c)return dims.target.width>f?(i("top","center"),!0):dims.target.offset.fromRight+dims.target.width>f?(i("top","left"),!0):(i("top","right"),!0)}function i(t,e){var o=void 0,s=void 0,i=void 0,r=void 0,n=void 0;"top"==t?(o=dims.target.margins.offset.top-2*a.padding,n="bottom",r="-100%"):(o=dims.target.margins.offset.toBottom+2*a.padding,n="top",r="0"),"right"==e?(s=dims.target.offset.toRight+a.padding,i="-100%"):"center"==e?(s=dims.target.offset.left+dims.target.width/2,i="-50%"):(s=dims.target.offset.left-a.padding,i="0"),els.wrap.css({left:s+"px",top:o+"px",transform:"translate("+i+","+r+")"}),els.tip.attr("class","vertical "+n+" "+e)}function r(t,e,o){var s=void 0,i=void 0,r=void 0,n=void 0,l=void 0;"right"==t?(i=dims.target.margins.offset.toRight+2*a.padding,l="left",r="0"):(i=dims.target.margins.offset.left-2*a.padding,l="right",r="-100%"),o?(s=dims.window.height/2,n="-50%"):"top"==e?(s=dims.target.offset.top,n="0"):"center"==e?(s=dims.target.offset.top+dims.target.height/2,n="-50%"):(s=dims.target.offset.toBottom,n="-100%"),els.wrap.css({left:i+"px",top:s+"px",transform:"translate("+r+","+n+")"}),els.tip.attr("class","horizontal "+l+" "+e)}function n(t,e){var o=void 0,s=void 0,i=void 0,r=void 0;"top"==t?(o=dims.target.margins.offset.top<dims.scroll.offset.top?service.current.margin:dims.target.offset.top,i="0"):(o=dims.target.margins.offset.toBottom>dims.scroll.offset.toBottom?dims.scroll.offset.toBottom-service.current.margin:dims.target.offset.toBottom,i="-100%"),"right"==e?(s=dims.target.offset.left+dims.target.width,r="-100%"):"center"==e?(s=dims.target.offset.left+dims.target.width/2,r="-50%"):(s=dims.target.offset.left,r="0"),els.wrap.css({left:s+"px",top:o+"px",transform:"translate("+r+","+i+")"}),els.tip.attr("class","hidden")}function l(){els.wrap.css({left:"50%",top:"50%",transform:"translate(-50%, -50%)",margin:"0"}),els.tip.attr("class","hidden")}var a=service.current;if(a){var d=service.current.steps[service.current.index],c=service.current.maxHeight,f=service.current.maxWidth;if(!els.target)return void l();var m={bottom:t,right:e,left:o,top:s},u=!1,h=d.placement||service.current.placement;return h.forEach(function(t){!u&&m[t]()&&(u=!0)}),u||n("bottom","center"),Promise.resolve(null)}}function moveMasks(){if(service.current){var t=service.current.padding||0;if(els.target?(els.masks_top.css({height:dims.target.offset.top+"px",top:dims.target.offset.top<0?dims.target.offset.top+"px":0}),els.masks_bottom.css({height:dims.target.offset.fromBottom+"px",bottom:dims.target.offset.fromBottom<0?dims.target.offset.fromBottom+"px":0}),els.masks_left.css({top:dims.target.offset.top+"px",height:dims.target.height+"px",width:dims.target.offset.left+"px"}),els.masks_right.css({top:dims.target.offset.top+"px",height:dims.target.height+"px",width:dims.target.offset.fromRight+"px"}),service.current.disableInteraction&&els.masks_center.css({height:dims.target.height+2*t+"px",top:dims.target.offset.top-t+"px",left:dims.target.offset.left-t+"px",right:dims.target.offset.fromRight-t+"px",backgroundColor:"transparent"})):(els.masks_top.css({height:service.current.maskVisibleOnNoTarget?"100%":"0px"}),els.masks_bottom.css({height:"0px"}),els.masks_left.css({top:"0px",height:"100%",width:"0px"}),els.masks_right.css({top:"0px",height:"100%",width:"0px"})),!service.current.maskVisible)return void els.canvas.css({opacity:0});els.canvas.css({opacity:1}),els.canvas[0].width=dims.window.width*els.pixelRatio,els.canvas[0].height=dims.window.height*els.pixelRatio,els.canvas.css({width:dims.window.width+"px",height:dims.window.height+"px"}),els.pixelRatio=window.devicePixelRatio||1,1!==els.pixelRatio&&els.ctx.scale(els.pixelRatio,els.pixelRatio),els.ctx.fillStyle=service.current.maskColor;var e=dims.target.offset.left-t,o=dims.target.offset.top-t,s=dims.target.offset.toRight+t,i=dims.target.offset.toBottom+t;return new Promise(function(t,r){_TinyAnimate2["default"].animate(0,1,service.current.animationDuration,function(t){els.ctx.clearRect(0,0,dims.window.width,dims.window.height),dims.canvas.left=dims.canvas.left+(e-dims.canvas.left)*t,dims.canvas.top=dims.canvas.top+(o-dims.canvas.top)*t,dims.canvas.right=dims.canvas.right+(s-dims.canvas.right)*t,dims.canvas.bottom=dims.canvas.bottom+(i-dims.canvas.bottom)*t,drawEmptyRoundedRectangle(els.ctx,dims.canvas.left,dims.canvas.top,dims.canvas.right,dims.canvas.bottom,5),els.ctx.fill()},"easeOutQuad",function(){t()})})}}function drawEmptyRoundedRectangle(t,e,o,s,i){var r=arguments.length<=5||void 0===arguments[5]?0:arguments[5];t.beginPath(),t.moveTo(dims.window.width/2,0),t.lineTo(0,0),t.lineTo(0,dims.window.height),t.lineTo(dims.window.width,dims.window.height),t.lineTo(dims.window.width,0),t.lineTo(dims.window.width/2,0),t.lineTo(dims.window.width/2,o),t.lineTo(s-r,o),t.quadraticCurveTo(s,o,s,o+r),t.lineTo(s,i-r),t.quadraticCurveTo(s,i,s-r,i),t.lineTo(e+r,i),t.quadraticCurveTo(e,i,e,i-r),t.lineTo(e,o+r),t.quadraticCurveTo(e,o,e+r,o),t.lineTo(dims.window.width/2,o),t.closePath()}function validPriorities(t){for(var e=0;e<t.length;e+=1)if(defaults.placement.indexOf(t[e])===-1)return!1;return!0}function throttle(t,e){var o=!1;return function(){o||(t.call(),o=!0,setTimeout(function(){o=!1},e))}}function debounce(t,e,o){var s=void 0;return function(){var i=this,r=arguments,n=function(){s=null,o||t.apply(i,r)},l=o&&!s;clearTimeout(s),s=setTimeout(n,e),l&&t.apply(i,r)}}function resolveEventSystem(){function t(t,e,i,l){t[r](s+e,"wheel"==n?i:o,l||!1)}function e(t,e,r,l){t[i](s+e,"wheel"==n?r:o,l||!1)}function o(t){!t&&(t=window.event);var e={originalEvent:t,target:t.target||t.srcElement,type:"wheel",deltaMode:"MozMousePixelScroll"==t.type?0:1,deltaX:0,deltaZ:0,preventDefault:function(){t.preventDefault?t.preventDefault():t.returnValue=!1}};return"mousewheel"==n?(e.deltaY=-.025*t.wheelDelta,t.wheelDeltaX&&(e.deltaX=-.025*t.wheelDeltaX)):e.deltaY=t.detail,callback(e)}var s="",i=void 0,r=void 0,n=void 0;window.addEventListener?(i="addEventListener",r="removeEventListener"):(i="attachEvent",r="detachEvent",s="on"),n="onwheel"in document.createElement("div")?"wheel":void 0!==document.onmousewheel?"mousewheel":"DOMMouseScroll",eventUtils.addWheelListener=function(t,o,s){e(t,n,o,s),"DOMMouseScroll"==n&&e(t,"MozMousePixelScroll",o,s)},eventUtils.removeWheelListener=function(e,o,s){t(e,n,o,s),"DOMMouseScroll"==n&&t(e,"MozMousePixelScroll",o,s)}}Object.defineProperty(exports,"__esModule",{value:!0});var _cashDom=require("cash-dom"),_cashDom2=_interopRequireDefault(_cashDom),_TinyAnimate=require("TinyAnimate"),_TinyAnimate2=_interopRequireDefault(_TinyAnimate),defaults={maskVisible:!0,maskVisibleOnNoTarget:!1,maskClickThrough:!1,canExit:!1,maskScrollThrough:!0,maskColor:"rgba(0,0,0,.3)",dark:!1,scrollBox:navigator.userAgent.indexOf("AppleWebKit")!=-1?"body":"html",previousText:"Previous",nextText:"Next",finishText:"Finish",animationDuration:400,placement:["bottom","right","top","left"],disableHotkeys:!1,showPrevious:!0,showNext:!0,padding:5,maxHeight:120,maxWidth:250},els={},dims={},errors={notFound:{error:"not_found",message:"Step could not be found."},interrupted:{error:"interrupted",message:"The tour was interrupted"}},eventUtils={onWindowScrollDebounced:throttle(onWindowScroll,16)},service={current:null,start:start,stop:stop,next:next,previous:previous,"goto":goto},initialized=void 0;exports["default"]=service;var template='\n  <div id="Tour" class="hidden">\n    <div id="Tour-box-wrap">\n      <div id="Tour-box">\n        <div id="Tour-tip" class="top center"></div>\n        <div id="Tour-step"></div>\n        <div id="Tour-length"></div>\n        <div id="Tour-close">&#10005</div>\n        <div id="Tour-content">\n          <div id="Tour-inner-content"></div>\n        </div>\n        <div id="Tour-actions">\n          <button id="Tour-previous"></button>\n          <button id="Tour-next"></button>\n        </div>\n      </div>\n    </div>\n    <div id="Tour-masks">\n      <div class="mask top"></div>\n      <div class="mask right"></div>\n      <div class="mask bottom"></div>\n      <div class="mask left"></div>\n      <div class="mask center"></div>\n    </div>\n    <canvas id="Tour-canvas"></canvas>\n  </div>\n';

},{"TinyAnimate":1,"cash-dom":2}]},{},[3])(3)
});