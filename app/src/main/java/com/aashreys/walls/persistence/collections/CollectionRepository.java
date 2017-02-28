package com.aashreys.walls.persistence.collections;

import android.support.annotation.NonNull;

import com.aashreys.walls.domain.display.collections.Collection;
import com.aashreys.walls.persistence.RepositoryCallback;

import java.util.List;

/**
 * Created by aashreys on 31/01/17.
 */

public interface CollectionRepository {

    void insert(Collection collection);

    boolean exists(Collection collection);

    void update(Collection collection);

    void remove(Collection collection);

    void replace(List<Collection> collections);

    @NonNull
    List<Collection> getAll();

    void addListener(CollectionRepositoryListener listener);

    void removeListener(CollectionRepositoryListener listener);

    interface CollectionRepositoryListener extends RepositoryCallback<Collection> {

        void onReplace(List<Collection> collections);

    }

}