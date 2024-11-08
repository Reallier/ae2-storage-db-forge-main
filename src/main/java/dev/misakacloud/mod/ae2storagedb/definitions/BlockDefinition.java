package dev.misakacloud.mod.ae2storagedb.definitions;

import java.util.Objects;

import com.google.common.base.Preconditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockDefinition<T extends Block> extends ItemDefinition<BlockItem> {
    private final T block;

    public BlockDefinition(String englishName, ResourceLocation id, T block, BlockItem item) {
        super(englishName, id, item);
        this.block = Objects.requireNonNull(block, "block");
    }

    public final T block() {
        return this.block;
    }

    @Override
    public final ItemStack stack(int stackSize) {
        Preconditions.checkArgument(stackSize > 0);

        return new ItemStack(block, stackSize);
    }

}
