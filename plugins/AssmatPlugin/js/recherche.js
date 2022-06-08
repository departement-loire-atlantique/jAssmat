!function($){
	$(function() {
	    var boutonRecherche = $("#btnRechercheAMContainer");
	    var boutonRechercheStuck = $("#sticky-container");
	
	    /* SGU. Le bouton de recherche a une position fixe (bas de page) tant qu'il n'a pas atteint sa place "logique" en fin de formulaire, 
	       après toutes les facettes.
	       Dès que le bouton s'apprête à sortir de la zone visible, il reprend sa position fixe (le "50" dans le calcul ci-dessous
	       correpond à la hauteur du bouton, fixée en css dans le formulaire.
	    */ 
	    $(window).on('scroll', function(){
	    	//console.log("Pos bouton : " + $(boutonRecherche).offset().top + " / Pos bouton dans form : " +$(boutonRechercheStuck).offset().top + " / ScrollTop : " +$(window).scrollTop() + " / Height : "+ $(window).height() + " / " + eval($(window).scrollTop()+$(window).height()));
			if($(boutonRechercheStuck).offset().top < $(boutonRecherche).offset().top ){
	        	  $(boutonRecherche).removeClass('stuck');
	        }
			else if($(boutonRechercheStuck).offset().top > eval($(window).scrollTop()+$(window).height()-50) ){
				$(boutonRecherche).addClass('stuck');
			}
	    });
	    
	    
	    /* SGU. Recopie du code de validation du formulaire présent dans plugin.js, en rajoutant les "return false" sinon
	     * le formulaire est soumis.
	     * 
	     * TODO : voir si on laisse ici ou pas. 
	    */
		jQuery('#formSearchAsmmat').submit( function(e){ 
	      e.preventDefault();
	    	if( (jQuery('#nomAssmat').val() == null || jQuery('#nomAssmat').val() == '' ) && (jQuery('#cityName').val() == null || jQuery('#cityName').val() =='')){
  	 		alert("Le champ 'Par commune' est obligatoire.");
  	 		//return false;
  	 	 } else if( (jQuery('#nomAssmat').val() == null || jQuery('#nomAssmat').val() == '' ) && jQuery('#cityName').val() != null && jQuery('#cityName').val() !='' &&  (jQuery('#idCommune').val() == null  || jQuery('#idCommune').val() =='' )){    		 
  	 		 
  	 		 $("input#idCommune").val('');
		     $("input#codeInsee").val('');
  	 		 alert("La commune de " + jQuery('#cityName').val() + " n'existe pas.");
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
	    	if($("#codeInsee").val()=="44109"){
		    	if($("#listePerimetre").val()=="-10"){
		    		$("#listeQuartiers").show();
		    	}else{
		    		$("#listeQuartiers").hide();
		    	}
	    	}
	    });
	    
	   

	

	});
	
	
}(window.jQuery);


	          
