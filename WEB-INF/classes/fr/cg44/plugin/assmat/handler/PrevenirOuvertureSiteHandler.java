package fr.cg44.plugin.assmat.handler;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.managers.AssmatSearchDAO;
import generated.OuvertureSite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.JcmsConstants;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.context.JcmsContext;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.db.HibernateUtil;
import com.jalios.jcms.handler.EditDataHandler;
import com.jalios.util.Util;

/**
 * FormHandler permettant de s'inscrire en indiquant son mail ou son SMS pour
 * Ãªtre prÃ©venu de l'ouverture du nouveau site
 * 
 * 
 */
public class PrevenirOuvertureSiteHandler extends EditDataHandler {

  private static final Logger logger = Logger.getLogger(PrevenirOuvertureSiteHandler.class);

  private String prevMail = "";
  private String tel = null;
  private String prevTel = "";

  @Override
  public boolean processAction() throws IOException {
    if (validateOpCreate()) {
      return performCreate();
    }
    return false;
  }

  /**
   * MÃ©thode permettant de crÃ©er l'inscription
   * 
   * @return un boolean
   */
  private boolean performCreate() {

    // Si le mail est a true
    if (Util.notEmpty(prevMail)) {
      if (AssmatUtil.getBooleanFromString(prevMail) && Util.notEmpty(loggedMember) && Util.notEmpty(loggedMember.getEmail())) {
        AssmatUtil.ajoutMailJet(loggedMember.getEmail(), channel.getProperty("jcmsplugin.assmatplugin.mailjet.inscription.ouverture.site.id"));
      } else if(!AssmatUtil.getBooleanFromString(prevMail) && Util.notEmpty(loggedMember) && Util.notEmpty(loggedMember.getEmail())){
        AssmatUtil.removeMailJet(loggedMember.getEmail(), channel.getProperty("jcmsplugin.assmatplugin.mailjet.inscription.ouverture.site.id"));
      }
    }
    // Si le tel est a true
    if (Util.notEmpty(prevTel)) {
      if (AssmatUtil.getBooleanFromString(prevTel) && Util.notEmpty(tel)) {
        if(Util.isEmpty(AssmatSearchDAO.getOuvertureSiteByTel(tel))){
        OuvertureSite content = new OuvertureSite();
        content.setTitle(tel);
        content.setAuthor(channel.getDefaultAdmin());
        
        ControllerStatus status = content.checkCreate(channel.getDefaultAdmin());
        if(status.isOK()){
         content.performCreate(channel.getDefaultAdmin());
         HibernateUtil.commitTransaction();
         HibernateUtil.beginTransaction();
        } else {
         JcmsContext.setWarningMsg(JcmsUtil.glp(userLang, "plugin.assmatplugin.error.globale", new Object[0]), request);
         logger.warn(status.getMessage(userLang));
         return false;
        }
        }
      }else{
       List<OuvertureSite> list =  AssmatSearchDAO.getOuvertureSiteByTel(tel);

        if(Util.notEmpty(list)){
          for(OuvertureSite itOuv : list){
            if(Util.notEmpty(itOuv)) {           
              itOuv.performDelete(channel.getDefaultAdmin());         
            }
          }
        }
       
      }
    }
    return true;
  }

  /**
   * MÃ©thode de validation du formulaire soumis
   * 
   * @return un boolean
   */
  private boolean validateOpCreate() {
    List<JcmsMessage> listError = new ArrayList<JcmsMessage>();

    if (!opCreate)
      return false;

    if (Util.isEmpty(prevMail) && Util.isEmpty(prevTel)) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParametersValues("NOT-EMPTY-DOUBLE", new String[] { "par e-mail",
          "par SMS" })));
    }

    return listError.isEmpty();
  }

  @Override
  public Class<? extends Data> getDataClass() {
    return null;
  }

  public String isPrevTel() {
    return prevTel;
  }

  public void setPrevTel(String prevTel) {
    this.prevTel = prevTel;
  }

  public String isPrevMail() {
    return prevMail;
  }

  public void setPrevMail(String prevMail) {
    this.prevMail = prevMail;
  }

	public String getTel() {
		return tel;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
}
