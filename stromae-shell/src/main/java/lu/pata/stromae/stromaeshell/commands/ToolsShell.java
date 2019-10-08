package lu.pata.stromae.stromaeshell.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.pata.stromae.lib.protocol.DataPackage;
import lu.pata.stromae.lib.protocol.RequestEnrollment;
import lu.pata.stromae.stromaeshell.InputReader;
import lu.pata.stromae.stromaeshell.KeyManager;
import lu.pata.stromae.stromaeshell.ShellHelper;
import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@ShellComponent
public class ToolsShell {
    @Autowired
    private ShellHelper shellHelper;
    @Autowired
    private InputReader inputReader;
    @Autowired
    private KeyManager keyManager;
    @Value("${app.url}")
    private String url;

    private ObjectMapper json = new ObjectMapper();

    @ShellMethod("Enroll this client with the server")
    public void enroll(@ShellOption(value = {"-o", "--overwrite"},defaultValue = "false",help = "Overwrite a previous enrollment") String overwrite) throws IOException, PGPException {
        if(!keyManager.hasLocalKeys() || overwrite.equals("true")) {
            if(keyManager.hasLocalKeys()) keyManager.deleteLocalKeys();

            String name = inputReader.prompt("Name");
            String email = inputReader.prompt("Email", "");
            String pass1;
            String pass2;
            do {
                pass1 = inputReader.prompt("Password", "", false);
                pass2 = inputReader.prompt("Password again", "", false);
                if (!pass1.equals(pass2)) shellHelper.printError("Passwords do not match!");
            } while (!pass1.equals(pass2));

            if(name!=null) {
                shellHelper.printInfo("Generating key pair...");
                keyManager.generateLocalKeys(name, email, pass1);

                RequestEnrollment req=keyManager.getRequestEnrollment("coco");
                DataPackage data=new DataPackage();
                data.setData(json.writeValueAsBytes(req));
                data.setDataType(req.getDataType());
                data.pack(keyManager.getLocalPublicKey(),keyManager.getLocalPrivateKey());

                RestTemplate t=new RestTemplate();
                DataPackage r=t.postForObject(url,data,DataPackage.class);
                if(r.getErr().orElse(false)){
                    shellHelper.printError("Server error: "+r.getErrMsg());
                } else {
                    shellHelper.printInfo("Ok");
                }
            } else {
                shellHelper.printWarning ("Name cannot be null. Canceling enrollment.");
            }
        } else {
            shellHelper.printWarning("Already enrolled. To create a new enrollment, use command: enroll -o true ");
        }
    }
}
