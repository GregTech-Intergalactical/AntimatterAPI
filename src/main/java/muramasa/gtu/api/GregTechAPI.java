package muramasa.gtu.api;

import muramasa.gtu.Configs;
import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.ICoverHandler;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.cover.impl.*;
import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.data.RecipeMaps;
import muramasa.gtu.api.gui.GuiData;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IGregTechRegistrar;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.TileEntityRecipeMachine;
import muramasa.gtu.api.tileentities.TileEntitySteamMachine;
import muramasa.gtu.api.tileentities.TileEntityTank;
import muramasa.gtu.api.tileentities.multi.*;
import muramasa.gtu.api.tileentities.pipe.TileEntityCable;
import muramasa.gtu.api.tileentities.pipe.TileEntityFluidPipe;
import muramasa.gtu.api.tileentities.pipe.TileEntityItemPipe;
import muramasa.gtu.api.tileentities.pipe.TileEntityPipe;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.integration.jei.GregTechJEIPlugin;
import muramasa.gtu.loaders.InternalRegistrar;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class GregTechAPI {

    public static final Set<Item> ITEMS = new LinkedHashSet<>();
    public static final Set<Block> BLOCKS = new LinkedHashSet<>();
    public static final Set<Class> TILES = new HashSet<>();
    private static final HashMap<Class<?>, LinkedHashMap<String, IGregTechObject>> OBJECTS = new HashMap<>();
    private static final IGregTechRegistrar INTERNAL_REGISTRAR = new InternalRegistrar();
    private static final HashMap<String, IGregTechRegistrar> REGISTRARS = new HashMap<>();
    private static final HashMap<String, List<Runnable>> CALLBACKS = new HashMap<>();
    private static final LinkedHashMap<String, ItemStack> REPLACEMENTS = new LinkedHashMap<>();

    static {
        register(TileEntityMachine.class);
        register(TileEntityRecipeMachine.class);
        register(TileEntitySteamMachine.class);
        register(TileEntityMultiMachine.class);
        register(TileEntityBasicMultiMachine.class);
        register(TileEntityTank.class);
        register(TileEntityHatch.class);

        register(TileEntityPipe.class);
        register(TileEntityItemPipe.class);
        register(TileEntityFluidPipe.class);
        register(TileEntityCable.class);
        register(TileEntityCasing.class);

        registerJEICategory(RecipeMaps.ORE_BY_PRODUCTS, Guis.MULTI_DISPLAY_COMPACT);
//        GregTechAPI.registerJEICategory(RecipeMaps.SMELTING, Guis.MULTI_DISPLAY_COMPACT);
        registerJEICategory(RecipeMaps.STEAM_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        registerJEICategory(RecipeMaps.GAS_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        registerJEICategory(RecipeMaps.COMBUSTION_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        registerJEICategory(RecipeMaps.NAQUADAH_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        registerJEICategory(RecipeMaps.PLASMA_FUELS, Guis.MULTI_DISPLAY_COMPACT);
    }

    public static void register(Object o) {
        if (o instanceof Item) ITEMS.add((Item) o);
        else if (o instanceof Block) BLOCKS.add((Block) o);
        else if (o instanceof Class) TILES.add((Class) o);
    }

    public static void register(Class c, IGregTechObject o) {
        if (!OBJECTS.containsKey(c)) OBJECTS.put(c, new LinkedHashMap<>());
        if (!OBJECTS.get(c).containsKey(o.getId()))  {
            OBJECTS.get(c).put(o.getId(), o);
        } else GregTech.LOGGER.error("Object: " + o.getId() + " has already been registered! This is a error!");
        register(o);
    }

    @Nullable
    public static <T> T get(Class<T> c, String id) {
        LinkedHashMap<String, IGregTechObject> map = OBJECTS.get(c);
        return map != null ? c.cast(map.get(id)) : null;
    }

    public static <T> List<T> all(Class<T> c) {
        LinkedHashMap<String, IGregTechObject> map = OBJECTS.get(c);
        if (map == null) return Collections.emptyList();
        return map.values().stream().map(c::cast).collect(Collectors.toList());
    }

    /** Registrar Section **/
    public static void onRegistration(RegistrationEvent event) {
        INTERNAL_REGISTRAR.onRegistrationEvent(event);
        REGISTRARS.values().forEach(r -> r.onRegistrationEvent(event));
        if (CALLBACKS.containsKey(event.name())) CALLBACKS.get(event.name()).forEach(Runnable::run);
    }

    public static void onEvent(RegistrationEvent event, Runnable runnable) {
        if (!CALLBACKS.containsKey(event.name())) CALLBACKS.put(event.name(), new ArrayList<>());
        CALLBACKS.get(event.name()).add(runnable);
    }

    public static void addRegistrar(IGregTechRegistrar registrar) {
        if (registrar.isEnabled() || Configs.MISC.ENABLE_ALL_REGISTRARS) REGISTRARS.put(registrar.getId(), registrar);
    }

    @Nullable
    public static IGregTechRegistrar getRegistrar(String id) {
        return REGISTRARS.get(id);
    }

    public static boolean isRegistrarEnabled(String id) {
        IGregTechRegistrar registrar = getRegistrar(id);
        return registrar != null && registrar.isEnabled();
    }

    @Nullable
    public static Item getItem(String domain, String path) {
        return Item.getByNameOrId(new ResourceLocation(domain, path).toString());
    }

    @Nullable
    public static Block getBlock(String domain, String path) {
        return Block.getBlockFromName(new ResourceLocation(domain, path).toString());
    }

    /** Item Registry Section **/
    public static void addReplacement(MaterialType type, Material material, ItemStack stack) {
        REPLACEMENTS.put(type.getId() + material.getId(), stack);
    }

    @Nullable
    public static ItemStack getReplacement(MaterialType type, Material material) {
        ItemStack stack = REPLACEMENTS.get(type.getId() + material.getId());
        return stack != null ? stack.copy() : null;
    }

    /** JEI Registry Section **/
    public static void registerJEICategory(RecipeMap map, GuiData gui) {
        if (Utils.isModLoaded(Ref.MOD_JEI)) {
            GregTechJEIPlugin.registerCategory(map, gui);
        }
    }

    /** Fluid Cell Registry **/
    private final static Collection<ItemStack> FLUID_CELL_REGISTRY = new ArrayList<>();

    public static void registerFluidCell(ItemStack stack) {
        if (!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) return;
        FLUID_CELL_REGISTRY.add(stack);
    }

    public static List<ItemStack> getFluidCells() {
        List<ItemStack> cells = new ArrayList<>();
        FLUID_CELL_REGISTRY.forEach(c -> cells.add(c.copy()));
        return cells;
    }

    public static Collection<ItemStack> getFluidCells(Fluid fluid) {
        return getFluidCells(fluid, -1);
    }

    public static Collection<ItemStack> getFluidCells(Fluid fluid, int amount) {
        Collection<ItemStack> cells = getFluidCells();
        for (ItemStack stack : cells) {
            IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (fluidHandler == null) continue;
            amount = amount != -1 ? amount : Integer.MAX_VALUE;
            fluidHandler.fill(new FluidStack(fluid, amount), true);
        }
        return cells;
    }

    /** Cover Registry Section **/
    private final static HashMap<String, Cover> COVER_REGISTRY = new HashMap<>();
    private final static HashMap<Item, Cover> CATALYST_TO_COVER = new HashMap<>();

    /** IMPORTANT: These should only be used to compare instances. **/
    public final static Cover CoverNone = new CoverNone();
    public final static Cover CoverPlate = new CoverPlate();
    public final static Cover CoverItem = new CoverItem(Tier.LV);
    public final static Cover CoverFluid = new CoverFluid(Tier.LV);
    public final static Cover CoverEnergy = new CoverEnergy(Tier.LV);

    /**
     * Registers a cover behaviour. This must be done during preInit.
     * @param cover The behaviour instance to be attached.
     */
    public static void registerCover(Cover cover) {
        cover.onRegister();
        COVER_REGISTRY.put(cover.getName(), cover);
    }

    public static Cover getCover(String name) {
        return COVER_REGISTRY.get(name);
    }

    public static void registerCoverStack(ItemStack stack, Cover cover) {
        CATALYST_TO_COVER.put(stack.getItem(), cover);
    }

    public static Cover getCoverFromCatalyst(ItemStack stack) {
        return CATALYST_TO_COVER.get(stack.getItem());
    }

    public static Collection<Cover> getRegisteredCovers() {
        return COVER_REGISTRY.values();
    }

    /** Attempts to place a cover on a tile at a given side **/
    public static boolean placeCover(TileEntity tile, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (stack.isEmpty()) return false;
        ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, side);
        if (coverHandler == null) return false;
        Cover cover = GregTechAPI.getCoverFromCatalyst(stack);
        if (cover == null) return false;
        if (coverHandler.set(Utils.getInteractSide(side, hitX, hitY, hitZ), cover.onNewInstance(Utils.ca(1, stack), getCover(cover.getName()).getInternalId()))) {
            stack.shrink(1);
            return true;
        }
        return false;
    }

    /** Attempts to remove a cover at a given side **/
    public static boolean removeCover(EntityPlayer player, ICoverHandler coverHandler, EnumFacing side) {
        ItemStack toDrop = coverHandler.get(side).getDroppedStack();
        if (coverHandler.set(side, CoverNone)) {
            player.dropItem(toDrop, false);
            return true;
        }
        return false;
    }
}
