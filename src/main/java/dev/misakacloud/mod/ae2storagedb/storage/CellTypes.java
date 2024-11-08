package dev.misakacloud.mod.ae2storagedb.storage;

import appeng.api.stacks.AEKeyType;
import dev.misakacloud.mod.ae2storagedb.definitions.DBSItems;
import net.minecraft.world.item.Item;

public enum CellTypes {

    ITEM(AEKeyType.items(), "item"),

    FLUID(AEKeyType.fluids(), "fluid");

    private final AEKeyType aeKeyType;

    private final String prefix;


    CellTypes(AEKeyType key, String prefix) {
        this.aeKeyType = key;
        this.prefix = prefix;
    }

    public AEKeyType getAeKeyType() {
        return aeKeyType;
    }


    public String getPrefix() {
        return prefix;
    }

    public Item getComponent() {
        return DBSItems.CELL_COMPONENT_DB.asItem();
    }

    public Item getHousing() {
        switch (this) {
            case ITEM:
                return DBSItems.DB_ITEM_CELL_HOUSING.asItem();
            case FLUID:
                return DBSItems.DB_FLUID_CELL_HOUSING.asItem();
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    
    }
}
