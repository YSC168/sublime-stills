package com.aashreys.walls.domain.display.collections.search;

import android.support.annotation.NonNull;

import com.aashreys.walls.LogWrapper;
import com.aashreys.walls.domain.display.collections.Collection;
import com.aashreys.walls.domain.display.collections.FlickrTag;
import com.aashreys.walls.domain.values.Name;
import com.aashreys.walls.network.apis.FlickrApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by aashreys on 05/03/17.
 */

public class FlickrTagSearchService implements CollectionSearchService {

    private static final String TAG = FlickrTagSearchService.class.getSimpleName();

    private final FlickrApi flickrApi;

    @Inject
    public FlickrTagSearchService(FlickrApi flickrApi) {
        this.flickrApi = flickrApi;
    }

    @NonNull
    @Override
    public List<Collection> search(String tag) {
        List<Collection> tagList = new ArrayList<>();
        Call<ResponseBody> call = flickrApi.getRelatedTags(tag);
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                String responseString = response.body().string();
                responseString = responseString.substring(
                        responseString.indexOf('{'),
                        responseString.lastIndexOf('}') + 1
                );
                JSONArray tagJsonArray = new JSONObject(responseString)
                        .getJSONObject("tags")
                        .getJSONArray("tag");

                for (int i = 0; i < tagJsonArray.length(); i++) {
                    String tagString = tagJsonArray.getJSONObject(i).getString("_content");
                    FlickrTag flickrTag = new FlickrTag(new Name(tagString));
                    tagList.add(flickrTag);
                }
                if (tagList.size() > 0) {
                    tagList.add(0, new FlickrTag(new Name(tag)));
                }
            } else {
                LogWrapper.e(
                        TAG,
                        "Could not search flickr tags. Request failed with code " + response.code()
                );
            }
        } catch (IOException | JSONException e) {
            LogWrapper.e(TAG, "Could not search flickr tags", e);
        }
        return tagList;
    }
}