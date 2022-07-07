package fr.cg44.plugin.assmat.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.jalios.jcms.Channel;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;

public class AssmatHibernateManager {
	private SessionFactory sessionFactory;
	private static AssmatHibernateManager instance;
	private Session session;
	private Configuration cfg;

	private static final Logger logger = Logger.getLogger(AssmatHibernateManager.class);

	/**
	 * Singeleton pour RepriseManager
	 *
	 * @return RepriseManager
	 */
	public static AssmatHibernateManager getInstance() {
		if (instance == null) {
			instance = new AssmatHibernateManager();
			instance.init();
		}
		return instance;
	}

	/**
	 * Permet d'initialiser la connexion à la base de données Cette méthode doit
	 * être appelée avant toute interrogation à la base
	 *
	 * @param nomFichier
	 *            Fichier de configuration de hibernate
	 */
	public void init() {
		cfg = new Configuration();
		buildSessionFactory();
	}

	public Configuration getConfiguration() {
		return this.cfg;
	}

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	public SessionFactory buildSessionFactory() {
		logger.info("buildSessionFactory()");
		cfg.configure("assmat.cfg.xml");
		// Si une data source pour la bdd de renseignée alors initialise les dev spécifique avec la data source
		// Sinon les proprité JCMS de la bdd en BO
		if(Util.notEmpty(Channel.getChannel().getProperty("hibernate.cfg.postgresql.prop.hibernate.connection.datasource"))) {
			AssmatUtil.initPropertiesDataSource(cfg);	
		}else {
			AssmatUtil.initProperties(cfg);	
		}
		sessionFactory = cfg.buildSessionFactory();
		return sessionFactory;
	}

	/**
	 * Permet de libérer la connexion à la base de données Cette méthode doit
	 * être appelée après que toutes les interrogations ont été effectuées
	 */
	public void close() {
		if (session != null) {
			session.close();
		}
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	public Session getSession() {
		return session;
	}
}
