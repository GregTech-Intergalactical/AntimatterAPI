package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.block.BlockSurfaceRock;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import java.util.*;

public class MaterialType<T> implements IMaterialTag, IAntimatterObject {

    //Item Types
    public static MaterialType<?> DUST = new MaterialType<>("dust", 2, true, Ref.U);
    public static MaterialType<?> DUST_SMALL = new MaterialType<>("dust_small", 2, true, Ref.U4);
    public static MaterialType<?> DUST_TINY = new MaterialType<>("dust_tiny", 2, true, Ref.U9);
    public static MaterialType<?> DUST_IMPURE = new MaterialType<>("dust_impure", 2, true, Ref.U);
    public static MaterialType<?> DUST_PURE = new MaterialType<>("dust_pure", 2, true, Ref.U);
    public static MaterialType<IRockGetter> ROCK = new MaterialType<>("rock", 2, false, Ref.U9);
    public static MaterialType<?> CRUSHED = new MaterialType<>("crushed", 2, true, Ref.U);
    public static MaterialType<?> CRUSHED_CENTRIFUGED = new MaterialType<>("crushed_centrifuged", 2, true, Ref.U);
    public static MaterialType<?> CRUSHED_PURIFIED = new MaterialType<>("crushed_purified", 2, true, Ref.U);
    public static MaterialType<?> INGOT = new MaterialType<>("ingot", 2, true, Ref.U);
    public static MaterialType<?> INGOT_HOT = new MaterialType<>("ingot_hot", 2, true, Ref.U);
    public static MaterialType<?> NUGGET = new MaterialType<>("nugget", 2, true, Ref.U9);
    public static MaterialType<?> GEM = new MaterialType<>("gem", 2, true, Ref.U);
    public static MaterialType<?> GEM_BRITTLE = new MaterialType<>("gem_brittle", 2, true, Ref.U);
    public static MaterialType<?> GEM_POLISHED = new MaterialType<>("gem_polished", 2, true, Ref.U);
    public static MaterialType<?> LENS = new MaterialType<>("lens", 2, true, Ref.U * 3 / 4);
    public static MaterialType<?> PLATE = new MaterialType<>("plate", 2, true, Ref.U);
    public static MaterialType<?> PLATE_DENSE = new MaterialType<>("plate_dense", 2, true, Ref.U * 9);
    public static MaterialType<?> ROD = new MaterialType<>("rod", 2, true, Ref.U2);
    public static MaterialType<?> ROD_LONG = new MaterialType<>("rod_long", 2, true, Ref.U);
    public static MaterialType<?> RING = new MaterialType<>("ring", 2, true, Ref.U4);
    public static MaterialType<?> FOIL = new MaterialType<>("foil", 2, true, Ref.U);
    public static MaterialType<?> BOLT = new MaterialType<>("bolt", 2, true, Ref.U8);
    public static MaterialType<?> SCREW = new MaterialType<>("screw", 2, true, Ref.U9);
    public static MaterialType<?> GEAR = new MaterialType<>("gear", 2, true, Ref.U * 4);
    public static MaterialType<?> GEAR_SMALL = new MaterialType<>("gear_small", 2, true, Ref.U);
    public static MaterialType<?> WIRE_FINE = new MaterialType<>("wire_fine", 2, true, Ref.U8);
    public static MaterialType<?> SPRING = new MaterialType<>("spring", 2, true, Ref.U);
    public static MaterialType<?> ROTOR = new MaterialType<>("rotor", 2, true, Ref.U * 4 + Ref.U4);

    //Block Types
    public static MaterialType<IOreGetter> ORE = new MaterialType<>("ore", 1, true, -1);
    public static MaterialType<IOreGetter> ORE_SMALL = new MaterialType<>("ore_small", 1, false, -1);
    public static MaterialType<IOreStoneGetter> ORE_STONE = new MaterialType<>("ore_stone", 1, true, -1);
    public static MaterialType<IStorageGetter> BLOCK = new MaterialType<>("block", 1, false, -1);
    public static MaterialType<IStorageGetter> FRAME = new MaterialType<>("frame", 1, true, -1);

    //Dummy Types
    public static MaterialType<?> TOOLS = new MaterialType<>("tools", 1, false, -1).nonGen();
    public static MaterialType<?> LIQUID = new MaterialType<>("liquid", 1, true, -1).nonGen();
    public static MaterialType<?> GAS = new MaterialType<>("gas", 1, true, -1).nonGen();
    public static MaterialType<?> PLASMA = new MaterialType<>("plasma", 1, true, -1).nonGen();

