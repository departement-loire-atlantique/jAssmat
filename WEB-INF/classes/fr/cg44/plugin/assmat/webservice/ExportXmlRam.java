package fr.cg44.plugin.assmat.webservice;

import fr.cg44.plugin.assmat.selector.PlaceCategSelector;
import generated.Place;

import java.io.StringWriter;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.rest.DataRestResource;
import com.jalios.util.Util;



/**
 * Service REST pour la mise à jour du contenu JCMS Profile AM par rapport à la table solis
 * 
 */
public class ExportXmlRam extends DataRestResource {

  private static final Logger logger = Logger.getLogger(ExportXmlRam.class);

  private Set<Place> placeSet;

  public ExportXmlRam(Context context, Request request, Response response) {
    super(context, request, response);

    if (Util.isEmpty(getLoggedMember())) {
      // ie status code HTTP 401 Unauthorized
      response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
      return;
    }  


    // On récupère les fiche lieux RAM
    
    String idCategRAM = channel.getProperty("$plugin.assmatplugin.categ.relaiam");
    
// String token = "djI7al8yOzE1MjU1MTI0NzY2MTQ7R0VULDs7OyQyYSQwNCRpU2hrcWd2OUxPVjJpdVBYSmJ5WTZlZFM1SGJJWERveVpEaWFETjdPZi83T2ZZSExmQ3kwUw==";
    
    placeSet = (Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new PlaceCategSelector(idCategRAM));
    if(Util.notEmpty(placeSet)){
       getXmlRepresentation(placeSet); 
    }
 
    // Indique que l'on supporte la représentation text/xml de la réponse
    getVariants().add(new Variant(MediaType.TEXT_XML));
    // Indique que l'encodage du corps de la réponse est en UTF-8
    setXmlUTF8Encoding();
  }

  @Override
  protected String getXmlRepresentation() {   
    String xmlString = "";
      try {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
          // pour être conforme, désactiver complètement la déclaration DOCTYPE :
          docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
          docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
          logger.warn("Impossible de générer le XML", e);
        }
        
        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("relais");
        
        doc.appendChild(rootElement);
        for(Place itPlace: placeSet){
          if(Util.notEmpty(itPlace.getSolisId())){
            Element firstname = doc.createElement("id_ram");
            firstname.appendChild(doc.createTextNode(itPlace.getSolisId()));
            rootElement.appendChild(firstname);
          }
        }     
        xmlString = toString(doc);
      } catch (TransformerException e) {
        logger.warn("Impossible de générer le XML", e);
        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
      }
    
    return xmlString;
  }
  
  public static String toString(Document doc) throws TransformerException {
    StringWriter sw = new StringWriter();
    TransformerFactory tf = TransformerFactory.newInstance();
    // pour être conforme, désactiver complètement la déclaration DOCTYPE :
    tf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.transform(new DOMSource(doc), new StreamResult(sw));
    return sw.toString();    
  }


  protected void getXmlRepresentation(Set<Place> placeSet) {   
    
    
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = null;
    try {
      // pour être conforme, désactiver complètement la déclaration DOCTYPE :
      docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      docBuilder = docFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      logger.warn("Impossible de générer le XML", e);
    }
    
    // root elements
    Document doc = docBuilder.newDocument();
    Element rootElement = doc.createElement("relais");
    
    doc.appendChild(rootElement);
    for(Place itPlace: placeSet){
     Element firstname = doc.createElement("id_ram");
     firstname.appendChild(doc.createTextNode(itPlace.getSolisId()));
     rootElement.appendChild(firstname);
    }
  }


  @Override
  public boolean allowGet() {
    // N'autorise pas la méthode HTTP GET
    return true;
  }  
}
