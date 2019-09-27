package lu.pata.stromae.lib;

import com.google.common.primitives.Longs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataPackage {
    Logger log=LoggerFactory.getLogger(DataPackage.class);

    private String fn;
    private byte[] data;
    private Long signKeyId;
    private Long encKeyId;
    private Integer packetNo;
    private Long expiry;
    private byte[] signature;

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Integer getPacketNo() {
        return packetNo;
    }

    public void setPacketNo(Integer packetNo) {
        this.packetNo = packetNo;
    }

    public Long getSignKeyId() {
        return signKeyId;
    }

    public void setSignKeyId(Long signKeyId) {
        this.signKeyId = signKeyId;
    }

    public Long getEncKeyId() {
        return encKeyId;
    }

    public void setEncKeyId(Long encKeyId) {
        this.encKeyId = encKeyId;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getHash() throws IOException {
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        os.write(fn.getBytes());
        os.write(data);
        os.write(Longs.toByteArray(Long.valueOf(packetNo)));
        os.write(Longs.toByteArray(expiry));

        return org.apache.commons.codec.digest.DigestUtils.sha256(os.toByteArray());
    }
}
