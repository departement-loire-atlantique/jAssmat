package fr.cg44.plugin.assmat.policyfilter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.jalios.jcms.Channel;
import com.jalios.jcms.FileDocument;
import com.jalios.jcms.Publication;
import com.jalios.jcms.WorkflowConstants;
import com.jalios.jcms.mashup.ImportManager;
import com.jalios.jcms.mashup.ImportOptions;
import com.jalios.jcms.mashup.ImportSource;
import com.jalios.jcms.mashup.ImportStatus;
import com.jalios.jcms.policy.BasicImportPolicyFilter;
import com.jalios.jcms.workspace.Workspace;
import com.jalios.util.HttpClientUtils;
import com.jalios.util.Util;
import com.jalios.util.XmlUtil;


/**
 * Après un import, ré-intérroge l'import et met en expiré toutes les publications non présentes dans le fichier xml d'import  
 *
 */
public class ImportPolicyFilterExpireCd44 extends BasicImportPolicyFilter  {

  private static final Logger LOGGER = Logger.getLogger(ImportPolicyFilterExpireCd44.class);
  
  private Channel channel = Channel.getChannel();
  
  @Override  
  public void afterImportData(Document doc, ImportOptions options, ImportStatus status, Map context) {
    LOGGER.debug("Met en expiré toutes les publications non présentes dans le fichier xml d'import");
    
    // Récupère les sources.
    ImportManager importMgr = ImportManager.getInstance();
    Map sourceMap = importMgr.getImportSourceMap();
      
    // Si il y a au moins une source d'import (et une seule)
    // Et que l'import demandé a réussi
    // Alors réintéroge l'url de la source unique pour supprimer les pub JCMS non présente dans le xml de retour
    if (!sourceMap.isEmpty() && status.isSuccess()) {     
      if(sourceMap.entrySet() != null && sourceMap.entrySet().size() == 1 ) {
        ImportSource source =  (ImportSource) ((Map.Entry) Util.getFirst(sourceMap.entrySet())).getValue() ;
        String urlXml = source.getUrl();
        
        try {
          // Appel la source d'import (en totalité)
          File localFile = File.createTempFile("trsb-delete-import", ".xml");
          int connectionTimeout = (1000 * Util.toInt(channel.getProperty("import-mgr.connection.timeout"), 5));
          HttpClientUtils.getContent(urlXml, localFile, connectionTimeout);
          // La liste de toutes les publications du xml (publications de loire-atlantique)
          Document localDocument = XmlUtil.getDocument(localFile);          
          Element localElement = localDocument.getRootElement();         
          List<Element> localList = localElement.getChildren("data");        
          // Si moins de 10 éléments alors pas d'expiration des contenus et un warn est levé
          if(Util.isEmpty(localList) || localList.size() < 10) {
            LOGGER.warn("Impossible d'expirer les contenus importés car moins de 10 éléments dans le fichier XML importé");
            return;
          }          
          Set<String> importIdSet = new HashSet<String>();
          for (Element itElement : localList) {
            String eltId = itElement.getAttributeValue("id");
            importIdSet.add(eltId);
          }         
          // Récupère toutes les publications JCMS importées (publications de assmat.fr)
          Workspace imptWorkspace = importMgr.getImportWorkspace();
          Set<Publication> jcmsPubSet = imptWorkspace.getPubSet();        
          if(Util.notEmpty(jcmsPubSet)) {
            
            StringBuilder expire = new StringBuilder();
            StringBuilder publie = new StringBuilder();
            
            for(Publication itPub : jcmsPubSet) {
              String itPubImportId = itPub.getImportId();
              if(Util.notEmpty(itPubImportId)) {
                // Ignore les documents pour le changement de status
                if(!(itPub instanceof FileDocument)) {                   
                  if(!importIdSet.contains(itPubImportId) && itPub.getPstatus() == WorkflowConstants.PUBLISHED_PSTATUS) {
                    // Passe en expriré toutes les publivations JCMS non présentent dans le XML d'import complet
                    // En ignorant les documents (ne sont pas présents dans le fichier XML)
                    Publication itClone = (Publication) itPub.getUpdateInstance();
                    itClone.setPstatus(WorkflowConstants.EXPIRED_PSTATUS);
                    itClone.performUpdate(importMgr.getImportAuthor());
                    expire.append(itPub.getId() + " ");
                    LOGGER.info("Passage de la publication " + itClone.getId() + " à l'état expiré");                
                  } else if(importIdSet.contains(itPubImportId) && itPub.getPstatus() == WorkflowConstants.EXPIRED_PSTATUS) {
                    // Passe de expiré à publié les publications présentent dans le XML mais qui ont étés expirées dans JCMS                
                    Publication itClone = (Publication) itPub.getUpdateInstance();
                    itClone.setPstatus(WorkflowConstants.PUBLISHED_PSTATUS);
                    itClone.performUpdate(importMgr.getImportAuthor());
                    publie.append(itPub.getId() + " ");
                    LOGGER.info("Passage de la publication " + itClone.getId() + " à l'état publié");
                  }
                }                
              }
            }
            
            ImportExpireStatus expireStatus = new ImportExpireStatus();
            expireStatus.setImportDate(new Date());
            expireStatus.setExpire(expire.toString().trim());
            expireStatus.setPublie(publie.toString().trim());
            ImportExpireStatus.addImportExpireStatus(expireStatus);
          } 
        
          
          localFile.delete();         
        } catch (IOException e) {
          LOGGER.warn("Impossible d'expirer les contenus importés", e);
        } catch (JDOMException e) {
          LOGGER.warn("Impossible d'expirer les contenus importés", e);
        }        
      }else {
        // TODO logger ERROR : une seule source
        LOGGER.warn("Impossible d'expirer les contenus importés car il y a plusieurs sources d'imports");
      }      
    }
  } 
  
  
  public static Map<String, Set<Publication>> getRapportMapPub(String[] idTab) {
    Map<String, Set<Publication>> pubExpireMap = new HashMap<String, Set<Publication>>();
    for(String itPubId : idTab) {
      Publication itPub = Channel.getChannel().getPublication(itPubId);
      if(itPub == null) {
        continue;
      }
      Set<Publication> pubSet = pubExpireMap.get(itPub.getClass().getSimpleName());
      if(Util.isEmpty(pubSet)) {
        pubSet = new HashSet<Publication>();       
      }
      pubSet.add(itPub);
      pubExpireMap.put(itPub.getClass().getSimpleName(), pubSet);
    }
    return pubExpireMap;
  }



}