    static {
        ROCK.get((m, s) -> {
            if (!MaterialType.ROCK.allowBlockGen(m)) return getEmptyAndLog(ROCK, m, s);
            BlockSurfaceRock rock = AntimatterAPI.get(BlockSurfaceRock.class, "surface_rock_" + m.getId() + "_" + s.getId());
            return new Container(rock != null ? rock.getDefaultState() : Blocks.AIR.getDefaultState());
        });
        ORE.get((m, s) -> {
            if (!MaterialType.ORE.allowBlockGen(m)) {
                return getEmptyAndLog(ORE, m, s);
            }
            BlockOre block = AntimatterAPI.get(BlockOre.class, MaterialType.ORE.getId() + "_" + m.getId() + "_" + s.getId());
            return new Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
        ORE_SMALL.get((m, s) -> {
            if (!MaterialType.ORE_SMALL.allowBlockGen(m)) return getEmptyAndLog(ORE_SMALL, m, s);
            BlockOre block = AntimatterAPI.get(BlockOre.class, MaterialType.ORE_SMALL.getId() + "_" + m.getId() + "_" + s.getId());
            return new Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
        ORE_STONE.get(m -> {
            if (!MaterialType.ORE_STONE.allowBlockGen(m)) return getEmptyAndLog(ORE_STONE, m);
            BlockOreStone block = AntimatterAPI.get(BlockOreStone.class, MaterialType.ORE_STONE.getId() + "_" + m.getId());
            return new Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
        BLOCK.get(m -> {
            if (!MaterialType.BLOCK.allowBlockGen(m)) return getEmptyAndLog(BLOCK, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, MaterialType.BLOCK.getId() + "_" + m.getId());
            return new Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
        FRAME.get(m -> {
            if (!MaterialType.FRAME.allowBlockGen(m)) return getEmptyAndLog(FRAME, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, MaterialType.FRAME.getId() + "_" + m.getId());
            return new Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
    }

    protected String id;;
    protected int unitValue, layers;
    protected boolean generating = true, blockType, visible;
    protected Set<Material> materials = new LinkedHashSet<>(); //Linked to preserve insertion order for JEI
    protected Map<MaterialType<?>, Tag> tagMap = new HashMap<>();
    protected T getter;

    public MaterialType(String id, int layers, boolean visible, int unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        this.layers = layers;
        this.tagMap.put(this, Utils.getForgeItemTag(Utils.getConventionalMaterialType(this)));
        register(MaterialType.class, this);
    }

    public MaterialType<T> nonGen() {
        generating = false;
        return this;
    }

    public MaterialType<T> blockType() {
        blockType = true;
        this.tagMap.put(this, Utils.getForgeBlockTag(Utils.getConventionalMaterialType(this)));
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public int getLayers() {
        return layers;
    }

    public <T> Tag<T> getTag() {
        return tagMap.get(this);
    }

    public MaterialType<T> get(T getter) {
        this.getter = getter;
        return this;
    }

    public T get() {
        return getter;
    }

    public Item get(Material material) {
        ItemStack replacement = AntimatterAPI.getReplacement(this, material);
        if (!replacement.isEmpty()) return replacement.getItem();
        MaterialItem item = AntimatterAPI.get(MaterialItem.class, id + "_" + material.getId());
        if (!allowItemGen(material)) Utils.onInvalidData("GET ERROR - DOES NOT GENERATE: T(" + id + ") M(" + material.getId() + ")");
        if (item == null) Utils.onInvalidData("GET ERROR - MAT ITEM NULL: T(" + id + ") M(" + material.getId() + ")");
        return item;
    }

    public ItemStack get(Material material, int count) {
        Item item = get(material);
        if (item == null) Utils.onInvalidData("GET ERROR - MAT ITEM NULL: T(" + id + ") M(" + material.getId() + ")");
        ItemStack stack = new ItemStack(item, count);
        if (stack.isEmpty()) Utils.onInvalidData("GET ERROR - MAT STACK EMPTY: T(" + id + ") M(" + material.getId() + ")");
        return stack;
    }

    @Override
    public Set<Material> all() {
        return materials;
    }

    public boolean isVisible() {
        return visible || Configs.JEI.SHOW_ALL_MATERIAL_ITEMS;
    }

    public boolean allowItemGen(Material material) {
        return generating && !blockType && materials.contains(material) && AntimatterAPI.getReplacement(this, material).isEmpty();
    }

    public boolean allowBlockGen(Material material) {
        return generating && materials.contains(material);
    }

    @Override
    public String toString() {
        return getId();
    }

    public static Container getEmptyAndLog(MaterialType<?> type, IAntimatterObject... objects) {
        Utils.onInvalidData("Tried to create " + type.getId() + " for objects: " + Arrays.toString(Arrays.stream(objects).map(IAntimatterObject::getId).toArray(String[]::new)));
        return new Container(Blocks.AIR.getDefaultState());
    }

    public interface IRockGetter {
        Container get(Material m, StoneType s);
    }

    public interface IOreGetter {
        Container get(Material m, StoneType s);
    }

    public interface IOreStoneGetter {
        Container get(Material m);
    }

    public interface IStorageGetter {
        Container get(Material m);
    }

    public static class Container {

        protected BlockState state;

        public Container(BlockState state) {
            this.state = state;
        }

        public BlockState asState() {
            return state;
        }

        public Block asBlock() {
            return state.getBlock();
        }

        public Item asItem() {
            return asBlock().asItem();
        }

        public ItemStack asStack(int count) {
            return new ItemStack(asItem(), count);
        }

        public ItemStack asStack() {
            return asStack(1);
        }
    }
}
