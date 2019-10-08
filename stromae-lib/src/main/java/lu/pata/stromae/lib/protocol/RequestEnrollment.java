package lu.pata.stromae.lib.protocol;

public class RequestEnrollment implements DataPayload {
    private final DataType dataType = DataType.ENROLL_REQ;
    private String publicKey;
    private String token;


    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DataType getDataType() {
        return dataType;
    }
}
