package fr.cg44.plugin.assmat.alert;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Group;
import com.jalios.jcms.alert.Alert;
import com.jalios.jcms.alert.AlertChannel;
import com.jalios.jcms.alert.BasicAlertPolicyFilter;

/**
 * Bloque les alertes envoyées aux assmat
 *
 */
public class AssmatAlert extends BasicAlertPolicyFilter {
   
  Channel channel =  Channel.getChannel();
  
  @Override
  public Alert beforeSendAlert(Alert alert, AlertChannel alertChannel) {  
    Group assmatGroup = channel.getGroup(channel.getProperty("jcmsplugin.assmatplugin.groupe.assistante_maternelle"));
    if(assmatGroup != null && alert.getRecipient() != null && alert.getRecipient().belongsToGroup(assmatGroup)) {
      return null;
    }
    return alert;
  }
  
  
  @Override
  public boolean saveAlert(Alert alert, boolean saveAlert) {
    Group assmatGroup = channel.getGroup(channel.getProperty("jcmsplugin.assmatplugin.groupe.assistante_maternelle"));
    if(assmatGroup != null && alert.getRecipient() != null && alert.getRecipient().belongsToGroup(assmatGroup)) {
      return false;
    }
    return saveAlert;
  }
  
}
