package fr.cg44.plugin.assmat.datacontroller;

import fr.cg44.plugin.assmat.util.ContactUtil;
import generated.FormContactAssmat;

import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Category;
import com.jalios.jcms.Channel;
import com.jalios.jcms.Data;
import com.jalios.jcms.Member;
import com.jalios.jcms.plugin.PluginComponent;
import com.jalios.util.Util;

public class ContactDataController extends BasicDataController implements PluginComponent {

  /** Initiliasation du logger */
  private static final Logger LOGGER = Logger.getLogger(ContactDataController.class);

  public void afterWrite(Data data, int op, Member mbr, @SuppressWarnings("rawtypes")
  Map context) {
    Channel channel = Channel.getChannel();

    if (op == OP_CREATE) {
    	FormContactAssmat form = (FormContactAssmat) data;

      // Si la catégorie du sujet a une description, utiliser cette dernière
      // comme email destinataire
      // sinon prendre l'email défini dans le module
      @SuppressWarnings("unchecked")
      Category sujet = ((TreeSet<Category>) form.getSubject(channel.getDefaultAdmin())).first();
      String contactMail = Util.notEmpty(sujet.getDescription()) ? sujet.getDescription() : channel.getDefaultEmail();

      if (Util.notEmpty(contactMail)) {
    	contactMail = contactMail.trim();
        if (Pattern.matches(EMAIL_REGEXP, contactMail)) {
          LOGGER.debug("Envoi du mail de contact à l'adresse : " + contactMail);
          ContactUtil.envoiMailContact(form, contactMail);
        } else {
          LOGGER.error("L'adresse e-mail du destinataire des mails de contact channel.getDefaultEmail() ne respecte pas le Pattern : "
              + EMAIL_REGEXP);
          ContactUtil.msgEchecEnvoiMailContact();
        }
      } else {
        LOGGER.error("L'adresse e-mail du destinataire des mails de contact "+channel.getDefaultEmail()+" est vide");
        ContactUtil.msgEchecEnvoiMailContact();
      }
    }
  }

}
