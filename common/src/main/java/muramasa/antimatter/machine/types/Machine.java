package muramasa.antimatter.machine.types;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.*;
import lombok.Getter;
import lombok.Setter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.blockentity.BlockEntityBase;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.dynamic.IDynamicModelProvider;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.gui.*;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.gui.widget.BackgroundWidget;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.machine.*;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.texture.IOverlayModeler;
import muramasa.antimatter.texture.IOverlayTexturer;
import muramasa.antimatter.texture.ITextureHandler;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.COVEROUTPUT;
import static muramasa.antimatter.machine.MachineFlag.RECIPE;
import static muramasa.antimatter.machine.Tier.NONE;

/**
 * Machine represents the base class for an Antimatter Machine. It provides tile entities, blocks as well as
 * features to configure machines such as vertical facing, the recipe map and smaller behaviours like if front IO is allowed.
 *
 * @param <T> this class as a generic argument.
 */
public class Machine<T extends Machine<T>> implements IAntimatterObject, IRegistryEntryProvider, ISlotProvider<Machine<T>>, IGuiHandler.IHaveWidgets, IDynamicModelProvider {

    
    /**
     * Basic Members
     **/
    protected BlockEntityType<? extends BlockEntityMachine<?>> tileType;
    protected BlockEntityBase.BlockEntitySupplier<BlockEntityMachine<?>, T> tileFunc = BlockEntityMachine::new;
    protected BiFunction<Machine<T>, Tier, BlockMachine> blockFunc = BlockMachine::new;
    @Getter
    protected Function<BlockMachine, AntimatterItemBlock> itemBlockFunction = AntimatterItemBlock::new;

    protected Supplier<Class<? extends BlockMachine>> itemClassSupplier = () -> BlockMachine.class;
    @Getter
    protected TagKey<Block> toolTag = AntimatterDefaultTools.WRENCH.getToolType();
    @Getter
    protected List<ITooltipInfo> tooltipFunctions = new ArrayList<>();
    @Getter
    protected IShapeGetter shapeGetter;
    protected String domain, id;
    @Getter
    protected List<Tier> tiers = new ObjectArrayList<>();
    //Assuming facing = north.
    protected CoverFactory[] DEFAULT_COVERS = new CoverFactory[]{ICover.emptyFactory, ICover.emptyFactory, ICover.emptyFactory, COVEROUTPUT, ICover.emptyFactory, ICover.emptyFactory};

    /**
     * Recipe Members
     **/
    protected Map<String, IRecipeMap> tierRecipeMaps = new Object2ObjectOpenHashMap<>();

    /**
     * GUI Members
     **/
    protected GuiData guiData;
    @Getter
    protected CreativeModeTab group = Ref.TAB_MACHINES;

    /**
     * Texture Members
     **/
    protected ITextureHandler baseTexture;
    protected IOverlayTexturer overlayTextures;
    protected IOverlayModeler overlayModels;
    @Getter
    protected ResourceLocation itemModelParent;
    @Getter
    protected IMachineColorHandlerBlock blockColorHandler = (state, world, pos, machine, i) -> -1;
    @Getter
    protected IMachineColorHandlerItem itemColorHandler = (stack, block, i) -> -1;
    @Getter
    protected ResourceLocation modelLoader = AntimatterModelManager.LOADER_MACHINE;

    protected boolean tierSpecificLang = false;

    public SoundEvent machineNoise;
    public float soundVolume;
    /**
     * Multi Members
     **/
    protected Object2ObjectMap<Tier, Structure> structures = new Object2ObjectOpenHashMap<>();

    /**
     * Energy data
     **/
    protected ToIntFunction<Tier> efficiency = t -> 100 - (5 * t.getIntegerId());
    //How many amps this machine requires.
    protected int amps = 1;

    /**
     * Behaviours
     **/
    protected boolean allowFrontCovers = false;
    @Getter
    protected boolean verticalFacingAllowed = false;
    @Getter
    protected boolean noFacing = false;
    @Getter
    protected boolean noTextureRotation = false;
    protected boolean frontIO = false;
    @Getter
    protected boolean clientTicking = false;
    @Getter
    protected boolean ambientTicking = false;

