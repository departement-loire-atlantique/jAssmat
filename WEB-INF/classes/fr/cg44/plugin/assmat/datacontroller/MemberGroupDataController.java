package fr.cg44.plugin.assmat.datacontroller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Channel;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.Group;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import generated.InscriptionAM;

/**
 * DataController sur les membres qui font parti du groupe RAM
 * DataController sur les membres qui font parti du groupe Associations
 * @author trsb
 *
 */
public class MemberGroupDataController extends BasicDataController {


  private static final Channel channel = Channel.getChannel();

  private static final String idGroupeRAM = channel.getProperty("jcmsplugin.assmatplugin.group.ram.id");

  private static final Group groupRAM = channel.getGroup(idGroupeRAM);
  
  private static final String idGroupeAsso = channel.getProperty("jcmsplugin.assmatplugin.group.asso.id");

  private static final Group groupAsso = channel.getGroup(idGroupeAsso);

  private static final String idPortletValidepass = channel.getProperty("plugin.assmatplugin.portlet.ram.validepass");

  private static final Publication portletValidepass= channel.getPublication(idPortletValidepass);
  
  @Override
  /**
   * 
   *
   *
   */
  public void afterWrite(Data data, int op, Member mbr, Map map) {

    // Si on a pas de categ relais
    if (Util.isEmpty(groupRAM) && Util.isEmpty(groupAsso) ) {
      return;
    }
    // Si le membre n'a pas de groupe
    Member memberData = (Member) data;
    if (Util.isEmpty(memberData.getGroups())) {
      return;
    }
    
    List<Group> grpList = Arrays.asList(memberData.getGroups());

    // Si le membre fait partie du groupe RAM o Asso
    if (grpList.contains(groupRAM) || grpList.contains(groupAsso) ) {
      Member mbrClone = (Member) memberData.clone();
      // Si c'est une création on désactive le comtpe
      if (op == OP_CREATE) {

  
        //le member est désactivé
        mbrClone.disable();

        // perform
        mbrClone.performUpdate(channel.getDefaultAdmin());

        sendMail(mbrClone);
        

      }

  
      if (op == OP_UPDATE) {
        
        //Si le groupe RAM ou ASSO est ajouté au membre
        // On verifie le pstatus avant modification
        Member oldMember= (Member ) map.get(CTXT_PREVIOUS_DATA);
        if(Util.notEmpty(memberData.getGroups())){
           if(Util.notEmpty(oldMember.getGroups())){
             
             List<Group> oldGroupList = Arrays.asList(oldMember.getGroups());
             List<Group> newGroupList = Arrays.asList(memberData.getGroups());
             
             if((!oldGroupList.contains(groupRAM) && newGroupList.contains(groupRAM) ) || (!oldGroupList.contains(groupAsso) && newGroupList.contains(groupAsso)) ){
               
               mbrClone.disable();

               // perform
               mbrClone.performUpdate(channel.getDefaultAdmin());
               
               sendMail(mbrClone);
      
             }
             
           }
        }
        
      }
      
      
    }

  }

  
  public static void sendMail(Member memberData){
    String idPortalAccueil = channel.getProperty("jcmsplugin.assmatplugin.general.portal.accueil");
    
    // Envoi du mail d'activation    
    String subject = AssmatUtil.getMessage("ACTIVATION-MEMBER-RAM-MAIL-SUBJET");
        
    String from = "assmat@loire-atlantique.fr";
    StringBuilder stbd = new StringBuilder();
    
    
    String[] parameters = new String[]{};
    parameters = (String[]) Util.insertArray(parameters, 0, memberData.getLogin());

    List<Group> grpList = Arrays.asList(memberData.getGroups());
    // Si le membre appartient au groupe Asso
    if(grpList.contains(groupAsso)) {
      subject = AssmatUtil.getMessage("ACTIVATION-MEMBER-ASSO-MAIL-SUBJET");
      parameters = (String[]) Util.insertArray(parameters, 1, "<a href='" + portletValidepass.getDisplayUrl(channel.getLocale()) + "?portal="+idPortalAccueil+"&idMember="+memberData.getId()+"'>" + AssmatUtil.getMessage("ACTIVATION-MEMBER-ASSO-MAIL-LIEN") + "</a>");
      stbd.append(AssmatUtil.getMessagePropertiesParameters("ACTIVATION-MEMBER-ASSO-MAIL-CONTENT", parameters));
    }else {
      parameters = (String[]) Util.insertArray(parameters, 1, "<a href='" + portletValidepass.getDisplayUrl(channel.getLocale()) + "?portal="+idPortalAccueil+"&idMember="+memberData.getId()+"'>" + AssmatUtil.getMessage("ACTIVATION-MEMBER-RAM-MAIL-LIEN") + "</a>");
      stbd.append(AssmatUtil.getMessagePropertiesParameters("ACTIVATION-MEMBER-RAM-MAIL-CONTENT", parameters));

    }
        
    AssmatUtil.sendMail(memberData.getEmail(), subject, stbd.toString(), from);
  }
  
  
  @Override
  public ControllerStatus checkIntegrity(Data data) {

    Member member = (Member) data;

    // Si on a pas de categ relais
    if (Util.notEmpty(groupRAM) || (Util.notEmpty(groupAsso))) {

      List<Group> grpList = Arrays.asList(member.getGroups());

      // Si le membre fait partie du groupe RAM
      if (grpList.contains(groupRAM) || grpList.contains(groupAsso) ) {

        // Si pas de mails renseigné
        if (Util.isEmpty(member.getEmail())) {
          return new ControllerStatus(AssmatUtil.getMessage("MEMBER-RELAIS-MAM-EMPTY-MAIL"));
        }
      
      }
    }
    return ControllerStatus.OK;
  }
}
