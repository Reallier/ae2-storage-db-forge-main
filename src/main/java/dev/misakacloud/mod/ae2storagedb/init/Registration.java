package dev.misakacloud.mod.ae2storagedb.init;

import com.mojang.logging.LogUtils;
import dev.misakacloud.mod.ae2storagedb.definitions.DBSBlockEntities;
import dev.misakacloud.mod.ae2storagedb.definitions.DBSBlocks;
import dev.misakacloud.mod.ae2storagedb.definitions.DBSItems;
import dev.misakacloud.mod.ae2storagedb.menu.DBDriveMenu;
import dev.misakacloud.mod.ae2storagedb.menu.DBDriveScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.slf4j.Logger;

public class Registration {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void registerItems(RegistryEvent.Register<Item> event) {
        initItems(event.getRegistry());
    }

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        initBlocks(event.getRegistry());
    }

    public static void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        initBlockEntities(event.getRegistry());
    }

    private static void initBlocks(IForgeRegistry<Block> registry) {
        for (var definition : DBSBlocks.getBlocks()) {
            Block block = definition.block();
            block.setRegistryName(definition.id());
            registry.register(block);
        }
    }


    public static void initItems(IForgeRegistry<Item> registry) {
        for (var definition : DBSBlocks.getBlocks()) {
            var item = definition.asItem();
            item.setRegistryName(definition.id());
            registry.register(item);
        }
        for (var definition : DBSItems.getItems()) {
            var item = definition.asItem();
            item.setRegistryName(definition.id());
            registry.register(item);
        }
    }

    public static void initBlockEntities(IForgeRegistry<BlockEntityType<?>> registry) {
        for (var entry : DBSBlockEntities.getBlockEntityTypes().entrySet()) {
            registry.register(entry.getValue());
        }
    }
    
    public static void initMenuScreens(FMLClientSetupEvent event) {
        
    }

}
