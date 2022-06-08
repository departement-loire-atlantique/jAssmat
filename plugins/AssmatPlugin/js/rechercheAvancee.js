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
	    
	    


    

});
	
	
}(window.jQuery);


	          
