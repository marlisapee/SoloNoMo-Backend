package org.marlisapee.models;

import java.sql.Date;

public class TripWithUser {
    private int tripId;
    private int userId;
    private String destination;
    private Date startDate;
    private Date endDate;
    private String description;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userProfilePicture;
    private int userTripCount;

    public TripWithUser(int tripId, int userId, String destination, Date startDate, Date endDate, String description, String userFirstName, String userLastName, String userEmail, String userProfilePicture, int userTripCount) {
        this.tripId = tripId;
        this.userId = userId;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userProfilePicture = userProfilePicture;
        this.userTripCount = userTripCount;
    }

    public int getUserTripCount() {
        return userTripCount;
    }

    public void setUserTripCount(int userTripCount) {
        this.userTripCount = userTripCount;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }
}
