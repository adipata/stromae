package lu.pata.stromae;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.pata.stromae.lib.pgp.crypto.PGPDecrypt;
import lu.pata.stromae.lib.pgp.crypto.PGPEncrypt;
import lu.pata.stromae.lib.pgp.crypto.PGPVerify;
import lu.pata.stromae.lib.protocol.DataPackage;
import lu.pata.stromae.lib.KeyManager;
import lu.pata.stromae.lib.protocol.DataType;
import lu.pata.stromae.lib.protocol.RequestEnrollment;
import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/dr")
public class DataReceiver {
    private Logger log=LoggerFactory.getLogger(DataReceiver.class);

    @Value("${app.sync.folder}")
    private String folderPath;
    @Value("${app.local}")
    private String localPathKey;
    @Value("${app.trusted}")
    private String trustedPathKey;
    @Value("${app.local.pass}")
    private String localPass;
    @Autowired
    private TokenManager tokenManager;

    private KeyManager keyManager;
    private ObjectMapper json=new ObjectMapper();

    @PostConstruct
    private void setup(){
        keyManager=new KeyManager(trustedPathKey,localPathKey);
        keyManager.login(localPass);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public DataPackage postData(@RequestBody DataPackage data, HttpServletRequest request) {
        /*
        try {
            PGPPublicKey key = keyManager.getPubKey(data.getSignKeyId());
            if (key != null) {
                if(data.getExpiry()<System.currentTimeMillis()) throw new Exception("Cannot accept expired data");
                byte[] signature=PGPVerify.verify(data.getSignature(), key);

                if(!Arrays.equals(signature,data.getHash())) throw new Exception("Receive data is not correctly signed.");

                if (data.getPacketNo() == 0) {
                    FileHelper.deleteFiles(new File(folderPath), data.getFn());
                } else {
                    if(!Files.exists(Paths.get(folderPath + data.getFn() + "." + (data.getPacketNo()-1)))) return "Unexpected packet no: "+data.getPacketNo();
                }

                try (FileOutputStream output = new FileOutputStream(folderPath + data.getFn() + "." + data.getPacketNo(), false)) {
                    output.write(data.getData());
                }
                return "Ok: "+data.getFn() + " / " + data.getData().length;
            } else {
                return "Err: unknown sign key";
            }
        } catch (Exception ex){
            return "Err: "+ex.getMessage();
        }
         */

        DataPackage resp=new DataPackage();
        try{
            if(data.getExpiry()<System.currentTimeMillis()) throw new Exception("Cannot accept expired data.");
            //If we have an enroll request, enroll before checking signature
            if(data.getDataType()== DataType.ENROLL_REQ) enroll(data);

        }catch(Exception ex){
            resp.setErr(Optional.of(true));
            resp.setErrMsg(ex.getMessage());
        }


        /*
        if(data.getIsPing().orElse(false)){
            log.info("Received ping request from client {}.",request.getRemoteAddr());
            resp.setIsPing(Optional.of(true));
        } else if (data.getErr().orElse(false)) {
            log.warn("Received error message from client {}: {}",request.getRemoteAddr(),data.getErrMsg());
        } else {
            try{
                if(data.getExpiry()<System.currentTimeMillis()) throw new Exception("Cannot accept expired data.");

            }catch(Exception ex){
                resp.setErr(Optional.of(true));
                resp.setErrMsg(ex.getMessage());
            }
        }
         */


        return resp;
    }

    private void enroll(DataPackage dataPackage) throws Exception {
        byte[] payload=PGPDecrypt.decrypt(dataPackage.getData(),keyManager.getLocalPrivateKey());
        RequestEnrollment req=json.readValue(payload, RequestEnrollment.class);
        if(!req.getToken().equals(tokenManager.getCurrentToken())) throw new Exception("Invalid token.");

    }
}
