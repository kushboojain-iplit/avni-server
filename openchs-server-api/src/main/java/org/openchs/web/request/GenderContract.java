package org.openchs.web.request;

public class GenderContract extends CHSRequest {
    private String name;

    public GenderContract(String uuid, String name) {
        super(uuid);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
