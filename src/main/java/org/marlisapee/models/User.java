package org.marlisapee.models;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private String password;
    private String bio;
    private int tripCount;

    public User(int id, String firstName, String lastName, String email, String profilePicture, String password, String bio, int tripCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.password = password;
        this.bio = bio;
        this.tripCount = tripCount;
    }

    public User(String firstName, String lastName, String email, String profilePicture, String password, String bio, int tripCount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.password = password;
        this.bio = bio;
        this.tripCount = tripCount;
    }


    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getTripCount() {
        return tripCount;
    }

    public void setTripCount(int tripCount) {
        this.tripCount = tripCount;
    }
}
