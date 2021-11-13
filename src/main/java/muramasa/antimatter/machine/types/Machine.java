package muramasa.antimatter.machine.types;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.*;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.gui.widget.BackgroundWidget;
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
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.COVEROUTPUT;
import static muramasa.antimatter.machine.MachineFlag.BASIC;
import static muramasa.antimatter.machine.MachineFlag.RECIPE;

/**
 * Machine represents the base class for an Antimatter Machine. It provides tile entities, blocks as well as
 * features to configure machines such as vertical facing, the recipe map and smaller behaviours like if front IO is allowed.
 *
 * @param <T> this class as a generic argument.
 */
public class Machine<T extends Machine<T>> implements IAntimatterObject, IRegistryEntryProvider, ISlotProvider<Machine<T>>, IGuiHandler.IHaveWidgets {

    /**
     * Basic Members
     **/
    protected TileEntityType<?> tileType;
    protected Function<T, Supplier<? extends TileEntityMachine<?>>> tileFunc = m -> () -> new TileEntityMachine<>(this);
    protected String domain, id;
    protected List<Tier> tiers = new ObjectArrayList<>();
    //Assuming facing = north.
    protected CoverFactory[] DEFAULT_COVERS = new CoverFactory[]{ICover.emptyFactory, ICover.emptyFactory, ICover.emptyFactory, COVEROUTPUT, ICover.emptyFactory, ICover.emptyFactory};

    /**
     * Recipe Members
     **/
    protected RecipeMap<?> recipeMap;

    /**
     * GUI Members
     **/
    protected GuiData guiData;
    protected ItemGroup group = Ref.TAB_MACHINES;

    /**
     * Texture Members
     **/
    protected ITextureHandler baseTexture;
    protected IOverlayTexturer overlayTextures;

    /**
     * Multi Members
     **/
    protected Object2ObjectMap<Tier, Structure> structures = new Object2ObjectOpenHashMap<>();

    /**
     * Energy data
     **/
    protected float efficiency = 1;
    //How many amps this machine requires.
    protected int amps = 1;

    /**
     * Behaviours
     **/
    protected boolean allowFrontCovers = false;
    protected boolean allowVerticalFacing = false;
    protected boolean frontIO = false;

    /**
     * Covers
     **/
    protected CoverFactory outputCover = COVEROUTPUT;

    /**
     * Slots
     **/
    private final Map<String, Object2IntOpenHashMap<SlotType<?>>> countLookup = new Object2ObjectOpenHashMap<>();
    private final Map<String, List<SlotData<?>>> slotLookup = new Object2ObjectOpenHashMap<>();

    private final List<Consumer<GuiInstance>> guiCallbacks = new ObjectArrayList<>(1);

