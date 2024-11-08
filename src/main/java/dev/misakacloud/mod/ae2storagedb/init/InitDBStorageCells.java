package dev.misakacloud.mod.ae2storagedb.init;

import appeng.api.storage.StorageCells;
import dev.misakacloud.mod.ae2storagedb.storage.DBCellHandler;

public class InitDBStorageCells {
    
    public static void init() {
        StorageCells.addCellHandler(DBCellHandler.INSTANCE);
    }
}
