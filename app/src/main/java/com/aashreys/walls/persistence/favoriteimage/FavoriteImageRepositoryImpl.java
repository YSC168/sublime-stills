package com.aashreys.walls.persistence.favoriteimage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aashreys.walls.domain.display.images.Image;
import com.aashreys.walls.domain.display.images.ImageImpl;
import com.aashreys.walls.domain.values.Name;
import com.aashreys.walls.domain.values.Pixel;
import com.aashreys.walls.domain.values.ServerId;
import com.aashreys.walls.domain.values.Url;
import com.aashreys.walls.persistence.RepositoryCallback;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aashreys on 06/12/16.
 */

public class FavoriteImageRepositoryImpl implements FavoriteImageRepository {

    private static final String TAG = FavoriteImageRepositoryImpl.class.getSimpleName();

    private static List<RepositoryCallback<Image>> listeners = new ArrayList<>();

    @Override
    public void favorite(Image image) {
        FavoriteImageModel model = _getModel(image);
        if (model == null) {
            model = new FavoriteImageModel(image);
            model.insert();
            _notifyFavorited(image);
        }
    }

    @Override
    public void unfavorite(Image image) {
        FavoriteImageModel model = _getModel(image);
        if (model != null) {
            model.delete();
            _notifyUnfavorited(image);
        }
    }

    @Override
    public boolean isFavorite(Image image) {
        return _getModel(image) != null;
    }

    @Override
    public void addListener(RepositoryCallback<Image> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(RepositoryCallback<Image> listener) {
        listeners.remove(listener);
    }

    @NonNull
    @Override
    public List<Image> get(int fromIndex, int count) {
        List<Image> images = new ArrayList<>();
        List<FavoriteImageModel> models = SQLite.select()
                .from(FavoriteImageModel.class)
                .orderBy(FavoriteImageModel_Table.localId, false)
                .offset(fromIndex)
                .limit(count)
                .queryList();
        if (models.size() > 0) {
            for (FavoriteImageModel model : models) {
                Image image = createFromModel(model);
                images.add(image);
            }
        }
        return images;
    }

    private void _notifyUnfavorited(Image image) {
        if (listeners.size() > 0) {
            for (RepositoryCallback<Image> listener : listeners) {
                listener.onDelete(image);
            }
        }
    }

    @Nullable
    private FavoriteImageModel _getModel(Image image) {
        return SQLite.select()
                .from(FavoriteImageModel.class)
                .where(FavoriteImageModel_Table.serverId.is(image.serverId().value()))
                .and(FavoriteImageModel_Table.serviceName.is(image.serviceName().value()))
                .querySingle();
    }

    private void _notifyFavorited(Image image) {
        if (listeners.size() > 0) {
            for (RepositoryCallback<Image> listener : listeners) {
                listener.onInsert(image);
            }
        }
    }

    private Image createFromModel(FavoriteImageModel model) {
        return new ImageImpl(
                new ServerId(model.getServerId()),
                new Pixel(model.getResX()),
                new Pixel(model.getResY()),
                model.getCreatedAt(),
                new Name(model.getPhotographerName()),
                new Url(model.getUserProfileUrl()),
                new Url(model.getUserPortfolioUrl()),
                new Url(model.getSmallImageUrl()),
                new Url(model.getRegularImageUrl()),
                new Url(model.getFullImageUrl()),
                new Name(model.getServiceName()),
                new Url(model.getServiceUrl())
        );
    }
}