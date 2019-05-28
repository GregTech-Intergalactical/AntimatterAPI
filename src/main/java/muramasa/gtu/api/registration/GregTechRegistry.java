package muramasa.gtu.api.registration;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Casing;
import muramasa.gtu.api.data.Coil;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.items.StandardItem;
import muramasa.gtu.api.materials.ItemFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.pipe.types.Cable;
import muramasa.gtu.api.pipe.types.FluidPipe;
import muramasa.gtu.api.pipe.types.ItemPipe;
import muramasa.gtu.api.tools.MaterialTool;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.common.blocks.*;
import muramasa.gtu.common.blocks.pipe.BlockCable;
import muramasa.gtu.common.blocks.pipe.BlockFluidPipe;
import muramasa.gtu.common.blocks.pipe.BlockItemPipe;
import muramasa.gtu.loaders.InternalRegistrar;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class GregTechRegistry {

    /** Item/Block/Tile Registration **/
    private static Set<Item> ITEMS = new LinkedHashSet<>();
    private static Set<Block> BLOCKS = new LinkedHashSet<>();
    private static Set<Class> TILES = new LinkedHashSet<>();

    public static void register(Item item) {
        ITEMS.add(item);
    }

    public static void register(Block block) {
        BLOCKS.add(block);
    }

    public static void register(Class tile) {
        TILES.add(tile);
    }

    public static Collection<Item> getRegisteredItems() {
        return ITEMS;
    }

    public static Collection<Block> getRegisteredBlocks() {
        return BLOCKS;
    }

    /** Registrar Section **/
    private static IGregTechRegistrar INTERNAL_REGISTRAR = new InternalRegistrar();

    private static HashMap<String, IGregTechRegistrar> REGISTRARS = new HashMap<>();

    public static void addRegistrar(IGregTechRegistrar registrar) {
        if (registrar.isEnabled() || Ref.ENABLE_ALL_REGISTRARS) REGISTRARS.put(registrar.getId(), registrar);
    }

    public static void callRegistrationEvent(RegistrationEvent event) {
        INTERNAL_REGISTRAR.onRegistrationEvent(event);
        REGISTRARS.values().forEach(r -> r.onRegistrationEvent(event));
    }

    public static boolean isRegistrarEnabled(String id) {
        IGregTechRegistrar registrar = getRegistrar(id);
        return registrar != null && registrar.isEnabled();
    }

    @Nullable
    public static IGregTechRegistrar getRegistrar(String id) {
        return REGISTRARS.get(id);
    }

    public static Collection<IGregTechRegistrar> getRegistrars() {
        return REGISTRARS.values();
    }

    public static MaterialItem getMaterialItem(Prefix prefix, Material material) {
        return (MaterialItem) getItem(prefix.getName() + material.getName());
    }

    public static StandardItem getStandardItem(ItemType type) {
        return (StandardItem) getItem(type.getName());
    }

    public static MaterialTool getMaterialTool(ToolType type) {
        return (MaterialTool) getItem(type.getName());
    }

    public static BlockCable getCable(Cable type) {
        return (BlockCable) getBlock("cable_" + type.getName());
    }

    public static BlockItemPipe getItemPipe(ItemPipe type) {
        return (BlockItemPipe) getBlock("item_pipe_" + type.getName());
    }

    public static BlockFluidPipe getFluidPipe(FluidPipe type) {
        return (BlockFluidPipe) getBlock("fluid_pipe_" + type.getName());
    }

    public static BlockCasing getCasing(Casing type) {
        return (BlockCasing) getBlock("casing_" + type.getName());
    }

    public static BlockCoil getCoil(Coil type) {
        return (BlockCoil) getBlock("coil_" + type.getName());
    }

    public static BlockOre getOre(Material material) {
        if (!material.has(ItemFlag.GENERATE_ORE)) {
            if (Ref.RECIPE_EXCEPTIONS) {
                throw new IllegalStateException("GET ERROR - DOES NOT GENERATE: P(" + Prefix.Ore.getName() + ") M(" + material.getName() + ")");
            } else {
                System.err.println("GET ERROR - DOES NOT GENERATE: P(" + Prefix.Ore.getName() + ") M(" + material.getName() + ")");
            }
        }
        return (BlockOre) getBlock("ore_" + material.getName());
    }

    public static BlockStone getStone(StoneType type) {
        return (BlockStone) getBlock("stone_" + type.getName());
    }

    public static BlockStorage getStorage(Material material) {
        return (BlockStorage) getBlock("block_" + material.getName());
    }

    public static Item getItem(String path) {
        return getItem(new ResourceLocation(Ref.MODID, path));
    }

    public static Block getBlock(String path) {
        return getBlock(new ResourceLocation(Ref.MODID, path));
    }

    public static Item getItem(ResourceLocation loc) {
        return Item.getByNameOrId(loc.toString());
    }

    public static Block getBlock(ResourceLocation loc) {
        return Block.getBlockFromName(loc.toString());
    }
}
