package lu.pata.stromae;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/dr")
public class DataReceiver {
    Logger log=LoggerFactory.getLogger(DataReceiver.class);

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public String postData(@RequestBody DataPackage data) throws IOException {
        try (FileOutputStream output = new FileOutputStream(data.getFn(), true)) {
            output.write(data.getData());
        }
        return data.getFn()+" / "+data.getData().length;
    }
}
