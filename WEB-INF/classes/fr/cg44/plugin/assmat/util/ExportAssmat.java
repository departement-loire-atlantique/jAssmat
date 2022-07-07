package fr.cg44.plugin.assmat.util;

import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.Disponibilite;
import generated.ProfilASSMAT;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.jcms.WikiRenderer;
import com.jalios.util.HtmlUtil;
import com.jalios.util.ReflectUtil;
import com.jalios.util.Util;


/**
 * Classe de gestion des Exports
 * @author kfauconnier
 *
 */
public class ExportAssmat {

  final static Channel channel = Channel.getChannel();

  private static final String SEPARATOR = ";";
  private static final String DOUBLE_QUOTE = "\"";


  /**
   * Creation du header pour l'export.
   * @param printWriter
   */
  private static void getHeaderMemberAm(PrintWriter printWriter){		
    StringBuilder header = new StringBuilder();

    // Information sur le membre
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Civilité") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Prénom") + DOUBLE_QUOTE + SEPARATOR);		
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Nom") + DOUBLE_QUOTE + SEPARATOR);		
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("E-mail") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Mobile") + DOUBLE_QUOTE + SEPARATOR);

    // Date de dernière mise à jour du profil ou d'une dispo liée
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Date de dernière mise à jour (solis)") + DOUBLE_QUOTE + SEPARATOR);
    
    // Information sur le profil
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Visibilité site") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Type login") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Compte activé") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Profil renseigné") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Téléphone fixe") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Visbilité téléphone fixe") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Visibilité téléphone portable") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Créneau horaires d'appel") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Visibilité adresse email") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Afficher contact uniquement si dispo") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Canal de communication site") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Autorisation site") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Autorisation CAF") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Autorisation relais") + DOUBLE_QUOTE + SEPARATOR);	
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Accueil temps partiel") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Accueil périscolaire") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Accueil mercredi") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Accueil pendant les vacances scolaires") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Horaires atypiques") + DOUBLE_QUOTE + SEPARATOR);		
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Avant 7h") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Après 20h") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Le samedi") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Le dimanche") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("La nuit") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Accepte dépannage") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Précisions dépannage") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Accueil enfant handicap") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Précisions enfant handicap") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Logement accessible") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Précisions logement accessible") + DOUBLE_QUOTE + SEPARATOR);

    // Information solis     
