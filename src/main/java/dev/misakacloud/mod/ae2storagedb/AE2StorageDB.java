package dev.misakacloud.mod.ae2storagedb;

import appeng.api.networking.GridServices;
import appeng.api.storage.StorageCells;
import appeng.client.gui.style.StyleManager;
import com.mojang.logging.LogUtils;
import dev.misakacloud.mod.ae2storagedb.init.InitScreens;
import dev.misakacloud.mod.ae2storagedb.init.Registration;
import dev.misakacloud.mod.ae2storagedb.storage.DBCellHandler;
import dev.misakacloud.mod.ae2storagedb.storage.DBStorageService;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AE2StorageDB.MOD_ID)
public class AE2StorageDB {

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "ae2_storage_db";

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(AE2StorageDB.MOD_ID, path);
    }

    public AE2StorageDB() {
        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // 注册一下自定义 Handler
        StorageCells.addCellHandler(DBCellHandler.INSTANCE);
        GridServices.register(DBStorageService.class,DBStorageService.class);
        // 注册一些东西
        eventBus.addGenericListener(Item.class, Registration::registerItems);
        eventBus.addGenericListener(Block.class, Registration::registerBlocks);
        eventBus.addGenericListener(BlockEntityType.class, Registration::registerBlockEntities);
        eventBus.addListener(this::clientSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }


    private void postClientSetup(Minecraft minecraft) {
        StyleManager.initialize(minecraft.getResourceManager());
        LOGGER.info("现在初始化菜单屏幕");
        InitScreens.init();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("现在进入客户端设定");
        event.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            postClientSetup(minecraft);
        });
    }


}
