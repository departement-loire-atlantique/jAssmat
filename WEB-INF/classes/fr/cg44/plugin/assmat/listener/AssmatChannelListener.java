package fr.cg44.plugin.assmat.listener;

import org.apache.log4j.Logger;

import com.jalios.jcms.ChannelListener;
import com.jalios.jcms.plugin.Plugin;

import fr.cg44.plugin.assmat.hibernate.AssmatHibernateManager;
import fr.trsb.cd44.solis.manager.SolisManager;



public class AssmatChannelListener extends ChannelListener {

	private static final Logger logger = Logger.getLogger(AssmatChannelListener.class);

	@Override
	public boolean init(Plugin arg0) {
		return true;
	}

	@Override
	public void handleFinalize() {

	}

	@Override
	public void initAfterStoreLoad() throws Exception {
		logger.info("initAfterStoreLoad()");
		initHibernate();
	}

	@Override
	public void initBeforeStoreLoad() throws Exception {
	}

	private void initHibernate() {
		logger.info("initHibernate()");

		logger.info("initHibernateAssmat");
		AssmatHibernateManager.getInstance();	

		logger.info("initHibernateSolis");
		initHibernateSolis();

	}
	
	private void initHibernateSolis(){
		 // Récupere la factory de assmat.cfg.xml
	  SolisManager.init();
	}
}
