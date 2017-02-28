package com.aashreys.walls.domain.display.collections;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.aashreys.walls.domain.display.sources.FavoriteSource;
import com.aashreys.walls.domain.values.Name;
import com.aashreys.walls.domain.values.ServerId;

/**
 * Created by aashreys on 04/02/17.
 */

public class FavoriteCollection implements Collection<FavoriteSource>, Parcelable {

    public static final Parcelable.Creator<FavoriteCollection> CREATOR
            = new Parcelable.Creator<FavoriteCollection>() {
        @Override
        public FavoriteCollection createFromParcel(Parcel parcel) {
            return new FavoriteCollection(parcel);
        }

        @Override
        public FavoriteCollection[] newArray(int size) {return new FavoriteCollection[size];}
    };

    private static final ServerId ID   = new ServerId("1");
    private static final Name     NAME = new Name("Favorites");

    public FavoriteCollection() {

    }

    private FavoriteCollection(Parcel in) {

    }

    @NonNull
    @Override
    public ServerId id() {
        return ID;
    }

    @NonNull
    @Override
    public Name name() {
        return NAME;
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof FavoriteCollection)) { return false; }
        return true;

    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

}