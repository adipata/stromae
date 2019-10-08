package lu.pata.stromae.stromaeshell;

import lu.pata.stromae.lib.protocol.RequestEnrollment;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class KeyManager {
    private Logger log= LoggerFactory.getLogger(KeyManager.class);
    private lu.pata.stromae.lib.KeyManager keyManager;

    @Value("${app.local}")
    private String localPath;
    @Value("${app.trusted}")
    private String trustedPath;

    @PostConstruct
    private void init(){
        keyManager=new lu.pata.stromae.lib.KeyManager(trustedPath,localPath);
    }

    public boolean hasLocalKeys(){
        if(keyManager.getLocalPublicKey()==null || keyManager.getLocalPrivateKey()==null){
            return true;
        } else {
            return false;
        }
    }

    public void deleteLocalKeys(){
        try {
            if (Files.exists(Paths.get(localPath + "/local.priv.asc")))
                Files.delete(Paths.get(localPath + "/local.priv.asc"));
            if (Files.exists(Paths.get(localPath + "/local.pub.asc")))
                Files.delete(Paths.get(localPath + "/local.pub.asc"));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public boolean generateLocalKeys(String usr,String email,String pass){
        try {
            keyManager.generateKey(usr,email,pass);
            return true;
        } catch (PGPException | IOException e) {
            log.error("Error generating keys: {}",e.getMessage());
            return false;
        }
    }

    public RequestEnrollment getRequestEnrollment(String token) throws IOException {
        RequestEnrollment req=new RequestEnrollment();
        req.setPublicKey(new String(keyManager.getLocalPublicKey().getEncoded()));
        req.setToken(token);
        return req;
    }

    public PGPPublicKey getLocalPublicKey(){
        return keyManager.getLocalPublicKey();
    }

    public PGPPrivateKey getLocalPrivateKey(){
        return keyManager.getLocalPrivateKey();
    }
}
