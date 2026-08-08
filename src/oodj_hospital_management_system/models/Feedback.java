/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.Objects;

/**
 *
 * @author sheaw
 */
public class Feedback {
    private String feedbackId;
    private Consultation consultation;
    private Patient patient;

    private int rating;
    private String comment;
    private String submittedDateTime;

    public Feedback() {
        this.feedbackId = "";
        this.consultation = null;
        this.patient = null;

        this.rating = 0;
        this.comment = "";
        this.submittedDateTime = null;
    }

    public Feedback(
            String feedbackId,
            Consultation consultation,
            Patient patient,
            int rating,
            String comment,
            String submittedDateTime
    ) {
        this.feedbackId = feedbackId;
        this.consultation = consultation;
        this.patient = patient;

        this.rating = rating;
        this.comment = comment;
        this.submittedDateTime = submittedDateTime;
    }

    @Override
    public String toString() {
        return String.format(
                "Feedback {%s - %d/5}",
                this.feedbackId,
                this.rating
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Feedback)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        Feedback feedbackObj = (Feedback) obj;

        return Objects.equals(
                this.feedbackId,
                feedbackObj.feedbackId
        );
    }

    // Getters

    public String getFeedbackId() {
        return feedbackId;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public Patient getPatient() {
        return patient;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getSubmittedDateTime() {
        return submittedDateTime;
    }

    // Setters

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setSubmittedDateTime(String submittedDateTime) {
        this.submittedDateTime = submittedDateTime;
    }
}
