package fr.cg44.plugin.assmat.datacontroller;

import fr.cg44.plugin.assmat.AssmatUtil;
import generated.FormulaireDeContactRAM;

import java.util.Map;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Category;
import com.jalios.jcms.Data;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

public class FormContactRAMDataController extends BasicDataController {

	  @Override
	  public void afterWrite(Data data, int op, Member mbr, Map map) {
		  // A la création du formulaire pour les relais de la petite enfance on envois un mail au relais choisi
		  if(op  == OP_CREATE){
			  FormulaireDeContactRAM pub = (FormulaireDeContactRAM) data;

			  String to = pub.getMailRAM();
			  String subject = AssmatUtil.getMessage("CONTACT-MAIL-RAM-SUBJECT");

			  // ajout du contenu du mail
			  String[] parameters = new String[]{};
			  parameters = (String[]) Util.insertArray(parameters, 0, pub.getFirstName());
			  parameters = (String[]) Util.insertArray(parameters, 1, pub.getName());
			  parameters = (String[]) Util.insertArray(parameters, 2, pub.getCourriel());
			  parameters = (String[]) Util.insertArray(parameters, 3, pub.getMessage());
			  String content = AssmatUtil.getMessagePropertiesParametersValues("CONTACT-MAIL-RAM-CONTENT", parameters);

			  //String from = pub.getCourriel();
			  String from = AssmatUtil.getDefaultEmail(); 
			  String replyTo = pub.getCourriel();
			  
			  if(Util.notEmpty(to)){
   		  AssmatUtil.sendMail(to, subject, content, from, replyTo);
			  }
		  }
	  }
}
