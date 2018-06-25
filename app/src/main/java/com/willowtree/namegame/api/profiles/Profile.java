package com.willowtree.namegame.api.profiles;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.reactivex.annotations.Nullable;
import io.realm.RealmList;
import io.realm.RealmObject;
import java9.util.Optional;

public class Profile extends RealmObject {

    @SerializedName("id")
    String id;

    @SerializedName("type")
    String type;


    @SerializedName("jobTitle")
    @Nullable
    String jobTitle;


    @SerializedName("firstName")
    String firstName;

    @SerializedName("lastName")
    String lastName;

    @SerializedName("bio")
    @Nullable
    String bio;

    @SerializedName("headshot")
    @Nullable
    Headshot headshot;


    @SerializedName("socialLinks")
    @Nullable
    RealmList<SocialLink> socialLinks;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Optional<String> getJobTitle() {
        return Optional.ofNullable(jobTitle);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public Optional<String> getBio() {
        return Optional.ofNullable(bio);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id) &&
                Objects.equals(type, profile.type) &&
                Objects.equals(jobTitle, profile.jobTitle) &&
                Objects.equals(firstName, profile.firstName) &&
                Objects.equals(lastName, profile.lastName) &&
                Objects.equals(bio, profile.bio) &&
                Objects.equals(headshot, profile.headshot) &&
                Objects.equals(socialLinks, profile.socialLinks);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", bio='" + bio + '\'' +
                ", headshot=" + headshot +
                ", socialLinks=" + socialLinks +
                '}';
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, type, jobTitle, firstName, lastName, bio, headshot, socialLinks);
    }

    public Optional<Headshot> getHeadshot() {
        return Optional.ofNullable(headshot);
    }

    public Optional<RealmList<SocialLink>> getSocialLinks() {
        return Optional.of(socialLinks);
    }

}