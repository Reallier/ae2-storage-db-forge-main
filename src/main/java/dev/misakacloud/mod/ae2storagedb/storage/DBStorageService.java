/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package dev.misakacloud.mod.ae2storagedb.storage;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.networking.security.ISecurityService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.storage.IStorageWatcherNode;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.me.helpers.InterestManager;
import appeng.me.helpers.StackWatcher;
import appeng.me.service.SecurityService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.IdentityHashMap;
import java.util.Map;

public class DBStorageService implements IStorageService, IGridServiceProvider {


    private final SetMultimap<AEKey, StackWatcher<IStorageWatcherNode>> interests = HashMultimap.create();
    private final InterestManager<StackWatcher<IStorageWatcherNode>> interestManager = new InterestManager<>(
            this.interests);
    private final DBStorage storage;
    /**
     * Publicly exposed cached available stacks.
     */
    private KeyCounter cachedAvailableStacks = new KeyCounter();
    private KeyCounter cachedAvailableStacksBackBuffer = new KeyCounter();
    /**
     * Private cached amounts, to ensure that we send correct change notifications even if
     * {@link #cachedAvailableStacks} is modified by mistake.
     */
    private final Object2LongMap<AEKey> cachedAvailableAmounts = new Object2LongOpenHashMap<>();
    private boolean cachedStacksNeedUpdate = true;
    /**
     * Tracks the stack watcher associated with a given grid node. Needed to clean up watchers when the node leaves the
     * grid.
     */
    private final Map<IGridNode, StackWatcher<IStorageWatcherNode>> watchers = new IdentityHashMap<>();

    public DBStorageService(ISecurityService security) {
        this.storage = new DBStorage((SecurityService) security);
    }

    @Override
    public void onServerEndTick() {
        if (interestManager.isEmpty()) {
            // lazily rebuild cache list
            cachedStacksNeedUpdate = true;
        } else {
            // we need to rebuild the cache every tick to notify listeners
            updateCachedStacks();
        }
    }

    private void updateCachedStacks() {
        cachedStacksNeedUpdate = false;

        // Update cache
        var previousStacks = cachedAvailableStacks;
        var currentStacks = cachedAvailableStacksBackBuffer;
        cachedAvailableStacks = currentStacks;
        cachedAvailableStacksBackBuffer = previousStacks;

        currentStacks.clear();
        storage.getAvailableStacks(currentStacks);

        // Post watcher update for currently available stacks
        for (var entry : currentStacks) {
            var what = entry.getKey();
            var newAmount = entry.getLongValue();
            if (newAmount != cachedAvailableAmounts.getLong(what)) {
                postWatcherUpdate(what, newAmount);
            }
        }
        // Post watcher update for removed stacks
        for (var entry : cachedAvailableAmounts.object2LongEntrySet()) {
            var what = entry.getKey();
            var newAmount = currentStacks.get(what);
            if (newAmount == 0) {
                postWatcherUpdate(what, newAmount);
            }
        }

        // Update private amounts
        cachedAvailableAmounts.clear();
        for (var entry : currentStacks) {
            cachedAvailableAmounts.put(entry.getKey(), entry.getLongValue());
        }
    }

    private void postWatcherUpdate(AEKey what, long newAmount) {
        for (var watcher : interestManager.get(what)) {
            watcher.getHost().onStackChange(what, newAmount);
        }
        for (var watcher : interestManager.getAllStacksWatchers()) {
            watcher.getHost().onStackChange(what, newAmount);
        }
    }

    /**
     * When a node joins the grid, we automatically register provided {@link IStorageProvider} and
     * {@link IStorageWatcherNode}.
     */
    @Override
    public void addNode(IGridNode node) {

    }

    /**
     * When a node leaves the grid, we automatically unregister the previously registered {@link IStorageProvider} or
     * {@link IStorageWatcherNode}.
     */
    @Override
    public void removeNode(IGridNode node) {

    }

    @Override
    public MEStorage getInventory() {
        return storage;
    }

    @Override
    public KeyCounter getCachedInventory() {
        if (cachedStacksNeedUpdate) {
            updateCachedStacks();
        }
        return cachedAvailableStacks;
    }

    @Override
    public void addGlobalStorageProvider(IStorageProvider provider) {

    }

    @Override
    public void removeGlobalStorageProvider(IStorageProvider provider) {

    }

    @Override
    public void refreshNodeStorageProvider(IGridNode node) {

    }

    @Override
    public void refreshGlobalStorageProvider(IStorageProvider provider) {

    }

    @Override
    public void invalidateCache() {

    }


}
