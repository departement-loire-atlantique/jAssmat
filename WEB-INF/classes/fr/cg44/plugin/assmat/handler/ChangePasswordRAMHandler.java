package fr.cg44.plugin.assmat.handler;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.HttpUtil;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.ResourceHelper;
import com.jalios.jcms.authentication.handlers.SessionAuthenticationHandler;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.db.HibernateUtil;
import com.jalios.jcms.dbmember.DBMember;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.jcms.i18n.I18nUtil;
import com.jalios.jcms.mail.MailMessage;
import com.jalios.util.BCrypt;
import com.jalios.util.DateUtil;
import com.jalios.util.ServletUtil;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.managers.SmsDAO;

public class ChangePasswordRAMHandler extends JcmsFormHandler {
   
    private boolean opReset = false;
    

    private String password;
    private String newpassword;
    private String confirmnewpassword;
    

    private static final Logger logger = Logger.getLogger(ChangePasswordRAMHandler.class);
    
    public boolean processAction() throws IOException {
     
        
        if (validateRequestChangePass()) {
            return performRequestChange();
        }
       
        return super.processAction();
    }

    public boolean validateRequestChangePass() {
        if (!opReset) {
            return false;
        }
     
        if(Util.isEmpty(password)){
            addMsgSession(new JcmsMessage(JcmsMessage.Level.WARN, "Le champ 'Mot de passe' est obligatoire"));
            return false;
        }
       
        if(! channel.checkCrypt(password, loggedMember.getPassword())){
          addMsgSession(new JcmsMessage(JcmsMessage.Level.WARN, "Votre mot de passe est incorrect"));
          return false;
      }
        if(Util.isEmpty(newpassword)){
          addMsgSession(new JcmsMessage(JcmsMessage.Level.WARN, "Le champ 'Nouveau mot de passe' est obligatoire"));
          return false;
      }
        if(Util.isEmpty(confirmnewpassword)){
          addMsgSession(new JcmsMessage(JcmsMessage.Level.WARN, "Le champ 'Confirmation nouveau mot de passe' est obligatoire"));
          return false;
      }
        if(!newpassword.equals(confirmnewpassword)){
          addMsgSession(new JcmsMessage(JcmsMessage.Level.WARN, "Le mot de passe de confirmation est incorrect"));
          return false;
      }
  
        return true;
    }

    public boolean performRequestChange() throws IOException {
    	
     return changeAndPerformPassword(loggedMember, newpassword);
      
    }
    
    
    
    private boolean changeAndPerformPassword(Member member, String password){
		Member memberClone = (Member) member.getUpdateInstance();
		memberClone.setPassword(channel.crypt(password));
		
		ControllerStatus status = memberClone.checkUpdate(channel.getDefaultAdmin()); 
		if(status.isOK()){
			memberClone.performUpdate(channel.getDefaultAdmin());
// Envoi du mail d'activation
   String subject = "Changement de mot de passe de votre compte sur le site assmat.loire-atlantique.fr";
   //String from = "assmat@loire-atlantique.fr";
   String from = AssmatUtil.getDefaultEmail(); 
   // On met a jour les membres liés au contenu

   
  

   StringBuilder stbd = new StringBuilder();
   stbd.append("Bonjour" + "\n");
   stbd.append("Votre mot de passe a bien été modifié." + "\n");


   AssmatUtil.sendMail(memberClone.getEmail(), subject, stbd.toString(), from);
			
			
			return true;
		} else {
			logger.error("Impossible de mettre à jour le membre <"+memberClone.getFullName()+"> <"+member.getId()+"> / " + status.getMessage(userLang) );
		}
				
		return false;
    }


    public boolean isOpReset() {
      return opReset;
    }

    public void setOpReset(boolean opReset) {
      this.opReset = opReset;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getNewpassword() {
      return newpassword;
    }

    public void setNewpassword(String newpassword) {
      this.newpassword = newpassword;
    }

    public String getConfirmnewpassword() {
      return confirmnewpassword;
    }

    public void setConfirmnewpassword(String confirmnewpassword) {
      this.confirmnewpassword = confirmnewpassword;
    }


    
    
}
