package fr.cg44.plugin.assmat.listener;

import org.apache.log4j.Logger;

import com.jalios.jdring.AlarmEntry;
import com.jalios.jdring.AlarmListener;

import fr.cg44.plugin.assmat.managers.SmsDAO;

public class SMSAlarmListener implements AlarmListener {
	private static final Logger logger = Logger.getLogger(SMSAlarmListener.class);

	/**
	 * Traitement à effectuer à intervalle régulier (Batch de mise à jour des sms)
	 */
	@Override
	public void handleAlarm(AlarmEntry entry) {

		SmsDAO dao = new SmsDAO();

		//Tentative de renvoi des sms non envoyés
		logger.info("Tentative de renvoi des sms non envoyés");
		dao.reSendSms();

		//Lance la mise à jour des Statuts des SMS
		logger.info("Début du batch de mise à jour des statuts des SMS en bdd");
		dao.updateAllSmsStatut();

	}
}
