package com.coolio.godfather.templates;

/**
 * @author Aseem Savio
 */

public class AwakeResponse {

    private String microServiceName;
    private String status;

    public String getMicroServiceName() {
        return microServiceName;
    }

    public void setMicroServiceName(String microServiceName) {
        this.microServiceName = microServiceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AwakeResponse(String microServiceName, String status) {
        this.microServiceName = microServiceName;
        this.status = status;
    }

    public AwakeResponse() {
    }
}
