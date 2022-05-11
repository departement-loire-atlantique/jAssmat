package fr.cg44.plugin.assmat;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

public class TokenUtil2 {
    public static String generatePasswordResetToken(Member member, long tokenDuration)
    {
        if(Util.isEmpty(member.getPassword()))
            throw new IllegalArgumentException("Member must not have an empty password");
        if(member.isDisabled())
            throw new IllegalArgumentException("Member must be enabled (have existing valid password) : cannot generated password reset token for disabled member.");
        if(!member.isPersisted())
        {
            throw new IllegalArgumentException("Member must be persisted (saved in store or database) : cannot generated password reset token for unpersisted member.");
        } else
        {
            long expirationDuration = getExpirationDuration();
            long dateExpirationDuration = System.currentTimeMillis() + (tokenDuration > 0L ? tokenDuration : expirationDuration);
            String crypt = Channel.getChannel().crypt((new StringBuilder()).append(String.valueOf(dateExpirationDuration)).append(member.getPassword()).toString());
            String concat = (new StringBuilder()).append(member.getId()).append(":").append(dateExpirationDuration).append(":").append(crypt).toString();
            String encoded = Util.encodeBASE64(concat);
            return encoded;
        }
    }
    
    public static Member getMemberFromPasswordResetToken(String token)
    {
        String decoded = Util.decodeBASE64(token);
        if(decoded == null)
            return null;
        int i = decoded.indexOf(':');
        if(i < 0)
            return null;
        int j = decoded.indexOf(':', i + 1);
        if(j <= i)
            return null;
        String idMbr = decoded.substring(0, i);
        Member member = Channel.getChannel().getMember(idMbr);
        if(member == null)
            return null;
        String dateExpiration = decoded.substring(i + 1, j);
        String password = decoded.substring(j + 1);
        boolean flag = Channel.getChannel().checkCrypt((new StringBuilder()).append(dateExpiration).append(member.getPassword()).toString(), password);
        if(!flag)
            return null;
        long l = Util.toLong(dateExpiration, 0L);
        if(l < System.currentTimeMillis())
            return null;
        else
            return member;
    }
    
    private static long getExpirationDuration()
    {
        return Channel.getChannel().getLongProperty("channel.lost-password-link.expiration-duration", 0x5265c00L);
    }
}
