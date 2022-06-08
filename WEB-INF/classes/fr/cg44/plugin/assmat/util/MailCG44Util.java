package fr.cg44.plugin.assmat.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.jalios.jcms.FileDocument;
import com.jalios.jcms.mail.MailMessage;
import com.jalios.util.Util;

/**
 * Classe permettant l'envoi de mail avec un pied spécifique au Conseil général
 * de Loire Atlantique.
 */
public class MailCG44Util {

  private static final Logger LOGGER = Logger.getLogger(MailCG44Util.class);

  /**
   * Envoi de mail avec le pied de mail du CG44.
   * 
   * @param subject
   *          Objet du mail
   * @param content
   *          Contenu du mail
   * @param emailFrom
   *          Email de l'expéditeur
   * @param emailTo
   *          Email du destinataire
   * @param listePieceJointe
   *          Liste des pièces jointes
   * @param clause
   *          ajout de la clause.
   */
  public static void sendMail(String subject, String content, String emailFrom, String emailTo, ArrayList<FileDocument> listePieceJointe, boolean clause)
      throws javax.mail.MessagingException {

    LOGGER.debug("Envoi du message " + subject);
    LOGGER.debug("emailFrom = " + emailFrom);
    LOGGER.debug("emailTo = " + emailTo);
    String contenu = "";
    contenu += content;
    if (clause) {
      contenu = addClause(contenu);
    }
    MailMessage mail = new MailMessage("Conseil Général de Loire Atlantique");
    mail.setFrom(emailFrom);
    mail.setTo(emailTo);
    mail.setSubject(subject);
    mail.setContentHtml(contenu);
    if (Util.notEmpty(listePieceJointe)) {
      for (FileDocument f : listePieceJointe) {
        mail.addAttachements(f);
      }
    }
    // Envoi du mail
    mail.send();
  }

  /**
   * Envoi de mail avec le pied de mail du CG44 pour les candidatures.
   * 
   * @param subject
   *          Objet du mail
   * @param content
   *          Contenu du mail
   * @param emailFrom
   *          Email de l'expéditeur
   * @param emailTo
   *          Email du destinataire
   * @param listePieceJointe
   *          Liste des pièces jointes
   * @param clause
   *          ajout de la clause.
   */
  public static void sendMailOffreEmploi(String subject, String content, String emailFrom, String emailTo, HashMap<File, String> listePieceJointe,
      boolean clause) throws javax.mail.MessagingException {

    LOGGER.debug("Envoi du message " + subject);
    LOGGER.debug("emailFrom = " + emailFrom);
    LOGGER.debug("emailTo = " + emailTo);
    String contenu = "";
    contenu += content;
    if (clause) {
      contenu = addClause(contenu);
    }
    MailMessage mail = new MailMessage("Conseil Général de Loire Atlantique");
    mail.setFrom(emailFrom);
    mail.setTo(emailTo);
    mail.setSubject(subject);
    mail.setContentText(Util.html2Ascii(contenu));
    mail.setContentHtml(contenu);
    if (Util.notEmpty(listePieceJointe)) {
      for (File f : listePieceJointe.keySet()) {
        mail.addFile(f);
      }
    }
    // Envoi du mail
    mail.send();
  }

  /**
   * Ajout des clauses à la fin du message.
   * 
   * @param contenu
   *          le message du mail.
   * @return le message avec les clauses.
   */
  public static String addClause(String contenu) {
    StringBuilder sb = new StringBuilder(contenu);
    sb.append("<br />");
    sb.append("Ce message a &eacute;t&eacute; envoy&eacute; &agrave; partir d'une adresse ne pouvant recevoir d'e-mails. Merci de ne pas y r&eacute;pondre.<br/>");
    sb.append("<br />");
    sb.append("---------------------------<br/>");
    sb.append("<br />");
    sb.append("En application de la loi n&ordm; 78-17 du 6 janvier 1978 relative &agrave; l'informatique, aux fichiers et aux libert&eacute;s, vous disposez des droits d'opposition, d'acc&egrave;s et de rectification des donn&eacute;es vous concernant. Ainsi, vous pouvez demander une mise &agrave; jour ou une suppression des informations vous concernant si elles s'av&egrave;rent inexactes, incompl&egrave;tes, &eacute;quivoques, p&eacute;rim&eacute;es ou si leur collecte ou leur utilisation, communication ou conservation est interdite.");
    return sb.toString();
  }
}
