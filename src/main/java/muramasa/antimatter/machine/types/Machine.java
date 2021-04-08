package muramasa.antimatter.machine.types;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.map.RecipeBuilder;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.texture.IOverlayTexturer;
import muramasa.antimatter.texture.ITextureHandler;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.COVERNONE;
import static muramasa.antimatter.Data.COVEROUTPUT;
import static muramasa.antimatter.machine.MachineFlag.BASIC;
import static muramasa.antimatter.machine.MachineFlag.RECIPE;

public class Machine<T extends Machine<T>> implements IAntimatterObject, IRegistryEntryProvider {

    /** Basic Members **/
    protected TileEntityType<?> tileType;
    protected Function<Machine<?>, Supplier<? extends TileEntityMachine>> tileFunc = m -> () -> new TileEntityMachine(this);
    protected String domain, id;
    protected List<Tier> tiers = new ObjectArrayList<>();
    //Assuming facing = north.
    protected ICover[] DEFAULT_COVERS = new ICover[]{COVERNONE,COVERNONE,COVERNONE,COVEROUTPUT,COVERNONE,COVERNONE};

    /** Recipe Members **/
    protected RecipeMap<?> recipeMap;

    /** GUI Members **/
    protected GuiData guiData;
    protected ItemGroup group = Ref.TAB_MACHINES;

    /** Texture Members **/
    protected ITextureHandler baseTexture;
    protected IOverlayTexturer overlayTextures;

    /** Multi Members **/
    protected Object2ObjectMap<Tier, Structure> structures = new Object2ObjectOpenHashMap<>();

    /** Energy data **/
    protected float efficiency = 1;
    //How many amps this machine requires.
    protected int amps = 1;

    /** Behaviours **/
    protected boolean allowFrontCovers = false;
    protected ICover outputCover = COVEROUTPUT;
    //TODO get valid covers

    public Machine(String domain, String id, Object... data) {
        addData(data);
        this.domain = domain;
        this.id = id;
        AntimatterAPI.register(Machine.class, this);
    }

    public T amps(int amps) {
        this.amps = amps;
        return (T) this;
    }

    public T frontCovers() {
        allowFrontCovers = true;
        return (T) this;
    }

    public void setOutputCover(ICover cover) {
        this.outputCover = cover;
    }

    public ICover getOutputCover() {
        return outputCover;
    }

    public boolean allowsFrontCovers() {
        return allowFrontCovers;
    }

    /**
     * Allows you to configure default covers.
     * @param covers if null, set n
     * @return
     */
    public T covers(ICover... covers) {
        if (covers == null) {
            setOutputCover(COVERNONE);
            this.DEFAULT_COVERS = new ICover[]{COVERNONE,COVERNONE,COVERNONE,COVERNONE,COVERNONE,COVERNONE};
            return (T) this;
        }
        if (covers.length == 1) {
            setOutputCover(covers[0]);
            this.DEFAULT_COVERS = new ICover[]{COVERNONE,COVERNONE,COVERNONE,covers[0],COVERNONE,COVERNONE};
        } else {
            this.DEFAULT_COVERS = covers;
        }
        return (T) this;
    }

    public ICover defaultCover(Direction dir) {
        return DEFAULT_COVERS[dir.getIndex()];
    }

