package com.willowtree.namegame.profiles;

import com.willowtree.namegame.api.profiles.Profile;
import com.willowtree.namegame.util.RealmUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import java9.util.stream.Collectors;
import timber.log.Timber;

import static java9.util.stream.StreamSupport.stream;

public class RealmProfileRepository implements ProfileRepository {

    private Random random = new Random();
    private Realm realm;

    public RealmProfileRepository(Realm realm) {
        this.realm = realm;
    }

    @Override
    public Completable setProfiles(List<Profile> profiles) {
        return Completable.create((emitter) ->
                realm.executeTransactionAsync(transaction -> {

                    transaction.delete(Profile.class);
                    transaction.copyToRealm(profiles);

                    Timber.i("Wrote %d profiles to Realm", profiles.size());

                    emitter.onComplete();
                }, emitter::onError));
    }

    @Override
    public Single<Boolean> hasProfiles() {
        return Single.create(emitter ->
                realm.executeTransactionAsync(transaction -> {
                    boolean hasProfiles = transaction.where(Profile.class).count() > 0;
                    emitter.onSuccess(hasProfiles);
                }, emitter::onError));
    }

    @Override
    public Single<Profile> getById(String profileId) {
        return Single.<Profile>create(emitter ->
                realm.executeTransactionAsync(transaction ->
                                emitter.onSuccess(
                                        transaction.copyFromRealm(transaction.where(Profile.class)
                                                .equalTo("id", profileId)
                                                .findFirst()
                                        )
                                )
                        , emitter::onError)
        ).subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<List<Profile>> getRandomProfiles(int count, boolean needHeadshots, String... filteredHeadshotUrls) {
        return Single.<List<Profile>>create(emitter ->
                realm.executeTransactionAsync(transaction -> {

                    RealmResults<Profile> profiles = needHeadshots ?
                            RealmUtil.excludeFieldValue(
                                    transaction.where(Profile.class)
                                            .isNotNull("headshot")
                                            .isNotNull("headshot.url"), "headshot.url", filteredHeadshotUrls)
                                    .findAll() :
                            transaction.where(Profile.class).findAll();


                    List<Profile> randomProfiles = new ArrayList<>();

                    while (randomProfiles.size() < count) {
                        Profile randomProfile = profiles.get(random.nextInt(profiles.size()));
                        if (!randomProfiles.contains(randomProfile)) {
                            randomProfiles.add(randomProfile);
                        }
                    }

                    emitter.onSuccess(
                            stream(randomProfiles)
                                    .map(transaction::copyFromRealm)
                                    .collect(Collectors.toList())
                    );

                }, emitter::onError)
        ).subscribeOn(AndroidSchedulers.mainThread());
    }
}
