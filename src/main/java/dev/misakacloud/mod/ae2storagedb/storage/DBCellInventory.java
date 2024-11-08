package dev.misakacloud.mod.ae2storagedb.storage;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.AELog;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.FuzzyPriorityList;
import appeng.util.prioritylist.IPartitionList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.Objects;

public class DBCellInventory implements StorageCell {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ITEM_COUNT_TAG = "item_count";
    private static final String STACK_KEYS = "keys";
    private static final String STACK_AMOUNTS = "amounts";

    private final ISaveProvider container;
    private IPartitionList partitionList;
    private IncludeExclude partitionListMode;
    private int maxItemTypes;
    private short storedItems;
    private long storedItemCount;
    private Object2LongMap<AEKey> storedAmounts;
    private final ItemStack i;
    private final IDBCellItem cellType;
    private final long maxItemsPerType; // max items per type, basically infinite unless there is a distribution card.
    private boolean isPersisted = true;

    private DBCellInventory(IDBCellItem cellType, ItemStack o, ISaveProvider container) {
        this.i = o;
        this.cellType = cellType;
        // 暂时设定为最大
        this.maxItemTypes = Integer.MAX_VALUE;

        if (this.maxItemTypes < 1) {
            this.maxItemTypes = 1;
        }

        this.container = container;
        this.storedItems = (short) getTag().getLongArray(STACK_AMOUNTS).length;
        this.storedItemCount = getTag().getLong(ITEM_COUNT_TAG);
        this.storedAmounts = null;

        // Updates the partition list and mode based on installed upgrades and the configured filter.
        var builder = IPartitionList.builder();

        var upgrades = getUpgradesInventory();
        var config = getConfigInventory();

        boolean hasInverter = upgrades.isInstalled(AEItems.INVERTER_CARD);
        boolean isFuzzy = upgrades.isInstalled(AEItems.FUZZY_CARD);
        if (isFuzzy) {
            builder.fuzzyMode(getFuzzyMode());
        }

        builder.addAll(config.keySet());

        partitionListMode = (hasInverter ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST);
        partitionList = builder.build();


        this.maxItemsPerType = Long.MAX_VALUE;
    }

    public IncludeExclude getPartitionListMode() {
        return partitionListMode;
    }

    public boolean isPreformatted() {
        return !partitionList.isEmpty();
    }

    public boolean isFuzzy() {
        return partitionList instanceof FuzzyPriorityList;
    }

    private CompoundTag getTag() {
        // On Fabric, the tag itself may be copied and then replaced later in case a portable cell is being charged.
        // In that case however, we can rely on the itemstack reference not changing due to the special logic in the
        // transactional inventory wrappers. So we must always re-query the tag from the stack.
        return this.i.getOrCreateTag();
    }

    public static DBCellInventory createInventory(ItemStack o, ISaveProvider container) {
        Objects.requireNonNull(o, "Cannot create cell inventory for null itemstack");

        if (!(o.getItem() instanceof IDBCellItem cellType)) {
            return null;
        }

        if (!cellType.isStorageCell(o)) {
            // This is not an error. Items may decide to not be a storage cell temporarily.
            return null;
        }

        // The cell type's channel matches, so this cast is safe
        return new DBCellInventory(cellType, o, container);
    }

    public static boolean isCell(ItemStack input) {
        LOGGER.info("判断有没有标签");
        // 检测 ItemStack 中是否有对应的 NBT 标签
        // 名字为 UUID 的 NBT 标签
        if (getStorageCell(input) == null) {
            return false;
        }
        if (input.isEmpty()) {
            return false;
        }
        if (!input.hasTag()) {
            return false;
        }
        LOGGER.info("UUID 检测为 {}", input.getTag().contains("UUID"));
        // return input.getTag().contains("UUID");
        return true;

    }

    private boolean isStorageCell(AEItemKey key) {
        var type = getStorageCell(key);
        return type != null && !type.storableInStorageCell();
    }

