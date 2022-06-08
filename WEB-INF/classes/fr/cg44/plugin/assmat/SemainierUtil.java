package fr.cg44.plugin.assmat;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.jalios.util.Util;

public class SemainierUtil {
	
	private static final Logger logger = Logger.getLogger(SemainierUtil.class);
	public static String encoded(Semainier semainier){
		try {
			ObjectMapper mapper = new ObjectMapper();
			String semainierInJson = mapper.writeValueAsString(semainier);
			return semainierInJson;
		} catch (JsonGenerationException e) {
			logger.warn(e.getMessage(),e);
		} catch (JsonMappingException e) {
			logger.warn(e.getMessage(),e);
		} catch (IOException e) {
			logger.warn(e.getMessage(),e);
		}
		return null;
	}
	
	public static Semainier decoded(String chaine){
		if(Util.notEmpty(chaine)){
			try {
				ObjectMapper mapper = new ObjectMapper();
				Semainier semainier = mapper.readValue(chaine,Semainier.class);
				return semainier;
			} catch (JsonGenerationException e) {
				logger.warn(e.getMessage(),e);
			} catch (JsonMappingException e) {
				logger.warn(e.getMessage(),e);
			} catch (IOException e) {
				logger.warn(e.getMessage(),e);
			}
		}
		return null;
	}
	

}
