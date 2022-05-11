package fr.cg44.plugin.assmat.alarm.listener;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.plugin.Plugin;
import com.jalios.jcms.plugin.PluginComponent;
import com.jalios.jdring.AlarmEntry;
import com.jalios.jdring.AlarmListener;

import fr.cg44.plugin.assmat.alarm.CompteManager;

public class AvailabilityAlarmListener implements AlarmListener, PluginComponent {

	private static final Logger logger = Logger.getLogger(AvailabilityAlarmListener.class);

	@Override
	public void handleAlarm(AlarmEntry alarm) {
		Channel channel = Channel.getChannel();

		boolean execute = true;
		if(channel.isJSyncEnabled() && !channel.isMainLeader())
			execute = false;

		if(execute){
			logger.info("Traitement - prévention envois de mail 1 mois avant futur disponibilité");
			CompteManager.getInstance().runAvailable();
			logger.info("Fin de traitement - prévention envois de mail 1 mois avant futur disponibilité");
		}
	}

	@Override
	public boolean init(Plugin plugin) {
		return true;
	}
}
