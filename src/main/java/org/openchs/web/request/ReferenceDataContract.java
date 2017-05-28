package org.openchs.web.request;

public class ReferenceDataContract extends CHSRequest {
    private String name;

    public ReferenceDataContract() {
    }

    public ReferenceDataContract(String uuid, String userUUID, String name) {
        super(uuid, userUUID);
        this.name = name;
    }

    public String getName() {
        return name == null ? null : name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }
}