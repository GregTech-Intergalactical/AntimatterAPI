package muramasa.gtu.api.registration;

import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.BlockOre;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.blocks.BlockStorage;
import muramasa.gtu.api.blocks.pipe.BlockCable;
import muramasa.gtu.api.blocks.pipe.BlockFluidPipe;
import muramasa.gtu.api.blocks.pipe.BlockItemPipe;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.items.StandardItem;
import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.pipe.types.Cable;
import muramasa.gtu.api.pipe.types.FluidPipe;
import muramasa.gtu.api.pipe.types.ItemPipe;
import muramasa.gtu.loaders.InternalRegistrar;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GregTechRegistry {

    public static Set<Item> ITEMS = new LinkedHashSet<>();
    public static Set<Block> BLOCKS = new LinkedHashSet<>();
    public static Set<Class> TILES = new LinkedHashSet<>();
    public static HashMap<String, HashMap<String, IGregTechObject>> OBJECTS = new HashMap<>();

    public static void register(Object o) {
        if (o instanceof Item) ITEMS.add((Item) o);
        else if (o instanceof Block) BLOCKS.add((Block) o);
        else if (o instanceof Class) TILES.add((Class) o);
    }

    public static void register(Class c, IGregTechObject o) {
        if (!OBJECTS.containsKey(c.getName())) OBJECTS.put(c.getName(), new HashMap<>());
        OBJECTS.get(c.getName()).put(o.getId(), o);
        register(o);
    }

    public static <T> T get(Class<T> c, String name) {
        return (T) OBJECTS.get(c.getName()).get(name);
    }

    public static <T> List<T> getAll(Class<T> c) {
        return OBJECTS.get(c.getName()).values().stream().map(c::cast).collect(Collectors.toList());
    }

    /** Registrar Section **/
    private static final IGregTechRegistrar INTERNAL_REGISTRAR = new InternalRegistrar();

    public static final HashMap<String, IGregTechRegistrar> REGISTRARS = new HashMap<>();

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

    public static StandardItem getStandardItem(ItemType type) {
        return (StandardItem) getItem(type.getId());
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

    public static BlockOre getOre(Material material) {
        if (!material.has(GenerationFlag.ORE)) {
            if (Ref.RECIPE_EXCEPTIONS) {
                throw new IllegalStateException("GET ERROR - DOES NOT GENERATE: P(" + Prefix.Ore.getId() + ") M(" + material.getId() + ")");
            } else {
                System.err.println("GET ERROR - DOES NOT GENERATE: P(" + Prefix.Ore.getId() + ") M(" + material.getId() + ")");
            }
        }
        return (BlockOre) getBlock("ore_" + material.getId());
    }

    public static BlockStone getStone(StoneType type) {
        return (BlockStone) getBlock("stone_" + type.getId());
    }

    public static BlockStorage getStorage(Material material) {
        return (BlockStorage) getBlock("block_" + material.getId());
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
