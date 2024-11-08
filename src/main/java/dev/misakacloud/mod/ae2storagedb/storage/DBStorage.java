package dev.misakacloud.mod.ae2storagedb.storage;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.me.service.SecurityService;
import appeng.me.storage.NetworkStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DBStorage implements MEStorage {
    // 模拟操作和实际操作的队列
    private static final ThreadLocal<Deque<DBStorage>> DEPTH_MOD = new ThreadLocal<>();
    private static final ThreadLocal<Deque<DBStorage>> DEPTH_SIM = new ThreadLocal<>();

    /**
     * 挂载是否在被使用,可以作为一种锁
     */
    private boolean mountsInUse;
    /**
     * 当前遍历深度
     */
    private static int currentPass = 0;
    /**
     * 已完成的遍历深度
     */
    private int myPass = 0;
    private final SecurityService security;

    /**
     * 操作队列
     */
    @Nullable
    private List<DBStorage.QueuedOperation> queuedOperations;

    public DBStorage(SecurityService security) {
        this.security = security;
    }


    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        return MEStorage.super.insert(what, amount, mode, source);
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        return MEStorage.super.extract(what, amount, mode, source);
    }



    @Override
    public Component getDescription() {
        return new TranslatableComponent("dbstorage.description");
    }

    sealed interface QueuedOperation permits DBStorage.MountOperation, DBStorage.UnmountOperation {
    }

    private record MountOperation(int priority, MEStorage storage) implements DBStorage.QueuedOperation {
    }

    private record UnmountOperation(MEStorage storage) implements DBStorage.QueuedOperation {
    }

    /**
     * 此处我们从数据库里把所有东西拉出来
     * @param out The amounts for all available keys will be added to this tally.
     */
    @Override
    public void getAvailableStacks(KeyCounter out) {
        // 生成一个固定的 橡木
        AEKey key = AEItemKey.of(Items.OAK_LOG);
        out.add(key,100);
    }



}
