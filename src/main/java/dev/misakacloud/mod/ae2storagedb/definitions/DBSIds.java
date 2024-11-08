package dev.misakacloud.mod.ae2storagedb.definitions;

import dev.misakacloud.mod.ae2storagedb.AE2StorageDB;
import net.minecraft.resources.ResourceLocation;

public class DBSIds {
    public static final ResourceLocation DB_DRIVE = buildId("db_drive");

    private static ResourceLocation buildId(String id) {
        return new ResourceLocation(AE2StorageDB.MOD_ID, id);
    }
}
