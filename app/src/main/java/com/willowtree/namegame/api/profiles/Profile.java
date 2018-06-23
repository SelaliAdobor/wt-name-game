package com.willowtree.namegame.api.profiles;

import com.google.gson.annotations.SerializedName;

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

    public Optional<Headshot> getHeadshot() {
        return Optional.ofNullable(headshot);
    }

    public Optional<RealmList<SocialLink>> getSocialLinks() {
        return Optional.of(socialLinks);
    }

}