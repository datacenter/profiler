angular.module('maple.formvalidation', []).
directive('mplFormValidation', function() {
  return {
    restrict: 'A',
    require: '^form',
    link: function(scope, el, attrs, formCtrl) {
      
      var formGroups = el[0].querySelectorAll(".form-group");
      var formGroupsDict = [];
      var ngFormGroups = [];
      var inputNames = [];
      var config = {
        'ERROR_CLASS': 'has-error',
        'SUCCESS_CLASS': 'has-success'
      }

      el.bind('submit', function(){
        angular.forEach(formGroupsDict, function(formGroupDict){
          toggleErrorMessage(formGroupDict.ngFormGroup, formGroupDict.inputName);
        });
        
      });

      angular.forEach(formGroups, function(formGroup){
        
        // convert the native dom element to an angular element
        var ngFormGroup = angular.element(formGroup);        
        // get input element, which has the 'name' attribute
        var inputEl = formGroup.querySelector("[name]");
        // convert the native input element to an angular element
        var ngInputEl = angular.element(inputEl);
        // get the name on the input element
        var inputName = ngInputEl.attr('name');

        ngFormGroups.push(ngFormGroup);
        inputNames.push(inputName);
        formGroupsDict.push({
          'ngFormGroup' : ngFormGroup,
          'inputName' : inputName
        });

        // only apply the has-error class after the user leaves the text box
        ngInputEl.bind('blur', function() {
          ngFormGroup.toggleClass(config.SUCCESS_CLASS, formCtrl[inputName].$valid);
          ngFormGroup.toggleClass(config.ERROR_CLASS, formCtrl[inputName].$invalid);
        });

      });



      function toggleErrorMessage(ngElem, elemName){
        ngElem.toggleClass(config.ERROR_CLASS, formCtrl[elemName].$invalid);
      }

      function reset(ngElem){
        ngElem.removeClass(config.SUCCESS_CLASS+' '+config.ERROR_CLASS);
      }
      
    }
  }


});
