package fr.cg44.plugin.assmat.managers;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import fr.cg44.plugin.assmat.beans.TrsbActivation;
import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;

public class ActivationDAO {

	public List<TrsbActivation> getAllActivation() {
		HibernateCD44Util.beginTransaction();
		List<TrsbActivation> list = HibernateCD44Util.getSession().createCriteria(TrsbActivation.class).list();
		HibernateCD44Util.commitTransaction();
		return list;
	}


	public List<TrsbActivation> getActivationById(long id) {
		HibernateCD44Util.getSession().beginTransaction();
		List<TrsbActivation> list = HibernateCD44Util.getSession().createCriteria(TrsbActivation.class).add(Restrictions.eq("JRowId", id)).list();
		HibernateCD44Util.getSession().getTransaction().commit();
		return list;
	}

	public List<TrsbActivation> getActivationByIdForm(String id) {
		HibernateCD44Util.getSession().beginTransaction();
		List<TrsbActivation> list = HibernateCD44Util.getSession().createCriteria(TrsbActivation.class).add(Restrictions.eq("inscriptionId", id)).list();
		HibernateCD44Util.getSession().getTransaction().commit();
		return list;
	}
}
