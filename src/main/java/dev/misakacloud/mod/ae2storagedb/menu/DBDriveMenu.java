package dev.misakacloud.mod.ae2storagedb.menu;

import appeng.blockentity.storage.DriveBlockEntity;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.DriveMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import dev.misakacloud.mod.ae2storagedb.blocks.entity.DBDriveBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class DBDriveMenu extends AEBaseMenu {
    public static final MenuType<DBDriveMenu> TYPE = MenuTypeBuilder
            .create(DBDriveMenu::new, DBDriveBlockEntity.class)
            .build("db_drive");

    public DBDriveMenu(MenuType<?> menuType, int id, Inventory ip, DBDriveBlockEntity drive) {
        super(TYPE, id, ip, drive);
        // 在这里实现 GUI

        for (int i = 0; i < 10; i++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.STORAGE_CELLS,
                    drive.getInternalInventory(), i), SlotSemantics.STORAGE_CELL);
        }

        this.createPlayerInventorySlots(ip);
    }
}