    public int amps() {
        return amps;
    }

    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (registry != ForgeRegistries.BLOCKS) return;
        tileType = new TileEntityType<>(tileFunc.apply(this), tiers.stream().map(t -> getBlock(this, t)).collect(Collectors.toSet()), null).setRegistryName(domain, id);
        AntimatterAPI.register(TileEntityType.class, getId(), getTileType());
    }

    protected Block getBlock(Machine<T> type, Tier tier) {
        return new BlockMachine(type, tier);
    }
    //Call only after registration!
    public Item getItem(Tier tier) {
        return BlockItem.BLOCK_TO_ITEM.get(AntimatterAPI.get(BlockMachine.class,this.getId() + "_" + tier.getId()));
    }

    public void registerJei() {
        if (this.guiData != null && recipeMap != null) {
            AntimatterAPI.registerJEICategory(this.recipeMap,this.guiData, this);
        }
    }

    protected void addData(Object... data) {
        List<Tier> tiers = new ObjectArrayList<>();
        Set<MachineFlag> flags = new ObjectOpenHashSet<>();
        for (Object o : data) {
            if (o instanceof RecipeMap) {
                recipeMap = (RecipeMap<?>) o;
                flags.add(RECIPE);
            }
            if (o instanceof Tier) tiers.add((Tier) o);
            if (o instanceof MachineFlag) flags.add((MachineFlag) o);
            if (o instanceof Texture) baseTexture = (m, state) -> new Texture[]{(Texture) o};
            if (o instanceof IOverlayTexturer) overlayTextures = (IOverlayTexturer) o;
            if (o instanceof ITextureHandler) baseTexture = (ITextureHandler) o;
            if (o instanceof ItemGroup) group = (ItemGroup) o;
            if (o instanceof ICover) {
                covers(COVERNONE,COVERNONE,((ICover)o),COVERNONE,COVERNONE,COVERNONE);
                setOutputCover((ICover) o);
            }
            //if (data[i] instanceof ITextureHandler) baseData = ((ITextureHandler) data[i]);
        }
        setTiers(tiers.size() > 0 ? tiers.toArray(new Tier[0]) : Tier.getStandard());
        addFlags(flags.toArray(new MachineFlag[0]));

        if (overlayTextures == null) {
            overlayTextures = (type, state) -> {
                if (state != MachineState.ACTIVE && state != MachineState.INVALID_STRUCTURE) state = MachineState.IDLE;
                String stateDir = state == MachineState.IDLE ? "" : state.getId() + "/";
                return new Texture[] {
                        new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "bottom"),
                        new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "top"),
                        new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "front"),
                        new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "back"),
                        new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "side"),
                        new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "side"),
                };
            };
        }
        if (baseTexture == null) {
            baseTexture = (m, tier) -> new Texture[]{tier.getBaseTexture()};
        }
    }

    public T setTile(Function<Machine<?>, Supplier<? extends TileEntityMachine>> func) {
        this.tileFunc = func;
        return (T) this;
    }

    public T setTile(Supplier<? extends TileEntityMachine> supplier) {
        setTile(m -> supplier);
        return (T) this;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public ITextComponent getDisplayName(Tier tier) {
        if (has(BASIC)) {
            return new TranslationTextComponent("machine." + id + "." + tier.getId());
        } else {
            return new TranslationTextComponent("machine." + id);
        }
    }

    public double getMachineEfficiency() {
        return efficiency;
    }

    public List<Texture> getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        for (Tier tier : getTiers()) {
            //textures.addAll(Arrays.asList(baseHandler.getBase(this, tier)));
            textures.addAll(Arrays.asList(getBaseTexture(tier)));
        }
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.IDLE)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.ACTIVE)));
        return textures;
    }

    public Texture[] getBaseTexture(Tier tier) {
        return baseTexture.getBase(this, tier);
    }

    public Texture getBaseTexture(Tier tier, Direction dir) {
        Texture[] texes = baseTexture.getBase(this, tier);
        if (texes.length == 1) return texes[0];
        return texes[dir.getIndex()];
    }


    public Texture[] getOverlayTextures(MachineState state) {
        return overlayTextures.getOverlays(this, state);
    }

    public ResourceLocation getOverlayModel(Direction side) {
        return new ResourceLocation(domain, "block/machine/overlay/" + id + "/" + side.getString());
    }

    public RecipeMap<?> getRecipeMap() {
        return recipeMap;
    }

    public RecipeBuilder getRecipeBuilder() {
        return recipeMap.RB();
    }

    public void addFlags(MachineFlag... flags) {
        Arrays.stream(flags).forEach(f -> f.add(this));
    }

    public void setFlags(MachineFlag... flags) {
        Arrays.stream(MachineFlag.VALUES).forEach(f -> f.remove(this));
        addFlags(flags);
    }

    public T setTiers(Tier... tiers) {
        this.tiers = new ObjectArrayList<>(Arrays.asList(tiers));
        return (T) this;
    }

    public void setGUI(MenuHandler<?> menuHandler) {
        guiData = new GuiData(this, menuHandler);

        registerJei();
    }

    public void setStructure(Function<StructureBuilder, Structure> func) {
        getTiers().forEach(t -> setStructure(t, func));
    }

    public void setStructure(Tier tier, Function<StructureBuilder, Structure> func) {
        structures.put(tier, func.apply(new StructureBuilder()));
    }

    public boolean has(MachineFlag flag) {
        return flag.getTypes().contains(this);
    }

    /** Getters **/
    public TileEntityType<?> getTileType() {
        return tileType;
    }

    public Collection<Tier> getTiers() {
        return tiers;
    }

    //TODO needed?
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
        return structures.get(tier);
    }

    /** Static Methods **/
    public static Machine<?> get(String name) {
        Machine<?> machine = AntimatterAPI.get(Machine.class, name);
        return machine != null ? machine : Data.MACHINE_INVALID;
    }

    public static Collection<Machine<?>> getTypes(MachineFlag... flags) {
        List<Machine<?>> types = new ObjectArrayList<>();
        for (MachineFlag flag : flags) {
            types.addAll(flag.getTypes());
        }
        return types;
    }
}
