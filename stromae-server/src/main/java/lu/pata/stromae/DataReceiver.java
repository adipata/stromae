package lu.pata.stromae;

import lu.pata.stromae.lib.DataPackage;
import lu.pata.stromae.lib.FileHelper;
import lu.pata.stromae.lib.KeyManager;
import lu.pata.stromae.lib.pgp.crypto.PGPSign;
import lu.pata.stromae.lib.pgp.crypto.PGPVerify;
import lu.pata.stromae.lib.pgp.key.PGPKeyHelper;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/dr")
public class DataReceiver {
    private Logger log=LoggerFactory.getLogger(DataReceiver.class);

    @Value("${app.sync.folder}")
    private String folderPath;

    private KeyManager keyManager;

    @PostConstruct
    private void setup(){
        keyManager=new KeyManager("keys");
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public String postData(@RequestBody DataPackage data) throws IOException, PGPException {
        if(data.getPacketNo()==0) FileHelper.deleteFiles(new File(folderPath),data.getFn());

        PGPPublicKey key= keyManager.getPubKey(data.getSignKeyId());
        if(key!=null){
            PGPVerify.verify(data.getData(),key);

            try (FileOutputStream output = new FileOutputStream(folderPath+data.getFn()+"."+data.getPacketNo(), false)) {
                output.write(data.getData());
            }
            return data.getFn()+" / "+data.getData().length;
        } else {
            return "Unknown sign key";
        }

    }
}
