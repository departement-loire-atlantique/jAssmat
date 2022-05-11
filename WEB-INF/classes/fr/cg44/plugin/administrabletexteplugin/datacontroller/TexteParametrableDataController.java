package fr.cg44.plugin.administrabletexteplugin.datacontroller;

import fr.cg44.plugin.administrabletexteplugin.selector.TexteParametrableSelector;
import generated.TexteParametrable;

import java.util.Set;
import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.JcmsUtil;
import com.jalios.util.Util;

/**
 * DataController pour le type TexteParametrable qui verifie que deux contenus
 * TexteParametrable n'utilisent pas la meme clé
 * 
 * @author c.rabiller
 *
 */
public class TexteParametrableDataController extends BasicDataController {

	@Override
	public ControllerStatus checkIntegrity(Data data) {

		if (!(data instanceof TexteParametrable)) {
			return ControllerStatus.OK;
		}

		TexteParametrable texteParam = (TexteParametrable) data;
		

		// Contrôle que la key n'existe pas deja pour un autre contenu

		if (Util.notEmpty(texteParam.getIdentifiantTechnique())) {
			String identifiantTechnique = texteParam.getIdentifiantTechnique();

			// On recupere le/les contenus TexteParametrable
			Set<TexteParametrable> setTexteParam = channel.getPublicationSet(TexteParametrable.class, channel.getDefaultAdmin());

			// Si le set est vide, aucun contenu donc on retourne Ok
			if (Util.notEmpty(setTexteParam)) {

				// On ne compte pas le contenu lui meme
				setTexteParam.remove(texteParam);

				// On filtre avec la key
				Set<TexteParametrable> newSetTexteParam = (Set<TexteParametrable>) JcmsUtil.applyDataSelector(setTexteParam, new TexteParametrableSelector(identifiantTechnique));

				// Si une publication existe deja
				if (Util.notEmpty(newSetTexteParam)) {
					ControllerStatus status = new ControllerStatus();
					status.setProp("jcmsplugin.administrabletexteplugin.controller.doublekey", identifiantTechnique, Util.getFirst(newSetTexteParam));
					return status;
				}
			}

		}

		return ControllerStatus.OK;
	}

}
