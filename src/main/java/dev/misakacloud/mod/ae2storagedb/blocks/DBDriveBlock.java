package dev.misakacloud.mod.ae2storagedb.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.util.InteractionUtil;
import com.mojang.logging.LogUtils;
import dev.misakacloud.mod.ae2storagedb.blocks.entity.DBDriveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;

public class DBDriveBlock extends AEBaseEntityBlock<DBDriveBlockEntity> {
    public static Logger logger = LogUtils.getLogger();

    public DBDriveBlock() {
        super(defaultProps(Material.METAL));
    }

    @Override
    public InteractionResult onActivated(Level level, BlockPos pos, Player p,
                                         InteractionHand hand,
                                         @Nullable ItemStack heldItem, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(p)) {
            return InteractionResult.PASS;
        }

        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            if (!level.isClientSide()) {
                // be.openMenu(p);
                logger.debug("输出方块信息");
                be.getDebugInfo(p);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }


    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        // 如果 oldState 是空气,那么就是放置方块
        if (oldState.getBlock().equals(Blocks.AIR)) {
            var be = this.getBlockEntity(level, pos);
            if (be != null) {
                // 为他写入一个 uuid
                UUID uuid = UUID.randomUUID();
                be.getTileData().putString("uuid", uuid.toString());
            }
        }
        super.onBlockStateChange(level, pos, oldState, newState);
    }
}
