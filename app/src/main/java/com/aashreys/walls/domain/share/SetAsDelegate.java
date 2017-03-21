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

package com.aashreys.walls.domain.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.aashreys.walls.R;
import com.aashreys.walls.domain.device.DeviceResolution;
import com.aashreys.walls.domain.display.images.Image;
import com.aashreys.walls.domain.values.Url;
import com.aashreys.walls.ui.helpers.GlideHelper;
import com.aashreys.walls.ui.utils.UiHandler;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

/**
 * Created by aashreys on 05/12/16.
 */

class SetAsDelegate implements ShareDelegate {

    private static final String TAG = SetAsDelegate.class.getSimpleName();

    private static final String MIME_TYPE = "image/*";

    private boolean isCancelled;

    private UiHandler uiHandler = new UiHandler();

    private final DeviceResolution deviceResolution;

    public SetAsDelegate(DeviceResolution deviceResolution) {
        this.deviceResolution = deviceResolution;
    }

    @Override
    public void share(final Context context, final Image image, final Listener listener) {
        isCancelled = false;
        final Url imageUrl = image.getUrl(deviceResolution.getPortraitWidth() * 2);
        GlideHelper.downloadImageAsync(
                context,
                imageUrl,
                new SimpleTarget<File>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onShareFailed();
                            }
                        });
                    }

                    @Override
                    public void onResourceReady(
                            File resource, GlideAnimation<? super File> glideAnimation
                    ) {
                        if (!isCancelled) {
                            // Glide has been configured to use an external cache so that cached
                            // images are shareable by default. See {@link @GlideConfiguration}.
                            Uri imageUri = Uri.fromFile(resource);
                            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setDataAndType(imageUri, MIME_TYPE);
                            intent.putExtra("mimeType", MIME_TYPE);
                            context.startActivity(Intent.createChooser(
                                    intent,
                                    context.getString(R.string.share_set_as_title)
                            ));
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onShareComplete();
                                }
                            });
                        }
                    }
                }
        );
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }
}