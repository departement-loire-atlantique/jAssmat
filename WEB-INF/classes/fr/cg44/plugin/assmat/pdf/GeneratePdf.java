package fr.cg44.plugin.assmat.pdf;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.jalios.jcms.Category;
import com.jalios.jcms.Channel;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.util.DateComparator;
import com.jalios.util.ReflectUtil;
import com.jalios.util.ReverseComparator;
import com.jalios.util.Util;
import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo;
import fr.cg44.plugin.assmat.PointAssmat;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.cg44.plugin.assmat.beans.DispoAssmat;
import fr.cg44.plugin.assmat.comparator.AssmatSolisDistanceComparator;
import fr.cg44.plugin.assmat.comparator.DisponibiliteComparator;
import fr.cg44.plugin.assmat.managers.AssmatSearchDAO;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.cg44.plugin.assmat.selector.PlaceSelector;
import fr.cg44.plugin.corporateidentity.annuairePH.AnnuairePHConstants;
import fr.cg44.plugin.corporateidentity.annuairePH.AnnuairePhJcmsProperties;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.QuartierDAO;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.Disponibilite;
import generated.Place;
import generated.ProfilASSMAT;

/**
 * classe permettant la g??n??ration PDF d'une recherche d'assistante maternelle
 * 
 * @author Micka??l Formont
 * @author Guillaume Gautier
 *
 */

public class GeneratePdf extends PdfPageEventHelper {

  private static final Logger LOGGER = Logger.getLogger(GeneratePdf.class);
  private static final Channel channel = Channel.getChannel();
  private static final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
  private static final SimpleDateFormat formaterMoisAnnee = new SimpleDateFormat("MMMM yyyy");


