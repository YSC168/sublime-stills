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

package com.aashreys.walls.domain.display.sources;

import android.support.annotation.NonNull;

import com.aashreys.walls.domain.display.images.Image;

import java.io.IOException;
import java.util.List;

/**
 * Created by aashreys on 03/03/17.
 */

public class DiscoverSource implements Source {

    private final FlickrRecentSource flickRecentSource;

    private final UnsplashRecentSource unsplashRecentSource;

    public DiscoverSource(FlickrRecentSource source, UnsplashRecentSource unsplashRecentSource) {
        this.flickRecentSource = source;
        this.unsplashRecentSource = unsplashRecentSource;
    }

    @NonNull
    @Override
    public List<Image> getImages(int fromIndex) throws IOException {
        // TODO: Integrate both image sources
        return unsplashRecentSource.getImages(fromIndex);
    }
}
