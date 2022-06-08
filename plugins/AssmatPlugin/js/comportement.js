jQuery(document).ready(function () {
	
	var JOURS = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"];
	
	
	// Permet de copier coller la premiere ligne du jour coché de creneaux horraire dans tous les autres jours cochés pour la semaien courante
	jQuery('.copierColler a').click(function (event) {
		var semaine = jQuery(this).parents('.semaineBlock');

		var PremierJour = "";
		for(cpt = 6 ; cpt >= 0 ; cpt-- ) {
			if(semaine.find('.cal' + JOURS[cpt]).is(':checked')) {
				PremierJour = JOURS[cpt];
			}
		}
		
		if(PremierJour != "") {
			var debCr1 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau1.debut input').val(); 
			var finCr1 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau1.fin input').val(); 
			var debCr2 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau2.debut input').val(); 
			var finCr2 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau2.fin input').val(); 
			var debCr3 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau3.debut input').val(); 
			var finCr3 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau3.fin input').val(); 
			
			for(cpt = 0 ; cpt < 7 ; cpt++ ) {
				if(jQuery(semaine).find('.cal' + JOURS[cpt]).is(':checked')) {
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + ' .creneau1.debut input').val(debCr1); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + ' .creneau1.fin input').val(finCr1); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + ' .creneau2.debut input').val(debCr2); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + ' .creneau2.fin input').val(finCr2); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + ' .creneau3.debut input').val(debCr3); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + ' .creneau3.fin input').val(finCr3); 
				}				
			}
		}				
	});
	// Pour la semaine de vancances (copier/coller du au HTML... et aux autres fonctions déja existantes...)
	// Permet de copier coller la premiere ligne du jour coché de creneaux horraire dans tous les autres jours cochés pour la semaien courante
	jQuery('.copierCollerVac a').click(function (event) {
		var semaine = jQuery(this).parents('.semaineBlock');

		var PremierJour = "";
		for(cpt = 6 ; cpt >= 0 ; cpt-- ) {
			if(semaine.find('#' + JOURS[cpt] +'Vac').is(':checked')) {
				PremierJour = JOURS[cpt]+'Vac';
			}
		}
			
		if(PremierJour != "") {
			var debCr1 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau1Vac.debut input').val(); 
			var finCr1 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau1Vac.fin input').val(); 
			var debCr2 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau2Vac.debut input').val(); 
			var finCr2 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau2Vac.fin input').val(); 
			var debCr3 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau3Vac.debut input').val(); 
			var finCr3 = jQuery(semaine).find('.tableCreneau' + ' .'+PremierJour + ' .creneau3Vac.fin input').val(); 			
			
			for(cpt = 0 ; cpt < 7 ; cpt++ ) {
				if(jQuery(semaine).find('#' + JOURS[cpt]+'Vac').is(':checked')) {
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + 'Vac .creneau1Vac.debut input').val(debCr1); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + 'Vac .creneau1Vac.fin input').val(finCr1); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + 'Vac .creneau2Vac.debut input').val(debCr2); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + 'Vac .creneau2Vac.fin input').val(finCr2); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + 'Vac .creneau3Vac.debut input').val(debCr3); 
					jQuery(semaine).find('.tableCreneau' + ' .'+JOURS[cpt] + 'Vac .creneau3Vac.fin input').val(finCr3); 
				}				
			}
		}				
	});
	
	
	jQuery('#adresseDifferente').change(function () {
		if (!this.checked){
			if(!jQuery('#adresseDifferente').is(':checked')){
				jQuery('.blocAdresseDifferente').hide();
				jQuery('.blocAdresseDifferente input').prop( "disabled", true );
			} 
		} else {
			jQuery('.blocAdresseDifferente').show();
			jQuery('.blocAdresseDifferente input').prop( "disabled", false );
		}
	});
	
	
	/* Accueil régulier/variable - Modalités */
	jQuery("input[name='planningRegulier']").change(function () {
		if(jQuery('input#accueilVarible').is(':checked')) {			
			jQuery('.accueil-occasionnel input').prop("disabled", false );			
			if(!jQuery('input#OuiOcca').is(':checked')) {
				jQuery('.semaine-type input').prop("disabled", false );
			}			
			jQuery('.accueilOcca-semaineType').fadeIn('slow');
		} else {			
			jQuery('.accueil-occasionnel input').prop("disabled", true );
			jQuery('.semaine-type input').prop("disabled", true );
			jQuery('.accueilOcca-semaineType').fadeOut('slow');
		}
    });
	
	
	/* Accueil occasionnel - Modalités */
	jQuery("input[name='accueilOccasionnel']").change(function () {
		if(jQuery('input#OuiOcca').is(':checked')) {			
			jQuery('.semaine-type input').prop("disabled", true );
			jQuery('.semaine-type').fadeOut('slow');
		} else {									
			jQuery('.semaine-type input').prop("disabled", false );
			jQuery('.semaine-type').fadeIn('slow');
		}
    });
	
	
	/* Accueil provisoire - Modalités */ 	
	jQuery("input[name='estProvisoire']").change(function () {
		if(jQuery('input#accueilProvisoireOui').is(':checked')) {			
			jQuery('.accueilProvisoire input').prop("disabled", false );
			jQuery('.accueilProvisoire').fadeIn('slow');
		} else {			
			jQuery('.accueilProvisoire input').prop("disabled", true );
			jQuery('.accueilProvisoire').fadeOut('slow');
		}
    });
	

	
	/* Affichage des jours - Planning */   
    function hideShowJourSemaine(gid, cptJour) { 
    	var semaine = ".semaine" + gid;	 
        var gjour = cptJour; 
        var jour = JOURS[cptJour]
        
        // Pour les semaine 1 à 10 (planning régulier = semaine 1)
        if(gid <= 10) {
	        
	        // Affiche ou cache le jour dans le planning
	        jQuery(semaine + " .cal" + jour).change(function () {
	            if (!this.checked) {
	            	jQuery(semaine + " ." + jour).fadeOut('slow');
	            	jQuery(semaine + " ." + jour + " input").prop( "disabled", true );
	            }else {
	            	jQuery(semaine + " ." + jour).fadeIn('slow');
	            	jQuery(semaine + " ." + jour + " input").prop( "disabled", false );
	            }
	        });               
	        // Permet d'afficher ou de cacher le planning si aucun jour n'est coché
	        jQuery(semaine + " .cal" + jour).change(function () {
	    		if (!this.checked){
	    			if(!jQuery(semaine + " .callundi").is(':checked') && !jQuery(semaine + " .calmardi").is(':checked') && !jQuery(semaine + ' .calmercredi').is(':checked') && !jQuery(semaine + ' .caljeudi').is(':checked') && !jQuery(semaine + ' .calvendredi').is(':checked') && !jQuery(semaine + ' .calsamedi').is(':checked') && !jQuery(semaine + ' .caldimanche').is(':checked')){
	    				jQuery(semaine + " .semainier").hide();
	    				jQuery(semaine + " .renseignementPlanning").hide();
	    			} 
	    		} else {
	    			jQuery(semaine + " .semainier").show();
	    			jQuery(semaine + " .renseignementPlanning").show();
	    		}
	    	}); 
        
        
        // Vacances (planning régulier)
        } else {
	                   	
	    	jQuery('#' + jour + 'Vac').change(function () {
	            if (!this.checked) {
	            	jQuery('.' + jour + 'Vac').fadeOut('slow');
	            	jQuery('.' + jour + 'Vac input').prop( "disabled", true );
	            } else {
	            	jQuery('.' + jour + 'Vac').fadeIn('slow');
	            	jQuery('.' + jour + 'Vac input').prop( "disabled", false );
	            }
	        });
	    		    	
	    	jQuery('#' + jour + 'Vac').change(function () {
	    		if (!this.checked){
	    			if(!jQuery('#lundiVac').is(':checked') && !jQuery('#mardiVac').is(':checked') && !jQuery('#mercrediVac').is(':checked') && !jQuery('#jeudiVac').is(':checked') && !jQuery('#vendrediVac').is(':checked') && !jQuery('#samediVac').is(':checked') && !jQuery('#dimancheVac').is(':checked')){
	    				jQuery('.semainierVac').hide();
	    			} 
	    		} else {
	    			jQuery('.semainierVac').show();
	    		}
	    	});    	
        }      
    } 
    
    
	
    // Gérer les 10 semaines  ( + 1 :  la semaine de vacances pour planning régulier) 
	for(var cpt=1; cpt<=11; cpt++){
	    (function() {
	        var gid = cpt;	        
	        
	        // Gérer le planning par semaine et par jour
	        for(var cptJour=0; cptJour < 7; cptJour++){
	    	    hideShowJourSemaine(gid, cptJour);
	    	}
	        
	        // Ajouter une semaine
	    	jQuery(".ajouterSemaine" + gid).click(function () {				
	    		jQuery('.semaine' + gid).show();
	    		jQuery('.semaine' + gid + ' input').prop( "disabled", false );
	    		jQuery('.ajouterSemaine' + gid).hide();
	    		jQuery('.supprimerSemaine' + (gid - 1)).hide();
	    	});
	    	
	    	// Supprimer une semaine
	    	jQuery(".supprimerSemaine" + gid).click(function () {				
	    		jQuery('.semaine' + gid).hide();
	    		jQuery('.semaine' + gid + ' input').prop( "disabled", true );
	    		jQuery('.ajouterSemaine' + gid).show();
	    		jQuery('.supprimerSemaine' + (gid - 1)).show();
	    	});	        	        
	    })();
	}
	        
		
	/* Affichage du planning vacances scolaires */
		
	// AccueilVacances
	jQuery("input[name='accueilVacances']").change(function () {
		if(jQuery('input#ouiVs').is(':checked')) {
			affichePlanningDifferentVacances();
		} else {
			cachePlanningDifferentVacances();
		}
		// Permet de cacher ou d'afficher le semainier des vancances scolaire (planning régulier)
		gererPlanningDifferentVacances();
	});
		
	// planningDifferentVacances
	jQuery("input[name='planningDifferentVacances']").change(function () {
		gererPlanningDifferentVacances();
	});
			
	// Permet de cacher ou d'afficher le semainier des vancances scolaire (planning régulier)
	function gererPlanningDifferentVacances () {
		if(jQuery('input#memeHVs').is(':checked') && jQuery('input#ouiVs').is(':checked')) {
			afficheHorairesVacances();
		} else {
			cacheHorairesVacances();
		}
	}
		
	function affichePlanningDifferentVacances () {
		jQuery('#planningVacances').fadeIn('slow'); 
		jQuery('#planningVacances input').prop("disabled", false);
	}
	
	function cachePlanningDifferentVacances () {
		jQuery('#planningVacances').fadeOut('slow'); 
		jQuery('#planningVacances input').prop("disabled", true);
	}
		
	function afficheHorairesVacances () {
		jQuery('#horairesVacances').fadeIn('slow'); 
		jQuery('#horairesVacances input').prop("disabled", false);
	}
	
	function cacheHorairesVacances () {
		jQuery('#horairesVacances').fadeOut('slow'); 
		jQuery('#horairesVacances input').prop("disabled", true);
	}

	
			
	/* Affichage des créneaux - Planning */
	
	/* SEMAINE VACANCES */
	
	jQuery(".semaine .ajouterCreneauVac").click(function (event) {				
		jQuery('.semaine .creneau2Vac').show();
		jQuery('.semaine .creneau2Vac input').prop('disabled',false);
		jQuery('.semaine .supprimerCreneau2Vac').show();
		jQuery('.semaine .ajouterCreneauVac').hide();
		jQuery('.semaine .ajouterCreneau3Vac').show();
	});
	
	jQuery(".semaine .ajouterCreneau3Vac").click(function (event) {				
		jQuery('.semaine .creneau3Vac').show();
		jQuery('.semaine .creneau3Vac input').prop('disabled',false);
		jQuery('.semaine .supprimerCreneau2Vac').hide();
		jQuery('.semaine .supprimerCreneau3Vac').show();
	});
	
	jQuery(".semaine .supprimerCreneau2Vac a").click(function (event) {				
		jQuery('.semaine .creneau2Vac').hide();
		jQuery('.semaine .creneau2Vac input').prop('disabled',true);
		jQuery('.semaine .supprimerCreneau2Vac').hide();
		jQuery('.semaine .ajouterCreneauVac').show();
		jQuery('.semaine .ajouterCreneau3Vac').hide();
	});
	
	jQuery(".semaine .supprimerCreneau3Vac a").click(function (event) {				
		jQuery('.semaine .creneau3Vac').hide();
		jQuery('.semaine .creneau3Vac input').prop('disabled',true);
		jQuery('.semaine .supprimerCreneau3Vac').hide();
		jQuery('.semaine .ajouterCreneau3Vac').show();
		jQuery('.semaine .supprimerCreneau2Vac').show();
	});
			
	/* FIN SEMAINE VACANCES */

	
	/* GESTION DES SEMAINES */
	/* AJOUT/SUPPRESION de creneau */
		
	for(var cpt=1; cpt<=10; cpt++){
	    (function() {
	        var gid = cpt;	        
	        var semaine = ".semaine" + gid;
	        
	        // Ajouter un deuxieme créneau
	        jQuery(semaine + " .ajouterCreneau").click(function (event) {
	        	jQuery(semaine + " .creneau2").show();
	        	jQuery(semaine + " .creneau2 input").prop( "disabled", false );
	        	jQuery(semaine + " .supprimerCreneau2").show();
	        	jQuery(semaine + " .ajouterCreneau").hide();
	        	jQuery(semaine + " .ajouterCreneau3").show();	        	
	        	//event.stopPropagation();
	        });
	        
	        // Ajouter un troisieme créneau
			jQuery(semaine + " .ajouterCreneau3").click(function (event) {				
				jQuery(semaine + " .creneau3").show();
				jQuery(semaine + " .creneau3 input").prop( "disabled", false );
				jQuery(semaine + " .supprimerCreneau2").hide();
				jQuery(semaine + " .supprimerCreneau3").show();				
				//event.stopPropagation();
			});
			
	        // Supprimer le deuxieme créneau
			jQuery(semaine + " .supprimerCreneau2 a").click(function (event) {				
				jQuery(semaine + " .creneau2").hide();
				jQuery(semaine + " .creneau2 input").prop( "disabled", true );
				jQuery(semaine + " .supprimerCreneau2").hide();
				jQuery(semaine + " .ajouterCreneau").show();
				jQuery(semaine + " .ajouterCreneau3").hide();				
				//event.stopPropagation();
			});
	        
	        // Supprimer le troisieme créneau
			jQuery(semaine + " .supprimerCreneau3 a").click(function (event) {				
				jQuery(semaine + " .creneau3").hide();
				jQuery(semaine + " .creneau3 input").prop( "disabled", true );
				jQuery(semaine + " .supprimerCreneau3").hide();
				jQuery(semaine + " .ajouterCreneau3").show();
				jQuery(semaine + " .supprimerCreneau2").show();				
				//event.stopPropagation();
			});		
	        
	    })();
	}

	/* FIN GESTION DES SEMAINES */	

	
});