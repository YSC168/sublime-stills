/*
 * Copyright {2017} {Aashrey Kamal Sharma}
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.aashreys.walls.network.apis;

import com.aashreys.safeapi.SafeApi;
import com.aashreys.walls.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * A class for creating implementations of Retrofit api interfaces.
 * Created by aashreys on 04/03/17.
 */

public class ApiInstanceCreator {

    private static final String TAG = ApiInstanceCreator.class.getSimpleName();

    public UnsplashApi createUnsplashApi(OkHttpClient client, String baseUrl) {
        return new Retrofit.Builder()
                .client(client.newBuilder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request originalRequest = chain.request();
                                // Add api headers
                                Request requestWithHeaders = originalRequest.newBuilder()
                                        .addHeader(
                                                "Authorization",
                                                "Client-ID " +
                                                        SafeApi.decrypt(BuildConfig
                                                                .UNSPLASH_API_KEY)
                                        )
                                        .addHeader("Accept-Version", UnsplashApi.API_VERSION)
                                        .build();
                                return chain.proceed(requestWithHeaders);
                            }
                        })
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                Response response = chain.proceed(request);
                                String requestUrl = request.url().toString();
                                String header = "public, " + UnsplashApi.GENERAL_CACHE_DURATION;
                                if (requestUrl.contains("photos/")) {
                                    header = "public, " + UnsplashApi.PHOTO_INFO_CACHE_DURATION;
                                }
                                if (requestUrl.contains("/collections/featured")) {
                                    header = "public, " + UnsplashApi.FEATURED_COLLECTION_CACHE_DURATION;
                                }
                                return response.newBuilder()
                                        .header("Cache-Control", header)
                                        .build();
                            }
                        })
                        .build())
                .validateEagerly(true)
                .baseUrl(baseUrl)
                .build()
                .create(UnsplashApi.class);
    }

    public UrlShortenerApi createUrlShortenerApi(OkHttpClient client, String baseUrl) {
        return new Retrofit.Builder()
                .client(client.newBuilder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                // Add api key
                                request = request.newBuilder().url(
                                        request.url().toString() + "?key=" +
                                                SafeApi.decrypt(BuildConfig.GOOGL_API_KEY)).build();
                                return chain.proceed(request);
                            }
                        })
                        .build()
                )
                .validateEagerly(true)
                .baseUrl(baseUrl)
                .build()
                .create(UrlShortenerApi.class);
    }

    public FlickrApi createFlickrApi(OkHttpClient client, String baseUrl) {
        return new Retrofit.Builder()
                .client(client.newBuilder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                // Add format and api key params
                                Request request = chain.request();
                                request = request.newBuilder()
                                        .url(
                                                request.url().toString() + "&format=json&api_key=" +
                                                        SafeApi.decrypt(BuildConfig.FLICKR_API_KEY)
                                        ).build();
                                return chain.proceed(request);

                            }
                        })
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                Response response = chain.proceed(request);
                                return response.newBuilder().header(
                                        "Cache-Control",
                                        "public, " +
                                                (request.url()
                                                        .toString()
                                                        .contains("flickr.photos.getInfo") ?
                                                        FlickrApi.PHOTO_INFO_CACHE_CONTROL :
                                                        FlickrApi.GENERAL_CACHE_CONTROL
                                                )
                                ).build();
                            }
                        })
                        .build())
                .validateEagerly(true)
                .baseUrl(baseUrl)
                .build()
                .create(FlickrApi.class);
    }

}
