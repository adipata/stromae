package lu.pata.stromae;

import lu.pata.stromae.domain.FileSync;
import lu.pata.stromae.lib.protocol.DataPackage;
import lu.pata.stromae.lib.FileHelper;
import lu.pata.stromae.lib.pgp.crypto.PGPEncrypt;
import lu.pata.stromae.lib.pgp.crypto.PGPSign;
import lu.pata.stromae.lib.pgp.key.PGPKeyHelper;
import lu.pata.stromae.repository.FileRepository;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

@SpringBootApplication
public class StromaeClientApplication implements CommandLineRunner {
	private Logger log= LoggerFactory.getLogger(StromaeClientApplication.class);

	@Value("${app.buffer}")
	private Integer bufferSize;
	@Value("${app.url}")
	private String url;
	@Value("${app.sync.folder}")
	private String folderPath;

	@Value("${app.cert.sign}")
	private String signCert;
	@Value("${app.cert.enc}")
	private String encCert;
	@Value("${app.cert.pass}")
	private String pass;
	@Value("${app.me}")
	private String me;
    @Value("${app.package.validity}")
    private Long packageValidity;

	@Autowired
	private FileRepository fileRepository;

	public static void main(String[] args) {
		SpringApplication.run(StromaeClientApplication.class, args);
	}

	@Override
	public void run(String... args) {

        try {
            scanForUpload();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

	private void scanForUpload() throws Exception {
		File folder=new File(folderPath);
		for(File f:folder.listFiles()) {
			Optional<FileSync> dbFile =fileRepository.findByName(f.getName());
			FileSync fDb;
			if(dbFile.isEmpty())
				fDb=createFileSync(f);
			else
				fDb=dbFile.get();
			if(fileNeedsToBeSync(f,fDb)) sendFile(f,fDb);
		}
	}

	private void sendFile(File f,FileSync fDb) throws Exception {
		log.info("Sending file: "+fDb.getName());
		upload(f);
		fDb.setSize(f.length());
		fDb.setHash(FileHelper.calculateHash(f));
		fileRepository.save(fDb);


	}

	private FileSync createFileSync(File f){
		FileSync file=new FileSync(f.getName(),me);
		fileRepository.save(file);
		log.info("File created: "+file);
		return file;
	}

	private boolean fileNeedsToBeSync(File fDisk,FileSync fDb){
		if(!fDb.getOwner().equals(me)) return false;
		if(fDisk.length()!=fDb.getSize()) return true;
		if(!FileHelper.calculateHash(fDisk).equals(fDb.getHash())) return true;
		return false;
	}

	private void upload(File f)throws Exception{
		RestTemplate t=new RestTemplate();

		byte[] buffer=new byte[bufferSize];
		FileInputStream is=new FileInputStream(f);
		int tr;
		int c=0;

		PGPPublicKey encKey=PGPKeyHelper.readPublicKey(encCert);
		PGPPrivateKey signKey=PGPKeyHelper.readPrivateKey(signCert,"salam");

		while((tr=is.read(buffer))>0){
			byte[] td=buffer;
			if(tr<bufferSize) {
				td = new byte[tr];
				System.arraycopy(buffer, 0, td, 0, tr);
				buffer=td;
			}
			DataPackage data=new DataPackage();
			data.setFn(f.getName());
			data.setData(PGPEncrypt.encrypt(td,encKey));
			data.setEncKeyId(encKey.getKeyID());
			data.setSignKeyId(signKey.getKeyID());
			data.setPacketNo(c);
			data.setExpiry(System.currentTimeMillis()+packageValidity);
			data.setSignature(PGPSign.sign(data.getHash(),signKey));

			long startTime = System.currentTimeMillis();

			String r=t.postForObject(url,data,String.class);

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;

            log.info(r+" ["+elapsedTime+"] "+c);
			c++;

			if(!r.startsWith("Ok")) throw new Exception(r);
		}
	}
}
