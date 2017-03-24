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

package com.aashreys.walls.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;

import com.aashreys.walls.R;
import com.aashreys.walls.domain.display.collections.Collection;
import com.aashreys.walls.persistence.collections.CollectionRepository;
import com.aashreys.walls.ui.adapters.CollectionsAdapter;
import com.aashreys.walls.ui.views.CollectionView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by aashreys on 21/02/17.
 */

public class CollectionsActivity extends BaseActivity implements CollectionsAdapter.DragListener {

    private static final String TAG = CollectionsActivity.class.getSimpleName();

    @Inject CollectionRepository collectionRepository;

    private CollectionsAdapter adapter;

    private CollectionRepository.CollectionRepositoryListener repositoryCallback;

    private ItemTouchHelper itemTouchHelper;

    private Snackbar dragHintSnackbar;

    private RecyclerView collectionRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        getUiComponent().inject(this);
        this.repositoryCallback = new CollectionRepository.CollectionRepositoryListener() {
            @Override
            public void onReplaceAll(List<Collection> collections) {

            }

            @Override
            public void onInsert(Collection object) {
                adapter.add(object);
            }

            @Override
            public void onUpdate(Collection object) {

            }

            @Override
            public void onDelete(Collection object) {
                adapter.remove(object);
            }
        };
        collectionRecyclerView = (RecyclerView) findViewById(R.id.list_image_sources);
        collectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CollectionsAdapter();
        adapter.setOnCollectionClickListener(new CollectionsAdapter.OnCollectionClickListener() {
            @Override
            public void onClick(CollectionView view, int position) {
                saveCollections();
                finish();
                Context context = view.getContext();
                context.startActivity(StreamActivity.createLaunchIntent(
                        view.getContext(),
                        position
                ));
            }
        });
        adapter.setDragListener(this);
        collectionRecyclerView.setAdapter(adapter);
        adapter.add(collectionRepository.getAll());
        collectionRepository.addListener(repositoryCallback);

        ItemTouchHelper.Callback callback = new CollectionsAdapter.ItemMoveHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(collectionRecyclerView);

        ImageButton closeButton = (ImageButton) findViewById(R.id.button_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton addCollectionButton = (ImageButton) findViewById(R.id.button_add_collection);
        addCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddCollectionsActivity.createLaunchIntent(CollectionsActivity.this, false));
            }
        });
    }

    private void saveCollections() {
        if (adapter.isCollectionListModified()) {
            collectionRepository.replaceAll(adapter.getCollectionList());
        }
        adapter.onCollectionsSaved();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        collectionRepository.removeListener(repositoryCallback);
        if (adapter.isCollectionListModified()) {
            collectionRepository.replaceAll(adapter.getCollectionList());
        }
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
        dragHintSnackbar = Snackbar.make(
                collectionRecyclerView,
                R.string.hint_reorder_collection,
                BaseTransientBottomBar.LENGTH_INDEFINITE
        );
        dragHintSnackbar.show();
    }

    @Override
    public void onDragFinished() {
        if (dragHintSnackbar != null) {
            dragHintSnackbar.dismiss();
        }
    }
}
