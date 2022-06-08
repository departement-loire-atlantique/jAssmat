package fr.cg44.plugin.assmat.listener;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.ChannelListener;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jdring.AlarmEntry;
import com.jalios.jdring.AlarmManager;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.listener.SMSAlarmListener;


public class SMSChannelListener extends ChannelListener {

	private static final Logger logger = Logger.getLogger(SMSChannelListener.class);
	private static final Channel channel = Channel.getChannel();

	@Override
	public void initAfterStoreLoad() throws Exception {
		// Pattern de l'alarme attendue 
		String regex = "^[\\* 0-9 ]*$";
		//Récupération des proprietés
		String smsAlarm = channel.getProperty("jcmsplugin.assmatplugin.sms.scheduler.schedule");
		if(Util.notEmpty(smsAlarm)){

			// Respet du format d'alarme (CRON)
			if(!smsAlarm.matches(regex)){
				logger.error("La mise à jour des statuts des sms en BDD est annulé, le format d'alarm n'est pas un format valide : " + smsAlarm);

				// Planification par défaut
				smsAlarm = "* * * * * *";
				logger.warn("Utilisation de la planification par défaut du batch de mise à jour : " + smsAlarm);
			}

			
				AlarmManager alarmMgr = channel.getCommonAlarmManager();
				AlarmEntry alarmEntry;

				alarmEntry = new AlarmEntry(smsAlarm, new SMSAlarmListener());
				logger.info("Bacth updateSMS scheduled on " + JcmsUtil.formatSchedule("en", smsAlarm));
				alarmMgr.addAlarm(alarmEntry);
			
		}
	}

	@Override
	public void handleFinalize() {	}

	@Override
	public void initBeforeStoreLoad() throws Exception {	}
}
