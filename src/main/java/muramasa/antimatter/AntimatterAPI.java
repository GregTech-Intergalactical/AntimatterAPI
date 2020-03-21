package muramasa.antimatter;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.recipe.RecipeMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.RegistrationEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class AntimatterAPI {

    private static final HashMap<Class<?>, LinkedHashMap<String, Object>> OBJECTS = new HashMap<>();
    private static final HashMap<String, List<Runnable>> CALLBACKS = new HashMap<>();

    private static final Int2ObjectOpenHashMap<Material> MATERIAL_HASH_LOOKUP = new Int2ObjectOpenHashMap<>();
    private static final Set<RegistrationEvent> REGISTRATION_EVENTS_HANDLED = new HashSet<>();

    private static IAntimatterRegistrar INTERNAL_REGISTRAR;

    public static final ToolType WRENCH_TOOL_TYPE = ToolType.get("wrench");

    private static void registerInternal(Class<?> c, String id, Object o, boolean checkDuplicates) {
        OBJECTS.putIfAbsent(c, new LinkedHashMap<>());
        if (checkDuplicates && OBJECTS.get(c).containsKey(id)) throw new IllegalStateException("Object: " + id + " for class " + c.getName() + " has already been registered by " + OBJECTS.get(c).get(id));
        OBJECTS.get(c).put(id, o);
    }

    private static boolean hasBeenRegistered(Class<?> c, String id) {
        return OBJECTS.containsKey(c) && OBJECTS.get(c).containsKey(id);
    }

    public static void register(Class<?> c, String id, Object o) {
        registerInternal(c, id, o, true);
        if (o instanceof Item && !hasBeenRegistered(Item.class, id)) registerInternal(Item.class, id, o, true);
        if (o instanceof Block && !hasBeenRegistered(Block.class, id)) registerInternal(Block.class, id, o, true);
        if (o instanceof Material) MATERIAL_HASH_LOOKUP.put(((Material) o).getHash(), (Material) o);
    }

    public static void register(Class<?> c, IAntimatterObject o) {
        register(c, o.getId(), o);
    }

    public static void overrideRegistryObject(Class<?> c, String id, Object o) {
        registerInternal(c, id, o, false);
    }

    @Nullable
    public static <T> T get(Class<T> c, String id) {
        LinkedHashMap<String, Object> map = OBJECTS.get(c);
        return map != null ? c.cast(map.get(id)) : null;
    }

    public static <T> boolean has(Class<T> c, String id) {
        LinkedHashMap<String, Object> map = OBJECTS.get(c);
        return map != null && map.containsKey(id);
    }

    public static <T> List<T> all(Class<T> c) {
        LinkedHashMap<String, Object> map = OBJECTS.get(c);
        return map != null ? map.values().stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static Material getMaterial(String id) {
        Material material = get(Material.class, id);
        return material != null ? material : Data.NULL;
    }

    @Nullable
    public static Material getMaterialById(int hash) {
        return MATERIAL_HASH_LOOKUP.get(hash);
    }

    /** Registrar Section **/
    public static void onRegistration(RegistrationEvent event) {
        if (REGISTRATION_EVENTS_HANDLED.contains(event)) throw new IllegalStateException("The RegistrationEvent " + event.name() + " has already been handled");
        REGISTRATION_EVENTS_HANDLED.add(event);
        if (INTERNAL_REGISTRAR == null) INTERNAL_REGISTRAR = Antimatter.INSTANCE;
        INTERNAL_REGISTRAR.onRegistrationEvent(event);
        all(IAntimatterRegistrar.class).forEach(r -> r.onRegistrationEvent(event));
        if (CALLBACKS.containsKey(event.name())) CALLBACKS.get(event.name()).forEach(Runnable::run);
    }

    public static void onEvent(RegistrationEvent event, Runnable runnable) {
        CALLBACKS.computeIfAbsent(event.name(), k -> new ArrayList<>()).add(runnable);
    }

    public static void addRegistrar(IAntimatterRegistrar registrar) {
        if (registrar.isEnabled() || Configs.MODCOMPAT.ENABLE_ALL_REGISTRARS) registerInternal(IAntimatterRegistrar.class, registrar.getId(), registrar, true);
    }

    public static Optional<IAntimatterRegistrar> getRegistrar(String id) {
        IAntimatterRegistrar registrar = get(IAntimatterRegistrar.class, id);
        return registrar != null ? Optional.of(registrar) : Optional.empty();
    }

    public static boolean isRegistrarEnabled(String id) {
        return getRegistrar(id).map(IAntimatterRegistrar::isEnabled).orElse(false);
    }

//    @Nullable
//    public static Item getItem(String domain, String path) {
//        return Item.getByNameOrId(new ResourceLocation(domain, path).toString());
//    }
//
//    @Nullable
//    public static Block getBlock(String domain, String path) {
//        return Block.getBlockFromName(new ResourceLocation(domain, path).toString());
//    }

    /** Item Registry Section **/
    public static void addReplacement(MaterialType<?> type, Material material, ItemStack stack) {
        registerInternal(ItemStack.class, type.getId() + material.getId(), stack, true);
    }

    public static ItemStack getReplacement(MaterialType<?> type, Material material) {
        ItemStack stack = get(ItemStack.class, type.getId() + material.getId());
        return stack != null ? stack.copy() : ItemStack.EMPTY;
    }

    /** JEI Registry Section **/
    public static void registerJEICategory(RecipeMap map, GuiData gui) {
        if (ModList.get().isLoaded(Ref.MOD_JEI)) {
            //AntimatterJEIPlugin.registerCategory(map, gui);
        }
    }

    /** Fluid Cell Registry **/
    private final static Collection<ItemStack> FLUID_CELL_REGISTRY = new ArrayList<>();

    public static void registerFluidCell(ItemStack stack) {
        //if (!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) return;
        //FLUID_CELL_REGISTRY.add(stack);
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
//        for (ItemStack stack : cells) {
//            IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
//            if (fluidHandler == null) continue;
//            amount = amount != -1 ? amount : Integer.MAX_VALUE;
//            fluidHandler.fill(new FluidStack(fluid, amount), true);
//        }
        return cells;
    }

    /** Cover Registry Section **/
    private final static HashMap<String, Cover> COVER_REGISTRY = new HashMap<>();
    private final static HashMap<Item, Cover> CATALYST_TO_COVER = new HashMap<>();

    /**
     * Registers a cover behaviour. This must be done during preInit.
     * @param cover The behaviour instance to be attached.
     */
    public static void registerCover(Cover cover) {
        cover.onRegister();
        COVER_REGISTRY.put(cover.getId(), cover);
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

    /** Attempts to do smart interaction with a compatible Tile/Block **/
    public static boolean interact(TileEntity tile, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        Direction targetSide = Utils.getInteractSide(side, hitX, hitY, hitZ);
//        if (GregTechAPI.placeCover(tile, player, player.getHeldItem(hand), targetSide, hitX, hitY, hitZ)) return true;
//        if (tile.hasCapability(GTCapabilities.COVERABLE, targetSide)) {
//            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, targetSide);
//            if (coverHandler != null && coverHandler.onInteract(player, hand, targetSide, ToolType.get(player.getHeldItem(hand)))) return true;
//        }
//        if (tile.hasCapability(GTCapabilities.CONFIGURABLE, targetSide)) {
//            IConfigHandler configHandler = tile.getCapability(GTCapabilities.CONFIGURABLE, targetSide);
//            if (configHandler != null && configHandler.onInteract(player, hand, targetSide, ToolType.get(player.getHeldItem(hand)))) return true;
//        }
        return false;
    }

    /** Attempts to place a cover on a tile at a given side **/
    public static boolean placeCover(TileEntity tile, PlayerEntity player, ItemStack stack, Direction side, float hitX, float hitY, float hitZ) {
//        if (stack.isEmpty()) return false;
//        ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, side);
//        if (coverHandler == null) return false;
//        Cover cover = GregTechAPI.getCoverFromCatalyst(stack);
//        if (cover == null) return false;
//        if (coverHandler.set(Utils.getInteractSide(side, hitX, hitY, hitZ), cover.onNewInstance(Utils.ca(1, stack)))) {
//            if (!player.isCreative()) stack.shrink(1);
//            return true;
//        }
        return false;
    }

    /** Attempts to remove a cover at a given side **/
    public static boolean removeCover(PlayerEntity player, ICoverHandler coverHandler, Direction side) {
        ItemStack toDrop = coverHandler.get(side).getDroppedStack();
        if (coverHandler.set(side, Data.COVER_NONE)) {
            if (!player.isCreative()) player.dropItem(toDrop, false);
            return true;
        }
        return false;
    }
}
