package fr.cg44.plugin.assmat.webservice;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import fr.cg44.plugin.assmat.managers.ProfilManager;
import generated.ProfilASSMAT;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jalios.jcms.rest.DataRestResource;
import com.jalios.util.Util;



/**
 * Service REST pour la mise à jour du contenu JCMS Profile AM par rapport à la table solis
 * 
 */
public class SolisRestMemberResources extends DataRestResource {

  private static final Logger logger = Logger.getLogger(SolisRestMemberResources.class);

  private ProfilASSMAT profil;

  public SolisRestMemberResources(Context context, Request request, Response response) {
    super(context, request, response);

    if (Util.isEmpty(getLoggedMember())) {
      // ie status code HTTP 401 Unauthorized
      response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
      return;
    }  


    // On récupère l'identifiant de l'espace de travail
    String idAgrement = (String) request.getAttributes().get("idDossier");

    if (Util.isEmpty(idAgrement)) {
      getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Incorrect format parameter 'idDossier'");
      return;
    }

    // L'attribut data est défini dans la classe DataRestResource
    //data = Channel.getChannel().getWorkspace(idAgrement);
    profil = ProfilManager.getInstance().getProfilASSMAT(Integer.parseInt(idAgrement));

    if(Util.notEmpty(profil)){
      data = profil.getAuthor();    	
    }else {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
    }

    // Indique que l'on supporte la représentation text/xml de la réponse
    getVariants().add(new Variant(MediaType.TEXT_XML));
    // Indique que l'encodage du corps de la réponse est en UTF-8
    setXmlUTF8Encoding();
  }



  @Override
  protected String getXmlRepresentation() {   
    
    String xmlString = data.exportXml();    
    DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();     
    DocumentBuilder built = null;
    org.w3c.dom.Document doc = null;
    org.w3c.dom.Element root = null;

    try {
      built = fact.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(xmlString));
      doc =  built.parse(is);      
      root = doc.getDocumentElement(); 
    } catch (ParserConfigurationException e) {
      logger.warn("Impossible de générer le XML", e);
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
    } catch (SAXException e) {
      logger.warn("Impossible de générer le XML", e);
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
    } catch (IOException e) {
      logger.warn("Impossible de générer le XML", e);
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
    }

    // 0011709: API pour récupérer l'id technique de JCMS d'une AM à partir du numéro de dossier 
    org.w3c.dom.Element docNodeProfilUrl = doc.createElement("field");
    docNodeProfilUrl.setAttribute("name", "profilUrl"); 
    Text ProfilUrlIdText = doc.createTextNode(channel.getUrl() + profil.getDisplayUrl(null));
    docNodeProfilUrl.appendChild(ProfilUrlIdText);
    root.appendChild(docNodeProfilUrl); 

    // Ajoute le tel fix du profilAM à l'export du membre
    if(Util.notEmpty(profil.getTelephoneFixe())){      
      //creation du noeud document a ajouter
      org.w3c.dom.Element docNodeTelFix = doc.createElement("field");
      docNodeTelFix.setAttribute("name", "telfixe");      
      Text idText= doc.createTextNode(profil.getTelephoneFixe());
      docNodeTelFix.appendChild(idText);      
      root.appendChild(docNodeTelFix);              
    }

    // Ajoute le canal de communication du profilAM (email ou mobile) à l'export du membre
    if(Util.notEmpty(profil.getCanalDeCommunicationSite())) {
      //creation du noeud document a ajouter
      org.w3c.dom.Element docNodeTelFix = doc.createElement("field");
      docNodeTelFix.setAttribute("name", "canalDeCommunicationSite");      
      Text idText=doc.createTextNode("1".equals(profil.getCanalDeCommunicationSite()) ? "email" : "mobile" );
      docNodeTelFix.appendChild(idText);      
      root.appendChild(docNodeTelFix);             
    } 
    
    try {
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
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.transform(new DOMSource(doc), new StreamResult(sw));
    return sw.toString();    
  }


  @Override
  public boolean allowGet() {
    // N'autorise pas la méthode HTTP GET
    return true;
  }  
}
