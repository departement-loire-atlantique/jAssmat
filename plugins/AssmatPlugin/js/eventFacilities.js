!function ($) {
  $(function(){
    $("button.uncheckAll").click(function () {
      var $this = $(this);
      var checkbox = $this.parent().parent().find("input:checkbox");
      $this.removeClass("active");
      $this.parent().find("button.checkAll").addClass("active");
      checkbox.attr("checked", false);
      checkbox.each(function(){
    	  var $this = $(this);
    	  var id = $this.attr("id");
    	  var $label = $("label[for="+id+"]");
          $label.removeClass("checked");
      });
      jQuery.plugin.AssmatPlugin.submitForm();
       });
    
    $("button.checkAll").click(function () {
      var $this = $(this);
      var checkbox = $this.parent().parent().find("input:checkbox");
      $this.removeClass("active");
      $this.parent().find("button.uncheckAll").addClass("active");
      checkbox.attr("checked", true);
      checkbox.each(function(){
    	  var $this = $(this);
    	  var id = $this.attr("id");
    	  var $label = $("label[for="+id+"]");
          $label.addClass("checked");
      });
      jQuery.plugin.AssmatPlugin.submitForm();
    });
    $("li > input").live('click', function() {
      var $this = $(this);
      //Gestion du cochage et décochage des enfants.
      if($this.attr("checked")){
    	  var checkAll = $this.parent().find("li button.checkAll").get(0);
    	  if(checkAll != undefined){
    		  $this.parent().find("ul").removeClass("hide");
    	  }
      } else {
    	  var uncheckAll = $this.parent().find("li button.uncheckAll").get(0);
    	  if(uncheckAll != undefined){
    		  uncheckAll.click();
    		  $this.parent().find("ul").addClass("hide");
    	  }
      }
      $this.closest(".facets").find("button.main").click();
      
      //Gestion des liens "Tout cocher" / "Tout décocher".
      var checkbox = $this.parent().parent().find(":checkbox");
      if(checkbox != undefined){
	      var checkboxValues = new Array();
	      var checkboxAllChecked = true;
	      checkbox.each(function() {
	    	if($(this).attr("checked")){
	    		checkboxValues[checkboxValues.length] = true;
	      	}
	    	else{
	    		checkboxValues[checkboxValues.length] = false;
	    	}
	      });
	      for(var i=0; i<checkboxValues.length; i++){
	    	  checkboxAllChecked = checkboxAllChecked && checkboxValues[i];
	      }
	      if(checkboxAllChecked){
	        var $checkboxUncheckAll = $this.parent().parent().find("li > button.uncheckAll").first();
	        if(!$checkboxUncheckAll.hasClass("active")){
	          $checkboxUncheckAll.addClass("active");
	        }
	        var $checkboxCheckAll = $this.parent().parent().find("li > button.checkAll").first();
	        if($checkboxCheckAll.hasClass("active")){
	          $checkboxCheckAll.removeClass("active");
	        }
	      } else {
	        var $checkboxUncheckAll = $this.parent().parent().find("li > button.uncheckAll").first();
	        if($checkboxUncheckAll.hasClass("active")){
	          $checkboxUncheckAll.removeClass("active");
	        }
	        var $checkboxCheckAll = $this.parent().parent().find("li > button.checkAll").first();
	        if(!$checkboxCheckAll.hasClass("active")){
	          $checkboxCheckAll.addClass("active");
	        }
	      }
      }
      if(!$(this).hasClass("noSubmitEven")) {
    	  jQuery.plugin.AssmatPlugin.submitForm();
      }
    });
  })
}(window.jQuery)