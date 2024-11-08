package dev.misakacloud.mod.ae2storagedb.init;

import dev.misakacloud.mod.ae2storagedb.command.CommandUUID;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InitCommand {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandUUID.register(event.getDispatcher());
    }
}
