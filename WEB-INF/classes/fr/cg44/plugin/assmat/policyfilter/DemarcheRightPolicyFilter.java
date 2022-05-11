package fr.cg44.plugin.assmat.policyfilter;

import generated.Demarche;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Data;
import com.jalios.jcms.Group;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.jcms.policy.BasicRightPolicyFilter;

/**
 * Gère les droit pour les démarches en ligne
 * 
 * @author mformont
 * 
 */
public class DemarcheRightPolicyFilter extends BasicRightPolicyFilter {

  //private static Logger logger = Logger.getLogger(DemarcheRightPolicyFilter.class); 
  private static Channel channel = Channel.getChannel();  
  private static String ASSMAT_GROUP = "jcmsplugin.assmatplugin.groupe.assistante_maternelle";
  
  /*
   * Le groupe des assmat ne peut pas modifier des démarches (même si elles en sont auteur ) 
   */
  @Override
  public boolean canWorkOn(boolean isAuthorized, Publication pub, Member member) {
    if (member.isAdmin()) {
      return true;
    } 
    Group groupAssmat = channel.getGroup(channel.getProperty(ASSMAT_GROUP));
    if (pub instanceof Demarche && member.belongsToGroup(groupAssmat)) {
      return false;
    }
    return isAuthorized;
  }

  /**
   * Le groupe des assmat ne peut pas supprimer des démarches (même si elles en sont auteur) 
   */
  @Override
  public boolean canDeleteOther(boolean isAuthorized, Member member, Data data) {
    Group groupAssmat = channel.getGroup(channel.getProperty(ASSMAT_GROUP));
    if (data instanceof Demarche && member.belongsToGroup(groupAssmat)) {
      return false;
    }
    return isAuthorized;
  }
  
  /**
   * Seul les auteurs des démarches peuvent voir celles-ci
   */
  @Override
  public boolean canBeReadBy(boolean isAuthorized, Publication pub, Member member, boolean searchInGroups) {
    if(pub instanceof Demarche && member == null) {
      return false;
    } 
    if(member == null) {
      return isAuthorized;
    }
    if(member.isAdmin()) {
      return true;
    }
    if (pub instanceof Demarche && !JcmsUtil.isSameId(member, pub.getAuthor())) {
      return false;
    }
    return isAuthorized;
  }

}
