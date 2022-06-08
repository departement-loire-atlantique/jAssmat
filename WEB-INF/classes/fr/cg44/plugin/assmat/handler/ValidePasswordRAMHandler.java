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

public class ValidePasswordRAMHandler extends JcmsFormHandler {
   
    private boolean opReset = false;
    

    private String password;
    private String confirmpassword;
    private String idMember;
    
    private Member member;

    private static final Logger logger = Logger.getLogger(ValidePasswordRAMHandler.class);
    
    public boolean processAction() throws IOException {
     
        
        if (validateRequestActivePass()) {
            return performRequestActive(password);
        }
       
        return false;
    }

    public boolean validateRequestActivePass() {
        if (!opReset) {
            return false;
        }
     
        if(Util.isEmpty(password)){
            addMsgSession(new JcmsMessage(JcmsMessage.Level.WARN,  AssmatUtil.getMessage("VALIDATION-PASSWORD-ERROR-NULL-MDP")));
            return false;
        }
       
        if(Util.isEmpty(confirmpassword)){
          addMsgSession(new JcmsMessage(JcmsMessage.Level.WARN,  AssmatUtil.getMessage("VALIDATION-PASSWORD-ERROR-NULL-MDPCONFIRM")));
          return false;
      }
        if(!password.equals(confirmpassword)){
          addMsgSession(new JcmsMessage(JcmsMessage.Level.WARN,  AssmatUtil.getMessage("VALIDATION-PASSWORD-ERROR-MPD-NOTEQUAL")));
          return false;
      }
  
        return true;
    }

    public boolean performRequestActive(String pass) throws IOException {
    	
     member = (Member)channel.getData(idMember);
     return activePerformPassword(member, pass);
      
    }
    
    
    
    private boolean activePerformPassword(Member member, String password){
      
      if(Util.isEmpty(member)){
        return false;
      }
      
 		Member memberClone = (Member) member.getUpdateInstance();
 		memberClone.setPassword(channel.crypt(password));
		
 		ControllerStatus status = memberClone.checkUpdate(channel.getDefaultAdmin()); 
 		if(status.isOK()){
		  
		  
		 //Réactive le compte
			memberClone.performUpdate(channel.getDefaultAdmin());
// Envoi du mail d'activation
			AssmatUtil.getMessage("VALIDATION-PASSWORD-MEMBER-RAM-MAIL-SUBJET");
			
   String subject =  AssmatUtil.getMessage("VALIDATION-PASSWORD-MEMBER-RAM-MAIL-SUBJET");
   String from = AssmatUtil.getDefaultEmail();
   // On met a jour les membres liés au contenu

   StringBuilder stbd = new StringBuilder();
   stbd.append( AssmatUtil.getMessage("VALIDATION-PASSWORD-MEMBER-RAM-MAIL-CONTENT"));
   
   AssmatUtil.sendMail(memberClone.getEmail(), subject, stbd.toString(), from);
			
   //autolog
   if(Util.isEmpty(channel.getCurrentLoggedMember())){
     channel.getCurrentServletRequest().getSession().setAttribute("loggedMemberId", memberClone.getId());
    }
			
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

    public String getConfirmpassword() {
      return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
      this.confirmpassword = confirmpassword;
    }

    public static Logger getLogger() {
      return logger;
    }

    public String getIdMember() {
      return idMember;
    }

    public void setIdMember(String idMember) {
      this.idMember = idMember;
    }

    public Member getMember(){
    	return member;
    }


   

    
    
}
