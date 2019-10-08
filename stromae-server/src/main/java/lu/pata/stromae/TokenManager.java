package lu.pata.stromae;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;

@Component
public class TokenManager {
    private Logger log= LoggerFactory.getLogger(TokenManager.class);
    private String currentToken;

    @PostConstruct
    private void init(){
        generateToken();
    }

    public String getCurrentToken(){
        return currentToken;
    }

    public void generateToken(){
        byte[] b = new byte[32];
        new SecureRandom().nextBytes(b);
        currentToken= Hex.encodeHexString(b);
        log.info("**** Current enroll token: {} ****",currentToken);
    }
}
