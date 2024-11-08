package dev.misakacloud.mod.ae2storagedb.storage;

import appeng.api.stacks.AEKey;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.util.ConfigInventory;
import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface IDBCellItem extends ICellWorkbenchItem {
    default boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        return false;
    }

    /**
     * 是否允许嵌套储存在其他单元 <br>
     * 干脆禁止
     *
     * @return
     */
    default boolean storableInStorageCell() {
        return false;
    }

    default boolean isStorageCell(ItemStack i) {
        return true;
    }

    default double getIdleDrain() {
        return 0d;
    }

    ConfigInventory getConfigInventory(ItemStack is);
    /**
     * Convenient helper to append useful tooltip information.
     */
    default void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        DBCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }

    /**
     * Helper to get the additional tooltip image line showing the content/filter/upgrades.
     */
    default Optional<TooltipComponent> getCellTooltipImage(ItemStack is) {
        Preconditions.checkArgument(is.getItem() == this);
        return DBCellHandler.INSTANCE.getTooltipImage(is);
    }

}
