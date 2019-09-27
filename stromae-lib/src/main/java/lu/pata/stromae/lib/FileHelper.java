package lu.pata.stromae.lib;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Random;

public class FileHelper {
    private static Logger log=LoggerFactory.getLogger(FileHelper.class);

    public static String calculateHash(File f) {
        try (InputStream is = new FileInputStream(f)) {
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        } catch (IOException ex){
            log.error("Could not calculate hash for file {}:{}",f.getName(),ex.getMessage());

            byte[] b = new byte[20];
            new Random().nextBytes(b);
            return Hex.encodeHexString(b); //return a random hash
        }
    }

    public static void deleteFiles(File folder,String pattern){
        for (File f : folder.listFiles()) {
            if (f.getName().startsWith(pattern)) {
                f.delete();
            }
        }
    }
}
