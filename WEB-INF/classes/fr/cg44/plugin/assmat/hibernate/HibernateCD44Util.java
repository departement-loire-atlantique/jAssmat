package fr.cg44.plugin.assmat.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.jalios.jcms.db.HibernateUtil;

public class HibernateCD44Util {
	
	private static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();
	private static final Logger logger = Logger.getLogger(HibernateCD44Util.class);
	
	public static SessionFactory getSessionFactory() {
		return AssmatHibernateManager.getInstance().getSessionFactory();
	}

	public static Session getSession() {
		Session session = (Session) threadSession.get();
		try {
			if (session == null) {
				SessionFactory factory = getSessionFactory();
				if (factory == null) {
					return null;
				}
				session = factory.openSession();
				session.setFlushMode(FlushMode.COMMIT);
				threadSession.set(session);
			}
		} catch (Exception exception) {
			HibernateUtil.handleTimeoutException(exception);
			logger.warn("An exception occured while opening the session:", exception);
		}
		return session;
	}

	/**
	 * Permet de libérer la connexion à la base de données Cette méthode doit
	 * être appelée après que toutes les interrogations ont été effectuées
	 */
	public static void close() {
		if (getSession() != null) {
			getSession().close();
		}
		if (getSessionFactory() != null) {
			getSessionFactory().close();
		}
	}
	
	public static void closeSession() {
	    try
	    {
	      Session session = (Session)threadSession.get();
	      threadSession.remove();

	      if ((session != null) && (session.isOpen())) {
	        Transaction transaction = session.getTransaction();
	        if (transaction.isActive()) {
	          transaction.commit();
	        }
	        session.close();
	      }
	    } catch (Exception exception) {
	      HibernateUtil.handleTimeoutException(exception);
	      logger.warn("Impossible de fermer la session CD44 (hibernate)", exception);
	    }
	}

	public static void beginTransaction() {
	 if(threadSession.get() == null) {	
	   Session session = getSession();
	   session.beginTransaction();
	 }
	}

	public static void commitTransaction() {
		Transaction localTransaction = getSession().beginTransaction();
		localTransaction.commit();
	}

	public static void create(Object obj) {
		try {
			HibernateCD44Util.getSession().beginTransaction();
			HibernateCD44Util.getSession().save(obj);
		} catch (org.hibernate.JDBCException e) {
			HibernateCD44Util.getSession().getTransaction().rollback();
			logger.error(e.getSQLException().getNextException().getMessage(),e);
		} finally {
			HibernateCD44Util.getSession().getTransaction().commit();
			HibernateCD44Util.closeSession();
		}
	}

	public static void update(Object obj) {
		try {
			HibernateCD44Util.getSession().beginTransaction();
			HibernateCD44Util.getSession().update(obj);
		} catch (org.hibernate.JDBCException e) {
			HibernateCD44Util.getSession().getTransaction().rollback();
			logger.error(e.getSQLException().getNextException().getMessage(),e);
		} finally {
			HibernateCD44Util.getSession().getTransaction().commit();
			HibernateCD44Util.closeSession();
		}
	}

	public static void add(Object obj) {
		try {
			HibernateCD44Util.getSession().beginTransaction();
			HibernateCD44Util.getSession().saveOrUpdate(obj);
		} catch (org.hibernate.JDBCException e) {
			HibernateCD44Util.getSession().getTransaction().rollback();
			logger.error(e.getSQLException().getNextException().getMessage(),e);
		} finally {
			HibernateCD44Util.getSession().getTransaction().commit();
			HibernateCD44Util.closeSession();
		}
	}

	public static void delete(Object obj) {
		try {
			HibernateCD44Util.getSession().beginTransaction();
			HibernateCD44Util.getSession().delete(obj);
		} catch (org.hibernate.JDBCException e) {
			HibernateCD44Util.getSession().getTransaction().rollback();
			logger.error(e.getSQLException().getNextException().getMessage(),e);
		} finally {
			HibernateCD44Util.getSession().getTransaction().commit();
			HibernateCD44Util.closeSession();
		}
	}

}