  /**
   * ajoute le contenu d??taill?? d'une structure dans le document courant
   * 
   * @param doc
   * @param entry la structure
   * @param isSelection
   *            Si le contenu viens de la selection de l'utilisateur ou de la
   *            recherche
   * @param resultDispoFuturMap 
   * @param resultDispoRechercheMap 
   * @throws NoSuchFieldException
   */
  private static void addAssmatFullContent(Document doc, Entry<AssmatSearch, PointAssmat> entry, boolean isSelection,
      PdfPTable table, Map<Long, Set<DispoAssmat>> resultDispoRechercheMap, Map<Long, Set<DispoAssmat>> resultDispoFuturMap, String codeInseeSearch) throws DocumentException, MalformedURLException, IOException, NoSuchFieldException {

    SolisManager solisMgr = SolisManager.getInstance();
    ProfilManager profilMgr = ProfilManager.getInstance();

    AssmatSearch assmatSearch = entry.getKey();
    PointAssmat pointAssmat = entry.getValue();

    ProfilASSMAT profilAssmat = (ProfilASSMAT) profilMgr.getProfilASSMATbyAssmatSearch(assmatSearch);
    Member mbrAssmat = profilAssmat.getAuthor();
    AssmatSolis asmmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profilAssmat.getNum_agrement()));

    Boolean hasDispo = profilMgr.hasDispo(profilAssmat.getAuthor());
    Boolean showContactDispo = !profilAssmat.getAfficherContactUniquementSiD() || hasDispo;

    // Nom pr??nom, adresse
    Anchor siteWeb = new Anchor(getNom(asmmatSolis));
    siteWeb.setName("Fiche d??taill??e sur le site");
    siteWeb.setReference(channel.getUrl() + profilAssmat.getDisplayUrl(null));
    Paragraph p = new Paragraph(0);
    p.setLeading(0, 1.2f);
    p.add(siteWeb);
    p.add(getAdresseSearchInscrites(asmmatSolis, assmatSearch.getIsDomicile()));
    table.addCell(p);

    // T??l??phone(s)
    String assmatTel = "";
    if (showContactDispo) {
      if (Util.notEmpty(profilAssmat.getTelephoneFixe())
          && AssmatUtil.getBooleanFromString(profilAssmat.getVisbiliteTelephoneFixe())) {
        assmatTel = profilAssmat.getTelephoneFixe();
      }
      if (Util.notEmpty(mbrAssmat.getMobile())
          && AssmatUtil.getBooleanFromString(profilAssmat.getVisibiliteTelephonePortable())) {
        assmatTel += Util.notEmpty(assmatTel) ? "\n" : "";
        assmatTel += mbrAssmat.getMobile();
      }
      // 0011892: Si l'am a mis non visible ?? son tel portable et son tel fixe et qu'elle a de la dispo par rapport ?? la recherche effectu??e, afficher au moins son tel fixe si renseign?? ou son tel portable si pas de tel fixe et que tel portable renseign?? 
      if( !AssmatUtil.getBooleanFromString(profilAssmat.getVisibiliteTelephonePortable()) && !AssmatUtil.getBooleanFromString(profilAssmat.getVisibiliteTelephonePortable())) {
        if(Util.notEmpty(profilAssmat.getTelephoneFixe())) {
          assmatTel = profilAssmat.getTelephoneFixe();
        }else if(Util.notEmpty(mbrAssmat.getMobile())) {
          assmatTel = mbrAssmat.getMobile();
        }
      }
    }
    PdfPCell telCell = new PdfPCell();
    Paragraph paraph = new Paragraph(0, new Chunk(assmatTel, Fonts.TEXTE));
    paraph.setLeading(0, 1.2f);
    paraph.setAlignment(Element.ALIGN_CENTER);
    telCell.addElement(paraph);
    table.addCell(telCell);

    // Agr??ment total
    table.addCell(new Paragraph(new Chunk(getAgrement(asmmatSolis, assmatSearch), Fonts.TEXTE)));

    // Disponibilit??s
    String listDispo = pointAssmat.getInfoPoint();
    // Si dispo ?? date de recherche
    if(pointAssmat.getEtatDispo() == 0) {        
      // Regroupe les disponibilit?? par tranche d'age et pr??cision
      Set<DispoAssmat> dispoAssmatRecherche = resultDispoRechercheMap.get(assmatSearch.getJRowId());
      Map<Date, Map<Integer, Map<String, Set<Disponibilite>>>> dispoAssmatMap = getDispoActuelleSearch(dispoAssmatRecherche);       
      // Affichage de la list dispos 
      listDispo = getDispoActuelleSearchLbl(dispoAssmatMap);     
      // Si dispo dans le furture   
    }else if(pointAssmat.getEtatDispo() == 1){   
      // Regroupe les disponibilit?? par tranche d'age et pr??cision
      Set<DispoAssmat> dispoAssmatFuture = resultDispoFuturMap.get(assmatSearch.getJRowId());   
      Map<Date, Map<Integer, Map<String, Set<Disponibilite>>>> dispoAssmatDateMap = getDispoFutureSearch(dispoAssmatFuture);    
      // Affichage de la list dispos  
      listDispo = getDispoFutureSearchLbl(dispoAssmatDateMap);   
    }  
    table.addCell(new Paragraph(new Chunk(listDispo, Fonts.TEXTE)));

    // Sp??cificit??s d'accueil possibles
    PdfPCell disposCell = new PdfPCell();
    addSpeAccueil(profilAssmat, disposCell);
    table.addCell(disposCell);

    // Date mise ?? jour
    String assmatMaj = "";
    Date maj = profilAssmat.getMdate();
    Date dateModifDispo = profilMgr.getDateModifDispo(profilAssmat.getAuthor());
    if (dateModifDispo != null && dateModifDispo.after(maj)) {
      maj = dateModifDispo;
    }
    assmatMaj = formater.format(maj);
    PdfPCell dateMajCell = new PdfPCell();
    Paragraph paraphDateMajCell = new Paragraph(0, new Chunk(assmatMaj, Fonts.TEXTE));
    paraphDateMajCell.setLeading(0, 1.2f);
    paraphDateMajCell.setAlignment(Element.ALIGN_CENTER);
    dateMajCell.addElement(paraphDateMajCell);
    table.addCell(dateMajCell);
  }

  /**
   * Affiche la liste de disponibilit?? future par rapport ?? la date de recherche tri?? dans une map pour chaque assmat
   * (sert pour la recherche)
   * @param dispoAssmatDateMap
   * @return le label de la liste de disponibilit?? future par rapport ?? la date de recherche
   */
  private static String getDispoFutureSearchLbl(Map<Date, Map<Integer, Map<String, Set<Disponibilite>>>> dispoAssmatDateMap) {
    String listDispo = "";
    Set<Entry<Date, Map<Integer, Map<String, Set<Disponibilite>>>>>  entryDateSet = dispoAssmatDateMap.entrySet();
    for(Entry<Date, Map<Integer, Map<String, Set<Disponibilite>>>> itDateEntry : entryDateSet){
      Map<Integer, Map<String, Set<Disponibilite>>> dispoAssmatMap = itDateEntry.getValue();
      Set<Entry<Integer, Map<String, Set<Disponibilite>>>> entrySet = dispoAssmatMap.entrySet();
      for(Entry<Integer, Map<String, Set<Disponibilite>>> itEntry : entrySet){
        Map<String, Set<Disponibilite>> itPrecisionMap = itEntry.getValue();
        for(String itPrecision : itPrecisionMap.keySet()){      
          Integer itNbDispo = itPrecisionMap.get(itPrecision).size();
          String trancheAgeLib = Util.getFirst(itPrecisionMap.get(itPrecision)).getLibelle();
          listDispo += "- ?? partir de " + formaterMoisAnnee.format(itDateEntry.getKey()) + " : " + JcmsUtil.glp("fr", "jcmsplugin.assmatplugin.list.dispo", itNbDispo, trancheAgeLib);
          if(Util.notEmpty(itPrecision.trim())) {
            listDispo += "\n(" + itPrecision + ")";
          }
          listDispo += "\n\n";
        }
      }
    }
    return listDispo;
  }

  /**
   * R??cup??re la liste de disponibilit?? future par rapport ?? la date de recherche tri?? dans une map pour chaque assmat
   * (sert pour la recherche)
   * @param dispoAssmatFuture
   * @return la liste de disponibilit?? future par rapport ?? la date de recherch
   */
  private static Map<Date, Map<Integer, Map<String, Set<Disponibilite>>>> getDispoFutureSearch(Set<DispoAssmat> dispoAssmatFuture) {
    Map<Date, Map<Integer, Map<String, Set<Disponibilite>>>> dispoAssmatDateMap = new TreeMap<Date, Map<Integer, Map<String, Set<Disponibilite>>>>();
    for(DispoAssmat itDispo : dispoAssmatFuture) {
      // la date de d??but de la dispo
      Date itDate = itDispo.getDateDebut();
      // La map avec la date en key
      Map<Integer, Map<String, Set<Disponibilite>>> dispoAssmatMap = new TreeMap<Integer, Map<String, Set<Disponibilite>>>();
      // la tranche d'age
      Integer itTrancheAge = itDispo.getTrancheAgeKey();
      // La map avec la pr??cison en key
      Map<String, Set<Disponibilite>> itMapTrancheAge = new TreeMap<String, Set<Disponibilite>>();
      // La liste des dispo pour cette tranche d'age et pr??cision
      Set<Disponibilite> itDispoList = new HashSet<Disponibilite>();
      // R??cup??re la pr??sicion depuis le contenu JCMS (appel en BDD)
      Disponibilite itDispoJcms = (Disponibilite) (channel.getPublication(itDispo.getJcmsId()));
      String itPrecision = itDispoJcms.getPrecisionPlaceFuture();
      itPrecision = itPrecision != null ? itPrecision.trim() : "";
      // Si d??ja une dispo avec cette date
      if(dispoAssmatDateMap.containsKey(itDate)){
        dispoAssmatMap = dispoAssmatDateMap.get(itDate);
        // Si d??j?? une dispo avec cette Tranche d'age
        if(dispoAssmatMap.containsKey(itTrancheAge)) {        
          itMapTrancheAge = dispoAssmatMap.get(itTrancheAge);
          // Si d??ja une dispo avec cette pr??cision
          if(itMapTrancheAge.containsKey(itPrecision)) {
            itDispoList = itMapTrancheAge.get(itPrecision);
          }
        }
      }
      itDispoList.add(itDispoJcms);
      itMapTrancheAge.put(itPrecision, itDispoList);
      dispoAssmatMap.put(itTrancheAge, itMapTrancheAge); 
      dispoAssmatDateMap.put(itDate, dispoAssmatMap);            
    }
    return dispoAssmatDateMap;
  }


  /**
   * Affiche la liste de disponibilit?? disponible ?? la date de recherche tri?? dans une map pour chaque assmat
   * (sert pour la recherche)
   * @param dispoAssmatMap
   * @return la liste de disponibilit?? disponible ?? la date de recherche
   */
  private static String getDispoActuelleSearchLbl(Map<Date, Map<Integer, Map<String, Set<Disponibilite>>>> dispoAssmatMap) {
    String listDispo = "";
    
    for(Date itDate : dispoAssmatMap.keySet()){
      
      Map<Integer, Map<String, Set<Disponibilite>>> itAgeMap = dispoAssmatMap.get(itDate);
    
      Set<Entry<Integer, Map<String, Set<Disponibilite>>>> entrySet = itAgeMap.entrySet();
      for(Entry<Integer, Map<String, Set<Disponibilite>>> itEntry : entrySet){
        Map<String, Set<Disponibilite>> itPrecisionMap = itEntry.getValue();
        for(String itPrecision : itPrecisionMap.keySet()){      
          Integer itNbDispo = itPrecisionMap.get(itPrecision).size();
          String trancheAgeLib = Util.getFirst(itPrecisionMap.get(itPrecision)).getLibelle();
          
          String dateLib = "- Disponible actuellement : ";
          if(!itDate.equals(new Date(0))) {
            dateLib = "- ?? partir de : " + formaterMoisAnnee.format(itDate) + " : ";
          }
          
          listDispo += dateLib + JcmsUtil.glp("fr", "jcmsplugin.assmatplugin.list.dispo", itNbDispo, trancheAgeLib);
          if(Util.notEmpty(itPrecision.trim())) {
            listDispo += "\n(" + itPrecision + ")";
          }
          listDispo += "\n\n";
        }
      }
    }
    return listDispo;
  }


  /**
   * R??cup??re la liste de disponibilit?? disponible ?? la date de recherche tri?? dans une map pour chaque assmat
   * (sert pour la recherche)
   * @param dispoAssmatRecherche
   * @return la liste de disponibilit?? disponible ?? la date de recherche
   */
  private static Map<Date, Map<Integer, Map<String, Set<Disponibilite>>>> getDispoActuelleSearch(Set<DispoAssmat> dispoAssmatRecherche) {
    Map<Date, Map<Integer, Map<String, Set<Disponibilite>>>> dispoAssmatMap = new TreeMap<Date, Map<Integer, Map<String, Set<Disponibilite>>>>(new ReverseComparator(new DateComparator()));
    
    
    for(DispoAssmat itDispo : dispoAssmatRecherche) {     
      // La tranche d'age
      Integer itTrancheAge = itDispo.getTrancheAgeKey();
      // La map avec la tranche d'age en key
      Map<Integer, Map<String, Set<Disponibilite>>> itMapDate = new TreeMap<Integer, Map<String, Set<Disponibilite>>>();
      // La map avec la pr??cison en key
      Map<String, Set<Disponibilite>> itMapTrancheAge = new TreeMap<String, Set<Disponibilite>>();
      // La liste des dispo pour cette tranche d'age et pr??cision
      Set<Disponibilite> itDispoList = new HashSet<Disponibilite>();
      // R??cup??re la pr??sicion depuis le contenu JCMS (appel en BDD)
      Disponibilite itDispoJcms = (Disponibilite) (channel.getPublication(itDispo.getJcmsId()));      
      // La date de d??but de disponibilit??
      Date itStartDate = new Date(0);    
      String itPrecision = "";
      // Si une future (mais date dans le pass??)
      if("2".equals(itDispoJcms.getEtatDispo())) {
        itPrecision = itDispoJcms.getPrecisionPlaceFuture();
        // La date de d??but de disponibilit??
        if(itDispoJcms.getDateDispoPlaceFuture().after(new Date())) {
          itStartDate = itDispoJcms.getDateDispoPlaceFuture();
        }
      }else {
        itPrecision = itDispoJcms.getPrecisionsPlaceDisponible();
      }
      itPrecision = itPrecision != null ? itPrecision.trim() : "";     
      // si d??ja une dispo avec cette date
      if(dispoAssmatMap.containsKey(itStartDate)){
        itMapDate = dispoAssmatMap.get(itStartDate);
        // Si d??j?? une dispo avec cette Tranche d'age
        if(itMapDate.containsKey(itTrancheAge)) {        
          itMapTrancheAge = itMapDate.get(itTrancheAge);
          // Si d??ja une dispo avec cette pr??cision
          if(itMapTrancheAge.containsKey(itPrecision)) {
            itDispoList = itMapTrancheAge.get(itPrecision);
          }
        }
        
      }
      itDispoList.add(itDispoJcms);
      itMapTrancheAge.put(itPrecision, itDispoList);
      itMapDate.put(itTrancheAge, itMapTrancheAge); 
      dispoAssmatMap.put(itStartDate, itMapDate);
    }
    return dispoAssmatMap;
  }


  /**
   * Affiche la liste des dispos de l'assmat par rapprt a la map de ses dispos (sert pour la selection)
   * @param dispoAssmatDateMap
   * @return le label de la liste des dispos de l'assmat par rapport a la map de ses dispos
   */
  private static String getDispoSelectLbl(Map<Date, Map<Integer, Map<String, Map<String, Set<Disponibilite>>>>> dispoAssmatDateMap) {
    String listDispo = "";
    Set<Entry<Date, Map<Integer, Map<String, Map<String, Set<Disponibilite>>>>>>  entryDateSet = dispoAssmatDateMap.entrySet();
    for(Entry<Date, Map<Integer, Map<String, Map<String, Set<Disponibilite>>>>> itDateEntry : entryDateSet){
      Map<Integer, Map<String, Map<String, Set<Disponibilite>>>> dispoAssmatMap = itDateEntry.getValue();
      Set<Entry<Integer, Map<String, Map<String, Set<Disponibilite>>>>> entrySet = dispoAssmatMap.entrySet();
      for(Entry<Integer, Map<String, Map<String, Set<Disponibilite>>>> itEntry : entrySet){
        Map<String, Map<String, Set<Disponibilite>>> itPrecisionMap = itEntry.getValue();    
        for(String itPrecision : itPrecisionMap.keySet()){          
          Map<String, Set<Disponibilite>> itLieuMap = itPrecisionMap.get(itPrecision);         
          for(String itLieu : itLieuMap.keySet()){         
            Integer itNbDispo = itLieuMap.get(itLieu).size();
            String trancheAgeLib = Util.getFirst(itLieuMap.get(itLieu)).getLibelle();
            String etatDispo = Util.getFirst(itLieuMap.get(itLieu)).getEtatDispo(); 
            // Disponible actuellement ou disponibilit?? future, avec date dispo > date du jour
            if("1".equals(etatDispo) || ("2".equals(etatDispo) && (itDateEntry.getKey().before(new Date()))	)	 ){
              listDispo += "- Disponible actuellement : " + JcmsUtil.glp("fr", "jcmsplugin.assmatplugin.list.dispo", itNbDispo, trancheAgeLib);  
            }else {
              listDispo += "- ?? partir de : " + formaterMoisAnnee.format(itDateEntry.getKey()) + " : " + JcmsUtil.glp("fr", "jcmsplugin.assmatplugin.list.dispo", itNbDispo, trancheAgeLib);  
            }           
            // Lieu d'exercice (DOM ou MAM)
            if("MAM".equalsIgnoreCase(itLieu)){
              listDispo += " en MAM";
            }else {
              listDispo += " ?? domicile";
            }           
            if(Util.notEmpty(itPrecision.trim())) {
              listDispo += "\n(" + itPrecision + ")";
            }
            listDispo += "\n\n";
          }
        }
      }
    }
    return listDispo;
  }


  /**
   * R??cup??re la liste de disponibilit?? tri?? dans une map pour chaque assmat (sert pour la s??lection)
   * @param dispoListImmFutur
   * @return la liste de disponibilit?? tri?? dans une map pour chaque assmat
   */
  private static Map<Date, Map<Integer, Map<String, Map<String, Set<Disponibilite>>>>> getDispoSelect(Set<Disponibilite> dispoListImmFutur) {
    Map<Date, Map<Integer, Map<String, Map<String, Set<Disponibilite>>>>> dispoAssmatDateMap = new TreeMap<Date, Map<Integer, Map<String, Map<String, Set<Disponibilite>>>>>();
    for(Disponibilite itDispoJcms : dispoListImmFutur) {
      // la date de d??but de la dispo
      Date itDate = itDispoJcms.getDateDispoPlaceFuture();
      // La map avec la date en key
      Map<Integer, Map<String, Map<String, Set<Disponibilite>>>> dispoAssmatMap = new TreeMap<Integer, Map<String, Map<String, Set<Disponibilite>>>>();
      // la tranche d'age
      Integer itTrancheAge = itDispoJcms.getTrancheDage();
      // La map avec la pr??cison en key
      Map<String, Map<String, Set<Disponibilite>>> itMapTrancheAge = new TreeMap<String, Map<String, Set<Disponibilite>>>();     
      // La map avec le lieu d'exercice en key
      Map<String, Set<Disponibilite>> itMapLieu = new TreeMap<String, Set<Disponibilite>>();
      String itLieu = itDispoJcms.getAgrement().toUpperCase().contains("MAM") ? "MAM" : "DOM";     
      // La liste des dispo pour cette tranche d'age et pr??cision
      Set<Disponibilite> itDispoList = new HashSet<Disponibilite>();
      // R??cup??re la pr??sicion depuis le contenu JCMS (appel en BDD)
      String itPrecision = "";
      if("2".equals(itDispoJcms.getEtatDispo())) {
        itPrecision = itDispoJcms.getPrecisionPlaceFuture();
      }else {
        itPrecision = itDispoJcms.getPrecisionsPlaceDisponible();
      }
      itPrecision = itPrecision != null ? itPrecision.trim() : "";
      if(dispoAssmatDateMap.containsKey(itDate)){
        dispoAssmatMap = dispoAssmatDateMap.get(itDate);
        // Si d??j?? une dispo avec cette Tranche d'age
        if(dispoAssmatMap.containsKey(itTrancheAge)) {        
          itMapTrancheAge = dispoAssmatMap.get(itTrancheAge);
          // Si d??ja une dispo avec cette pr??cision
          if(itMapTrancheAge.containsKey(itPrecision)) {
            itMapLieu = itMapTrancheAge.get(itPrecision);
            // Si une dispo est dans le meme lieu d'exercice (DOM ou MAM)
            if(itMapLieu.containsKey(itLieu)){
              itDispoList = itMapLieu.get(itLieu);
            }
          }       
        }
      }
      itDispoList.add(itDispoJcms);
      itMapLieu.put(itLieu, itDispoList);
      itMapTrancheAge.put(itPrecision, itMapLieu);
      dispoAssmatMap.put(itTrancheAge, itMapTrancheAge); 
      dispoAssmatDateMap.put(itDate, dispoAssmatMap); 
    }
    return dispoAssmatDateMap;
  }


  /**
   * Retourne un paragraphe ?? partir d'un contenu HTML
   * 
   * @param html
   *           La chaine en contenu HTML
   * @return un paragraphe ?? partir d'un contenu HTML
   * @throws IOException
   */
  private static Paragraph getParagraphFromHtml(String html) throws IOException {
    StyleSheet styles = new StyleSheet();
    ArrayList objects = HTMLWorker.parseToList(new StringReader(html), styles);
    Paragraph p = new Paragraph(10);
    p.setSpacingAfter(2);
    for (int k = 0; k < objects.size(); ++k) {
      Element e = (Element) objects.get(k);
      ArrayList chunks = e.getChunks();
      Chunk c = (Chunk) chunks.get(0);
      c.setFont(Fonts.TEXTE);
      // Si le contenu commence par un ast??risque, l'afficher en gras
      if ("*".equals(c.toString().substring(0, 1))) {
        c = new Chunk(c.toString().substring(1));
        c.setFont(Fonts.TEXTE_GRAS);
      }
      p.add(c);
    }
    return p;
  }

  /**
   * Retourne le nom de l'assmat (Informations r??cup??r??es sur solis) Sert pour
   * les assmats inscrites ou non sur le site
   * 
   * @param asmmatSolis
   * @return le nom de l'assmat
   */
  private static Chunk getNom(AssmatSolis asmmatSolis) {
    return new Chunk(asmmatSolis.getNomAssmat() + " " + asmmatSolis.getPrenomAssmat() + "\n", Fonts.TEXTE_GRAS);
  }

  /**
   * Retourne l'adresse de l'assmat (Informations r??cup??r??es sur solis) Sert pour la s??l??ction
   * 
   * @param asmmatSolis
   * @return l'adresse de l'assmat
   */
  private static Chunk getAdresseSelect(AssmatSolis assmatSolis) {
    Boolean hasExerceDomicile = Util.notEmpty(assmatSolis.getExerceDomicile()) && assmatSolis.getExerceDomicile();
    Boolean hasExerceMAM = Util.notEmpty(assmatSolis.getExerceMam()) && assmatSolis.getExerceMam();
    String quartierDom = "";
    String quartierMam = "";
    if(hasExerceDomicile && Util.notEmpty(assmatSolis.getIdMicroQuartierDom()) && "44109".equals(assmatSolis.getCodeInsee())) {
      quartierDom = "\nQuartier : " + getLibQuartierDom(assmatSolis);
    }
    if(hasExerceMAM && Util.notEmpty(assmatSolis.getIdMicroQuartierMam()) && "44109".equals(assmatSolis.getCodeInseeMam())) {
      quartierMam = "\nQuartier : " + getLibQuartierMam(assmatSolis);
    }    
    Chunk assmatAdresse = new Chunk("");
    if (hasExerceDomicile && hasExerceMAM) {     
      assmatAdresse = new Chunk("Domicile :\n" 
          + assmatSolis.getAdresseDomicile() + "\n"
          + assmatSolis.getCpDomicile() + " " + assmatSolis.getCommuneDomicile()
          + quartierDom
          + "\n\nMAM :\n"
          + assmatSolis.getNomMam() + "\n"
          + assmatSolis.getAdresseMam() + "\n" 
          + assmatSolis.getCpMam() + " " + assmatSolis.getCommuneMam()
          + quartierMam,
          Fonts.TEXTE);
    } else if (hasExerceDomicile) {
      assmatAdresse = new Chunk(assmatSolis.getAdresseDomicile() + "\n"
          + assmatSolis.getCpDomicile() + " " + assmatSolis.getCommuneDomicile()
          + quartierDom
          ,Fonts.TEXTE);
    } else if (hasExerceMAM) {
      assmatAdresse = new Chunk(assmatSolis.getNomMam() + "\n" 
          + assmatSolis.getAdresseMam() + "\n"
          + assmatSolis.getCpMam() + " " + assmatSolis.getCommuneMam()
          + quartierMam
          , Fonts.TEXTE);
    }
    return assmatAdresse;
  }
  
  /**
   * Retourne l'adresse de l'assmat (Informations r??cup??r??es sur solis) Sert pour la recherche (assmats non inscrites)
   * @param asmmatSolis
   * @return
   */
  private static Chunk getAdresseSearchNonInscrites(AssmatSolis assmatSolis) {
    Boolean hasExerceDomicile = Util.notEmpty(assmatSolis.getExerceDomicile()) && assmatSolis.getExerceDomicile();
    Boolean hasExerceMAM = Util.notEmpty(assmatSolis.getExerceMam()) && assmatSolis.getExerceMam();
    String lbl = "";  
    // Exerce a domicile
    if (hasExerceDomicile) { 
      // Si exerce a domicile et en mam affiche un libell?? d'indication
      if (hasExerceDomicile && hasExerceMAM) {
        lbl += "Domicile :\n";
      }
      lbl +=  assmatSolis.getAdresseDomicile() + "\n";
      lbl += assmatSolis.getCpDomicile() + " " + assmatSolis.getCommuneDomicile() + "\n";
      if("44109".equals(assmatSolis.getCodeInsee()) && Util.notEmpty(assmatSolis.getIdMicroQuartierDom())) {
        lbl += "Quartier : " + getLibQuartierDom(assmatSolis) + "\n";
      }
      lbl += "\n";
    }    
    // Exerce en MAM
    if (hasExerceMAM) { 
      // Si exerce a domicile et en mam affiche un libell?? d'indication
      if (hasExerceDomicile && hasExerceMAM) {
        lbl += "MAM :\n";
      }
      lbl +=  assmatSolis.getNomMam() + "\n";
      lbl +=  assmatSolis.getAdresseMam() + "\n";
      lbl += assmatSolis.getCpMam() + " " + assmatSolis.getCommuneMam() + "\n";
      if("44109".equals(assmatSolis.getCodeInseeMam()) && Util.notEmpty(assmatSolis.getIdMicroQuartierMam())) {
        lbl += "Quartier : " + getLibQuartierMam(assmatSolis) + "\n";
      }
      lbl += "\n";
    }
    return new Chunk(lbl, Fonts.TEXTE);
  }
  

  /**
   * Retourne l'adresse de l'assmat par rapport ?? lassmat search (soit DOM soit MAM) (Informations r??cup??r??es sur solis) Sert pour la recherche (assmats inscrites)
   * @param asmmatSolis
   * @return
   */
  private static Chunk getAdresseSearchInscrites(AssmatSolis assmatSolis, boolean isDOM) {
    String lbl = "";
    if(isDOM){
      lbl +=  assmatSolis.getAdresseDomicile() + "\n";
      lbl += assmatSolis.getCpDomicile() + " " + assmatSolis.getCommuneDomicile() + "\n";
      if("44109".equals(assmatSolis.getCodeInsee()) && Util.notEmpty(assmatSolis.getIdMicroQuartierDom())) {
        lbl += "Quartier : " + getLibQuartierDom(assmatSolis) + "\n";
      }
      lbl += "\n";
    }else {
      lbl += assmatSolis.getNomMam() + "\n"; 
      lbl +=  assmatSolis.getAdresseMam() + "\n";
      lbl += assmatSolis.getCpMam() + " " + assmatSolis.getCommuneMam() + "\n";
      if("44109".equals(assmatSolis.getCodeInseeMam()) && Util.notEmpty(assmatSolis.getIdMicroQuartierMam())) {
        lbl += "Quartier : " + getLibQuartierMam(assmatSolis) + "\n";
      }
      lbl += "\n";
    }    
    return new Chunk(lbl, Fonts.TEXTE);
  }
  
  
  /**
   * Retourne le libelle du quartier et micro quartier domicile
   * @param assmatSolis
   * @return
   */
  private static String getLibQuartierDom(AssmatSolis assmatSolis) {
	  String libelleMicroQuartier = Util.notEmpty(QuartierDAO.getLibMicroQuartier(assmatSolis.getIdMicroQuartierDom())) ? " ("+ QuartierDAO.getLibMicroQuartier(assmatSolis.getIdMicroQuartierDom()) +")" : ""; 
	  return QuartierDAO.getLibQuartier(QuartierDAO.getIdQuartier(assmatSolis.getIdMicroQuartierDom())) + libelleMicroQuartier;
  }
  
  
  /**
   * Retourne le libelle du quartier et micro quartier mam
   * @param assmatSolis
   * @return
   */
  private static String getLibQuartierMam(AssmatSolis assmatSolis) {
	  String libelleMicroQuartier = Util.notEmpty(QuartierDAO.getLibMicroQuartier(assmatSolis.getIdMicroQuartierMam())) ? " ("+ QuartierDAO.getLibMicroQuartier(assmatSolis.getIdMicroQuartierMam()) +")" : "";
	  return QuartierDAO.getLibQuartier(QuartierDAO.getIdQuartier(assmatSolis.getIdMicroQuartierMam())) + libelleMicroQuartier;
  }


  /**
   * Retourne les agr??ments totales d'une assmat (Informations r??cup??r??es sur
   * solis) Sert pour les assmats inscrites ou non sur le site
   * 
   * @param assmatSolis
   * @return les agr??ments totales d'une assmat
   */
  private static String getAgrement(AssmatSolis assmatSolis, AssmatSearch assmatSearch) {
    Boolean hasExerceDomicile = Util.notEmpty(assmatSolis.getExerceDomicile()) && assmatSolis.getExerceDomicile();
    Boolean hasExerceMAM = Util.notEmpty(assmatSolis.getExerceMam()) && assmatSolis.getExerceMam();
    // Agr??ment total
    
    String assmatAgre = "";
    if (hasExerceDomicile && hasExerceMAM && Util.isEmpty(assmatSearch)) {
      assmatAgre += "A Domicile\n";
    }
    // A domicile
    if ( (hasExerceDomicile && Util.isEmpty(assmatSearch)) || (Util.notEmpty(assmatSearch) && assmatSearch.getIsDomicile()) ) {
      assmatAgre += getAgrementLbl(assmatSolis, "dom-");
    }
    
    // En MAM
    if ( (hasExerceMAM && Util.isEmpty(assmatSearch)) || (Util.notEmpty(assmatSearch) && !assmatSearch.getIsDomicile()) ) {
      assmatAgre += Util.isEmpty(assmatAgre) ? "" : "\n";
      assmatAgre += "En MAM\n";
      assmatAgre += getAgrementLbl(assmatSolis, "mam-");
    }
    return assmatAgre;
  }


  /**
   * Retourne les agr??ment suivant le param??trage DOM ou MAM (dom- ou mam-)
   * @param assmatSolis
   * @param lieu
   * @return les agr??ment suivant le param??trage DOM ou MAM
   */
  private static String getAgrementLbl(AssmatSolis assmatSolis, String lieu) {
    String assmatAgre = "";
    for (int itPlace = 1; itPlace <= 8; itPlace++) {
      // agrementTrancheAgeKey
      Field agrementTracheAgeKeyField = ReflectUtil.getField(assmatSolis.getClass(),
          "place" + itPlace + "AgrementTrancheAgeKey");
      String agremenTracheAgeKey = (String) ReflectUtil.getFieldValue(assmatSolis, agrementTracheAgeKeyField);
      // placeTracheAgeKey
      Field placeTracheAgeKeyField = ReflectUtil.getField(assmatSolis.getClass(),
          "place" + itPlace + "TrancheAgeKey");
      Integer placeTracheAgeKey = (Integer) ReflectUtil.getFieldValue(assmatSolis, placeTracheAgeKeyField);
      // PlaceNbPlaces
      Field placeNbPlacesField = ReflectUtil.getField(assmatSolis.getClass(), "place" + itPlace + "NbPlaces");
      Integer placeNbPlaces = (Integer) ReflectUtil.getFieldValue(assmatSolis, placeNbPlacesField);
      // placeTrancheAge
      Field placeTrancheAgeField = ReflectUtil.getField(assmatSolis.getClass(),
          "place" + itPlace + "TrancheAge");
      String placeTrancheAge = (String) ReflectUtil.getFieldValue(assmatSolis, placeTrancheAgeField);
      // PlaceLibCompl
      Field placeLibComplField = ReflectUtil.getField(assmatSolis.getClass(), "place" + itPlace + "LibCompl");
      String placeLibCompl = (String) ReflectUtil.getFieldValue(assmatSolis, placeLibComplField);
      // SaisieDisponibilite
      Field placeSaisieDisponibiliteField = ReflectUtil.getField(assmatSolis.getClass(),
          "place" + itPlace + "SaisieDisponibilite");
      Boolean placeSaisieDisponibilite = (Boolean) ReflectUtil.getFieldValue(assmatSolis,
          placeSaisieDisponibiliteField);
      // Affichage
      if (Util.notEmpty(agremenTracheAgeKey) && agremenTracheAgeKey.contains(lieu)
          && placeSaisieDisponibilite) {
        assmatAgre += JcmsUtil.glp(channel.getCurrentUserLang(),
            "jcmsplugin.assmatplugin.inscription.verification.place", placeNbPlaces) + " "
            + AssmatUtil.getTitlePlace(placeTrancheAge, placeLibCompl, placeTracheAgeKey) + "\n";
      }
    }
    return assmatAgre;
  }


  /**
   * Ajoute les Sp??cificit??s d'accueil possibles de l'assmat pour la cellule donn??e en param??tre 
   * @param profilAssmat
   * @param disposCell
   * @throws IOException
   */
  private static void addSpeAccueil(ProfilASSMAT profilAssmat, PdfPCell disposCell) throws IOException {
    if (Util.notEmpty(profilAssmat.getAccueilTempsPartiel())
        && "true".equalsIgnoreCase(profilAssmat.getAccueilTempsPartiel())) {
      disposCell.addElement(
          getParagraphFromHtml(AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-SPECIFICITE-PARTIEL-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getAccueilPeriscolaire())
        && "true".equalsIgnoreCase(profilAssmat.getAccueilPeriscolaire())) {
      disposCell.addElement(
          getParagraphFromHtml(AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-PERISCOLAIRE-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getAccueilMercredi())
        && "true".equalsIgnoreCase(profilAssmat.getAccueilMercredi())) {
      disposCell.addElement(
          getParagraphFromHtml(AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-MERCREDI-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getAccueilPendantLesVacancesSco())
        && "true".equalsIgnoreCase(profilAssmat.getAccueilPendantLesVacancesSco())) {
      disposCell.addElement(
          getParagraphFromHtml(AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-VACANCES-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getAvant7h()) && "true".equalsIgnoreCase(profilAssmat.getAvant7h())) {
      disposCell.addElement(getParagraphFromHtml(
          AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-MATIN-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getApres20h()) && "true".equalsIgnoreCase(profilAssmat.getApres20h())) {
      disposCell.addElement(getParagraphFromHtml(
          AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-SOIR-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getLeSamedi()) && "true".equalsIgnoreCase(profilAssmat.getLeSamedi())) {
      disposCell.addElement(getParagraphFromHtml(
          AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-SAMEDI-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getLeDimanche()) && "true".equalsIgnoreCase(profilAssmat.getLeDimanche())) {
      disposCell.addElement(getParagraphFromHtml(
          AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-DIMANCHE-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getLaNuit()) && "true".equalsIgnoreCase(profilAssmat.getLaNuit())) {
      disposCell.addElement(getParagraphFromHtml(
          AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-NUIT-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getAccueilEnfantHandicap())
        && "true".equalsIgnoreCase(profilAssmat.getAccueilEnfantHandicap())) {
      disposCell.addElement(
          getParagraphFromHtml(AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-SPECIFICITE-HANDICAP-PDF")));
    }
    if (Util.notEmpty(profilAssmat.getAccepteDepannage())
        && "true".equalsIgnoreCase(profilAssmat.getAccepteDepannage())) {
      disposCell.addElement(
          getParagraphFromHtml(AssmatUtil.getMessage("PROFIL-ASSMAT-CONTENT-SPECIFICITE-REMPLACEMENT-PDF")));
    }    
  }


  private static PdfPTable getHeaderTabInscrite(Document doc, boolean isRechercher, boolean isGeoSearch) throws DocumentException {

    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(100);
    Color backFirstHeaderColor = WebColors.getRGBColor("#A8D08D");
    PdfPCell cell = new PdfPCell();

    // Libell?? pour : Assistantes maternelles agr????es INSCRITES sur le site
    // assmat
    Paragraph p1 = new Paragraph(
        new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-INSCRITES"), Fonts.TITRE_TABLEAU));
    p1.setAlignment(Element.ALIGN_CENTER);

    // Libell?? pour : Tri par disponibilit?? puis proximit?? [si adresse
    // renseign??e] OU tri par disponibilit??- quartier [si pas d???adresse]
    Paragraph p2 = new Paragraph();
    if(isRechercher) {
      if (isGeoSearch) {
        p2 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-TRI1-INSCRITES"),
            Fonts.SOUSTITRE_TABLEAU));
      } else {
        p2 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-TRI2-INSCRITES"),
            Fonts.SOUSTITRE_TABLEAU));
      }
      p2.setAlignment(Element.ALIGN_CENTER);
    }
    cell = new PdfPCell();
    cell.addElement(p1);
    if(isRechercher) {
      cell.addElement(p2);
    }
    cell.setPaddingBottom(10);
    cell.setBackgroundColor(backFirstHeaderColor);
    table.addCell(cell);
    table.setSpacingBefore(10);
    doc.add(table);

    // ------------------------------------------------------------------------------------------
    // FIN Ent??te 1??re ligne du tableau

    // Ent??te 2??me ligne du tableau
    // ------------------------------------------------------------------------------------------
    table = new PdfPTable(new float[] { 100, 50, 100, 100, 100, 55 });
    table.setWidthPercentage(100);
    Color backHeaderColor = WebColors.getRGBColor("#E2EFD9");
    cell = new PdfPCell();

    // Nom et pr??nom, adresse
    p1 = new Paragraph(
        new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-INSCRITES-COL1"), Fonts.TEXTE_GRAS));
    cell = new PdfPCell(p1);
    cell.setBackgroundColor(backHeaderColor);
    table.addCell(cell);

    // T??l??phone
    p2 = new Paragraph(
        new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-INSCRITES-COL2"), Fonts.TEXTE_GRAS));
    cell = new PdfPCell(p2);
    cell.setBackgroundColor(backHeaderColor);
    table.addCell(cell);

    // Agr??ment (et Agr??ment total pour panier)
    Paragraph p3 = new Paragraph();
    if(isRechercher) {
      p3 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-INSCRITES-COL3"), Fonts.TEXTE_GRAS));
    }else {
      p3 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-INSCRITES-COL3-SELECTION"), Fonts.TEXTE_GRAS));
    }
    cell = new PdfPCell(p3);
    cell.setBackgroundColor(backHeaderColor);
    table.addCell(cell);

    // Disponibilit??s
    Paragraph p4 = new Paragraph();
    p4 = new Paragraph(
        new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-INSCRITES-COL4"), Fonts.TEXTE_GRAS));
    cell = new PdfPCell(p4);
    cell.setBackgroundColor(backHeaderColor);
    table.addCell(cell);

    // Sp??cifit??s d'accueil possible
    Paragraph p5 = new Paragraph();
    p5 = new Paragraph(
        new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-INSCRITES-COL5"), Fonts.TEXTE_GRAS));
    cell = new PdfPCell(p5);
    cell.setBackgroundColor(backHeaderColor);
    table.addCell(cell);

    // Date de mise ?? jour
    Paragraph p6 = new Paragraph();
    p6 = new Paragraph(
        new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-INSCRITES-COL6"), Fonts.TEXTE_GRAS));
    cell = new PdfPCell(p6);
    cell.setBackgroundColor(backHeaderColor);
    table.addCell(cell);

    // Nombre de ligne pour l'entete
    table.setHeaderRows(1);

    // ------------------------------------------------------------------------------------------
    // FIN Ent??te 2??me ligne du tableau

    // Corps du tableau
    // ------------------------------------------------------------------------------------------

    Color backCellColor = WebColors.getRGBColor("#FFFFFF");
    table.getDefaultCell().setBackgroundColor(backCellColor);

    return table;
  }


  /**
   * supression d'un PDF du cache
   * 
   * @param key
   *            la cl??
   * @return si la suppression est correcte
   */
  public static boolean removePdfFromCache(String key) {
    File f = new File(channel.getRealPath(JcmsUtil.glpd("cg44.pdf.label.coderpa.pdf_path"))
        + AnnuairePHConstants.PDF_PREFFIX + key + ".pdf");
    return f.delete();
  }

  public static String getPdfPath(String id) {
    return AnnuairePhJcmsProperties.PDF_PATH_GENERATION.getString()
        + AnnuairePhJcmsProperties.PDF_PREFFIX_NAME.getString() + id + ".pdf";
  }

  public static String getPdfCompletPath(String id) {
    return AnnuairePhJcmsProperties.PDF_PATH_GENERATION.getString()
        + AnnuairePhJcmsProperties.PDF_COMPLET_NAME.getString();
  }

  /**
   * Traitement de la liste pour une selection d'assmat
   *
   */
  public static void traitementStructureSet(Collection<Publication> listeStructure, OutputStream output,
      boolean isSelection) throws DocumentException, MalformedURLException, IOException, NoSuchFieldException {

    if (Util.isEmpty(listeStructure)) {
      LOGGER.error("Aucun r??sultat d'assmat, PDF non g??n??r??");
      return;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("traitementStructureSet() - start." + listeStructure.size());
    }

    Document doc = new Document(PageSize.A4.rotate(), 25, 25, 25, 25);
    PdfWriter writer = PdfWriter.getInstance(doc, output);

    writer.setFullCompression();
    writer.setPageEvent(new GeneratePdf());

    // pied de page
    writer.setPageEvent(new FooterAssmatPdf());

    doc.open();

    PdfPTable table;
    PdfPCell cell;
    Paragraph p;

    p = new Paragraph();
    Date currentDate = new Date();
    p.add(new Chunk("Liste des assistantes maternelles agr????es au " + formater.format(currentDate),
        Fonts.TITRE_ENTETE));
    p.setAlignment(Element.ALIGN_RIGHT);
    doc.add(p);

    table = new PdfPTable(1);
    table.setWidthPercentage(100);

    cell = new PdfPCell();
    Chunk structureChunk = new Chunk("Votre s??lection", Fonts.TITRE_BLANC);
    cell.addElement(structureChunk);
    cell.setBackgroundColor(new Color(68, 84, 106));
    cell.setFixedHeight(30);
    cell.setPaddingLeft(10);
    cell.setBorder(Rectangle.NO_BORDER);
    table.setSpacingBefore(10);
    table.setSpacingAfter(5);
    table.addCell(cell);
    doc.add(table);

    // Libell?? pour : Mention de pr??caution sur l'actualisation des
    // informations
    p = new Paragraph();
    p.setLeading(0, 1.0f);
    p.add(new Chunk(AssmatUtil.getMessage("INFORMATION-RESULTAT-RECHERCHE-AM-RAM"), Fonts.DESCRIPTION));
    doc.add(p);

    table = getHeaderTabInscrite(doc, false, false);

    for(Publication itPub : listeStructure) {

      SolisManager solisMgr = SolisManager.getInstance();
      ProfilManager profilMgr = ProfilManager.getInstance();

      ProfilASSMAT profilAssmat = (ProfilASSMAT) itPub;
      Member mbrAssmat = profilAssmat.getAuthor();
      AssmatSolis asmmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profilAssmat.getNum_agrement()));

      Boolean hasDispo = profilMgr.hasDispo(profilAssmat.getAuthor());
      Boolean showContactDispo = !profilAssmat.getAfficherContactUniquementSiD() || hasDispo;

      // Nom pr??nom, adresse
      Anchor siteWeb = new Anchor(getNom(asmmatSolis));
      siteWeb.setName("Fiche d??taill??e sur le site");
      siteWeb.setReference(channel.getUrl() + profilAssmat.getDisplayUrl(null));
      p = new Paragraph();
      p.setLeading(0, 1.5f);
      p.add(siteWeb);
      p.add(getAdresseSelect(asmmatSolis));
      table.addCell(p);

      // T??l??phone(s)
      String assmatTel = "";
      if (showContactDispo) {
        if (Util.notEmpty(profilAssmat.getTelephoneFixe())
            && AssmatUtil.getBooleanFromString(profilAssmat.getVisbiliteTelephoneFixe())) {
          assmatTel = profilAssmat.getTelephoneFixe();
        }
        if (Util.notEmpty(mbrAssmat.getMobile())
            && AssmatUtil.getBooleanFromString(profilAssmat.getVisibiliteTelephonePortable())) {
          assmatTel += Util.notEmpty(assmatTel) ? "\n" : "";
          assmatTel += mbrAssmat.getMobile();
        }
        // 0011892: Si l'am a mis non visible ?? son tel portable et son tel fixe et qu'elle a de la dispo par rapport ?? la recherche effectu??e, afficher au moins son tel fixe si renseign?? ou son tel portable si pas de tel fixe et que tel portable renseign?? 
        if( !AssmatUtil.getBooleanFromString(profilAssmat.getVisibiliteTelephonePortable()) && !AssmatUtil.getBooleanFromString(profilAssmat.getVisibiliteTelephonePortable())) {
          if(Util.notEmpty(profilAssmat.getTelephoneFixe())) {
            assmatTel = profilAssmat.getTelephoneFixe();
          }else if(Util.notEmpty(mbrAssmat.getMobile())) {
            assmatTel = mbrAssmat.getMobile();
          }
        }
      }
      PdfPCell telCell = new PdfPCell();
      Paragraph paraph = new Paragraph(0, new Chunk(assmatTel, Fonts.TEXTE));
      paraph.setLeading(0, 1.2f);
      paraph.setAlignment(Element.ALIGN_CENTER);
      telCell.addElement(paraph);
      table.addCell(telCell);

      // Agr??ment total
      table.addCell(new Paragraph(new Chunk(getAgrement(asmmatSolis, null), Fonts.TEXTE)));

      // Disponibilit??s
      String listDispo = "";
      List<Disponibilite> dispoList = profilMgr.getDisponibilitesList(mbrAssmat, null);

      boolean hasInconnu = false;
      boolean hasNonDispo = false;                   
      Set<Disponibilite> dispoListImmFutur = new TreeSet<Disponibilite>(new DisponibiliteComparator());         
      for(Disponibilite itDispo : dispoList) {
        if((SelectionEtatDispo.IMMEDIATE.getValue().equalsIgnoreCase(itDispo.getEtatDispo())) || (SelectionEtatDispo.FUTURE.getValue().equalsIgnoreCase(itDispo.getEtatDispo())) ){
          dispoListImmFutur.add(itDispo);
        }else if(SelectionEtatDispo.INCONNU.getValue().equalsIgnoreCase(itDispo.getEtatDispo())) {
          hasInconnu = true;
        }else if(SelectionEtatDispo.NON_DISPO.getValue().equalsIgnoreCase(itDispo.getEtatDispo())) {
          hasNonDispo = true;
        }
      }

      // La liste des disponibilit??s de l'assmat
      if(Util.notEmpty(dispoListImmFutur)) {       
        // Regroupement des disponibilit??s de l'assmat
        Map<Date, Map<Integer, Map<String, Map<String, Set<Disponibilite>>>>> dispoAssmatDateMap = getDispoSelect(dispoListImmFutur);    
        // Lib??ll?? de la list des dispos de l'assmat
        listDispo = getDispoSelectLbl(dispoAssmatDateMap);      
      }else if(hasNonDispo && !hasInconnu ) {
        listDispo = AssmatUtil.getMessage("PROFIL-ASSMAT-LIBELLE-AUCUNE-DISPO-HTML");
      }else if(hasInconnu && !hasNonDispo) {
        listDispo = AssmatUtil.getMessage("PROFIL-ASSMAT-LIBELLE-NON-RENSEIGNE-HTML");
      }else {
        listDispo = AssmatUtil.getMessage("PROFIL-ASSMAT-LIBELLE-AUCUNE-DISPO-ET-RENSEIGNE-HTML");
      }          
      table.addCell(new Paragraph(new Chunk(listDispo, Fonts.TEXTE)));

      // Sp??cificit??s d'accueil possibles
      PdfPCell disposCell = new PdfPCell();
      addSpeAccueil(profilAssmat, disposCell);
      table.addCell(disposCell);

      // Date mise ?? jour
      String assmatMaj = "";
      Date maj = profilAssmat.getMdate();
      Date dateModifDispo = profilMgr.getDateModifDispo(profilAssmat.getAuthor());
      if (dateModifDispo != null && dateModifDispo.after(maj)) {
        maj = dateModifDispo;
      }
      assmatMaj = formater.format(maj);
      PdfPCell dateMajCell = new PdfPCell();
      Paragraph paraphDateMajCell = new Paragraph(0, new Chunk(assmatMaj, Fonts.TEXTE));
      paraphDateMajCell.setLeading(0, 1.2f);
      paraphDateMajCell.setAlignment(Element.ALIGN_CENTER);
      dateMajCell.addElement(paraphDateMajCell);
      table.addCell(dateMajCell);
    }

    doc.add(table);

    doc.close();
  }
  

  /**
   * Traitement de la liste pour le r??sulat d'une recherche d'assmat
   * 
   */
  public static void traitementStructureSet(TreeMap<AssmatSearch, PointAssmat> assmatResult, OutputStream output,
      boolean isSelection, HttpServletRequest request)
          throws DocumentException, MalformedURLException, IOException, NoSuchFieldException {

    if (Util.isEmpty(assmatResult)) {
      LOGGER.error("Aucun r??sultat d'assmat, PDF non g??n??r??");
      return;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("traitementStructureSet() - start." + assmatResult.size());
    }

    // Indique si le membre connect?? est un RAM ou si Contributeur avec
    // pouvoir
    Member loggedMember = Channel.getChannel().getMember((String) request.getSession().getAttribute("logMemberId"));
    Boolean isRAM = AssmatUtil.getMemberIsRam(loggedMember);
    Boolean isContribPower = AssmatUtil.getMemberIsContribPower(loggedMember);

    Document doc = new Document(PageSize.A4.rotate(), 25, 25, 25, 35);

    PdfWriter writer = PdfWriter.getInstance(doc, output);

    writer.setFullCompression();
    writer.setPageEvent(new GeneratePdf());

    // ---------------------------------------------
    // Pied de page
    // ---------------------------------------------
    final String mention;      
    Set<Place> setPlace = (Set<Place>) request.getSession().getAttribute("relais");
    
    
    // Affiche le relais de la recherche si un relais (avec l'adresse)
    if (Util.notEmpty(setPlace) && setPlace.size() == 1) {
      Place ramPlace = Util.getFirst(setPlace);
      String nomRelais = ramPlace.getTitle();
      String adresseRam = " - " + ramPlace.getStreet().replaceAll("[\r\n]+", " ") + " "
        + ramPlace.getZipCode() + " " + ramPlace.getCity().getTitle();
      String telRam = Util.notEmpty(Util.getFirst(ramPlace.getPhones())) ? " - T??l : " + Util.getFirst(ramPlace.getPhones()) : "";
      String emailRam = " - " + Util.getFirst(ramPlace.getMails());
      mention = nomRelais + adresseRam + telRam + emailRam;  
    }else if(Util.notEmpty(setPlace) && setPlace.size() > 1) {
    	// Affiche les  relais de la recherche si plusieurs relais (sans l'adresse)
    	String mentionsRelais = "";
    	for(Place itPlace : setPlace){
    		String telRam = Util.notEmpty(Util.getFirst(itPlace.getPhones())) ? " - T??l : " + Util.getFirst(itPlace.getPhones()) : "";
    		mentionsRelais +=  itPlace.getTitle() +  telRam + " // ";
    	}
    	mention = mentionsRelais.substring(0, mentionsRelais.length() - 3);
    } else{      
      mention = "";
    }

    
    writer.setPageEvent(new FooterAssmatPdf(mention));

    doc.open();

    PdfPTable table;
    PdfPCell cell;
    Paragraph p;

    // Haut de page : "Liste des assistantes maternelles agr????es au..."
    // ==========================================================================================

    p = new Paragraph();
    Date currentDate = new Date();
    p.add(new Chunk("Liste des assistantes maternelles agr????es au " + formater.format(currentDate),
        Fonts.TITRE_ENTETE));
    p.setAlignment(Element.ALIGN_RIGHT);
    doc.add(p);

    // ==========================================================================================
    // FIN Haut de page

    // Titre et description avant tableau
    // ==========================================================================================

    // R??cup??re les les liste de disponibilit?? pour chaque Assmat Search
    Map<Long, Set<DispoAssmat>> resultDispoRechercheMap = (Map) request.getSession().getAttribute("resultDispoRechercheMap");
    Map<Long, Set<DispoAssmat>> resultDispoFuturMap = (Map) request.getSession().getAttribute("resultDispoFuturMap");

    table = new PdfPTable(1);
    table.setWidthPercentage(100);

    // Crit??res de recherche
    Map<String, String[]> paramsMap = (Map) request.getSession().getAttribute("paramsMap");
    String commune = paramsMap.get("cityName")[0];
    String adress = paramsMap.get("adresse")[0];
    String adresseVille = "";
    if (Util.notEmpty(adress)) {
      adress = " - " + adress;
      adresseVille = adress;
    }
    
    boolean isGeoSearch = request.getSession().getAttribute("userLocation") != null;
    
    String codeInsee = paramsMap.get("codeInsee")[0];
    
    String[] idQuartier = paramsMap.get("quartier");
    String[] idMicroQuartier = paramsMap.get("microQuartier");
    
    String distanceParam = paramsMap.get("distance")[0];
    String distance = " - ";
    if (Util.isEmpty(distanceParam) || "0".equals(distanceParam) || ("-20".equals(distanceParam) && Util.isEmpty(idMicroQuartier)) ||  "-10".equals(distanceParam) && Util.isEmpty(idQuartier)) {
      distance += "Toute la commune";
    } else if("-10".equals(distanceParam) && Util.notEmpty(idQuartier)) {     
      ArrayList<String> quartierList = new ArrayList<String>();
      for(String itIdQuartier : idQuartier) {
        quartierList.add(QuartierDAO.getLibQuartier(itIdQuartier));        
      }
      distance += "Quartier : " + Util.join(quartierList, ", ");
    } else if("-20".equals(distanceParam) && Util.notEmpty(idMicroQuartier)) {
      Map<String, ArrayList<String>> microQuartierMap = new HashMap<String, ArrayList<String>>();     
      for(String itIdMicroQuartier : idMicroQuartier) {
        String itQuartier = QuartierDAO.getLibQuartier(QuartierDAO.getIdQuartier(itIdMicroQuartier));
        ArrayList<String> itMicroQuartierList = new ArrayList<String>();
        if(microQuartierMap.containsKey(itQuartier)) {
          itMicroQuartierList = microQuartierMap.get(itQuartier);
        }
        itMicroQuartierList.add(QuartierDAO.getLibMicroQuartier(itIdMicroQuartier));
        microQuartierMap.put(itQuartier, itMicroQuartierList);        
      }
      distance += "Quartier : " + microQuartierMap.toString().replaceAll("\\{", "").replaceAll("}", "").replaceAll("\\[", "(").replaceAll("]", ")").replaceAll("=", " ");         
    } else if (distanceParam.length() < 4) {
      distance += distanceParam + " m";
    } else {
      distance += Integer.parseInt(distanceParam) / 1000 + " km";
    }

    String ageParam = paramsMap.get("age")[0];
    Category categAge = channel.getCategory("$jcmsplugin.assmatplugin.categ.trancheage");
    String age = "";
    for (Category itCat : categAge.getChildrenSet()) {
      if (itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping").equals(ageParam)) {
        age = " - " + StringEscapeUtils.unescapeHtml(itCat.getName());
      }
    }

    Long dateRechercheParam = Long.parseLong(paramsMap.get("month")[0]);
    Date dateTime = new Date(dateRechercheParam);
    SimpleDateFormat formaterDateRecherche = new SimpleDateFormat("MMMM yyyy");
    String dateRechercheMoisAnnee = formaterDateRecherche.format(dateTime);
    String dateRecherche = " - ?? partir de " + dateRechercheMoisAnnee;

    cell = new PdfPCell();
    Chunk structureChunk = new Chunk("R??sultats pour : " + commune + adress + distance + age + dateRecherche,
        Fonts.TITRE_BLANC);
    cell.addElement(structureChunk);
    cell.setBackgroundColor(new Color(68, 84, 106));
    cell.setPaddingBottom(10);
    cell.setPaddingLeft(10);
    cell.setBorder(Rectangle.NO_BORDER);
    table.setSpacingBefore(10);
    table.setSpacingAfter(5);
    table.addCell(cell);
    doc.add(table);

    // Libell?? pour : Mention de pr??caution sur l'actualisation des
    // informations
    p = new Paragraph();
    p.setLeading(0, 1.0f);
    p.add(new Chunk(AssmatUtil.getMessage("INFORMATION-RESULTAT-RECHERCHE-AM-RAM"), Fonts.DESCRIPTION));
    doc.add(p);

    // ==========================================================================================
    // FIN Titre et description avant tableau

    // Tableau des Assmat INSCRITES
    // ==========================================================================================

    // Ent??te 1??re ligne du tableau
    // Tableau s??par?? car on ne veut pas r??p??rer cette ligne sur toutes les
    // pages, contrairement ?? la 2??me ligne
    // ------------------------------------------------------------------------------------------
    
    table = getHeaderTabInscrite(doc, true, isGeoSearch);

    Set<Entry<AssmatSearch, PointAssmat>> assmatResultSet = assmatResult.entrySet();
    for (Entry itEntry : assmatResultSet) {
      addAssmatFullContent(doc, itEntry, isSelection, table,  resultDispoRechercheMap, resultDispoFuturMap, codeInsee);
    }

    doc.add(table);

    // ------------------------------------------------------------------------------------------
    // FIN Corps du tableau

    // ==========================================================================================
    // FIN Tableau des Assmat INSCRITES

    // Tableau des Assmat NON INSCRITES
    // ==========================================================================================

    // Si le membre identif?? est du groupe RAM ou Contributeur avec pouvoir
    // Alors afficher le tableau des assmat non inscrite sur le site
    if (isRAM || isContribPower) {

      // Passage ?? l'orientation portrait et nouvelle page
      doc.setPageSize(PageSize.A4);
      doc.newPage();

      table = new PdfPTable(1);
      table.setWidthPercentage(100);
      Color backFirstHeaderColor = WebColors.getRGBColor("#B4C6E7");
      cell = new PdfPCell();

      // Libell?? pour : Assistantes maternelles agr????es NON INSCRITES sur le
      // site assmat
      Paragraph p1 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-NON-INSCRITES"),
          Fonts.TITRE_TABLEAU));
      p1.setAlignment(Element.ALIGN_CENTER);

      // Libell?? pour : Tri par disponibilit?? puis proximit?? [si adresse
      // renseign??e] OU tri par disponibilit??- quartier [si pas d???adresse]
      Paragraph p2 = new Paragraph();
      if (Util.notEmpty(adress)) {
        p2 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-TRI1-NON-INSCRITES"),
            Fonts.SOUSTITRE_TABLEAU));
      } else {
        p2 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-TRI2-NON-INSCRITES"),
            Fonts.SOUSTITRE_TABLEAU));
      }
      p2.setAlignment(Element.ALIGN_CENTER);

      cell = new PdfPCell();
      cell.addElement(p1);
      cell.addElement(p2);
      cell.setPaddingBottom(10);
      cell.setBackgroundColor(backFirstHeaderColor);
      table.addCell(cell);
      table.setSpacingBefore(10);


      // ------------------------------------------------------------------------------------------
      // FIN Ent??te 1??re ligne du tableau

      // Ent??te 2??me ligne du tableau
      // ------------------------------------------------------------------------------------------
      PdfPTable tableNonInscrit = new PdfPTable(new float[] { 100, 50, 100 });
      tableNonInscrit.setWidthPercentage(100);
      Color nonInscritBackHeaderColor = WebColors.getRGBColor("#D9E2F3");
      cell = new PdfPCell();

      // Nom et pr??nom, adresse
      p1 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-NON-INSCRITES-COL1"),
          Fonts.TEXTE_GRAS));
      cell = new PdfPCell(p1);
      cell.setBackgroundColor(nonInscritBackHeaderColor);
      tableNonInscrit.addCell(cell);

      // T??l??phone
      p2 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-NON-INSCRITES-COL2"),
          Fonts.TEXTE_GRAS));
      cell = new PdfPCell(p2);
      cell.setBackgroundColor(nonInscritBackHeaderColor);
      tableNonInscrit.addCell(cell);

      // Agr??ment total
      Paragraph p3 = new Paragraph();
      p3 = new Paragraph(new Chunk(AssmatUtil.getMessage("RESULTAT-RECHERCHE-AM-PDF-RAM-NON-INSCRITES-COL3"),
          Fonts.TEXTE_GRAS));
      cell = new PdfPCell(p3);
      cell.setBackgroundColor(nonInscritBackHeaderColor);
      tableNonInscrit.addCell(cell);

      // Nombre de ligne pour l'entete
      tableNonInscrit.setHeaderRows(1);

      // ------------------------------------------------------------------------------------------
      // FIN Ent??te 2??me ligne du tableau

      // Corps du tableau
      // ------------------------------------------------------------------------------------------

      // Requete solis sur les crit??res de recerche pour les assmats non
      // inscrites sur le site
      
      // Seulement assmat qui correspond a la distance demand??e (si une adresse de renseign??e)          
      // Si une adresse est renseign???? elle prend le dessus sur la geoloc     
      
        
      Double geoLongDouble = 0.0; 
      Double geoLatDouble = 0.0; 
      

      PointAssmat pointUser = (PointAssmat) request.getSession().getAttribute("userLocation");
      // Trie al??atoire si pas d'adresse de renseign??e (meme si geoloc activ?? ne pas prendre l'adresse de la RAM)
      if( pointUser != null) {                       
        geoLongDouble = Double.valueOf(pointUser.getLongitude()); 
        geoLatDouble = Double.valueOf(pointUser.getLatitude()); 
      }
          
      List<AssmatSolis> assmatSolisList = AssmatSearchDAO.getNonInscritResultSearch(paramsMap, loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu"));   
      String hashKey = paramsMap.get("hashKey")[0];
      Set<AssmatSolis> nonInscriteList = new TreeSet<AssmatSolis>(new AssmatSolisDistanceComparator(pointUser, hashKey));  
      Set<ProfilASSMAT> profilAMSet = channel.getAllPublicationSet(ProfilASSMAT.class, Channel.getChannel().getDefaultAdmin());
      // R??cup??re le num??ro de dossier des assmats inscrites sur le site (et les inscrites mais refusant d'etre affich?? sur le site)
      List<Integer> numDossierAssmatInscrites = new ArrayList<Integer>();
      for(ProfilASSMAT itProfil : profilAMSet){
        if(itProfil.getVisibiliteSite()) {
          numDossierAssmatInscrites.add(itProfil.getNum_agrement());
        }
      }

      // Filtre par distance si une adresse dans la recherche     
      if(Util.notEmpty(distanceParam) && Integer.parseInt(distanceParam) > 0 && geoLatDouble != 0.0 && geoLongDouble != 0.0) {
        List<AssmatSolis> assmatNotDistanceList = new ArrayList<AssmatSolis>();
        for(AssmatSolis itAssmatSolis : assmatSolisList) {        
          Double itLat = 0.0;
          Double itLong = 0.0;          
          if(Util.notEmpty(itAssmatSolis.getLatitude()) && Util.notEmpty(itAssmatSolis.getLongitude())) {
            itLat = itAssmatSolis.getLatitude().doubleValue();
            itLong = itAssmatSolis.getLongitude().doubleValue();
          }else if(Util.notEmpty(itAssmatSolis.getLatitudeMam()) && Util.notEmpty(itAssmatSolis.getLongitudeMam())) {
            itLat = itAssmatSolis.getLatitudeMam().doubleValue();
            itLong = itAssmatSolis.getLongitudeMam().doubleValue();
          }
          
          Double itDistance = AssmatUtil.getDistance(geoLatDouble, geoLongDouble, itLat, itLong);          
          if(Double.parseDouble(distanceParam) <= itDistance * 1000) {
            assmatNotDistanceList.add(itAssmatSolis);
          }
        } 
        assmatSolisList.removeAll(assmatNotDistanceList);
      }

      // Seulement les assmats non inscrite sur le site (ou les inscrites mais refusant d'etre affich?? sur le site)  
      for(AssmatSolis itAssmatSolis : assmatSolisList) {     
        if (!numDossierAssmatInscrites.contains(itAssmatSolis.getNumDossierAssmat())) {
          nonInscriteList.add(itAssmatSolis);
        }
      }

      // Affiche le tableau des assmat non inscrites seulement si il y a au moins une assmat dans solis
      if(Util.notEmpty(nonInscriteList)){

        doc.add(table);

        for (AssmatSolis asmmatSolis : nonInscriteList) {

          // Nom pr??nom, adresse
          p = new Paragraph();
          p.setLeading(0, 1.5f);
          p.add(getNom(asmmatSolis));
          p.add(getAdresseSearchNonInscrites(asmmatSolis));
          tableNonInscrit.addCell(p);

          // T??l??phone
          String assmatTel = "";
          if (Util.notEmpty(asmmatSolis.getTelPrincipal())) {
           assmatTel = asmmatSolis.getTelPrincipal();
          }
          if (Util.notEmpty(asmmatSolis.getTelPortable())) {
            assmatTel += Util.notEmpty(assmatTel) ? "\n" : "";
            assmatTel += asmmatSolis.getTelPortable();
          }
          PdfPCell telCell = new PdfPCell();
          Paragraph paraph = new Paragraph(0, new Chunk(assmatTel, Fonts.TEXTE));
          paraph.setLeading(0, 1.2f);
          paraph.setAlignment(Element.ALIGN_CENTER);
          telCell.addElement(paraph);
          tableNonInscrit.addCell(telCell);

          // Agr??ment total
          tableNonInscrit.addCell(new Paragraph(new Chunk(getAgrement(asmmatSolis, null), Fonts.TEXTE)));
        }
        doc.add(tableNonInscrit);
      }
    }

    // ------------------------------------------------------------------------------------------
    // FIN Corps du tableau
    // ==========================================================================================

    doc.close();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("traitementStructureSet() - END - PDF complet g??n??r??.");
    }
  }

}
