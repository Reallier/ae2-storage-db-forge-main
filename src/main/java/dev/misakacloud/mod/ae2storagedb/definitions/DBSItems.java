package dev.misakacloud.mod.ae2storagedb.definitions;

import appeng.items.materials.MaterialItem;
import appeng.items.materials.StorageComponentItem;
import dev.misakacloud.mod.ae2storagedb.AE2StorageDB;
import dev.misakacloud.mod.ae2storagedb.storage.CellTypes;
import dev.misakacloud.mod.ae2storagedb.items.cells.DBStorageCell;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;


public class DBSItems {
    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();


    public static final ItemDefinition<MaterialItem> DB_ITEM_CELL_HOUSING = buildItem("db_item_cell_housing", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> DB_FLUID_CELL_HOUSING = buildItem("db_fluid_cell_housing", MaterialItem::new);


    public static final ItemDefinition<DBStorageCell> ITEM_CELL_DB = buildCell(CellTypes.ITEM);
    public static final ItemDefinition<DBStorageCell> FLUID_CELL_DB = buildCell(CellTypes.FLUID);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_DB = buildComponent();


    // public static buildCell
    private static ItemDefinition<DBStorageCell> buildCell(CellTypes type) {
        return buildItem(type.getPrefix() + "_storage_cell_db", DBStorageCell::new);
    }

    private static ItemDefinition<StorageComponentItem> buildComponent() {
        return buildItem("cell_component_db", p -> new StorageComponentItem(p, Integer.MAX_VALUE));
    }


    private static <T extends Item> ItemDefinition<T> buildItem(String id, Function<Item.Properties, T> factory) {
        Item.Properties p = new Item.Properties().tab(DBSItems.CREATIVE_TAB);
        T item = factory.apply(p);

        ItemDefinition<T> definition = new ItemDefinition<>(id, AE2StorageDB.makeId(id), item);
        ITEMS.add(definition);
        return definition;
    }

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(AE2StorageDB.MOD_ID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(DBSItems.ITEM_CELL_DB);
        }
    };

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

}
