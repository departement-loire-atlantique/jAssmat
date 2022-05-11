package fr.cg44.plugin.assmat.datacontroller;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.selector.RoutageSelector;
import generated.FormulaireDeContactActualiserCoo;
import generated.FormulaireDeContactRAM;
import generated.Routage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import com.jalios.util.Util;

public class FormActualiserCoordRAMDataController extends BasicDataController {

  
  
  @Override
  public ControllerStatus checkIntegrity(Data arg0) {
    // TODO Auto-generated method stub
    return super.checkIntegrity(arg0);
  }
  
  private static final Logger logger = Logger.getLogger(FormActualiserCoordRAMDataController.class);
  
	  @Override
	  public void afterWrite(Data data, int op, Member mbr, Map map) {
		  // A la création du formulaire pour 
		  if(op  == OP_CREATE){
			 
   Category categActuDispo = channel.getCategory(channel.getProperty("plugin.assmatplugin.categ.actualiser.coord.ram.id"));
	  FormulaireDeContactActualiserCoo pub = (FormulaireDeContactActualiserCoo) data;
			  
	    Set<String> to = new HashSet<String>();

	    Set<Routage> allRoutages = (Set<Routage>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Routage.class), new RoutageSelector(categActuDispo));

	    //On ajoute le mail de l'UA
	    if(Util.notEmpty(pub.getCouriel())){
			    to.add(pub.getCouriel());
	    }
			  
			  if(Util.notEmpty(allRoutages)){
			    
			    for(Routage itRoutage : allRoutages){
			      
			      if(Util.notEmpty(itRoutage.getEmail())){
			        
			        for(String itEmail : itRoutage.getEmail()){
			          to.add(itEmail);
			        }
			      }
			    
			    }
			  }
			  
			  
			  String subject = AssmatUtil.getMessage("CONTACT-MAIL-ACTUALISER-COORD-RAM-SUBJECT");

			  // ajout du contenu du mail
			  String[] parameters = new String[]{};
			  parameters = (String[]) Util.insertArray(parameters, 0, pub.getNom());
			  parameters = (String[]) Util.insertArray(parameters, 1, pub.getPrenom());
			  parameters = (String[]) Util.insertArray(parameters, 2, pub.getMessage());
			  String content = AssmatUtil.getMessagePropertiesParametersValues("CONTACT-MAIL-ACTUALISER-COORD-RAM-CONTENT", parameters);

			  String from = AssmatUtil.getDefaultEmail(); 
			  String replyTo = "";
			  if(mbr!= null && Util.notEmpty(mbr.getEmail())){
			    replyTo = mbr.getEmail(); 
			  }
			  if(Util.isEmpty(to)){
			    to.add(channel.getDefaultEmail());
			    logger.warn("Aucun mail (UA + routage) trouvé, envoi du mail à :"+ channel.getDefaultEmail());
			  }

				  AssmatUtil.sendMail(to, subject, content, from, replyTo);
			  
		  }
	  }
}
