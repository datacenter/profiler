(function() {
  'use strict';
  angular
    .module('profiler')
    .factory('util', [function() {
      var fn = {};

      fn.getFormmatedDate = function(date) {
        try {
          var yyyy = date.getFullYear();
          var m = (date.getMonth() + 1); //to make the month starts from index 1
          var d = date.getDate();
          var mm = (m < 10) ? ('0' + m) : m;
          var dd = (d < 10) ? ('0' + d) : d;
          var str = (yyyy + '-' + mm + '-' + dd);
          return str;
        } catch (err) {
          return '';
        }
      }; //getFormmatedDate



      fn.getItemsByPropVal = function(list, prop, val) {
        var i, val, items = [];
        for (var i = 0; i < list.length; i++) {
          if (val === list[i][prop]) {
            items.push(list[i]);
          }
        }
        return items;
      };

      fn.getUniqueValuesOfProp = function(list, prop) {
        var i, val, items = [];
        for (var i = 0; i < list.length; i++) {
          val = list[i][prop];
          if (items.indexOf(val) === -1) {
            items.push(val);
          }
        }
        return items;
      };

      return fn;
    }])


})();
