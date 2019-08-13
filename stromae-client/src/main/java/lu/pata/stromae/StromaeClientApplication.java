package lu.pata.stromae;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;

@SpringBootApplication
public class StromaeClientApplication implements CommandLineRunner {
	private Logger log= LoggerFactory.getLogger(StromaeClientApplication.class);

	@Value("${app.buffer}")
	private Integer bufferSize;
	@Value("${app.url}")
	private String url;

	public static void main(String[] args) {
		SpringApplication.run(StromaeClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Buffer size: "+bufferSize);
		log.info("URL: "+url);
		log.info("File in: "+args[0]);
		log.info("File out: "+args[1]);

		RestTemplate t=new RestTemplate();

		byte[] buffer=new byte[bufferSize];
		FileInputStream is=new FileInputStream(args[0]);
		int tr;

		while((tr=is.read(buffer))>0){
			byte[] td=buffer;
			if(tr<bufferSize) {
				td = new byte[tr];
				System.arraycopy(buffer, 0, td, 0, tr);
				buffer=td;
			}
			DataPackage data=new DataPackage();
			data.setFn(args[1]);
			data.setData(td);

			long startTime = System.currentTimeMillis();

			String r=t.postForObject(url,data,String.class);

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;

			log.info(r+" ["+elapsedTime+"]");
		}

	}
}
