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
import com.jalios.jcms.handler.EditMemberHandler;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.jcms.i18n.I18nUtil;
import com.jalios.jcms.mail.MailMessage;
import com.jalios.util.DateUtil;
import com.jalios.util.ServletUtil;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.managers.SmsDAO;

public class ResetPasswordAssmatHandler extends JcmsFormHandler {
    private static final int MAX_CACHED_EMAIL_REQUEST_NBR = 1000;
    private static final Map<String, Date> EMAIL_TO_LAST_REQUEST_DATE_MAP = Collections.synchronizedMap(new HashMap<String,Date>(MAX_CACHED_EMAIL_REQUEST_NBR));
    private static final String DISPLAY_REQUEST_FORM_SESSION_ATTRIBUTE = "reset-password.isResetRequestFormDisplayed";
    public static final String EXPIRATION_DURATION_PROP = "channel.reset-password-link.duration";
    private static final long EXPIRATION_DURATION_DEFAULT = 86400000;
    private boolean isResetRequestFormDisplayed = true;
    private boolean isResetFormDisplayed = false;
    boolean opRequestReset = false;
    String email;
    private boolean opReset = false;
    private Member mbr;
    private String password1;
    private String password2;
    private String passwordResetToken;
    
    // TRSB
    String telephone;
    private static final Logger logger = Logger.getLogger(ResetPasswordAssmatHandler.class);
    