//    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Civilité (solis)") + DOUBLE_QUOTE + SEPARATOR);
//    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Nom (solis)") + DOUBLE_QUOTE + SEPARATOR);
//    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Prénom (solis)") + DOUBLE_QUOTE + SEPARATOR);        
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Date de naissance (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Numéro dossier (solis)") + DOUBLE_QUOTE + SEPARATOR);    
//    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Téléphone principal (solis)") + DOUBLE_QUOTE + SEPARATOR);
//    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Téléphone portable (solis)") + DOUBLE_QUOTE + SEPARATOR);    
//    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Email (solis)") + DOUBLE_QUOTE + SEPARATOR);    
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Date premier agrement (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Formation initiale (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Date dernier renouvellement (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Date prochain renouvellement (solis)") + DOUBLE_QUOTE + SEPARATOR);    
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Date dernier renouvellement MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Date prochain renouvellement MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);   
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Adresse domicile (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Cp domicile (solis)") + DOUBLE_QUOTE + SEPARATOR);    
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Commune domicile (solis)") + DOUBLE_QUOTE + SEPARATOR);   
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Exerce MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Nom MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);    
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Adresse MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);   
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Cp MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Commune MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);   
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("En activité (solis)") + DOUBLE_QUOTE + SEPARATOR);    
    // 8 tranches d'age solis
    for(int i = 1; i<= 8 ; i++) {
     header.append(DOUBLE_QUOTE + escapeDoubleQuote("Place " + i + " agrement tranche age key (solis)") + DOUBLE_QUOTE + SEPARATOR);
     header.append(DOUBLE_QUOTE + escapeDoubleQuote("Place " + i + " tranche age key (solis)") + DOUBLE_QUOTE + SEPARATOR);
     header.append(DOUBLE_QUOTE + escapeDoubleQuote("Place " + i + " tranche age (solis)") + DOUBLE_QUOTE + SEPARATOR);    
     header.append(DOUBLE_QUOTE + escapeDoubleQuote("Place " + i + " lib compl (solis)") + DOUBLE_QUOTE + SEPARATOR);
     header.append(DOUBLE_QUOTE + escapeDoubleQuote("Place " + i + " nb place (solis)") + DOUBLE_QUOTE + SEPARATOR);
     header.append(DOUBLE_QUOTE + escapeDoubleQuote("Place " + i + "saisie dispo (solis)") + DOUBLE_QUOTE + SEPARATOR);
    }   
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Staut (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Autorisation activation (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Latitude (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Longitude (solis)") + DOUBLE_QUOTE + SEPARATOR);    
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Latitude MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Longitude MAM (solis)") + DOUBLE_QUOTE + SEPARATOR);    
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Id ua (solis)") + DOUBLE_QUOTE + SEPARATOR);    
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Exerce domicile(solis)") + DOUBLE_QUOTE + SEPARATOR);
    header.append(DOUBLE_QUOTE + escapeDoubleQuote("Aide caf (solis)") + DOUBLE_QUOTE + SEPARATOR);
    
    printWriter.println(header);
  }

  /**
   * Creer le CSV d'export des AM
   * @param profilSet
   * @param paramWriter
   */
  public static void exportCsvAM(Set<ProfilASSMAT> profilSet, Writer paramWriter) {
    PrintWriter localPrintWriter = new PrintWriter(paramWriter);
    getHeaderMemberAm(localPrintWriter);
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    for(ProfilASSMAT itProfil : profilSet) {      
      Member itMbr = ProfilManager.getInstance().getMember(itProfil);
      if(Util.isEmpty(itMbr)) {
        continue;
      }
      StringBuilder chaine = new StringBuilder();
      
      // Information sur le membre      
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote(itMbr.getSalutation()) + DOUBLE_QUOTE); 
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote(itMbr.getFirstName()) + DOUBLE_QUOTE); 
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote(itMbr.getLastName()) + DOUBLE_QUOTE); 
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote(itMbr.getEmail()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote(itMbr.getMobile()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      
      // Date de dernière mise à jour du profil ou d'une dispo liée
      Date itDateModifProfil = itProfil.getMdate();
      List<Disponibilite> itDispoList = ProfilManager.getInstance().getDisponibilitesList(itMbr,"");
      for(Disponibilite itDispo : itDispoList) {
        if(itDateModifProfil.getTime() < itDispo.getMdate().getTime()) {
          itDateModifProfil = itDispo.getMdate();
        }
      }
      chaine.append(DOUBLE_QUOTE + sdf.format(itDateModifProfil) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      
      // Information sur le profil
      chaine.append(DOUBLE_QUOTE + itProfil.getVisibiliteSiteLabel(channel.getLanguage()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getTypeLoginLabel(itProfil.getTypeLogin() != null ? itProfil.getTypeLogin() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + itProfil.getCompteActiveLabel(channel.getLanguage()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + itProfil.getProfilRenseigneLabel(channel.getLanguage()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote(itProfil.getTelephoneFixe(channel.getLanguage()))  + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getVisbiliteTelephoneFixeLabel(itProfil.getVisbiliteTelephoneFixe() != null ? itProfil.getVisbiliteTelephoneFixe() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getVisibiliteTelephonePortableLabel(itProfil.getVisibiliteTelephonePortable() != null ? itProfil.getVisibiliteTelephonePortable() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote (itProfil.getCreneauHorairesDappel()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getVisibiliteAdresseEmailLabel(itProfil.getVisibiliteAdresseEmail() != null ? itProfil.getVisibiliteAdresseEmail() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + itProfil.getAfficherContactUniquementSiDLabel(channel.getLanguage()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getCanalDeCommunicationSiteLabel(itProfil.getCanalDeCommunicationSite() != null ? itProfil.getCanalDeCommunicationSite() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + itProfil.getAutorisationSiteLabel(channel.getLanguage()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + itProfil.getAutorisationCAFLabel(channel.getLanguage()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + itProfil.getAutorisationRelaisLabel(channel.getLanguage()) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);          
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getAccueilTempsPartielLabel(itProfil.getAccueilTempsPartiel() != null ? itProfil.getAccueilTempsPartiel() : "" ) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getAccueilPeriscolaireLabel(itProfil.getAccueilPeriscolaire() != null ? itProfil.getAccueilPeriscolaire() : "" ) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getAccueilMercrediLabel(itProfil.getAccueilMercredi() != null ? itProfil.getAccueilMercredi() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getAccueilPendantLesVacancesScoLabel(itProfil.getAccueilPendantLesVacancesSco() != null ? itProfil.getAccueilPendantLesVacancesSco() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);          
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getHorairesAtypiquesLabel(itProfil.getHorairesAtypiques() != null ? itProfil.getHorairesAtypiques() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getAvant7hLabel(itProfil.getAvant7h()!= null ? itProfil.getAvant7h() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getApres20hLabel(itProfil.getApres20h()!= null ? itProfil.getApres20h() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getLeSamediLabel(itProfil.getLeSamedi()!= null ? itProfil.getLeSamedi() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getLeDimancheLabel(itProfil.getLeDimanche()!= null ? itProfil.getLeDimanche() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getLaNuitLabel(itProfil.getLaNuit()!= null ? itProfil.getLaNuit() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getAccepteDepannageLabel(itProfil.getAccepteDepannage()!= null ? itProfil.getAccepteDepannage() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + HtmlUtil.html2text( WikiRenderer.wiki2html(itProfil.getPrecisionsDepannage())) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getAccueilEnfantHandicapLabel(itProfil.getAccueilEnfantHandicap()!= null ? itProfil.getAccueilEnfantHandicap() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + HtmlUtil.html2text( WikiRenderer.wiki2html(itProfil.getPrecisionsEnfantHandicap())) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + ProfilASSMAT.getLogementAccessibleLabel(itProfil.getLogementAccessible() != null ? itProfil.getLogementAccessible() : "") + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + HtmlUtil.html2text( WikiRenderer.wiki2html(itProfil.getPrecisionsLogementAccessible())) + DOUBLE_QUOTE);
      chaine.append(SEPARATOR);
      
      // Information solis  
      SolisManager solisManager = SolisManager.getInstance();
      AssmatSolis itSolis = Util.getFirst(solisManager.getAssmatSolisByNumAgrement(itProfil.getNum_agrement()));      
//      chaine.append(DOUBLE_QUOTE + itSolis.getCiviliteAssmat() + DOUBLE_QUOTE).append(SEPARATOR);
//      chaine.append(DOUBLE_QUOTE + itSolis.getNomAssmat() + DOUBLE_QUOTE).append(SEPARATOR);
//      chaine.append(DOUBLE_QUOTE + itSolis.getPrenomAssmat() + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getDateNaissAssmat() != null ? sdf.format(itSolis.getDateNaissAssmat()) : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + itSolis.getNumDossierAssmat() + DOUBLE_QUOTE).append(SEPARATOR);  
//      chaine.append(DOUBLE_QUOTE + (itSolis.getTelPrincipal() != null ? itSolis.getTelPrincipal() : "") + DOUBLE_QUOTE).append(SEPARATOR);     
//      chaine.append(DOUBLE_QUOTE + (itSolis.getTelPortable() != null ? itSolis.getTelPortable() : "") + DOUBLE_QUOTE).append(SEPARATOR);
//      chaine.append(DOUBLE_QUOTE + (itSolis.getEmailAssmat() != null ? itSolis.getEmailAssmat() : "") + DOUBLE_QUOTE).append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + (itSolis.getDatePremierAgrement() != null ? sdf.format(itSolis.getDatePremierAgrement()) : "") + DOUBLE_QUOTE).append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + (itSolis.getFormationInitiale() != null && itSolis.getFormationInitiale() ? "Oui" : "Non") + DOUBLE_QUOTE).append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + (itSolis.getDateDernierRenouvellement() != null ? sdf.format(itSolis.getDateDernierRenouvellement()) : "") + DOUBLE_QUOTE).append(SEPARATOR);           
      chaine.append(DOUBLE_QUOTE + (itSolis.getDateProchainRenouvellement() != null ? sdf.format(itSolis.getDateProchainRenouvellement()) : "") + DOUBLE_QUOTE).append(SEPARATOR);            
      chaine.append(DOUBLE_QUOTE + (itSolis.getDateDernierRenouvellementMam() != null ? sdf.format(itSolis.getDateDernierRenouvellementMam()) : "") + DOUBLE_QUOTE).append(SEPARATOR);      
      chaine.append(DOUBLE_QUOTE + (itSolis.getDateProchainRenouvellementMam() != null ? sdf.format(itSolis.getDateProchainRenouvellementMam()) : "") + DOUBLE_QUOTE).append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote ((itSolis.getAdresseDomicile() != null ? itSolis.getAdresseDomicile() : "")) + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getCpDomicile()  != null ? itSolis.getCpDomicile() : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getCommuneDomicile()  != null ? itSolis.getCommuneDomicile() : "") + DOUBLE_QUOTE).append(SEPARATOR);     
      chaine.append(DOUBLE_QUOTE + (itSolis.getExerceMam() != null && itSolis.getExerceMam() ? "Oui" : "Non") + DOUBLE_QUOTE).append(SEPARATOR);         
      chaine.append(DOUBLE_QUOTE + (itSolis.getNomMam() != null ? itSolis.getNomMam() : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + escapeDoubleQuote ((itSolis.getAdresseMam() != null ? itSolis.getAdresseMam() : "")) + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getCpMam() != null ? itSolis.getCpMam() : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getCommuneMam() != null ? itSolis.getCommuneMam() : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getEnActivite() != null && itSolis.getEnActivite() ? "Oui" : "Non") + DOUBLE_QUOTE).append(SEPARATOR);         
      // 8 tranches d'age solis
      for(int i = 1; i<= 8 ; i++) {
        // agrementTrancheAgeKey
        Field agrementTracheAgeKeyField = ReflectUtil.getField(itSolis.getClass(), "place"+i+"AgrementTrancheAgeKey");
        String agremenTracheAgeKey = (String) ReflectUtil.getFieldValue(itSolis, agrementTracheAgeKeyField);
        // placeTracheAgeKey
        Field placeTracheAgeKeyField = ReflectUtil.getField(itSolis.getClass(), "place"+i+"TrancheAgeKey");
        Integer placeTracheAgeKey = (Integer) ReflectUtil.getFieldValue(itSolis, placeTracheAgeKeyField);  
        // placeTrancheAge
        Field placeTrancheAgeField = ReflectUtil.getField(itSolis.getClass(), "place"+i+"TrancheAge");
        String placeTrancheAge = (String) ReflectUtil.getFieldValue(itSolis, placeTrancheAgeField);
        // PlaceLibCompl
        Field placeLibComplField = ReflectUtil.getField(itSolis.getClass(), "place"+i+"LibCompl");
        String placeLibCompl = (String) ReflectUtil.getFieldValue(itSolis, placeLibComplField);
        // PlaceNbPlaces
        Field placeNbPlacesField = ReflectUtil.getField(itSolis.getClass(), "place"+i+"NbPlaces");
        Integer placeNbPlaces = (Integer) ReflectUtil.getFieldValue(itSolis, placeNbPlacesField);
        // Saisie disponible
        Field placeSaisieField = ReflectUtil.getField(itSolis.getClass(), "place"+i+"SaisieDisponibilite");
        Boolean placeSaisie = (Boolean) ReflectUtil.getFieldValue(itSolis, placeSaisieField);
        
        chaine.append(DOUBLE_QUOTE + (agremenTracheAgeKey != null ? agremenTracheAgeKey : "") + DOUBLE_QUOTE).append(SEPARATOR);
        chaine.append(DOUBLE_QUOTE + (placeTracheAgeKey != null ? placeTracheAgeKey + "" : "") + DOUBLE_QUOTE).append(SEPARATOR);
        chaine.append(DOUBLE_QUOTE + (placeTrancheAge != null ? placeTrancheAge : "") + DOUBLE_QUOTE).append(SEPARATOR);
        chaine.append(DOUBLE_QUOTE + (placeLibCompl != null ? placeLibCompl : "") + DOUBLE_QUOTE).append(SEPARATOR);
        chaine.append(DOUBLE_QUOTE + (placeNbPlaces !=null ? placeNbPlaces + "" : "") + DOUBLE_QUOTE).append(SEPARATOR);
        chaine.append(DOUBLE_QUOTE + (placeSaisie != null && placeSaisie ? "Oui" : "Non") + DOUBLE_QUOTE).append(SEPARATOR);             
      }
              
      chaine.append(DOUBLE_QUOTE + (itSolis.getStatut() != null ? itSolis.getStatut() : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getAutorisationActivation() != null && itSolis.getAutorisationActivation() ? "Oui" : "Non") + DOUBLE_QUOTE).append(SEPARATOR);       
      chaine.append(DOUBLE_QUOTE + (itSolis.getLatitude() != null ? itSolis.getLatitude()+"" : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getLongitude() != null ? itSolis.getLongitude()+"" : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getLongitudeMam() != null ? itSolis.getLongitudeMam()+"" : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getLatitudeMam() != null ? itSolis.getLatitudeMam()+"" : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getIdUa() != null ? itSolis.getIdUa() : "") + DOUBLE_QUOTE).append(SEPARATOR);
      chaine.append(DOUBLE_QUOTE + (itSolis.getExerceDomicile() != null && itSolis.getExerceDomicile() ? "Oui" : "Non") + DOUBLE_QUOTE).append(SEPARATOR); 
      chaine.append(DOUBLE_QUOTE + (itSolis.getAideCaf() != null && itSolis.getAideCaf() ? "Oui" : "Non") + DOUBLE_QUOTE).append(SEPARATOR); 

      localPrintWriter.println(chaine);
    }
    
  }

  private static String escapeDoubleQuote(String value){
    if(value == null) {
      return "";
    }
    return value.replace("\"", "\"\"");
  }

}
