package fr.cg44.plugin.assmat.datacontroller;

import fr.cg44.plugin.assmat.AssmatUtil;
import generated.City;
import generated.FormContactAssmat;
import generated.Routage;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Category;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.plugin.PluginComponent;
import com.jalios.util.Util;

public class FormContactAssmatDataController extends BasicDataController implements PluginComponent {

	private static final Logger logger = Logger.getLogger(FormContactAssmatDataController.class);

	public ControllerStatus checkIntegrity(Data data) {
		ControllerStatus localControllerStatus = ControllerStatus.OK;
		if(data instanceof FormContactAssmat){
			FormContactAssmat form = (FormContactAssmat) data;
			if(Util.notEmpty(form.getCourriel()) && !AssmatUtil.checkEmail(form.getCourriel())){
				ControllerStatus status = new ControllerStatus(JcmsUtil.glpd("plugin.assmatplugin.contacter.courriel.error"));
				return status;
			}
		}
		return localControllerStatus;
	}

	public void afterWrite(Data data, int op, Member mbr, Map map) {
		// A la création du formulaire envoyer un mail au(x) routage(s) ratache(s)
		if(op  == OP_CREATE){
			FormContactAssmat pub = (FormContactAssmat) data;
			TreeSet<String> to = new TreeSet<String>();

			TreeSet<Routage> allRoutages = channel.getAllPublicationSet(Routage.class, channel.getDefaultAdmin());

			if(Util.notEmpty(allRoutages)){
				String codePostal = pub.getCity();
				TreeSet<Routage> routages = new TreeSet<Routage>();

				// récupération des routages en fonction de la commune
				for(Routage itRoutages: allRoutages){
					City[] communes = itRoutages.getCommunes();
					for(City itCommunes: communes){
						String cityCode = String.valueOf(itCommunes.getCityCode());
						if(Util.notEmpty(cityCode) && cityCode.equals(codePostal)){
							routages.add(itRoutages);
							break;
						}  
					}
				}

				allRoutages.clear();
				allRoutages.addAll(routages);
				routages.clear();

				// récupération des routages par le sujet
				if(Util.notEmpty(allRoutages)){
					TreeSet<Category> sujets = pub.getSubject(channel.getDefaultAdmin());
					for(Routage itRoutages: allRoutages){
						for(Category itSujet: sujets){
							if(itRoutages.getSujets(channel.getDefaultAdmin()).contains(itSujet)){
								routages.add(itRoutages);
								break;
							}
						}
					}

					if(Util.notEmpty(routages)){
						for(Routage itRoutage: routages){
							for(String itEmail: itRoutage.getEmail()){
								to.add(itEmail);
							}
						}
					} else {
						logger.warn("Il n'existe pas de contenus Routage liés au sujet <"+sujets.first()+"> et au code postal <"+codePostal+">");
					}
				}
			}
			envoiMail(pub, to);
		}
	}

	private void envoiMail(FormContactAssmat pub, Set<String> setTo){
		// ajout du contenu du mail
		String[] parameters = new String[]{};

		// On rajoute aussi le mail inscrit sur la catégorie
		TreeSet<Category> sujets = pub.getSubject(channel.getDefaultAdmin());
		String subject = "";
		if(Util.notEmpty(sujets)){
			Category catSubject = ((Category) sujets.first());
			if(Util.notEmpty(catSubject)){
				subject = catSubject.getName();
				if(Util.notEmpty(catSubject)){
					String contactMail = Util.notEmpty(catSubject.getDescription()) ? catSubject.getDescription() : "";
					if(Util.notEmpty(contactMail)){
						setTo.add(contactMail);
					}
				}
			}
		}

		parameters = (String[]) Util.insertArray(parameters, 0, pub.getFirstName());
		parameters = (String[]) Util.insertArray(parameters, 1, pub.getName());
		parameters = (String[]) Util.insertArray(parameters, 2, pub.getCourriel());
		parameters = (String[]) Util.insertArray(parameters, 3, pub.getCity());
		parameters = (String[]) Util.insertArray(parameters, 4, pub.getPhone());
		parameters = (String[]) Util.insertArray(parameters, 5, subject);
		parameters = (String[]) Util.insertArray(parameters, 6, pub.getMessage());
		String content = AssmatUtil.getMessagePropertiesParametersValues("CONTACT-MAIL-CONTENT", parameters);

		String from = AssmatUtil.getDefaultEmail();

		if(Util.notEmpty(setTo)){
			AssmatUtil.sendMail(setTo, subject, content, from);
		}		  
	}
}
