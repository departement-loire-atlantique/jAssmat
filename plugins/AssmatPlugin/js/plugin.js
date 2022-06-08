/**
 * -----------------------------------------------------------------
 *
 *  Plugin's JavaScript sample.
 * 
 *  To include this file :
 *   - in a JSP : jcmsContext.addJavaScript("plugins/{Name}/js/plugin.js");
 *   - or in Java : implements PortalPolicyFilter.setupHeaders()
 * 
 *  You should use JSLint (http://www.jslint.com/) to ensure 
 *  you have a nice and clean JavaScript code.
 * 
 *  More information at :
 *  - http://jqueryboilerplate.com/
 *  - http://docs.jquery.com/Plugins/Authoring
 *  - See Bootstrap's code sample for advanced object wrapper
 * 
 * -----------------------------------------------------------------
 */ 

!function ($) {

  // ------------------------------------------
  //  PUBLIC CODE
  // ------------------------------------------

  // Namespace
  if (!$.plugin) { $.plugin = {}; }
  
  /**
   * Documentation of the Assmat JavaScript class.<br/>
   * <br/>
   * <h5>Description</h5>
   * 
   * @namespace $.plugin.AssmatPlugin
   */
  $.plugin.AssmatPlugin = {
    
		  
		  
				    
				    getLocation: function(){
				        if (navigator.geolocation) {
				        	navigator.geolocation.getCurrentPosition($.plugin.AssmatPlugin.success, $.plugin.AssmatPlugin.errorCallback);
				        } else { 
				            alert("Geolocation is not supported by this browser.");
				        }
				    },
				    
				    success: function(pos) {
				    	  var crd = pos.coords;

				    	  //console.log('Your current position is:');
				    	  //console.log('Latitude : ' + crd.latitude);
				    	  //console.log('Longitude: ' + crd.longitude);
				    	  jQuery('#geoLongId').val(crd.longitude);
				    	  jQuery('#geoLatId').val(crd.latitude);
				    },
				    
				    errorCallback: function(error){
				        switch(error.code){
				            case error.PERMISSION_DENIED:
				                // 0011300: Partager sa géolocalisation - pb si non 
				            	//alert("L'utilisateur n'a pas autorisé l'accès à sa position");
				                break;          
				            case error.POSITION_UNAVAILABLE:
				                alert("L'emplacement de l'utilisateur n'a pas pu être déterminé");
				                break;
				            case error.TIMEOUT:
				                alert("Le service n'a pas répondu à temps");
				                break;
				            }
				    },

		  
    /**
     * Selector : L'id de l'élément input à verifier (sans le #)
     * 
     * Cette fonction surcharge le message d'erreur natif html5
     * 
     */
    addMailVerificationListenner: function(selector){ 
    	var messageErreur = "Cette adresse n’est pas valide. Merci de vérifier votre saisie."
    	var email = document.getElementById(selector);
    	email.addEventListener("keyup", function (event) {
    	  if(email.validity.typeMismatch) {
    	    email.setCustomValidity(messageErreur);
    	  } else {
    	    email.setCustomValidity("");
    	  }
    	});

    },
  
    /**
     * Selector : L'id de l'élément input à verifier (sans le #)
     * 
     * Cette fonction surcharge le message d'erreur natif html5
     * 
     */
  addTelephoneVerificationListenner: function(selector){ 
	  	var messageErreur = "Ce numéro n’est pas valide. Merci de vérifier votre saisie."
	  	var telephone = document.getElementById(selector);
	  	telephone.addEventListener("keyup", function (event) {
	  	  if(telephone.validity.typeMismatch) {
	  		telephone.setCustomValidity("messageErreur");
	  	  } else {
	  		telephone.setCustomValidity("");
	  	  }
	  	});
	  },
  /**
   * 
   * Verification du formulaire
   * 
   */
    addFormVerificationListenner: function(){ 

    	var form = document.getElementById('formContact');
    	var email = document.getElementById('email');
    	var telephone = document.getElementById('telephone');
    	var typeEnvoi = document.getElementsByName('typeEnvoie');
   
     form.addEventListener("submit", function (event) {
    	  // Chaque fois que l'utilisateur tente d'envoyer les données
    	  // on vérifie que le champ email est valide.
    	  if (!email.validity.valid) {
    	    
    	    // S'il est invalide, on affiche un message d'erreur
    	    error.innerHTML = "Veuillez saisisr une adresse e-mail correcte SVP.";
    	    error.className = "error active";
    	    // Et on empêche l'envoi des données du formulaire
    	    event.preventDefault();
    	  }
    	}, false);
    },
    
    
    
    /**
     *Méthode qui permet de verifier si les champs obligatoire du formulaire on été remplis
     * 
     * 
     */
    changeBoxDispo: function(element, checked){
    	//console.log(element.id);
    	var name = element.id;
    	var newValue ="true";
    	if(checked){
    		newValue="true";
    	}else{
    		newValue="false";
    	}
    	jQuery('.'+name).attr("value", newValue);
    	jQuery('#formSearchAsmmat').submit();

    	
    },
    
    

    
    /**
     * Méthode qui permet de verifier si les champs obligatoire du formulaire on été remplis
     * 
     * 
     */
    
    /* SGU. L'envoi auto du formulaire n'est plus souhaité pour le moteur à facettes.
     * On supprime le "submit".
     * 
     * */
 	
    submitForm: function(){   	
  	 	 if( (jQuery('#nomAssmat').val() == null || jQuery('#nomAssmat').val() == '' ) && (jQuery('#cityName').val() == null || jQuery('#cityName').val() =='')){
  	 		alert("Le champ 'Commune ou code postal' est obligatoire.");
  	 		$("#listePerimetre").val("0");
  	 		$("#adresse").val("");
  	 	 } else if( (jQuery('#nomAssmat').val() == null || jQuery('#nomAssmat').val() == '' ) && jQuery('#cityName').val() != null && jQuery('#cityName').val() !='' &&  (jQuery('#idCommune').val() == null  || jQuery('#idCommune').val() =='' )){    		 
	  	 	 
  	 		 $("input#idCommune").val('');
		     $("input#codeInsee").val('');
  	 		 alert("La commune de " + jQuery('#cityName').val() + " n'existe pas.") ;
    	 }else{     		 
    		 // Déchoche les enfants de horaires atypiques si celui-ci n'est pas coché
    		 if(!$("input#cra_67021").is(':checked')) {
    			 $(".secondNiveauUl input").prop('checked', false);
    		 }    		 
    		 jQuery('#formSearchAsmmatAccueil').submit();
    		 //jQuery('#formSearchAsmmat').submit();
    	 }
    	},
    	
    	

    /* SGU : plus utilisé
    searchByNameSubmitForm: function(){   	
   		 jQuery('#formSearchAsmmat').submit();
   	},
   	*/
   	
    
    /**
     * Effectue la recherche à partir de la carte du département qui apparait
     * sur la page de recherche avancée (info-bulles sur les communes)
     */
    getCitySearch: function(){
		 jQuery('#idCommune').val(jQuery("#linkMapCity").attr("attr-city-id"));
    	 jQuery('#cityName').val(jQuery("#linkMapCity").attr("attr-city-name"));
    	 jQuery('#codeInsee').val(jQuery("#linkMapCity").attr("attr-city-insee"));
    	 
    	 jQuery('.selectDatePlace').attr("disabled", true);
    	 jQuery('.selectPlace').attr("disabled", true);
    	 //jQuery('.selectTranche').attr("disabled", true);
    	 
    	 jQuery('#formSearchAsmmat').submit();
    	},
    
    
    /**
     *Méthode qui verifie si un mot de passe Commence bien par une majuscule, contient au moins 8 caracteres et un chiffre
     * 
     * 
     */
	    verifyMotDePasse: function(selector, texte){
	    	
	     var nbCaracterMin = 7;	
	     var pass= jQuery(selector).val();
	   	 var passConfirm= jQuery(selector).val();
	   	 if(passConfirm !=""){
		   	 var isOk= (/[a-z]/.test(pass) && /[A-Z]/.test(pass) && pass.length >nbCaracterMin  && /\d/.test(pass));
		   	 if(!isOk){
		   	    
		   		jQuery(".alert-block.alertPass p").text(texte);
		   		 jQuery(".alert-block.alertPass").show();
		   	  
		   	 }else{
		   		jQuery(".alert-block.alertPass").hide();
		   	 }
	   	 }
	   	},

	   	/**
	   	 * Affiche ou cache un element
	   	 * @param elem l'element dont la visibilite va etre modifiee
	   	 * @param visible la visibilite qu'on veut appliquer à l'element
	   	 */
		toggle: function(elem, visible){
			if(visible === 'show'){
				document.getElementById(elem).show();
			} else {
				document.getElementById(elem).hide();
			}
	  	},


	   	/**
	   	 * Affiche ou cache un element
	   	 * @param selector1 le premier selecteur dont va dépendre la visibilite de l'element
	   	 * @param selector2 le second selecteur dont va dépendre la visibilite de l'element
	   	 * @param elem l'element dont la visibilite va etre modifiee
	   	 * @param visible la visibilite qu'on veut appliquer à l'element
	   	 */
	  	doubleToggle: function(selector1, selector2, elem, visible){
		  		if(document.getElementById(selector1).checked && document.getElementById(selector2).checked){
		  			document.getElementById(elem).hide();
		  		}
	  	},

	  	
	  	autoComplete: function(codeInsee, cityId, cityName, cityLong, cityLat) {
	  		//console.log(codeInsee+" "+cityId+" "+cityName+" "+cityLong+" "+cityLat);
	    	$("input#idCommune").val(cityId);
	    	$("input#codeInsee").val(codeInsee);
	    	$("input#cityName").val(cityName);
	    	$("input#cityName").attr("data-longitude",cityLong);
	    	$("input#cityName").attr("data-latitude",cityLat);
	    	$("#longitude").val(cityLong);
	    	$("#latitude").val(cityLat);
	    },
	    autoCompleteAdresse: function(adresseLong, adresseLat) {
	  		//console.log(adresseLong+" "+adresseLat);
	    	$("input#adresse").attr("data-longitude",adresseLong);
	    	$("input#adresse").attr("data-latitude",adresseLat);
	    	$("#longitude").val(adresseLong);
	    	$("#latitude").val(adresseLat);
	    },
	  
   
	  	/**
	  	 * Permet d'enregistrer les modifications du semainier
	  	 * @param semainier le tableau 
	  	 */
	  	semainier: function(input){
			col=['lundi','mardi','mercredi','jeudi','vendredi','samedi','dimanche'];
			ligne=['avant_ecole','matin','midi','pm','apres_ecole','nuit'];
			input = document.getElementById(input);
			res = '';

			col.forEach(function(element1){
				ligne.forEach(function(element2){
					elem = document.getElementById(element1+'_'+element2);
					if(elem.checked){
						res += '"'+element1+'_'+element2+'":true,';
					} else {
						res += '"'+element1+'_'+element2+'":false,';
					}
				});
			});

			// suppression la virgule en fin de l'objet
			res = res.substring(0, res.length - 1);

			// on change la valeur du champ caché
			input.value = '{' + res + '}';
	  	},
	    
	    /**
	  	 * Réinitialisation de la liste "Périmètre" du formulaire de la recherche avancée
	  	 * Si la commune ou l'adresse change, réinitialiser cette liste. 
	  	 */
	  	initListePerimetre: function(){
	  		//$("#listePerimetre").val("0");
			//$(".distanceCourte").hide();
			if($("#codeInsee").val()!="44109"){
				$(".optQuartier").hide();
		    }
			else{
				//$("#listePerimetre option").hide();
				$("#listePerimetre option[value='0']").show();// "Toute la commune"
				$(".optQuartier").show();
			}

			var checkboxQuartiers = $("#listeQuartiers").find("input:checkbox");
			checkboxQuartiers.attr("checked", false);
			$("#listeQuartiers label").removeClass("checked");
	    	$("#listeQuartiers").hide();
	    	
	    	var checkboxMicroQuartiers = $("#listeMicroQuartiers").find("input:checkbox");
			checkboxMicroQuartiers.attr("checked", false);
	    	$("#listeMicroQuartiers label").removeClass("checked");
	    	$("#listeMicroQuartiers").hide();
	  	}
    }

  // ------------------------------------------
  //  DOM READY CODE
  // ------------------------------------------
  
  $(document).ready(function($) {
	  
	  $(".clearAdressRepresentant").on( "click", function() {
		  var representant = $(this).attr("data-representant");
		  $("#adresseAutoCompleteRepresentant"+representant).val("");
		  $("#voie"+representant).val("");
		  $("#codePostal"+representant).val("");
		  $("#ville"+representant).val("");
		  $("#clearAdressRepresentant"+representant).hide();
		  $("#adresseAutoCompleteRepresentant"+representant).removeAttr("disabled");
	  });
	  
	  $("input.adresseAutoCompleteRepresentant").change(function () {
		  var representant = $(this).attr("data-representant");
		  var liSelect = $(".dropdown-menu-adress-"+representant +" li.active a");
		  if(liSelect.length == 1) {
			  $("#voie"+representant).val( $(liSelect).attr("data-street") );
			  $("#codePostal"+representant).val( $(liSelect).attr("data-cityPostCode") );
			  $("#ville"+representant).val( $(liSelect).attr("data-cityname") );
			  $("#adresseAutoCompleteRepresentant"+representant).val(liSelect.html());
			  $("#clearAdressRepresentant"+representant).show();
			  $("#adresseAutoCompleteRepresentant"+representant).attr("disabled", "disabled");
		  }		  
	  });
	  
	  
	  $("input.saisieAdresseCheckBox").change(function () {
		  var representant = $(this).attr("data-representant");
		  if($(this).prop( "checked" )) {
			  $("#blockSaisieAdresse"+representant).show();
			  $("#adresseAutoCompleteRepresentant"+representant).val("");
			  $("#clearAdressRepresentant"+representant).hide();
			  $("#adresseAutoCompleteRepresentant"+representant).attr("disabled", "disabled");			  
		  }else {
			  $("#blockSaisieAdresse"+representant).hide();
			  $("#adresseAutoCompleteRepresentant"+representant).removeAttr("disabled");			 
		  }
		  $("#voie"+representant).val("");
		  $("#codePostal"+representant).val("");
		  $("#ville"+representant).val("");
		  $("#adresseAutoCompleteRepresentant"+representant).val("");
		  $("#clearAdressRepresentant"+representant).hide();
	  });
	  
	  
	  $("input#cityName.autoCompleteCityJSVerif").change(function () {
		  	// $("input#cityName.autoCompleteCityJSVerif").removeClass("hide");
			$li = $(".dropdown-menu-cities li.active a");    	
			//if($li.attr("title") == $(this).val()){
			if($($li).length == 1) {
				$.plugin.AssmatPlugin.autoComplete($li.attr("data-cityCode"), $li.attr("data-cityId"), $li.attr("data-cityName"),$li.attr("data-cityLong"),$li.attr("data-cityLat"));
				$("#cityName").attr("readonly",true);
	  		}else {
	  		 // Commune non cliquée dans la liste des choix (sortie avec que la liste soit apparue)	  		
	  		 // Cherche la commune dans JCMS 
	  		 var textSearch = $("input#cityName").val();
  	 		 $.getJSON('plugins/AssmatPlugin/jsp/autocomplete/acsearchCityJson.jsp?text=' + textSearch, function (data) { 	 			 	  	 			 
  	 			 if(data.codeInsee != '') { 	 			 
  	 				 // Commune trouvée dans JCMS
  	 				 $.plugin.AssmatPlugin.autoComplete(data.codeInsee, data.idCommune, data.cityName, data.cityLong, data.cityLat);	 			 
  	 			 } else { 
  	 				 // Commune introuvable
  	 				 $("input#idCommune").val('');
  	 				 $("input#codeInsee").val('');		    	
			  	 	 if( (jQuery('#nomAssmat').val() == null || jQuery('#nomAssmat').val() == '' ) && (jQuery('#cityName').val() == null || jQuery('#cityName').val() =='')){
			  	 		alert("Le champ 'Commune ou code postal' est obligatoire.");
			  	 	 } else if (jQuery('#nomAssmat').val() == null || jQuery('#nomAssmat').val() == '' ) {
			  	 		alert("La commune de " + jQuery('#cityName').val() + " n'est pas trouvée.") ;
			  	 	 } 	 				 
  	 			 }  	 			 
  	 		 });	  			
	  		}
			
			// On modifie l'attribut "data-jalios-ajax-refresh-url" du champ de recherche d'adresse, pour passer la ville en paramètre
			var urlRechercheAdresse = "plugins/AssmatPlugin/jsp/autocomplete/acAdresseInCity.jsp?cityCode="+$li.attr("data-cityCode");
			$("#adresse").attr('data-jalios-ajax-refresh-url',urlRechercheAdresse);
			
			// Si on choisit "Nantes", débloquer l'option "Par quartier" dans la liste déroulante.
			if($("#codeInsee").val()=="44109"){
				$("#listePerimetre option").hide();
				$("#listePerimetre option[value='0']").show();// "Toute la commune"
				$(".optQuartier").show();
				$("#listePerimetre").val("0");
		    }
			// Si on passe de Nantes à une autre commune : 
			else{
				//$("#listePerimetre option").show();
				$("#adresse").val("");
		    	$.plugin.AssmatPlugin.initListePerimetre();
		    }
			
			
		});
	  
		  
	
	  
	/*
	 * Si l'adresse est remplie, on active les courtes distances dans la recherche par périmètre
	 * Si la commune est une grande ville (Nantes pour l'instant), on active la distance "100m"
	 * et on masque la "200m".
	 * Pour les autres communes, on active la "200m", et pas la "100m"
	 * 
 	 * */
	  $("input#adresse").change(function () {
		  $.plugin.AssmatPlugin.initListePerimetre();
		  $("#listePerimetre").val("0");
			if($("input#adresse").val()!=""){
				$(".distanceCourte").show();
				if($("#codeInsee").val()=="44109"){
					$("#listePerimetre option").show();
					//$(".distanceCourte.agglo").show();
					$(".distanceCourte.notAgglo").hide();
				}
				else{
					$(".distanceCourte.agglo").hide();
				}
		    }
		});	  
	
	  $("input.adresseAutoComplete").on('change blur',function() {
		  var liSelect = $(".dropdown-menu-adress li.active a");
		  if($(this).val()!=""){
			  if(liSelect.length == 1) {
			  	$("#adresse").val(liSelect.html());
			  	$.plugin.AssmatPlugin.autoCompleteAdresse($(liSelect).attr("data-long"), $(liSelect).attr("data-lat"));
			  }
			  $("#adresse").attr("readonly",true);
		  }
		  
	  });
	  
  	  
	  
		jQuery('#formSearchAsmmat').submit( function(e){ 
		  e.preventDefault();
			if( (jQuery('#nomAssmat').val() == null || jQuery('#nomAssmat').val() == '' ) && (jQuery('#cityName').val() == null || jQuery('#cityName').val() =='')){
			alert("Le champ 'Commune ou code postal' est obligatoire.");
			//return false;
		 } else if( (jQuery('#nomAssmat').val() == null || jQuery('#nomAssmat').val() == '' ) && jQuery('#cityName').val() != null && jQuery('#cityName').val() !='' &&  (jQuery('#idCommune').val() == null  || jQuery('#idCommune').val() =='' )){    		 
			 
			 $("input#idCommune").val('');
		     $("input#codeInsee").val('');
			 alert("La commune de " + jQuery('#cityName').val() + " n'est pas trouvée.");
			 //return false;
		 }else{     		 
			 // Décoche les enfants de horaires atypiques si celui-ci n'est pas coché
			 if(!$("input#cra_67021").is(':checked')) {
				 $(".secondNiveauUl input").prop('checked', false);
			 }    		 
			 jQuery('#formSearchAsmmatAccueil').submit();
			 jQuery('#formSearchAsmmat')[0].submit();    		    		 
		 }
		});
	    
	    
    /* SGU. Si la commune est Nantes : si on choisit la recherche par quartier, faire apparaitre la liste des quartiers. */ 
    $("#listePerimetre").on('change keyup', function(){
    	$.plugin.AssmatPlugin.initListePerimetre();
    	if($("#codeInsee").val()=="44109"){
    		switch($("#listePerimetre").val()){
	    		case "-10":
	    			$("#listeMicroQuartiers").hide();
	        		$("#listeQuartiers").show();
	        		break;
	    		case "-20":
	    			$("#listeQuartiers").hide();
	    			$("#listeMicroQuartiers").show();
	        		break;
	    		default:
	    			$("#listeQuartiers").hide();
	    			$("#listeMicroQuartiers").hide();
		    	/*if($("#listePerimetre").val()=="-10"){
		    		$("#listeQuartiers").show();
		    	}else{
		    		$("#listeQuartiers").hide();
		    	}*/
    		}
    	}
    });
	    
	 /*
	  * Vide le champ input qui précède la croix/
	  * Reprise de plugins\CorporateIdentityPlugin\js\facets\citiesCommunitiesFacet.js
	  * Modification pour que la réinit des input text via les bouton "croix" soit générique.
	  */
	$(".citiesFacet input[type=text]").on('change blur keypress', function(){    
		var $this = $(this);
		$btnReset = $this.closest("fieldset").find("button.resetCross");
		if($this.val() != ""){
			$btnReset.css('display', 'inline-block');
		}else{
			$btnReset.hide();
		}
    });
	
	// Active/désactive les champs "adresse" et "perimetre"
	$("#cityName").on('blur change',function (){
		var $this = $(this);
		if($this.val() != ""){
			$this.attr("readonly",true);
			$("#cityFieldSet div.inputwithaction").addClass("readonly");
			$(".adresseFacet div.inputwithaction").removeClass("readonly");
			$("#adresse").removeAttr("readonly");
			$("#adresse").focus();
			$("#listePerimetre").removeAttr("disabled");
			$("fieldset.perimetre div.form-select").removeClass("readonly");
		}else{
			$("#cityFieldSet div.inputwithaction").removeClass("readonly");
			$(".adresseFacet div.inputwithaction").addClass("readonly");
			$("#adresse").attr("readonly",true);
			$("fieldset.perimetre div.form-select").addClass("readonly");
			$("#listePerimetre").prop("disabled", true );
		}
	});
	
	// Désactive le champ "Assmat"
	$("#nomAssmat").on('blur change',function (){
		var $this = $(this);
		if($this.val() != ""){
			$this.attr("readonly",true);
			$(".citiesFacet div.inputwithaction").addClass("readonly");
		}else{
			$(".citiesFacet div.inputwithaction").removeClass("readonly");
			$this.attr("readonly",false);
		}
	});	
	
	//Réinitialisation de la commune. On vide aussi l'adresse.
    $("#resetCity").click(function () {
    	$this = $(this);
    	$this.hide();
    	$("#cityName").val("");
    	$("#cityName").removeAttr("readonly");
    	$("#cityFieldSet div.inputwithaction").removeClass("readonly");
    	$("#idCommune").val("");
    	$("#codeInsee").val("");
    	$("#cityId").val("");
    	$("#longitude").val("");
	    $("#latitude").val("");
    	$("#adresse").val("");
    	$("#resetAddress").hide();
    	$.plugin.AssmatPlugin.initListePerimetre();
    	$(".distanceCourte").hide();
    	$("#listePerimetre").val("0");
    	
    	// Active/désactive les champs "adresse" et "perimetre"
    	if($("#cityName").val() == ""){
			$(".adresseFacet div.inputwithaction").addClass("readonly");
			$("#adresse").attr("readonly",true);
			$("fieldset.perimetre div.form-select").addClass("readonly");
			$("#listePerimetre").prop("disabled", true );
		}
	});
	
    //Réinitialisation de l'adresse.
	$("#resetAddress").click(function () {
    	$this = $(this);
    	$this.hide();
    	//$inputName = $this.closest("fieldset").find("input[type=text]");
    	$("#longitude").val($("#cityName").attr("data-longitude"));
	    $("#latitude").val($("#cityName").attr("data-latitude"));
    	$("#adresse").val("");
    	$("#adresse").removeAttr("readonly");
    	$(".adresseFacet div.inputwithaction").removeClass("readonly");
    	$("#listePerimetre option").hide();
		$("#listePerimetre option[value='0']").show();// "Toute la commune"
		if($("#codeInsee").val()=="44109"){
				$(".optQuartier").show();
		}
    	$.plugin.AssmatPlugin.initListePerimetre();
    	$(".distanceCourte").hide();
    	$("#listePerimetre").val("0");
	});	  
	
    //Réinitialisation du nom de l'assmat.
	$("#resetNomAssmat").click(function () {
    	$this = $(this);
    	$this.hide();
    	$("#nomAssmat").val("");
    	$("#nomAssmat").attr("readonly",false);
    	$(".citiesFacet div.inputwithaction").removeClass("readonly");
	});		
	  

	  		    
  
  });

}(window.jQuery);


  // ------------------------------------------
  //  EVERYWHERE ELSE
  // ------------------------------------------

  // Do not use $ because of noConflict() with Prototype
  // jQuery.plugin.MyPlugin.publicFunction(); 

  // To do things on DOMReady in HTML Page use jalios tag
  // <jalios:javascript>
  //   jQuery('#page .myplugin').otherFunction();
  // </jalios:javascript>

 