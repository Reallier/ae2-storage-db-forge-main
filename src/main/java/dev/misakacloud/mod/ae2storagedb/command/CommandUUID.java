package dev.misakacloud.mod.ae2storagedb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class CommandUUID {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("ae2db").then(Commands.literal("uuid")).executes(CommandUUID::execute));
    }
    private static int execute(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player player){
            // 获取玩家手持物品
            // 写入名为 UUID 的 NBT Tag
            UUID uuid = UUID.randomUUID();
            player.getItemInHand(player.getUsedItemHand()).getOrCreateTag().putString("UUID", uuid.toString());
            
        }
        return Command.SINGLE_SUCCESS;
    }
}
