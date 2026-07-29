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
    private int rating;
    private String comment;

    public Feedback() {
        this.feedbackId = "";
        this.rating = 0;
        this.comment = "";
    }

    public Feedback(
            String feedbackId,
            int rating,
            String comment
    ) {
        this.feedbackId = feedbackId;
        this.rating = rating;
        this.comment = comment;
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

    public String getFeedbackId() {
        return feedbackId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public void setRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
