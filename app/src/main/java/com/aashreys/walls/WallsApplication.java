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

package com.aashreys.walls;

import android.app.Application;

import com.aashreys.walls.di.ApplicationComponent;
import com.aashreys.walls.di.DaggerApplicationComponent;
import com.aashreys.walls.di.modules.ApiModule;
import com.aashreys.walls.di.modules.ApplicationModule;
import com.aashreys.walls.persistence.WallsDatabase;
import com.aashreys.walls.utils.LogWrapper;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;

/**
 * Created by aashreys on 30/11/16.
 */

public class WallsApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable crash reporting for production only.
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(!BuildConfig.PRODUCTION).build())
                .build();
        Fabric.with(this, crashlyticsKit);
        LogWrapper.setProductionLogging(BuildConfig.PRODUCTION);

        // Delete old SQL database since we've switched to NoSql
        deleteDatabase(WallsDatabase.NAME);

        Paper.init(this);
        this.applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .apiModule(new ApiModule(this))
                .build();
        this.applicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

}
