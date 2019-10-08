package lu.pata.stromae.lib;

import com.google.common.primitives.Longs;
import lu.pata.stromae.lib.pgp.key.PGPKeyHelper;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class KeyManager {
    Logger log= LoggerFactory.getLogger(KeyManager.class);

    private List<PGPPublicKey> trustedKeys =new ArrayList<>();
    private String trustPath;
    private String localPath;
    private PGPPublicKey localPubKey;
    private PGPPrivateKey localPrivKey;

    public KeyManager(String trustPath,String localPath){
        this.trustPath=trustPath;
        this.localPath=localPath;
        File folder=new File(trustPath);
        for(File f:folder.listFiles()){
            try {
                trustedKeys.add(PGPKeyHelper.readPublicKey(new FileInputStream(f)));
            } catch (IOException | PGPException e) {
                log.error("Error loading key file {}: {}",f.getName(),e.getMessage());
            }
        }

        for(PGPPublicKey k: trustedKeys){
            log.info("Trust key ID {}", Hex.encodeHexString(Longs.toByteArray(k.getKeyID())));
        }
    }

    public List<PGPPublicKey> getTrustedKeys(){
        return trustedKeys;
    }

    public void generateKey(String usr,String email,String pass) throws PGPException, IOException {
        OpenPGPKeyGenerator kg=new OpenPGPKeyGenerator(new SecureRandom());
        OpenPGPKeyGenerator.ArmoredKeyPair keys=kg.generateKeys(2048,usr,email,pass);
        Files.writeString(Paths.get(localPath+"/local.priv.asc"),keys.privateKey());
        Files.writeString(Paths.get(localPath+"/local.pub.asc"),keys.publicKey());

        localPubKey=PGPKeyHelper.readPublicKey(new ByteArrayInputStream(keys.publicKey().getBytes()));
        localPrivKey=PGPKeyHelper.readPrivateKey(new ByteArrayInputStream(keys.privateKey().getBytes()),pass);

        log.info("Public key ID: {} fingerprint: {}", Hex.encodeHexString(Longs.toByteArray(localPubKey.getKeyID())),PGPKeyHelper.getFingerprint(localPubKey));
        log.info("Private key ID: {}", Hex.encodeHexString(Longs.toByteArray(localPrivKey.getKeyID())));
    }

    public void login(String pass)  {
        try {
            localPrivKey=PGPKeyHelper.readPrivateKey(new FileInputStream(localPath+"/local.priv.asc"),pass);
            localPubKey=PGPKeyHelper.readPublicKey(new FileInputStream(localPath+"/local.pub.asc"));
        } catch (PGPException | IOException e) {
            log.error("Could not login: "+e.getMessage());
        }
    }

    public PGPPublicKey getLocalPublicKey(){
        return localPubKey;
    }

    public PGPPrivateKey getLocalPrivateKey(){
        return localPrivKey;
    }
}
