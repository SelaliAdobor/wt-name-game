package com.willowtree.namegame.profiles;

import com.willowtree.namegame.api.profiles.Profile;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.realm.Realm;
import timber.log.Timber;

public class RealmProfileRepository implements ProfileRepository {

    private Realm realm;

    public RealmProfileRepository(Realm realm) {
        this.realm = realm;
    }

    @Override
    public Completable setProfiles(List<Profile> profiles) {
        return Completable.create((emitter) -> {
            realm.executeTransactionAsync(transaction -> {

                transaction.delete(Profile.class);
                transaction.copyToRealm(profiles);

                Timber.i("Wrote %d profiles to Realm", profiles.size());

                emitter.onComplete();
            }, emitter::onError);
        });
    }

    @Override
    public Single<Boolean> hasProfiles() {
        return Single.create(emitter -> {
            realm.executeTransactionAsync(transaction -> {
                boolean hasProfiles = transaction.where(Profile.class).count() > 0;
                emitter.onSuccess(hasProfiles);
            }, emitter::onError);
        });
    }
}
