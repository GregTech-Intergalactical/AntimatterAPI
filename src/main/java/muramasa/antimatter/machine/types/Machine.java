package muramasa.antimatter.machine.types;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.RecipeBuilder;
import muramasa.antimatter.recipe.RecipeMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.texture.TextureData;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;

public class Machine implements IAntimatterObject {

    private static ArrayList<Machine> ID_LOOKUP = new ArrayList<>();

    /** Global Members **/
    private static int lastInternalId;

    /** Basic Members **/
    protected int internalId; //TODO needed?
    protected Object2ObjectOpenHashMap<Tier, BlockMachine> blocks = new Object2ObjectOpenHashMap<>();
    protected TileEntityType tileType;
    protected String domain, id;
    protected ArrayList<Tier> tiers = new ArrayList<>();

    /** Recipe Members **/
    protected RecipeMap recipeMap;

    /** GUI Members **/
    protected GuiData guiData;
    protected ItemGroup group = Ref.TAB_MACHINES;

    /** Texture Members **/
    protected TextureData baseData;
    protected Texture baseTexture;

    /** Multi Members **/
    protected Int2ObjectOpenHashMap<Structure> structures = new Int2ObjectOpenHashMap<>();

    //TODO get valid covers

    public Machine(String domain, String id, Supplier<? extends TileEntityMachine> tile, Object... data) {
        internalId = lastInternalId++;
        addData(data);
        this.domain = domain;
        this.id = id;
        build(tile);
        register(this);
    }

    public Machine(String domain, String id, Object... data) {
        this(domain, id, TileEntityMachine::new, data);
    }

    protected void register(Machine machine) {
        AntimatterAPI.register(Machine.class, machine);
        AntimatterAPI.register(TileEntityType.class, machine.getId(), machine.getTileType());
        ID_LOOKUP.add(machine.getInternalId(), machine);
    }

    protected void build(Supplier<? extends TileEntityMachine> tile) {
        tiers.forEach(t -> blocks.put(t, new BlockMachine(domain, this, t)));
        this.tileType = TileEntityType.Builder.create(tile, blocks.values().toArray(new BlockMachine[0])).build(null).setRegistryName(domain, id);
    }

    protected void addData(Object... data) {
        ArrayList<Tier> tiers = new ArrayList<>();
        ArrayList<MachineFlag> flags = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof RecipeMap) {
                recipeMap = (RecipeMap) data[i];
                flags.add(RECIPE);
            }
            if (data[i] instanceof Tier) tiers.add((Tier) data[i]);
            if (data[i] instanceof MachineFlag) flags.add((MachineFlag) data[i]);
            if (data[i] instanceof Texture) baseTexture = (Texture) data[i];
            if (data[i] instanceof ItemGroup) group = (ItemGroup) data[i];
            //if (data[i] instanceof ITextureHandler) baseData = ((ITextureHandler) data[i]);
        }
        setTiers(tiers.size() > 0 ? tiers.toArray(new Tier[0]) : Tier.getStandard());
        addFlags(flags.toArray(new MachineFlag[0]));
    }

    public int getInternalId() {
        return internalId;
    }

    @Override
    public String getId() {
        return id;
    }

    public ITextComponent getDisplayName(Tier tier) {
        return new TranslationTextComponent("machine." + id + "." + tier.getId());
    }

    public List<Texture> getTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (Tier tier : getTiers()) {
            //textures.addAll(Arrays.asList(baseHandler.getBase(this, tier)));
            textures.add(getBaseTexture(tier));
        }
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.IDLE)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.ACTIVE)));
        return textures;
    }

    public Texture getBaseTexture(Tier tier) {
        return baseTexture != null ? baseTexture : tier.getBaseTexture();
    }

    public Texture[] getOverlayTextures(MachineState state) {
        String stateDir = state == MachineState.IDLE ? "" : state.getId() + "/";
        return new Texture[] {
            new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "bottom"),
            new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "top"),
            new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "front"),
            new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "back"),
            new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "side"),
            new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "side"),
        };
    }

    public ResourceLocation getOverlayModel(String side) {
        return new ResourceLocation(domain, "machine/overlay/" + id + "/" + side);
    }

    public RecipeMap getRecipeMap() {
        return recipeMap;
    }

    public RecipeBuilder getRecipeBuilder() {
        return recipeMap.RB();
    }

    public void addFlags(MachineFlag... flags) {
        Arrays.stream(flags).forEach(f -> f.add(this));
    }

    public void setFlags(MachineFlag... flags) {
        Arrays.stream(MachineFlag.VALUES).forEach(f -> f.getTypes().remove(this));
        addFlags(flags);
    }

    public void setTiers(Tier... tiers) {
        this.tiers = new ArrayList<>(Arrays.asList(tiers));
    }

    public void setGUI(MenuHandler menuHandler) {
        guiData = new GuiData(this, menuHandler);
        addFlags(MachineFlag.GUI);
    }

    public void setStructure(Structure structure) {
        getTiers().forEach(t -> setStructure(t, structure));
    }

    public void setStructure(Tier tier, Structure structure) {
        structures.put(tier.getInternalId(), structure);
    }

    public boolean hasFlag(MachineFlag flag) {
        return flag.getTypes().contains(this);
    }

    /** Getters **/
    public Collection<BlockMachine> getBlocks() {
        return blocks.values();
    }

    @Nullable
    public BlockMachine getBlock(Tier tier) {
        return blocks.get(tier);
    }

    public TileEntityType getTileType() {
        return tileType;
    }

    public Collection<Tier> getTiers() {
        return tiers;
    }

    public Tier getFirstTier() {
        return tiers.get(0);
    }

    public GuiData getGui() {
        return guiData;
    }

    public ItemGroup getGroup() {
        return group;
    }

    public Structure getStructure(Tier tier) {
        return structures.get(tier.getInternalId());
    }

    /** Static Methods **/
    public static int getLastInternalId() {
        return lastInternalId;
    }

    public static Machine get(String name) {
        Machine machine = AntimatterAPI.get(Machine.class, name);
        return machine != null ? machine : Data.MACHINE_INVALID;
    }

    public static Machine get(int id) {
        Machine machine = ID_LOOKUP.get(id);
        return machine != null ? machine : Data.MACHINE_INVALID;
    }

//    public static MachineStack get(Machine type, Tier tier) {
//        return new MachineStack(type, tier);
//    }

    //TODO move to Antimatter
    public static Collection<Machine> getTypes(MachineFlag... flags) {
        ArrayList<Machine> types = new ArrayList<>();
        for (MachineFlag flag : flags) {
            types.addAll(flag.getTypes());
        }
        return types;
    }

//    public static Collection<MachineStack> getStacks(MachineFlag... flags) {
//        ArrayList<MachineStack> stacks = new ArrayList<>();
//        for (MachineFlag flag : flags) {
//            stacks.addAll(flag.getStacks());
//        }
//        return stacks;
//    }
}
