package fr.cg44.plugin.assmat.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.jalios.jcms.Category;
import com.jalios.jcms.Channel;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.context.JcmsContext;
import com.jalios.util.Util;

import generated.City;
import generated.FormContactAssmat;

/**
 * Classe utilitaire de contact
 * 
 * 
 * CODE REPRIS DU CD44 
 */
public class ContactUtil {

  private static final Logger LOGGER = Logger.getLogger(ContactUtil.class);

  private static final Channel channel = Channel.getChannel();

  /**
   * Constructeur
   */
  public static void Contact1Util() {
  }



  /**
   * Envoi d'un email de contact.
   */
  public static void envoiMailContact(FormContactAssmat form, String emailTo) {
    // Objet
   
        
    String objet =""; 
    Category categ = (Category) Util.getFirst(form.getSubject(channel.getDefaultAdmin()));
    if(Util.notEmpty(categ)){
      objet = categ.getName();
    }

    String cityName = form.getCity();
   
    // Contenu
    String contenu = "Nom : " + form.getName() + "<br />";
    contenu += "Prenom : " + form.getFirstName() + "<br />";
    contenu += "Email expediteur : " + form.getCourriel() + "<br />";
    contenu += "Commune : " + cityName + "<br />";
    contenu += "Telephone : " + form.getPhone() + "<br />";
    if (Util.notEmpty(channel.getDefaultAdmin())) {
      contenu += "Sujet : " + form.getSubject(channel.getDefaultAdmin()).first() + "<br />";
    }
    contenu += "Message : " + form.getMessage();

    // Envoi du mail d'activation à l'adresse mail saisie de l'utilisateur
    try {
      MailCG44Util.sendMail(objet, contenu, form.getCourriel(), emailTo, null, false);
    } catch (javax.mail.MessagingException e) {
      msgEchecEnvoiMailContact();
      LOGGER.error("Erreur lors de l'envoi du mail" + e.getMessage());
    }

  }

 

  /**
   * Envoi du message d'erreur d'envoi du mail.
   */
  public static void msgEchecEnvoiMailContact() {
    HttpServletRequest request = channel.getCurrentServletRequest();
    JcmsContext.setErrorMsgSession(JcmsUtil.glpd("cg44.echec.send.mail.contact"), request);
    // request.setAttribute(JcmsConstants.ERROR_MSG,
    // "Une erreur s'est produite lors de l'envoi de l'email");
    LOGGER.warn(JcmsUtil.glpd("cg44.echec.send.mail.contact"));
  }
}