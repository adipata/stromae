package lu.pata.stromae.lib.protocol;

import com.google.common.primitives.Longs;
import lu.pata.stromae.lib.pgp.crypto.PGPEncrypt;
import lu.pata.stromae.lib.pgp.crypto.PGPSign;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class DataPackage {
    Logger log=LoggerFactory.getLogger(DataPackage.class);

    private Optional<Boolean> isPing;
    private String fn;
    private byte[] data;
    private Long signKeyId;
    private Long encKeyId;
    private Integer packetNo;
    private Long expiry;
    private DataType dataType;
    private byte[] signature;

    private Optional<Boolean> err;
    private String errMsg;

    public Optional<Boolean> getIsPing() {
        return isPing;
    }

    public void setIsPing(Optional<Boolean> isPing) {
        this.isPing = isPing;
    }

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

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Optional<Boolean> getErr() {
        return err;
    }

    public void setErr(Optional<Boolean> err) {
        this.err = err;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public byte[] getHash() throws IOException {
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        if(fn!=null)os.write(fn.getBytes());
        if(data!=null)os.write(data);
        if(encKeyId!=null)os.write(Longs.toByteArray(Long.valueOf(encKeyId)));
        if(signKeyId!=null)os.write(Longs.toByteArray(Long.valueOf(signKeyId)));
        if(packetNo!=null)os.write(Longs.toByteArray(Long.valueOf(packetNo)));
        if(expiry!=null)os.write(Longs.toByteArray(expiry));

        return org.apache.commons.codec.digest.DigestUtils.sha256(os.toByteArray());
    }

    public void pack(PGPPublicKey encKey, PGPPrivateKey signKey) throws IOException, PGPException {
        expiry=(new Date()).getTime()+5000;
        if(data!=null) data= PGPEncrypt.encrypt(data,encKey);
        signature= PGPSign.sign(getHash(),signKey);
        if(encKey!=null)encKeyId=encKey.getKeyID();
        signKeyId=signKey.getKeyID();
    }
}
