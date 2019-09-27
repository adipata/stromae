package lu.pata.stromae.lib;

import lu.pata.stromae.lib.pgp.key.PGPKeyHelper;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class KeyManager {
    Logger log= LoggerFactory.getLogger(KeyManager.class);

    private List<PGPPublicKey> pubKeys=new ArrayList<>();
    private String path;

    public KeyManager(String path){
        this.path=path;
        File folder=new File(path);
        for(File f:folder.listFiles()){
            if(f.getName().contains("pub")) {
                try {
                    pubKeys.add(PGPKeyHelper.readPublicKey(new FileInputStream(f)));
                } catch (IOException | PGPException e) {
                    log.error("Error loading key file: {}",e.getMessage());
                }
            }
        }

        for(PGPPublicKey k:pubKeys){
            log.info("Loaded key: {}",PGPKeyHelper.getOwnerString(k));
        }
    }

    public PGPPublicKey getPubKey(Long id){
        for(PGPPublicKey k:pubKeys){
            if(k.getKeyID()==id) return k;
        }
        return null;
    }
}
