package alpha.payeasebe.payloads.res;

public interface ResponseShowVirtualAccountId {
    String getId();
    String getNumber();
    String getProvider_name(); 
    String getProfile_picture_url();
    Boolean getProvider_is_deleted(); 
}
