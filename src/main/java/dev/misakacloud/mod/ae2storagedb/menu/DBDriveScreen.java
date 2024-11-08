package dev.misakacloud.mod.ae2storagedb.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.DriveMenu;

public class DBDriveScreen extends AEBaseScreen<DBDriveMenu> {

    public DBDriveScreen(DBDriveMenu menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);

        widgets.addOpenPriorityButton();
    }

}