    /**
     * Rendering
     */
    protected boolean renderTesr = false;
    protected boolean renderContainedLiquids = false;
    protected boolean renderContainedLiquidLevel = false;
    @Getter
    @Setter
    protected int overlayLayers = 1;

    /**
     * Covers
     **/
    @Getter
    protected CoverFactory outputCover = COVEROUTPUT;

    /**
     * Slots
     **/
    private final Map<String, Object2IntOpenHashMap<SlotType<?>>> countLookup = new Object2ObjectOpenHashMap<>();
    private final Map<String, List<SlotData<?>>> slotLookup = new Object2ObjectOpenHashMap<>();

    private final List<Consumer<GuiInstance>> guiCallbacks = new ObjectArrayList<>(1);
    private static final Map<String, Set<Machine<?>>> FLAG_MAP = new Object2ObjectOpenHashMap<>();

    public Machine(String domain, String id) {
        this.domain = domain;
        this.id = id;
        //Default implementation.
        overlayTextures = (type, state, tier, i) -> {
            state = state.getTextureState();
            String stateDir = state == MachineState.IDLE ? "" : state.getId() + "/";
            return new Texture[]{
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "bottom"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "top"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "back"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "front"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "side"),
                    new Texture(domain, "block/machine/overlay/" + id + "/" + stateDir + "side"),
            };
        };
        baseTexture = (m, tier, state) -> new Texture[]{tier.getBaseTexture(m.getDomain())};
        overlayModels = (a,s,d) -> {
            return new ResourceLocation(Ref.ID, "block/machine/overlay/invalid/" + d.getName());
        };
        itemModelParent = new ResourceLocation(Ref.ID, "block/preset/layered");
        tiers = Arrays.asList(Tier.getStandard());
        AntimatterAPI.register(Machine.class, this);
        //if (FMLEnvironment.dist.isClient()) {
        setupGui();
        //}
    }

    protected void setupGui() {
        addGuiCallback(t -> t.addWidget(BackgroundWidget.build(t.handler.getGuiTexture(), t.handler.guiSize(), t.handler.guiHeight(), t.handler.guiTextureSize(), t.handler.guiTextureHeight())));
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

    public T efficiency(ToIntFunction<Tier> function){
        this.efficiency = function;
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

    public T setSound(SoundEvent loc, float volume) {
        this.soundVolume = volume;
        this.machineNoise = getDatedMachineSound(loc);
        return (T) this;
    }

    public T setTierSpecificLang(){
        this.tierSpecificLang = true;
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
     *               sides are bottom, top, front, back, right, left
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
        return DEFAULT_COVERS[dir.get3DDataValue()];
    }

    public int amps() {
        return amps;
    }

    public Direction handlePlacementFacing(BlockPlaceContext ctxt, Property<?> which, Direction dir) {
        return dir;
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {
        if (registry != RegistryType.BLOCKS) return;
        tileType = new BlockEntityType<>(new BlockEntityBase.BlockEntityGetter<>(tileFunc, (T)this), tiers.stream().map(t -> getBlock(this, t)).collect(Collectors.toSet()), null);
        AntimatterAPI.registerTransferApi(tileType);
        AntimatterAPI.register(BlockEntityType.class, getId(), getDomain(), getTileType());
    }

    @Override
    public List<Consumer<GuiInstance>> getCallbacks() {
        return this.guiCallbacks;
    }

    protected Block getBlock(Machine<T> type, Tier tier) {
        return blockFunc.apply(type, tier);
    }

    public BlockMachine getBlockState(Tier tier) {
        if (tileType == null) return null;
        return AntimatterAPI.get(itemClassSupplier.get(), this.getIdFromTier(tier), this.getDomain());
    }

    /**
     * Returns the item variant of this machine given the tier. Only use after registration or this is null!
     *
     * @param tier the tier to get.
     * @return this as an item.
     */
    public Item getItem(Tier tier) {
        return BlockItem.BY_BLOCK.get(AntimatterAPI.get(itemClassSupplier.get(), this.getIdFromTier(tier), getDomain()));
    }

    /**
     * Registers the recipemap into JEI. This can be overriden in RecipeMap::setGuiData.
     */
    public void registerJei() {
        if (this.guiData != null) {
            tierRecipeMaps.forEach((s, r) -> {
                if (s.isEmpty()){
                    for (int i = 0; i < tiers.size(); i++) {
                        Tier tier = tiers.get(i);
                        if (i == 0 && r.getGui() == null && !AntimatterJEIREIPlugin.containsCategory(r)){
                            AntimatterAPI.registerJEICategory(r, this.guiData, this, tier, false);
                        } else {
                            AntimatterAPI.registerJEICategoryWorkstation(r, this, tier);
                        }
                    }
                    return;
                }
                Tier t = AntimatterAPI.get(Tier.class, s);
                //If the recipe map has another GUI present don't register it.
                if (r.getGui() == null && !AntimatterJEIREIPlugin.containsCategory(r)) {
                    AntimatterAPI.registerJEICategory(r, this.guiData, this, t, false);
                } else {
                    AntimatterAPI.registerJEICategoryWorkstation(r, this, t);
                }
            });

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
     * @param tiers optional array of tiers this map is for
     * @return this.
     */
    public T setMap(IRecipeMap map, Tier... tiers){
        if (tiers.length == 0) {
            this.tierRecipeMaps.put("", map);
        } else {
            for (Tier tier : tiers) {
                this.tierRecipeMaps.put(tier.getId(), map);
            }
        }
        addFlags(RECIPE);
        registerJei();
        return (T) this;
    }

    public T baseTexture(Texture tex) {
        this.baseTexture = (m, tier, state) -> new Texture[]{tex};
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

    public T itemModelParent(ResourceLocation parent){
        this.itemModelParent = parent;
        return (T) this;
    }

    public T blockColorHandler(IMachineColorHandlerBlock handlerBlock){
        this.blockColorHandler = handlerBlock;
        return (T) this;
    }

    public T itemColorHandler(IMachineColorHandlerItem handlerItem){
        this.itemColorHandler = handlerItem;
        return (T) this;
    }

    public T modelLoader(ResourceLocation modelLoader){
        this.modelLoader = modelLoader;
        return (T) this;
    }

    public T itemGroup(CreativeModeTab group) {
        this.group = group;
        return (T) this;
    }

    public T setTile(BlockEntityBase.BlockEntitySupplier<BlockEntityMachine<?>, T> func) {
        this.tileFunc = func;
        return (T) this;
    }

    public T setBlock(BiFunction<Machine<T>, Tier, BlockMachine> function){
        this.blockFunc = function;
        return (T) this;
    }

    public T setItemBlockClass(Supplier<Class<? extends BlockMachine>> function){
        this.itemClassSupplier = function;
        return (T) this;
    }

    public T setItemBlock(Function<BlockMachine, AntimatterItemBlock> function){
        itemBlockFunction = function;
        return (T) this;
    }

    public T setToolTag(TagKey<Block> toolTag){
        this.toolTag = toolTag;
        return (T) this;
    }

    public T addTooltipInfo(String translationKey){
        return addTooltipInfo((m, s,w,t,f) -> t.add(Utils.translatable(translationKey)));
    }

    public T addTooltipInfo(Component tooltip){
        return addTooltipInfo((m, s,w,t,f) -> t.add(tooltip));
    }

    @Deprecated
    public T setTooltipInfo(ITooltipInfo info){
        return this.addTooltipInfo(info);
    }

    public T addTooltipInfo(ITooltipInfo info){
        this.tooltipFunctions.add(info);
        return (T) this;
    }

    public T customShape(VoxelShape shape){
        this.shapeGetter = (state, world, pos, context) -> shape;
        return (T) this;
    }

    public T customShape(IShapeGetter shapeGetter){
        this.shapeGetter = shapeGetter;
        return (T) this;
    }

    public T setVerticalFacingAllowed(boolean verticalFacingAllowed) {
        this.verticalFacingAllowed = verticalFacingAllowed;
        return (T) this;
    }

    public T setNoFacing(boolean noFacing){
        this.noFacing = noFacing;
        if (noFacing){
            allowFrontIO();
            frontCovers();
        }
        return (T) this;
    }

    public T setNoTextureRotation(boolean noTextureRotation){
        this.noTextureRotation = noTextureRotation;
        return (T) this;
    }


    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getIdFromTier(Tier tier){
        return id + (tier == NONE ? "" : "_" + tier.getId());
    }

    public Component getDisplayName(Tier tier) {
        String keyAddition = tierSpecificLang ? "." + tier.getId() : "";
        return Utils.translatable("machine." + id + keyAddition, Utils.literal(tier.getId().toUpperCase(Locale.ROOT)).withStyle(tier.getRarityFormatting()));
    }

    public T setClientTicking() {
        this.clientTicking = true;
        return (T) this;
    }

    public T setAmbientTicking(){
        this.ambientTicking = true;
        return (T) this;
    }

    public int getMachineEfficiency(Tier tier) {
        return efficiency.applyAsInt(tier);
    }

    public T custom() {
        return custom(IOverlayModeler.defaultOverride);
    }

    public T custom(IOverlayModeler modeler) {
        this.overlayModels = modeler;
        return (T)this;
    }
    public List<Texture> getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        for (Tier tier : getTiers()) {
            //textures.addAll(Arrays.asList(baseHandler.getBase(this, tier)));
            textures.addAll(Arrays.asList(getBaseTexture(tier, MachineState.IDLE)));
            textures.addAll(Arrays.asList(getBaseTexture(tier, MachineState.ACTIVE)));
            for (int i = 0; i < overlayLayers; i++) {
                textures.addAll(Arrays.asList(getOverlayTextures(MachineState.IDLE, tier, i)));
                textures.addAll(Arrays.asList(getOverlayTextures(MachineState.ACTIVE, tier, i)));
            }
        }
        return textures;
    }

    public Texture[] getBaseTexture(Tier tier, MachineState state) {
        return getDatedBaseHandler().getBase(this, tier, state);
    }

    public Texture getBaseTexture(Tier tier, Direction dir, MachineState state) {
        Texture[] texes = getDatedBaseHandler().getBase(this, tier, state);
        if (texes.length == 1) return texes[0];
        return texes[dir.get3DDataValue()];
    }


    public Texture[] getOverlayTextures(MachineState state, Tier tier, int index) {
        return getDatedOverlayHandler().getOverlays(this, state, tier, index);
    }

    public Texture[] getOverlayTextures(MachineState state, int index) {
        return getDatedOverlayHandler().getOverlays(this, state, this.getFirstTier(), index);
    }

    public ResourceLocation getOverlayModel(MachineState state,Direction side) {
        return overlayModels.getOverlayModel(this, state, side);
    }

    public IRecipeMap getRecipeMap(Tier tier) {
        if (tierRecipeMaps.containsKey(tier.getId())){
            return tierRecipeMaps.get(tier.getId());
        }
        return tierRecipeMaps.get("");
    }

    public T addFlags(MachineFlag... flags) {
        for (MachineFlag flag : flags) {
            FLAG_MAP.computeIfAbsent(flag.toString(), s -> new ObjectOpenHashSet<>()).add(this);
        }
        return (T) this;
    }

    public T addFlags(String... flags) {
        for (String flag : flags) {
            FLAG_MAP.computeIfAbsent(flag, s -> new ObjectOpenHashSet<>()).add(this);
        }
        return (T) this;
    }

    public T removeFlags(MachineFlag... flags) {
        for (MachineFlag flag : flags) {
            FLAG_MAP.computeIfAbsent(flag.toString(), s -> new ObjectOpenHashSet<>()).remove(this);
        }
        return (T) this;
    }

    public T removeFlags(String... flags) {
        for (String flag : flags) {
            FLAG_MAP.computeIfAbsent(flag, s -> new ObjectOpenHashSet<>()).remove(this);
        }
        return (T) this;
    }

    public void setFlags(MachineFlag... flags) {
        FLAG_MAP.forEach((s, m) -> m.remove(this));
        addFlags(flags);
    }

    public T setTiers(Tier... tiers) {
        boolean none = false;
        for (Tier t : tiers){
            if (t == NONE) none = true;
        }
        if (none) this.setTierSpecificLang();
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

    public T setGuiProgressBarForJEI(BarDir dir, boolean barFill){
        guiData.getMachineData().setDir(dir);
        guiData.getMachineData().setBarFill(barFill);
        return (T) this;
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
    public <U extends BlockEntityBasicMultiMachine<U>> void setStructure(Class<U> clazz, Function<StructureBuilder<U>, Structure> func) {
        getTiers().forEach(t -> setStructure(clazz, t, func));
    }

    /**
     * Set the multiblock structure for this machine, for one tier.
     * Useless if the tile is not a multiblock.
     *
     * @param func the function to build a structure.
     */
    public <U extends BlockEntityBasicMultiMachine<U>> void setStructure(Class<U> clazz, Tier tier, Function<StructureBuilder<U>, Structure> func) {
        structures.put(tier, func.apply(new StructureBuilder<>()));
    }

    @Environment(EnvType.CLIENT)
    public boolean renderAsTesr() {
        return renderTesr;
    }

    public T tesr() {
        this.renderTesr = true;
        return (T) this;
    }

    public T renderContainedLiquids(boolean renderContainedLiquidLevel) {
        this.renderContainedLiquids = true;
        this.renderContainedLiquidLevel = renderContainedLiquidLevel;
        return tesr();
    }

    public boolean renderContainerLiquids() {
        return renderContainedLiquids;
    }

    public boolean renderContainerLiquidLevel(){
        return renderContainedLiquidLevel;
    }

    public boolean hasTierSpecificLang(){
        return tierSpecificLang;
    }

    /**
     * Whether or not this machine has the given machine flag.
     *
     * @param flag the flag.
     * @return if it has it;.
     */
    public boolean has(MachineFlag flag) {
        return has(flag.toString());
    }

    /**
     * Whether or not this machine has the given machine flag.
     *
     * @param flag the flag.
     * @return if it has it;.
     */
    public boolean has(String flag) {
        return FLAG_MAP.containsKey(flag) && FLAG_MAP.get(flag).contains(this);
    }

    /**
     * Getters
     **/
    public BlockEntityType<?> getTileType() {
        return tileType;
    }

    public Tier getFirstTier() {
        return tiers.get(0);
    }

    public GuiData getGui() {
        return guiData;
    }

    public Structure getStructure(Tier tier) {
        return structures.get(tier);
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
            if (FLAG_MAP.containsKey(flag.toString())){
                types.addAll(FLAG_MAP.get(flag.toString()));
            }

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

    @Override
    public ResourceLocation getModel(String type, Direction dir) {
        return getOverlayModel(MachineState.IDLE, dir);
    }

    public static final IOverlayTexturer TROLL_OVERLAY_HANDLER = (type, state, tier, i) -> new Texture[] {
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
    };

    public static final ITextureHandler TROLL_BASE_HANDLER = (type, tier, state) -> new Texture[] {
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
            new Texture(Ref.ID, "block/machine/troll"),
    };

    private static SoundEvent getDatedMachineSound(SoundEvent original){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        if (calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) == 1){
            return Ref.JOHN_CENA;
        }
        if (calendar.get(Calendar.MONTH) == Calendar.MARCH && calendar.get(Calendar.DATE) == 31){
            return Ref.JOHN_CENA;
        }
        return original;
    }

    private IOverlayTexturer getDatedOverlayHandler(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        if (calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) == 1){
            return TROLL_OVERLAY_HANDLER;
        }
        if (calendar.get(Calendar.MONTH) == Calendar.MARCH && calendar.get(Calendar.DATE) == 31){
            return TROLL_OVERLAY_HANDLER;
        }
        return overlayTextures;
    }

    private ITextureHandler getDatedBaseHandler(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        if (calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) == 1){
            return TROLL_BASE_HANDLER;
        }
        if (calendar.get(Calendar.MONTH) == Calendar.MARCH && calendar.get(Calendar.DATE) == 31){
            return TROLL_BASE_HANDLER;
        }
        return baseTexture;
    }

    @Override
    public String getLang(String lang) {
        return Utils.lowerUnderscoreToUpperSpaced(this.getId());
    }
}
