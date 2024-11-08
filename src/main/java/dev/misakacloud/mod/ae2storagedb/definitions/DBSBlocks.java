package dev.misakacloud.mod.ae2storagedb.definitions;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.CreativeTab;
import appeng.core.definitions.BlockDefinition;
import dev.misakacloud.mod.ae2storagedb.blocks.DBDriveBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DBSBlocks {
    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
    public static final BlockDefinition<DBDriveBlock> DB_DRIVE = block("DB Drive", DBSIds.DB_DRIVE, DBDriveBlock::new);

    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id,
                                                              Supplier<T> blockSupplier) {
        return block(englishName, id, blockSupplier, null);
    }

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            ResourceLocation id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {

        // Create block and matching item
        T block = blockSupplier.get();

        Item.Properties itemProperties = new Item.Properties();
        itemProperties.tab(CreativeTab.INSTANCE);

        BlockItem item;
        if (itemFactory != null) {
            item = itemFactory.apply(block, itemProperties);
            if (item == null) {
                throw new IllegalArgumentException("BlockItem factory for " + id + " returned null");
            }
        } else if (block instanceof AEBaseBlock) {
            item = new AEBaseBlockItem(block, itemProperties);
        } else {
            item = new BlockItem(block, itemProperties);
        }

        BlockDefinition<T> definition = new BlockDefinition<>(englishName, id, block, item);
        CreativeTab.add(definition);

        BLOCKS.add(definition);

        return definition;
    }

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }
}