    private static IDBCellItem getStorageCell(ItemStack input) {
        if (input != null && input.getItem() instanceof IDBCellItem idbCellItem) {
            return idbCellItem;
        }

        return null;
    }

    private static IDBCellItem getStorageCell(AEItemKey itemKey) {
        if (itemKey.getItem() instanceof IDBCellItem cellItem) {
            return cellItem;
        }

        return null;
    }

    private static boolean isCellEmpty(DBCellInventory inv) {
        if (inv != null) {
            return inv.getAvailableStacks().isEmpty();
        }
        return true;
    }

    protected Object2LongMap<AEKey> getCellItems() {
        if (this.storedAmounts == null) {
            this.storedAmounts = new Object2LongOpenHashMap<>();
            this.loadCellItems();
        }

        return this.storedAmounts;
    }

    @Override
    public void persist() {
        if (this.isPersisted) {
            return;
        }

        long itemCount = 0;

        // add new pretty stuff...
        var amounts = new LongArrayList(storedAmounts.size());
        var keys = new ListTag();

        for (var entry : this.storedAmounts.object2LongEntrySet()) {
            long amount = entry.getLongValue();

            if (amount > 0) {
                itemCount += amount;
                keys.add(entry.getKey().toTagGeneric());
                amounts.add(amount);
            }
        }

        if (keys.isEmpty()) {
            getTag().remove(STACK_KEYS);
            getTag().remove(STACK_AMOUNTS);
        } else {
            getTag().put(STACK_KEYS, keys);
            getTag().putLongArray(STACK_AMOUNTS, amounts.toArray(new long[0]));
        }

        this.storedItems = (short) this.storedAmounts.size();

        this.storedItemCount = itemCount;
        if (itemCount == 0) {
            getTag().remove(ITEM_COUNT_TAG);
        } else {
            getTag().putLong(ITEM_COUNT_TAG, itemCount);
        }

        this.isPersisted = true;
    }

    protected void saveChanges() {
        // recalculate values
        this.storedItems = (short) this.storedAmounts.size();
        this.storedItemCount = 0;
        for (var storedAmount : this.storedAmounts.values()) {
            this.storedItemCount += storedAmount;
        }

        this.isPersisted = false;
        if (this.container != null) {
            this.container.saveChanges();
        } else {
            // if there is no ISaveProvider, store to NBT immediately
            this.persist();
        }
    }

    private void loadCellItems() {
        boolean corruptedTag = false;

        var amounts = getTag().getLongArray(STACK_AMOUNTS);
        var tags = getTag().getList(STACK_KEYS, Tag.TAG_COMPOUND);
        if (amounts.length != tags.size()) {
            AELog.warn("Loading storage cell with mismatched amounts/tags: %d != %d",
                    amounts.length, tags.size());
        }

        for (int i = 0; i < amounts.length; i++) {
            var amount = amounts[i];
            AEKey key = AEKey.fromTagGeneric(tags.getCompound(i));

            if (amount <= 0 || key == null) {
                corruptedTag = true;
            } else {
                storedAmounts.put(key, amount);
            }
        }

        if (corruptedTag) {
            this.saveChanges();
        }
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        for (var entry : this.getCellItems().object2LongEntrySet()) {
            out.add(entry.getKey(), entry.getLongValue());
        }
    }

    @Override
    public double getIdleDrain() {
        return this.cellType.getIdleDrain();
    }

    public FuzzyMode getFuzzyMode() {
        return this.cellType.getFuzzyMode(this.i);
    }

    public ConfigInventory getConfigInventory() {
        return this.cellType.getConfigInventory(this.i);
    }

    public IUpgradeInventory getUpgradesInventory() {
        return this.cellType.getUpgrades(this.i);
    }

    public int getBytesPerType() {
        return 1;
    }

    public boolean canHoldNewItem() {
        final long bytesFree = this.getFreeBytes();
        return (bytesFree > this.getBytesPerType()
                || bytesFree == this.getBytesPerType() && this.getUnusedItemCount() > 0)
                && this.getRemainingItemTypes() > 0;
    }

