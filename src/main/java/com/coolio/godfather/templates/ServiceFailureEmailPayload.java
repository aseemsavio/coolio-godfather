package com.coolio.godfather.templates;

import java.util.List;

/**
 * @author Aseem Savio
 * Payload for the email to be sent for application failure.
 */
public class ServiceFailureEmailPayload {

    private String firstName;
    private String from;
    private String to;
    private String subject;
    private List<String> failedServices;

    public ServiceFailureEmailPayload(String firstName, String from, String to, String subject, List<String> failedServices) {
        this.firstName = firstName;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.failedServices = failedServices;
    }

    public ServiceFailureEmailPayload() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getFailedServices() {
        return failedServices;
    }

    public void setFailedServices(List<String> failedServices) {
        this.failedServices = failedServices;
    }

    @Override
    public String toString() {
        return "ServiceFailureEmailPayload{" +
                "firstName='" + firstName + '\'' +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", failedServices=" + failedServices +
                '}';
    }
}
