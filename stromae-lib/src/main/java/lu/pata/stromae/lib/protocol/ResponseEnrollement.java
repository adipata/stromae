package lu.pata.stromae.lib.protocol;

public class ResponseEnrollement implements DataPayload {
    private final DataType dataType = DataType.ENROLL_REQ;
    private byte[] signature;

}
