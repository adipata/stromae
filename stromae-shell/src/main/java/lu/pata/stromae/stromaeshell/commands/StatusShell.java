package lu.pata.stromae.stromaeshell.commands;

import lu.pata.stromae.lib.protocol.DataPackage;
import lu.pata.stromae.lib.KeyManager;
import lu.pata.stromae.stromaeshell.InputReader;
import lu.pata.stromae.stromaeshell.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@ShellComponent
public class StatusShell {
    @Value("${app.url}")
    private String url;

    @Autowired
    ShellHelper shellHelper;
    @Autowired
    InputReader inputReader;

    @ShellMethod("Test the connectivity with the server, no security involved.")
    public String ping(){
        DataPackage data=new DataPackage();
        data.setIsPing(Optional.of(true));
        shellHelper.printInfo("Connecting to "+url);
        RestTemplate t=new RestTemplate();
        DataPackage r=t.postForObject(url,data,DataPackage.class);
        if(r.getIsPing().orElse(false)) {
            return "Ping reply ok.";
        } else {
            return "The server responded but the answer does not look like a ping reply.";
        }
    }

    @ShellMethod("Display the local status.")
    public void status(){
        shellHelper.printInfo("Configured endpoint: "+url);
    }

    @ShellMethod("Load keys")
    public void keys(){
        String fullName = inputReader.prompt("Full name");
        shellHelper.printInfo(fullName);
        KeyManager kman=new KeyManager("trusted","local");
    }

}
