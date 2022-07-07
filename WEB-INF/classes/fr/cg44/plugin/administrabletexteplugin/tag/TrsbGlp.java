package fr.cg44.plugin.administrabletexteplugin.tag;

import fr.cg44.plugin.administrabletexteplugin.selector.TexteParametrableSelector;
import generated.TexteParametrable;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.HttpUtil;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.context.JcmsJspContext;
import com.jalios.jcms.taglib.EditTag;
import com.jalios.util.Util;

/**
 * Tag <trsb:glp>
 * 
 * 
 * @author crabiller
 *
 */
public class TrsbGlp extends BodyTagSupport {

	private static final Logger logger = Logger.getLogger(TrsbGlp.class);

	private static final Channel channel = Channel.getChannel();

	private static final long serialVersionUID = 1L;

	// key passé en parametre
	private String key;

	// Object passé en param
	private String[] parameter;

	private String VARIABLE_TEL_SUPPORT = "telsupp";
	private String VARIABLE_MAIL_SUPPORT = "mailsupp";
	
	private boolean attribute = false;

	private boolean showButtonEdit = true;
	
	public void setKey(String key) {
		this.key = key;
	}

	public void setParameter(String[] parameter) {
		this.parameter = parameter;
	}

	public void setAttribute(boolean attribute) {
		this.attribute = attribute;
	}

	public void setShowButtonEdit(boolean showButtonEdit) {
		this.showButtonEdit = showButtonEdit;
	}

	/**
	 * Génère le libellé en fonction de la key et des parametres
	 */
	public int doEndTag() throws JspException {
		try {
			JspWriter out = this.pageContext.getOut();
			String html = getHtmlLibelle();
			out.print(html);
		} catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
			throw new JspException("GlpTag: " + ex.getMessage(), ex);
		}

		clean();

		return EVAL_PAGE;
	}

	void clean() {
		this.key = null;
		this.parameter = null;
		this.attribute = false;
		this.showButtonEdit = true;
	}

	/**
	 * Retourne le champs texte d'un type de contenu TexteParametrable en
	 * fonction de la key passé
	 * 
	 * @param key
	 *            La clé permettant d'identifer le contenu
	 * @param param
	 *            Le tableau de string contenant les parametres qui remplaceront
	 *            les {0}, {1} ect .. dans le libellé
	 * @return le libellé souhaité
	 * @throws Exception
	 */
	public String getHtmlLibelle() {

		String libelleRetour = "";
		// Pré-condtion (L'attribut key n'est pas vide)
		if (Util.isEmpty(key)) {
			logger.warn("L'attribut 'key' est obligatoire.");
		}

		// On recupere le/les contenus TexteParametrable
		Set<TexteParametrable> setTexteParam = channel.getPublicationSet(TexteParametrable.class, channel.getDefaultAdmin());

		// On filtre avec la key (normalement un seul resultat possible, les
		// doubles de key sont bloqués par un dataController)
		setTexteParam = (Set<TexteParametrable>) JcmsUtil.applyDataSelector(setTexteParam, new TexteParametrableSelector(key));

		// On teste le set
		if (Util.isEmpty(setTexteParam)) {
			libelleRetour = key;
			logger.info("La clé " + key + "ne correspond à aucuns contenus TexteParametrable");
			libelleRetour = configure(libelleRetour, null);
		} else {
			TexteParametrable texteParam = Util.getFirst(setTexteParam);
			// On recupere le libelle
			libelleRetour = texteParam.getTexte();
			if (Util.notEmpty(libelleRetour)) {
				String selector = "";
				// Si on a des parametres
				if (Util.notEmpty(parameter)) {

					int itCounter = 0;
					
					// On remplace les {0}, {1} ect .. par leurs equivalents
					// dans le tableau
					for (String itString : parameter) {
						selector = "{" + itCounter + "}";
						if (libelleRetour.contains(selector)) {
							libelleRetour = Util.replaceAll(libelleRetour, selector, itString);
						}
						itCounter++;
					}
				}
				selector = "{"+VARIABLE_TEL_SUPPORT+"}";
				if (libelleRetour.contains(selector)) {
					libelleRetour = Util.replaceAll(libelleRetour, selector, channel.getProperty("jcmsplugin.assmatplugin.telephone-support"));
				}
				selector = "{"+VARIABLE_MAIL_SUPPORT+"}";
				if (libelleRetour.contains(selector)) {
					TreeSet<TexteParametrable> setTexteMailSupport = channel.getPublicationSet(TexteParametrable.class, channel.getDefaultAdmin());
					setTexteMailSupport = (TreeSet<TexteParametrable>) JcmsUtil.applyDataSelector(setTexteMailSupport, new TexteParametrableSelector("MAIL-DEFAULT-FROM"));
					String from = "";
					if(Util.notEmpty(setTexteMailSupport)){
						TexteParametrable texteParamMail = setTexteMailSupport.first();
						if(Util.notEmpty(texteParamMail)){
							from = "<a href='mailto:"+texteParamMail.getTexte()+"'>"+texteParamMail.getTexte()+"</a>";
						} else {
							from = Channel.getChannel().getDefaultEmail();
						}
					} 
					libelleRetour = Util.replaceAll(libelleRetour, selector, from);
				}
			} else {
				libelleRetour = key;
				logger.warn("Le champ texte du contenu '" + texteParam + "' est vide.");
			}

			libelleRetour = configure(libelleRetour, texteParam);

		}
		return libelleRetour;
	}

	private String configure(String libelle, TexteParametrable texteParam) {
		if (!attribute && showButtonEdit) {
			String nameSpanclass = "texte-parametrable text-parametrable-" + key;

			// On ajoute un span avec la classe
			// text-parametrable-idtechnique
			libelle = "<span class='" + nameSpanclass + "'>" + libelle;
			if (Util.notEmpty(texteParam)) {
				String outEdit = EditTag.getEditIcon((JcmsJspContext) channel.getCurrentJcmsContext(), texteParam.getId(), "", "", true, "texte");
				if (Util.notEmpty(outEdit)) {
					libelle += outEdit;
				}
			}
			libelle += "</span>";
		} else if (attribute) {
			libelle = HttpUtil.encodeForHTMLAttribute(libelle);
		}
		return libelle;
	}
}
