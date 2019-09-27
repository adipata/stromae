package lu.pata.stromae.lib;

public class DataPackage {
    private String fn;
    private byte[] data;
    private Long signKeyId;
    private Long encKeyId;
    private Integer packetNo;

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
}
