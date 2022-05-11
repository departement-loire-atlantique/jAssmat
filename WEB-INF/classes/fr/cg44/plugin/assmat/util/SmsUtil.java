package fr.cg44.plugin.assmat.util;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.util.Util;

public class SmsUtil {

	public static final String STATUS_COMPLETED = "COMPLETED";

	public static enum ErrorSms {
		ERROR_100("100","Votre applicatif client n’est pas authentifié (100)"),
		ERROR_105("105","Le nom du compte client est manquant (105)."),
		ERROR_106("106","Le nom du compte client n'est pas valide (106)"),
		ERROR_110("110","Votre applicatif client n’est pas authentifié et/ou le nom du compte client n'est pas valide (110)"),
		ERROR_111("111","Diffusion impossible car dépasse votre quota (111) valable uniquement pour un compte de démo"),
		ERROR_200("200","La liste des destinataires est manquante (200)."),
		ERROR_201("201","La liste des destinataires n’est pas valide (201)."),
		ERROR_300("300","La stratégie de diffusion est manquante (300)."),
		ERROR_301("301","La stratégie de diffusion n'est pas valide pour le compte client (301)."),
		ERROR_302("302","Diffusion impossible car elle dépasserait votre seuil bloquant de SMS (302)"),
		ERROR_400("400","Le message à diffuser est manquant (400)"),
		ERROR_401("401","Le contenu à diffuser n'est pas complet (401)."),
		ERROR_413("413","Le sujet du message n'est pas valide (413)."),
		ERROR_414("414","Le contenu du message n'est pas valide (414)."),
		ERROR_415("415","Le résumé du message n'est pas valide (415)."),
		ERROR_INCONNU("NA","Erreur inconnue");

		private String code;
		private String erreur;

		ErrorSms(String code, String erreur) {
			this.code = code;
			this.erreur = erreur;
		}

		public String getCode() {
			return code;
		}

		public String getErreur() {
			return erreur;
		}

		public static ErrorSms getTypeErrorByValue(final String errorValue) {
			for (final ErrorSms error : ErrorSms.values()) {
				if (error.getCode().equalsIgnoreCase(errorValue)) {
					return error;
				}
			}
			return ErrorSms.ERROR_INCONNU;
		}
	}

	private static final Logger logger = Logger.getLogger(SmsUtil.class);

	private static final int LENGTH_SMS = 140;

	public static boolean checkSms(String content){
		if(Util.notEmpty(content) && content.length() <= LENGTH_SMS*3){
			return true;
		}
		return false;
	}

	public static String truncateSms(String content){
		if(Util.notEmpty(content)){
			return Util.truncate(content, LENGTH_SMS*3);
		}
		return null;
	}

	public static String getCustId(){
		return Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.custid");
	}
}