package dev.misakacloud.mod.ae2storagedb.init;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.menu.AEBaseMenu;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import dev.misakacloud.mod.ae2storagedb.menu.DBDriveMenu;
import dev.misakacloud.mod.ae2storagedb.menu.DBDriveScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;

import java.util.IdentityHashMap;
import java.util.Map;

public class InitScreens {
    private static final Logger LOGGER = LogUtils.getLogger();
    @VisibleForTesting
    static final Map<MenuType<?>, String> MENU_STYLES = new IdentityHashMap<>();

    public static void init() {
        register(DBDriveMenu.TYPE, DBDriveScreen::new, "/screens/db_drive.json");
    }


    private InitScreens() {
    }

    /**
     * Registers a screen for a given menu and ensures the given style is applied after opening the screen.
     */
    public static <M extends AEBaseMenu, U extends AEBaseScreen<M>> void register(MenuType<M> type,
                                                                                  StyledScreenFactory<M, U> factory,
                                                                                  String stylePath) {
        LOGGER.info("注册菜单屏幕: {} with style {}", type, stylePath);
        MENU_STYLES.put(type, stylePath);
        // MenuScreens.register(type, (menu, playerInv, title) -> {
        //     LOGGER.info("将加载 {} 样式", stylePath);
        //     var style = StyleManager.loadStyleDoc(stylePath);
        //
        //     return factory.create(menu, playerInv, title, style);
        // });
        MenuScreens.<M, U>register(type, (menu, playerInv, title) -> {
            LOGGER.info("将加载 {} 样式", stylePath);
            var style = StyleManager.loadStyleDoc(stylePath);

            return factory.create(menu, playerInv, title, style);
        });
    }

    /**
     * A type definition that matches the constructors of our screens, which take an additional {@link ScreenStyle}
     * argument.
     */
    @FunctionalInterface
    public interface StyledScreenFactory<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T t, Inventory pi, Component title, ScreenStyle style);
    }

}
