package org.marlisapee.models;

public class TripParticipant {
    private int id;
    private int tripId;
    private int userId;

    public TripParticipant(int id, int tripId, int userId) {
        this.id = id;
        this.tripId = tripId;
        this.userId = userId;
    }

    public TripParticipant(int tripId, int userId) {
        this.tripId = tripId;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
