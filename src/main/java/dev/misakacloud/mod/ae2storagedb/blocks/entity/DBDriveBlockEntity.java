package dev.misakacloud.mod.ae2storagedb.blocks.entity;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.util.AECableType;
import appeng.api.util.IOrientable;
import appeng.blockentity.grid.AENetworkInvBlockEntity;
import appeng.blockentity.inventory.AppEngCellInventory;
import appeng.helpers.IPriorityHost;
import appeng.menu.ISubMenu;
import dev.misakacloud.mod.ae2storagedb.definitions.DBSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class DBDriveBlockEntity extends AENetworkInvBlockEntity implements IPriorityHost, IStorageProvider, IOrientable, IActionHost {

    private boolean isCached = false;
    private int priority = 0;
    private boolean wasOnline = false;
    // This is only used on the client

    private boolean clientSideOnline;

    public DBDriveBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.getMainNode().addService(IStorageProvider.class, this).setFlags(GridFlags.REQUIRE_CHANNEL);

    }

    @Override
    public void setOrientation(Direction inForward, Direction inUp) {
        super.setOrientation(inForward, inUp);
        this.getMainNode().setExposedOnSides(EnumSet.complementOf(EnumSet.of(inForward)));
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);

    }

    @Override
    protected void saveVisualState(CompoundTag data) {
        super.saveVisualState(data);
        data.putBoolean("online", isPowered());

    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        var changed = super.readFromStream(data);


        return changed;
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);
        clientSideOnline = data.getBoolean("online");

    }

    public int getCellCount() {
        return 10;
    }

    @Nullable
    public Item getCellItem(int slot) {
        return null;
    }


    public boolean isPowered() {
        if (isClientSide()) {
            return clientSideOnline;
        }

        return this.getMainNode().isOnline();
    }

    public boolean isCellBlinking(int slot) {
        return false;
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.isCached = false;
        this.priority = data.getInt("priority");
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        // data.putInt("priority", this.priority);
    }


    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        var currentOnline = getMainNode().isOnline();
        if (this.wasOnline != currentOnline) {
            this.wasOnline = currentOnline;
            IStorageProvider.requestUpdate(getMainNode());
        }
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    /**
     * 获取这个方块内部可以存放的东西位置
     * 但其实我们不需要
     *
     * @return
     */
    @Override
    public InternalInventory getInternalInventory() {
        return new AppEngCellInventory(this, 1);
    }

    /**
     * 当里面塞的东西发生变化的时候调用 <br>
     * 在这里就是塞入或取出 Cell 时调用
     *
     * @param inv
     * @param slot
     */
    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        if (this.isCached) {
            this.isCached = false; // recalculate the storage cell.
        }

        IStorageProvider.requestUpdate(getMainNode());

        this.markForUpdate();
    }


    /**
     * 去计算每个指定 Cell 的耗电
     *
     * @param slot
     * @return
     */

    @Override
    public void onReady() {
        super.onReady();
    }

    /**
     * 挂载库存,这应该就是关键的获取储存内容的地方 <br>
     * 方块放入或取出都会触发
     *
     * @param storageMounts
     */
    @Override
    public void mountInventories(IStorageMounts storageMounts) {

    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int newValue) {
        this.priority = newValue;
        this.saveChanges();

        this.isCached = false; // recalculate the storage cell.

        IStorageProvider.requestUpdate(getMainNode());
    }


    public void getDebugInfo(Player p) {
        // 先检查这个方块是否存在 NBT Tag
        if (!this.getTileData().contains("uuid")) {
            p.displayClientMessage(new TextComponent("方块尚未加入 UUID"), false);
        }
        p.displayClientMessage(new TextComponent("UUID 为:" + this.getTileData().getString("uuid")), false);
    }


    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        // MenuOpener.returnTo(DBDriveMenu.TYPE, player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return DBSBlocks.DB_DRIVE.stack();
    }

}