    public long getTotalBytes() {
        return Integer.MAX_VALUE;
    }

    public long getFreeBytes() {
        return this.getTotalBytes() - this.getUsedBytes();
    }

    public long getTotalItemTypes() {
        return this.maxItemTypes;
    }

    public long getStoredItemCount() {
        return this.storedItemCount;
    }

    public long getStoredItemTypes() {
        return this.storedItems;
    }

    public long getRemainingItemTypes() {
        var basedOnStorage = this.getFreeBytes() / this.getBytesPerType();
        var baseOnTotal = this.getTotalItemTypes() - this.getStoredItemTypes();
        return Math.min(basedOnStorage, baseOnTotal);
    }

    public long getUsedBytes() {
        return this.getStoredItemTypes() * this.getBytesPerType();
    }

    public long getRemainingItemCount() {
        final long remaining = this.getFreeBytes() + this.getUnusedItemCount();
        return remaining > 0 ? remaining : 0;
    }

    public int getUnusedItemCount() {


        return (int) (Integer.MAX_VALUE - this.getStoredItemCount());
    }

    @Override
    public CellState getStatus() {
        if (this.getStoredItemTypes() == 0) {
            return CellState.EMPTY;
        }
        if (this.canHoldNewItem()) {
            return CellState.NOT_EMPTY;
        }
        if (this.getRemainingItemCount() > 0) {
            return CellState.TYPES_FULL;
        }
        return CellState.FULL;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0) {
            return 0;
        }

        if (!this.partitionList.matchesFilter(what, this.partitionListMode)) {
            return 0;
        }

        if (this.cellType.isBlackListed(this.i, what)) {
            return 0;
        }

        // Run regular insert logic and then apply void upgrade to the returned value.
        long inserted = innerInsert(what, amount, mode, source);
        return inserted;
    }

    // Inner insert for items that pass the filter.
    private long innerInsert(AEKey what, long amount, Actionable mode, IActionSource source) {
        // This is slightly hacky as it expects a read-only access, but fine for now.
        // TODO: Guarantee a read-only access. E.g. provide an isEmpty() method and
        // ensure CellInventory does not write
        // any NBT data for empty cells instead of relying on an empty IAEStackList
        if (what instanceof AEItemKey itemKey && this.isStorageCell(itemKey)) {
            // TODO: make it work for any cell, and not just DBCellInventory!
            var meInventory = createInventory(itemKey.toStack(), null);
            if (!isCellEmpty(meInventory)) {
                return 0;
            }
        }

        var currentAmount = this.getCellItems().getLong(what);
        long remainingItemCount = this.getRemainingItemCount();

        // Deduct the required storage for a new type if the type is new
        if (currentAmount <= 0) {
            if (!canHoldNewItem()) {
                // No space for more types
                return 0;
            }

            remainingItemCount -= (long) this.getBytesPerType();
            if (remainingItemCount <= 0) {
                return 0;
            }
        }

        // Apply max items per type
        remainingItemCount = Math.max(0, Math.min(this.maxItemsPerType - currentAmount, remainingItemCount));

        if (amount > remainingItemCount) {
            amount = remainingItemCount;
        }

        if (mode == Actionable.MODULATE) {
            getCellItems().put(what, currentAmount + amount);
            this.saveChanges();
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var currentAmount = getCellItems().getLong(what);
        if (currentAmount > 0) {
            if (amount >= currentAmount) {
                if (mode == Actionable.MODULATE) {
                    getCellItems().remove(what, currentAmount);
                    this.saveChanges();
                }

                return currentAmount;
            } else {
                if (mode == Actionable.MODULATE) {
                    getCellItems().put(what, currentAmount - amount);
                    this.saveChanges();
                }

                return amount;
            }
        }

        return 0;
    }

    @Override
    public Component getDescription() {
        return i.getHoverName();
    }
}