    public boolean processAction() throws IOException {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            isResetRequestFormDisplayed = Util.toBoolean(httpSession.getAttribute(DISPLAY_REQUEST_FORM_SESSION_ATTRIBUTE), isResetRequestFormDisplayed);
            httpSession.removeAttribute(DISPLAY_REQUEST_FORM_SESSION_ATTRIBUTE);
        }
        if (validateRequestReset()) {
            return performRequestReset();
        }
        if (validateReset()) {
            return performReset();
        }
        return super.processAction();
    }

    public boolean validateRequestReset() {
        if (!opRequestReset) {
            return false;
        }
        
        if(Util.isEmpty(email) && Util.isEmpty(telephone)){
            addMsgSession(new JcmsMessage("ui.fo.resetpass.request.form.missing-email-telephone", JcmsMessage.Level.WARN, glp("ui.fo.resetpass.request.form.missing-email-telephone", new Object[0])));
            return false;
        }
        
        if (Util.notEmpty(email) && !AssmatUtil.checkEmail(email)) {
            addMsgSession(new JcmsMessage("ui.fo.resetpass.request.form.invalid-email", JcmsMessage.Level.WARN, glp("ui.fo.resetpass.request.form.invalid-email", new Object[0])));
            return false;
        }
        
        if (Util.notEmpty(telephone) && !AssmatUtil.checkPhone(telephone)) {
            addMsgSession(new JcmsMessage("ui.fo.resetpass.request.form.invalid-telephone", JcmsMessage.Level.WARN, glp("ui.fo.resetpass.request.form.invalid-telephone", new Object[0])));
            return false;
        }
        
        Date dateMail = EMAIL_TO_LAST_REQUEST_DATE_MAP.get(email);
        long dureeMail = ResetPasswordAssmatHandler.getExpirationDuration();
        if (dateMail != null && System.currentTimeMillis() - dateMail.getTime() < dureeMail) {
            addMsgSession(new JcmsMessage("ui.fo.resetpass.request.form.request-already-done", JcmsMessage.Level.INFO, glp("ui.fo.resetpass.request.form.request-already-done", new Object[]{DateUtil.formatDuration(dureeMail, getUserLocale())})));
            return false;
        }
        
        Date datePhone = EMAIL_TO_LAST_REQUEST_DATE_MAP.get(telephone);
        long dureePhone = ResetPasswordAssmatHandler.getExpirationDuration();
        if (datePhone != null && System.currentTimeMillis() - datePhone.getTime() < dureePhone) {
            addMsgSession(new JcmsMessage("ui.fo.resetpass.request.form.request-already-done-phone", JcmsMessage.Level.INFO, glp("ui.fo.resetpass.request.form.request-already-done-phone", new Object[]{DateUtil.formatDuration(dureePhone, getUserLocale())})));
            return false;
        }        
        return true;
    }

    public boolean performRequestReset() throws IOException {
    	
    	if(Util.notEmpty(email)){
	        MailMessage mailMessage;
	        Member member = channel.getMemberFromEmail(email);
	        Locale locale = member != null ? member.getLocale() : userLocale;
	        String string = I18nUtil.getLanguageKey(locale);
	        DateFormat dateFormat = DateFormat.getDateTimeInstance(3, 3, locale);
	        Object[] tabParameters = new Object[4];
	        tabParameters[0] = member != null ? member.getFriendlyName() + "," : "";
	        tabParameters[1] = getEmail();
	        tabParameters[2] = channel.getName();
	        tabParameters[3] = channel.getUrl();
	        String string2 = JcmsUtil.glp(string, "ui.fo.resetpass.email.common.content-text-header", tabParameters);
	        String string3 = JcmsUtil.glp(string, "ui.fo.resetpass.email.common.content-text-footer", new Object[]{request.getRemoteAddr(), dateFormat.format(new Date())});
	        if (member == null) {
	            mailMessage = new MailMessage("reset-password.no-matching-member");
	            mailMessage.setTo(getEmail());
	            mailMessage.setSubject(JcmsUtil.glp(string, "ui.fo.resetpass.email.no-matching-member.subject", new Object[]{channel.getName()}));
	            mailMessage.setContentText(JcmsUtil.glp(string, "ui.fo.resetpass.email.no-matching-member.content-text", new Object[]{string2, string3}));
	        } else if (member.isDisabled()) {
	            mailMessage = new MailMessage("reset-password.disabled");
	            mailMessage.setTo(member);
	            mailMessage.setSubject(JcmsUtil.glp(string, "ui.fo.resetpass.email.disabled.subject", new Object[]{channel.getName()}));
	            mailMessage.setContentText(JcmsUtil.glp(string, "ui.fo.resetpass.email.disabled.content-text", new Object[]{string2, string3}));
	        } else if (member.isContact()) {
	            mailMessage = new MailMessage("reset-password.contact");
	            mailMessage.setTo(member);
	            mailMessage.setSubject(JcmsUtil.glp(string, "ui.fo.resetpass.email.contact.subject", new Object[]{channel.getName()}));
	            mailMessage.setContentText(JcmsUtil.glp(string, "ui.fo.resetpass.email.contact.content-text", new Object[]{string2, string3}));
	        } else if (member.isLdapAccount()) {
	            mailMessage = new MailMessage("reset-password.ldap-account");
	            mailMessage.setTo(member);
	            mailMessage.setSubject(JcmsUtil.glp(string, "ui.fo.resetpass.email.ldap-account.subject", new Object[]{channel.getName()}));
	            mailMessage.setContentText(JcmsUtil.glp(string, "ui.fo.resetpass.email.ldap-account.content-text", new Object[]{string2, string3}));
	        } else {
	            long l = ResetPasswordAssmatHandler.getExpirationDuration();
	            String urlReset = ResetPasswordAssmatHandler.getPasswordResetUrl(request, member, l);
	            
	            
	      
	            String subject = AssmatUtil.getMessage("RESET-PASSWORD-VALIDATION-SUBJECT");
             String from = AssmatUtil.getMessage("RESET-PASSWORD-MAIL-VALIDATION-FROM");

	            String[] parameters = new String[] {};

	            Date dateDemande = new Date();
	            DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	            
	            String ipAddress = request.getHeader("X-FORWARDED-FOR");
	            if (ipAddress == null) {
	                ipAddress = request.getRemoteAddr();
	            }
	            
	            parameters = (String[]) Util.insertArray(parameters, 0, member.getEmail());
	            parameters = (String[]) Util.insertArray(parameters, 1, urlReset);
	            parameters = (String[]) Util.insertArray(parameters, 2, shortDateFormat.format(dateDemande));
	            parameters = (String[]) Util.insertArray(parameters, 3, ipAddress);
	            
	        
	            
	            String htmlcontent = AssmatUtil.getMessagePropertiesParametersValues("RESET-PASSWORD-EMAIL-CONTENT", parameters);

	            mailMessage = new MailMessage("reset-password.account");
	            mailMessage.setTo(member);
	            mailMessage.setFrom(from);
	            mailMessage.setSubject(subject);
	            mailMessage.setContentText(htmlcontent);
	        }
	        try {
	            mailMessage.send();
	        }
	        catch (MessagingException e) {
	            addMsgSession(new JcmsMessage("ui.fo.resetpass.request.form.validation-email-notsent", JcmsMessage.Level.WARN, glp("ui.fo.resetpass.request.form.validation-email-notsent", new Object[]{HttpUtil.encodeForHTML(e.getMessage())})));
	            logger.error(e.getMessage(),e);
	            return sendRedirect();
	        }
	        addMsgSession(new JcmsMessage("ui.fo.resetpass.request.form.validation-email-sent", JcmsMessage.Level.INFO, glp("ui.fo.resetpass.request.form.validation-email-sent", new Object[0])));
	        EMAIL_TO_LAST_REQUEST_DATE_MAP.put(getEmail(), new Date());
	        
	        isResetRequestFormDisplayed = true;
	        getSession().setAttribute(DISPLAY_REQUEST_FORM_SESSION_ATTRIBUTE, isResetRequestFormDisplayed);
    	}
    	
    	if(Util.notEmpty(telephone)){
    		Member member = AssmatUtil.getMemberFromMobile(telephone);
	        if (member == null) {
	        	addMsgSession(new JcmsMessage("ui.fo.resetpass.phone.no-matching-member.content-text", JcmsMessage.Level.WARN, glp("ui.fo.resetpass.phone.no-matching-member.content-text", new Object[0])));
	        } else {
	        	String password = AssmatUtil.generateAssmatPassword();
    			boolean change = changeAndPerformPassword(member,password);
    			if(change){
		    		SmsDAO sms = new SmsDAO();
		    		
					String[] parameters = new String[] {};
					parameters = (String[]) Util.insertArray(parameters, 0, password);
							
		    		String subject = "TEXT-RESET-PASSWORD-MOBILE";
					String content = AssmatUtil.getMessage(AssmatUtil.getMessagePropertiesParametersValues(subject,parameters));
					String resumeContent = AssmatUtil.getMessage(AssmatUtil.getMessagePropertiesParametersValues(subject,parameters));
	
		    		
		    		sms.sendSMS(member.getName(), member.getFirstName(), member.getMobile(), subject, content, resumeContent, member.getLogin(), null);
			        addMsgSession(new JcmsMessage("ui.fo.resetpass.request.form.validation-sms-sent", JcmsMessage.Level.INFO, glp("ui.fo.resetpass.request.form.validation-sms-sent", new Object[0])));
			        EMAIL_TO_LAST_REQUEST_DATE_MAP.put(getTelephone(), new Date());												
			        isResetRequestFormDisplayed = false;
			        getSession().setAttribute(DISPLAY_REQUEST_FORM_SESSION_ATTRIBUTE, Boolean.FALSE);
    			}
    		}
    	}
        return sendRedirect();
    }
    
    
    
    private boolean changeAndPerformPassword(Member member, String password){
		Member memberClone = (Member) member.getUpdateInstance();
		memberClone.setPassword(channel.crypt(password));
		
		ControllerStatus status = memberClone.checkUpdate(channel.getDefaultAdmin()); 
		if(status.isOK()){
			memberClone.performUpdate(channel.getDefaultAdmin());
			return true;
		} else {
			logger.error("Impossible de mettre à jour le membre <"+memberClone.getFullName()+"> <"+member.getId()+"> / " + status.getMessage(userLang) );
		}
				
		return false;
    }

    public static String getPasswordResetUrl(HttpServletRequest httpServletRequest, Member member) {
        return ResetPasswordAssmatHandler.getPasswordResetUrl(httpServletRequest, member, -1);
    }

    public static String getPasswordResetUrl(HttpServletRequest httpServletRequest, Member member, long l) {
        StringBuilder stringBuilder = new StringBuilder();
        if (httpServletRequest != null) {
            stringBuilder.append(ServletUtil.getBaseUrl(httpServletRequest));
        } else {
            stringBuilder.append(channel.getUrl());
        }
        if (channel.isAuthenticationRequired()) {
            stringBuilder.append(ResourceHelper.getPrivateMailPassword());
        } else {
            stringBuilder.append(ResourceHelper.getMailPassword());
        }
        stringBuilder.append("?passwordResetToken=");
        stringBuilder.append(HttpUtil.encodeForURL(ResetPasswordAssmatHandler.generatePasswordResetToken(member, l)));
        String string = stringBuilder.toString();
        return string;
    }

    public boolean validateReset() {
        if (mbr == null && (opReset || Util.notEmpty(getPasswordResetToken()))) {
            addMsgSession(new JcmsMessage("ui.fo.resetpass.reset.form.invalid-token", JcmsMessage.Level.WARN, glp("ui.fo.resetpass.reset.form.invalid-token", new Object[0])));
            isResetFormDisplayed = false;
            isResetRequestFormDisplayed = true;
            return false;
        }
        if (!opReset) {
            return false;
        }
        if (Util.isEmpty(password1) || Util.isEmpty(password2)) {
            addMsgSession(new JcmsMessage("ui.fo.resetpass.reset.form.missing-password", JcmsMessage.Level.WARN, glp("ui.fo.resetpass.reset.form.missing-password", new Object[0])));
            return false;
        }
        if (!password1.equals(password2)) {
            addMsgSession(new JcmsMessage("ui.fo.resetpass.reset.form.invalid-password", JcmsMessage.Level.WARN, glp("ui.fo.resetpass.reset.form.invalid-password", new Object[0])));
            return false;
        }
        if (!AssmatUtil.checkPassword(password1)) {
            addMsg(new JcmsMessage(JcmsMessage.Level.WARN, glp("plugin.assmatplugin.pwd.regex.label")));
            return false;
        }
        
        return true;
    }

    public boolean performReset() throws IOException {
        HashMap hashMap = Util.getHashMap("password-reset", Boolean.TRUE);
        Member member = (Member)mbr.getUpdateInstance();
        member.setPassword(channel.crypt(password1));
        ControllerStatus controllerStatus = member.checkUpdate(channel.getDefaultAdmin(), hashMap);
        if (!controllerStatus.isOK()) {
            if (member instanceof DBMember) {
                HibernateUtil.evict(member);
            }
            JcmsMessage jcmsMessage = new JcmsMessage(JcmsMessage.Level.WARN, controllerStatus.getMessage(getUserLang()));
            addMsgSession(jcmsMessage);
            return false;
        }
        member.performUpdate(channel.getDefaultAdmin(), hashMap);
        ResetPasswordAssmatHandler.addMsgSession(request, (JcmsMessage)new JcmsMessage("ui.fo.resetpass.reset.form.password-updated", JcmsMessage.Level.INFO, glp("ui.fo.resetpass.reset.form.password-updated", new Object[0])));
        EMAIL_TO_LAST_REQUEST_DATE_MAP.remove(mbr.getEmail());
        isResetRequestFormDisplayed = false;
        isResetFormDisplayed = false;
        SessionAuthenticationHandler.storeSessionAuthentication(request, (Member)mbr);
        mbr.setLastLoginDate(null);
        sendRedirect("front/displayMessageBox.jsp");
        return true;
    }

    private boolean sendRedirect() throws IOException {
        if (channel.isAuthenticationRequired()) {
            sendRedirect(ResourceHelper.getPrivateMailPassword());
        } else {
            sendRedirect(ResourceHelper.getMailPassword());
        }
        return true;
    }

    public static String generatePasswordResetToken(Member member, long l) {
        if (Util.isEmpty(member.getPassword())) {
            throw new IllegalArgumentException("Member must not have an empty password");
        }
        if (member.isDisabled()) {
            throw new IllegalArgumentException("Member must be enabled (have existing valid password) : cannot generated password reset token for disabled member.");
        }
        if (!member.isPersisted()) {
            throw new IllegalArgumentException("Member must be persisted (saved in store or database) : cannot generated password reset token for unpersisted member.");
        }
        long l2 = System.currentTimeMillis() + (l <= 0 ? ResetPasswordAssmatHandler.getExpirationDuration() : l);
        String string = channel.crypt(String.valueOf(l2) + member.getPassword());
        String string2 = member.getId() + ":" + l2 + ":" + string;
        String string3 = Util.encodeBASE64(string2);
        return string3;
    }

    public static Member getMemberFromPasswordResetToken(String string) {
        String string2 = Util.decodeBASE64(string);
        if (string2 == null) {
            return null;
        }
        int n = string2.indexOf(58);
        if (n < 0) {
            return null;
        }
        int n2 = string2.indexOf(58, n + 1);
        if (n2 <= n) {
            return null;
        }
        String string3 = string2.substring(0, n);
        Member member = channel.getMember(string3);
        if (member == null) {
            return null;
        }
        String string4 = string2.substring(n + 1, n2);
        String string5 = string2.substring(n2 + 1);
        boolean bl = channel.checkCrypt(string4 + member.getPassword(), string5);
        if (!bl) {
            return null;
        }
        long l = Util.toLong(string4, 0);
        if (l < System.currentTimeMillis()) {
            return null;
        }
        return member;
    }

    public void setOpRequestReset(String string) {
        this.opRequestReset = true;
    }

    public void setOpReset(String string) {
        this.opReset = true;
    }

    public boolean isResetRequestFormDisplayed() {
        return this.isResetRequestFormDisplayed;
    }

    public boolean isResetFormDisplayed() {
        return this.isResetFormDisplayed;
    }

    public void setEmail(String string) {
        this.email = string != null ? string.trim() : string;
    }

    public String getEmail() {
        return this.email;
    }

    public Member getMember() {
        return this.mbr;
    }

    public void setPassword1(String string) {
        this.password1 = string;
    }

    public void setPassword2(String string) {
        this.password2 = string;
    }

    public void setPasswordResetToken(String string) {
        this.passwordResetToken = string;
        this.mbr = ResetPasswordAssmatHandler.getMemberFromPasswordResetToken(this.passwordResetToken);
        this.isResetRequestFormDisplayed = false;
        this.isResetFormDisplayed = this.mbr != null;
    }

    public String getPasswordResetToken() {
        return this.passwordResetToken;
    }

    public static long getExpirationDuration() {
        return channel.getLongProperty("channel.reset-password-link.duration", EXPIRATION_DURATION_DEFAULT);
    }

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
