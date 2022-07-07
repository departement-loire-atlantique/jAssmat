package fr.trsb.cd44.solis.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;

import com.jalios.util.Util;
import com.jalios.jcms.Channel;
import com.jalios.jcms.Group;
import com.jalios.jcms.Member;

import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;
import fr.trsb.cd44.solis.beans.Quartier;

/**
 * 
 * Classe de gestion des Quartiers
 * 
 * - affiche la liste des quartiers
 * - affiche la liste des micros-quartiers
 * 
 * 
 * @author sguellec
 * 
 */
public class QuartierDAO {
  private static final Logger logger = Logger.getLogger(QuartierDAO.class);

  
  /**
   * Renvoie la liste des quartiers de Nantes (Stockés en base)
   * 
   * @return La liste des quartiers de Nantes
   */
  
  public static List<Quartier> getAllQuartiersNantes() {
	  //
	  Type[] types = new Type[2]; 
	  types[0] = Hibernate.STRING;
	  types[1] = Hibernate.STRING;
	  //  
	  
	  Criteria crit = HibernateCD44Util.getSession().createCriteria(Quartier.class);
	  ProjectionList projectionList = Projections.projectionList();
	  projectionList.add(Projections.sqlGroupProjection("id_quartier, lib_quartier", "id_quartier, lib_quartier", new String[]{"id_quartier", "lib_quartier"}, types));
	  crit.setProjection(projectionList);
	  crit.add(Restrictions.like("idQuartier","44109%"));
	  crit.addOrder(Order.asc("libQuartier"));
	  
	  List<Object[]> resultSql = (List<Object[]>) crit.list();
	  //  
	  List<Quartier> quartierList = new ArrayList<Quartier>();
	  for(Object[] itQuartier : resultSql) {
		  quartierList.add(new Quartier((String)itQuartier[0], (String) itQuartier[1]));
	  }	

	    
    return quartierList;
  }
  
  public static List getAllQuartiersNantesHQL() {
	  String requete = "select quartier.idQuartier, quartier.libQuartier from Quartiers as quartier where idQuartier like '44109%' group by idQuartier, libQuartier order by libQuartier asc";
	  List list = HibernateCD44Util.getSession().createQuery(requete).list();

	  return list;
  }
  
  /**
   * Renvoie la liste des micros quartiers de Nantes (Stockés en base) pour un utilisateur donné.
   * Un Ram ne peut voir que les micro quartier qui le concerne.
   * Un utilisateur avec pouvoir ou un admin les verra tous.
   * 
   * @return La liste des micros quartiers de Nantes
   */
  public static List<Quartier> getAllMicrosQuartiersNantes(Member loggedMember) {
	  Type[] types = new Type[2]; 
	  types[0] = Hibernate.STRING;
	  types[1] = Hibernate.STRING;
	  
	  Criteria crit = HibernateCD44Util.getSession().createCriteria(Quartier.class);
	  ProjectionList projectionList = Projections.projectionList();
	  projectionList.add(Projections.sqlGroupProjection("id_micro_quartier, lib_micro_quartier", "id_micro_quartier, lib_micro_quartier", new String[]{"id_micro_quartier", "lib_micro_quartier"}, types));	  
	  crit.setProjection(projectionList);
	  crit.add(Restrictions.like("idMicroQuartier","44109%"));
	  
	  Group ramGroup = Channel.getChannel().getGroup(Channel.getChannel().getProperty("jcmsplugin.assmatplugin.group.ram.id"));
	  
	  /* Pour les RAM, on essaie de récupérer l'ID Solis (int), qui est inclu dans une extradata (string).
	   * Si on échoue, on renvoie une liste vide (la requête ne renvoie rien)
	   */
	  if(loggedMember.belongsToGroup(ramGroup)){
		  String idRam = loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu").replaceAll("RAM_", "");
		  int idRamSolis = -1;
		  try{
			  idRamSolis = Integer.parseInt(idRam);
			  }
		  catch(NumberFormatException nfe){
			  logger.warn("Recherche avancée - erreur lors de la récupération de l'idRam : "+idRam);
		  }
		  crit.add(Restrictions.eq("idRam",idRamSolis));
	  }
	  
	  crit.addOrder(Order.asc("libMicroQuartier"));
	  List<Object[]> resultSql = (List<Object[]>) crit.list();
	  //  
	  List<Quartier> microQuartierList = new ArrayList<Quartier>();
	  for(Object[] itMicroQuartier : resultSql) {
		  microQuartierList.add(new Quartier((String)itMicroQuartier[0], (String) itMicroQuartier[1], true));
	  }	  
    return microQuartierList;
  }

 
  /**
   * Retourne le quartier par rapport au micro quartier
   * @param idMicro
   * @return
   */
  public static String getIdQuartier(String idMicroQuartier) {
    if(Util.isEmpty(idMicroQuartier) || idMicroQuartier.length() != 9 ) {
      return "";
    }
    return idMicroQuartier.substring(0, 7);
  }
  
  
  /**
   * Retourne le libelle du quartier 
   * @param idQuartier
   * @return
   */
  public static String getLibQuartier(String idQuartier) {
    Criteria crit = HibernateCD44Util.getSession().createCriteria(Quartier.class).add(Restrictions.eq("idQuartier", idQuartier));
    List<Quartier> list = crit.list();
    if (Util.isEmpty(list)) {
      return "";
    }
    return Util.getFirst(list).getLibQuartier();
  }
  
  /**
   * Retourne le libelle du micro-quartier 
   * @param idQuartier
   * @return
   */
  public static String getLibMicroQuartier(String idMicroQuartier) {
    Criteria crit = HibernateCD44Util.getSession().createCriteria(Quartier.class).add(Restrictions.eq("idMicroQuartier", idMicroQuartier));
    List<Quartier> list = crit.list();
    if (Util.isEmpty(list)) {
      return "";
    }
    return Util.getFirst(list).getLibMicroQuartier();
  }
  

}