    public Machine(String domain, String id) {
        this.domain = domain;
        this.id = id;
        //Default implementation.
        overlayTextures = (type, state, tier) -> {
            if (state != MachineState.ACTIVE && state != MachineState.INVALID_STRUCTURE) state = MachineState.IDLE;
            String stateDir = state == MachineState.IDLE ? "" : state.getId() + "/";
            return new Texture[]{
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "bottom"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "top"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "front"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "back"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "side"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "side"),
            };
        };
        baseTexture = (m, tier) -> new Texture[]{tier.getBaseTexture(m.getDomain())};
        tiers = Arrays.asList(Tier.getStandard());
        AntimatterAPI.register(Machine.class, this);
        //if (FMLEnvironment.dist.isClient()) {
        setupGui();
        //}
    }

    protected void setupGui() {
        addGuiCallback(t -> t.addWidget(BackgroundWidget.build(t.handler.getGuiTexture(), t.handler.guiSize(), t.handler.guiHeight())));
    }

    /**
     * Sets the required amps for this machine.
     *
     * @param amps amperage
     * @return this.
     */
    public T amps(int amps) {
        this.amps = amps;
        return (T) this;
    }

    /**
     * Can you place covers on the front face of this machine?
     *
     * @return this.
     */
    public T frontCovers() {
        allowFrontCovers = true;
        return (T) this;
    }

    /**
     * Sets the output cover fort his machine, which is per default placed on the opposite side of the machine
     * upon placement.
     *
     * @param cover the cover.
     * @return this.
     */
    public T setOutputCover(CoverFactory cover) {
        this.outputCover = cover;
        return (T) this;
    }

    public CoverFactory getOutputCover() {
        return outputCover;
    }

    public boolean allowsFrontCovers() {
        return allowFrontCovers;
    }

    public boolean allowsFrontIO() {
        return frontIO;
    }

    public T allowFrontIO() {
        this.frontIO = true;
        return (T) this;
    }

    public T disableFrontIO() {
        this.frontIO = false;
        return (T) this;
    }

    /**
     * Allows you to configure default covers.
     *
     * @param covers if null, disable covers. (Icover[] null, not 1 null cover)
     *               1 cover sets 1 cover + output
     *               6 covers configures all covers.
     * @return this
     */
    public T covers(CoverFactory... covers) {
        if (covers == null) {
            setOutputCover(ICover.emptyFactory);
            this.DEFAULT_COVERS = new CoverFactory[]{ICover.emptyFactory, ICover.emptyFactory, ICover.emptyFactory, ICover.emptyFactory, ICover.emptyFactory, ICover.emptyFactory};
            return (T) this;
        }
        if (covers.length == 1) {
            setOutputCover(covers[0]);
            this.DEFAULT_COVERS = new CoverFactory[]{ICover.emptyFactory, ICover.emptyFactory, ICover.emptyFactory, covers[0], ICover.emptyFactory, ICover.emptyFactory};
        } else {
            this.DEFAULT_COVERS = covers;
        }
        return (T) this;
    }

    public T noCovers() {
        covers((CoverFactory[]) null);
        return (T) this;
    }

    public CoverFactory defaultCover(Direction dir) {
        return DEFAULT_COVERS[dir.getIndex()];
    }

    public int amps() {
        return amps;
    }

    public Direction handlePlacementFacing(BlockItemUseContext ctxt, Property<?> which, Direction dir) {
        return dir;
    }

    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (registry != ForgeRegistries.BLOCKS) return;
        tileType = new TileEntityType<>(tileFunc.apply((T) this), tiers.stream().map(t -> getBlock(this, t)).collect(Collectors.toSet()), null).setRegistryName(domain, id);
        AntimatterAPI.register(TileEntityType.class, getId(), getDomain(), getTileType());
    }

    @Override
    public List<Consumer<GuiInstance>> getCallbacks() {
        return this.guiCallbacks;
    }

    protected Block getBlock(Machine<T> type, Tier tier) {
        return new BlockMachine(type, tier);
    }

    public BlockMachine getBlockState(Tier tier) {
        if (tileType == null) return null;
        return AntimatterAPI.get(BlockMachine.class, this.getId() + "_" + tier.getId(), this.getDomain());
    }

    /**
     * Returns the item variant of this machine given the tier. Only use after registration or this is null!
     *
     * @param tier the tier to get.
     * @return this as an item.
     */
    public Item getItem(Tier tier) {
        return BlockItem.BLOCK_TO_ITEM.get(AntimatterAPI.get(BlockMachine.class, this.getId() + "_" + tier.getId(), getDomain()));
    }

    /**
     * Registers the recipemap into JEI. This can be overriden in RecipeMap::setGuiData.
     */
    public void registerJei() {
        if (this.guiData != null && recipeMap != null) {
            //If the recipe map has another GUI present don't register it.
            if (recipeMap.getGui() == null)
                AntimatterAPI.registerJEICategory(this.recipeMap, this.guiData, this, false);
        }
    }

    public T addTier(Tier tier) {
        Collection<Tier> tiers = getTiers();
        tiers.add(tier);
        setTiers(tiers.size() > 0 ? tiers.toArray(new Tier[0]) : Tier.getStandard());
        return (T) this;
    }

    /**
     * Sets the recipe map this machine uses for lookup. This will also register it in JEI
     * but it can be overriden by setGuiData in the RecipeMap.
     *
     * @param map the recipe map.
     * @return this.
     */
    public T setMap(RecipeMap<?> map) {
        this.recipeMap = map;
        addFlags(RECIPE);
        registerJei();
        return (T) this;
    }

    public T baseTexture(Texture tex) {
        this.baseTexture = (m, state) -> new Texture[]{tex};
        return (T) this;
    }

    /**
     * Set the getter for overlayTextures. All AM machines are base + overlay textures, this represents the getter for overlay texture. See default
     * behaviour in constructor.
     *
     * @param texturer the texture handler
     * @return this
     */
    public T overlayTexture(IOverlayTexturer texturer) {
        this.overlayTextures = texturer;
        return (T) this;
    }

    /**
     * Set the getter for baseTexture. All AM machines are base + overlay textures, this represents the getter for base texture. See default
     * behaviour in constructor.
     *
     * @param handler the texture handler
     * @return this
     */
    public T baseTexture(ITextureHandler handler) {
        this.baseTexture = handler;
        return (T) this;
    }

    public T itemGroup(ItemGroup group) {
        this.group = group;
        return (T) this;
    }

    public T setTile(Function<T, Supplier<? extends TileEntityMachine<?>>> func) {
        this.tileFunc = func;
        return (T) this;
    }

    public T setTile(Supplier<? extends TileEntityMachine<?>> supplier) {
        setTile(m -> supplier);
        return (T) this;
    }

    public T setAllowVerticalFacing(boolean allowVerticalFacing) {
        this.allowVerticalFacing = allowVerticalFacing;
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
            textures.addAll(Arrays.asList(getOverlayTextures(MachineState.IDLE, tier)));
            textures.addAll(Arrays.asList(getOverlayTextures(MachineState.ACTIVE, tier)));
        }
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


    public Texture[] getOverlayTextures(MachineState state, Tier tier) {
        return overlayTextures.getOverlays(this, state, tier);
    }

    public Texture[] getOverlayTextures(MachineState state) {
        return overlayTextures.getOverlays(this, state, this.getFirstTier());
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

    public T addFlags(MachineFlag... flags) {
        for (MachineFlag flag : flags) {
            flag.add(this);
        }
        return (T) this;
    }

    public void setFlags(MachineFlag... flags) {
        Arrays.stream(MachineFlag.VALUES).forEach(f -> f.remove(this));
        addFlags(flags);
    }

    public T setTiers(Tier... tiers) {
        this.tiers = new ObjectArrayList<>(Arrays.asList(tiers));
        return (T) this;
    }

    /**
     * Sets this machines GUI handler which provides containers and screens.
     *
     * @param menuHandler the menu handler.
     */
    public void setGUI(MenuHandler<?> menuHandler) {
        guiData = new GuiData(this, menuHandler);
        guiData.setSlots(this);
        registerJei();
    }

    public T setGuiTiers(ImmutableMap.Builder<Tier, Tier> tiers) {
        guiData.setTieredGui(tiers);
        return (T) this;
    }

    /**
     * Set the multiblock structure for this machine, for all tiers.
     * Useless if the tile is not a multiblock.
     *
     * @param func the function to build a structure.
     */
    public void setStructure(Function<StructureBuilder, Structure> func) {
        getTiers().forEach(t -> setStructure(t, func));
    }

    /**
     * Set the multiblock structure for this machine, for one tier.
     * Useless if the tile is not a multiblock.
     *
     * @param func the function to build a structure.
     */
    public void setStructure(Tier tier, Function<StructureBuilder, Structure> func) {
        structures.put(tier, func.apply(new StructureBuilder()));
    }

    /**
     * Whether or not this machine has the given machine flag.
     *
     * @param flag the flag.
     * @return if it has it.
     */
    public boolean has(MachineFlag flag) {
        return flag.getTypes().contains(this);
    }

    /**
     * Getters
     **/
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

    public boolean allowVerticalFacing() {
        return allowVerticalFacing;
    }

    /**
     * Static Methods
     **/
    public static Optional<Machine<?>> get(String name, String domain) {
        Machine<?> machine = AntimatterAPI.get(Machine.class, name, domain);
        return Optional.ofNullable(machine);
    }

    public static Collection<Machine<?>> getTypes(MachineFlag... flags) {
        List<Machine<?>> types = new ObjectArrayList<>();
        for (MachineFlag flag : flags) {
            types.addAll(flag.getTypes());
        }
        return types;
    }

    @Override
    public Map<String, Object2IntOpenHashMap<SlotType<?>>> getCountLookup() {
        return countLookup;
    }

    @Override
    public Map<String, List<SlotData<?>>> getSlotLookup() {
        return slotLookup;
    }
}
