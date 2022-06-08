package fr.cg44.plugin.assmat;

import generated.InscriptionAM;

import com.jalios.jcms.Channel;
import com.jalios.util.Util;


public class TokenUtil {
	private static final String CHAINE = "security";
	
	public static String generatePasswordResetToken(String idFormulaire, long tokenDuration) {
	     long expirationDuration = getExpirationDuration();
         long dateExpirationDuration = System.currentTimeMillis() + (tokenDuration > 0L ? tokenDuration : expirationDuration);
         String crypt = Channel.getChannel().crypt((new StringBuilder()).append(String.valueOf(dateExpirationDuration)).append(CHAINE).toString());
         String concat = (new StringBuilder()).append(idFormulaire).append(":").append(dateExpirationDuration).append(":").append(crypt).toString();
         String encoded = Util.encodeBASE64(concat,false);
         return encoded;
	}

	public static InscriptionAM getInscriptionFromToken(String token) {
		String decoded = Util.decodeBASE64(token);
        if(decoded == null)
            return null;
        int i = decoded.indexOf(':');
        if(i < 0)
            return null;
        int j = decoded.indexOf(':', i + 1);
        if(j <= i)
            return null;
        String idForm = decoded.substring(0, i);
        InscriptionAM form = Channel.getChannel().getData(InscriptionAM.class, idForm);
        if(form == null)
            return null;
        String dateExpiration = decoded.substring(i + 1, j);
        String password = decoded.substring(j + 1);
        boolean flag = Channel.getChannel().checkCrypt((new StringBuilder()).append(dateExpiration).append(CHAINE).toString(), password);
        if(!flag)
            return null;
        long l = Util.toLong(dateExpiration, 0L);
        if(l < System.currentTimeMillis())
            return null;
        else
            return form;
	}

	private static long getExpirationDuration() {
		return Channel.getChannel().getLongProperty("channel.lost-password-link.expiration-duration", 0x5265c00L);
	}
}
