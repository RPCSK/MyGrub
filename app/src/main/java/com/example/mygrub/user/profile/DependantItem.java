package com.example.mygrub.user.profile;

public class DependantItem {
    private String key, firstName, lastName, birthdate, relation, employmentStatus;

    public DependantItem() {
    }

    public DependantItem(String key, String firstName, String lastName, String birthdate, String relation, String employmentStatus) {
        this.key = key;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.relation = relation;
        this.employmentStatus = employmentStatus;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
}
