package fr.cg44.plugin.assmat.handler;

import fr.cg44.plugin.assmat.AssmatUtil;
import generated.OuvertureSite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.JcmsConstants;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.context.JcmsContext;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.handler.EditDataHandler;
import com.jalios.util.Util;

/**
 * FormHandler permettant de s'inscrire en indiquant son mail ou son SMS pour être prévenu de l'ouverture du nouveau site
 * @author tlegat
 *
 */
public class InscriptionOuvertureSiteHandler extends EditDataHandler {

	private static final Logger logger = Logger.getLogger(InscriptionOuvertureSiteHandler.class);

	private String email = "";
	private String tel = "";

	@Override
	public boolean processAction() throws IOException {
		if (validateOpCreate()) {
			return performCreate();
		}
		return false;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	/**
	 * Méthode permettant de créer l'inscription 
	 * @return un boolean
	 */
	private boolean performCreate() {
		if(Util.notEmpty(email)){
			AssmatUtil.ajoutMailJet(email, channel.getProperty("jcmsplugin.assmatplugin.mailjet.inscription.ouverture.site.id"));
		}
		if(Util.notEmpty(tel)){
			OuvertureSite content = new OuvertureSite();
			content.setTitle(tel);
			content.setAuthor(channel.getDefaultAdmin());
			
			ControllerStatus status = content.checkCreate(channel.getDefaultAdmin());
			if(status.isOK()){
				content.performCreate(channel.getDefaultAdmin());
			} else {
				JcmsContext.setWarningMsg(JcmsUtil.glp(userLang, "plugin.assmatplugin.error.globale", new Object[0]), request);
				logger.warn(status.getMessage(userLang));
				return false;
			}
		}
		return true;
	}

	/**
	 * Méthode de validation du formulaire soumis
	 * @return un boolean
	 */
	private boolean validateOpCreate() {
		List<JcmsMessage> listError= new ArrayList<JcmsMessage>();
		
		if(!opCreate)
			return false;
		
		if(Util.isEmpty(email) && Util.isEmpty(tel)){
			listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParametersValues("NOT-EMPTY-DOUBLE",new String[]{"par e-mail","par SMS"})));
		}
		
		if(Util.notEmpty(email)){
			if(!AssmatUtil.checkEmail(email)){
				listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParametersValues("CONTACT-MAIL-VALIDITE",new String[]{"par e-mail"})));
			}
		}
		
		if(Util.notEmpty(tel)){
			if(!AssmatUtil.checkPhone(tel)){
				listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParametersValues("CONTACT-TEL-VALIDITE",new String[]{"par SMS"})));
			}
		}

		if(!listError.isEmpty()){
			request.setAttribute(JcmsConstants.JCMS_MSG_LIST, listError);
		}
	      
		return listError.isEmpty();
	}

	@Override
	public Class<? extends Data> getDataClass() {
		return null;
	}
}
