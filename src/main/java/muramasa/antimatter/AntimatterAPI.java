package muramasa.antimatter;

import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.registration.*;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import speiger.src.collections.ints.maps.impl.hash.Int2ObjectOpenHashMap;
import speiger.src.collections.ints.maps.interfaces.Int2ObjectMap;
import speiger.src.collections.objects.lists.ObjectArrayList;
import speiger.src.collections.objects.lists.ObjectList;
import speiger.src.collections.objects.maps.impl.hash.Object2ObjectLinkedOpenHashMap;
import speiger.src.collections.objects.maps.impl.hash.Object2ObjectOpenHashMap;
import speiger.src.collections.objects.maps.interfaces.Object2ObjectMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static muramasa.antimatter.util.Utils.getConventionalMaterialType;

public final class AntimatterAPI {

    private static final Object2ObjectMap<Class<?>, Object2ObjectMap<String, Object>> OBJECTS = new Object2ObjectOpenHashMap<>();
    private static final EnumMap<RegistrationEvent, List<Runnable>> CALLBACKS = new EnumMap<>(RegistrationEvent.class);
    private static final EnumSet<RegistrationEvent> REGISTRATION_EVENTS_HANDLED = EnumSet.noneOf(RegistrationEvent.class);
    private static final ObjectList<IBlockUpdateEvent> BLOCK_UPDATE_HANDLERS = new ObjectArrayList<>();
    private static final Int2ObjectMap<Deque<Runnable>> DEFERRED_QUEUE = new Int2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<ResourceLocation, Object> REPLACEMENTS = new Object2ObjectOpenHashMap<>();

    private static IAntimatterRegistrar INTERNAL_REGISTRAR;

    public static void init() {

    }

    /** Internal Registry Section **/

    private synchronized static void registerInternal(Class<?> c, String id, Object o) {
        Object present = null;
        if((present = OBJECTS.computeIfAbsent(c, t -> new Object2ObjectLinkedOpenHashMap<>()).putIfAbsent(id, o)) != null) {
            throw new IllegalStateException(String.join("", "Class ", c.getName(), "'s object: ", id, " has already been registered by: ", present.toString()));
        }
    }

    public static void register(Class<?> c, String id, Object o) {
        registerInternal(c, id, o);
        if (o instanceof Block && notRegistered(Block.class, id)) registerInternal(Block.class, id, o);
        else if (o instanceof Item && notRegistered(Item.class, id)) registerInternal(Item.class, id, o);
        else if (o instanceof IRegistryEntryProvider) {
            String changedId = o instanceof Material ? "material_" + id : o instanceof StoneType ? "stone_" + id : id;
            if (notRegistered(IRegistryEntryProvider.class, changedId)) registerInternal(IRegistryEntryProvider.class, changedId, o);
        }
    }

    public static void register(Class<?> c, IAntimatterObject o) {
        register(c, o.getId(), o);
    }

    public static <T> T registerIfAbsent(Class<? extends T> clazz, String id, Supplier<? extends T> obj) {
        T o = get(clazz, id);
        if (o != null) return o;
        o = obj.get();
        //the constructor of get() might register.
        if (get(clazz, id) == null)
            register(clazz, id, o);
        return o;
    }

    private static boolean notRegistered(Class<?> c, String id) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map == null || !map.containsKey(id);
    }

    @Nullable
    public static <T> T get(Class<T> c, String id) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map != null ? c.cast(map.get(id)) : null;
    }

    @Nonnull
    public static <T> T getOrDefault(Class<T> c, String id, NonNullSupplier<? extends T> supplier) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        T t = map != null ? c.cast(map.get(id)) : null;
        return t != null ? t : supplier.get();
    }

    @Nonnull
    public static <T> T getOrThrow(Class<T> c, String id, Supplier<? extends RuntimeException> supplier) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        T t = map != null ? c.cast(map.get(id)) : null;
        if (t == null) throw supplier.get();
        return t;
    }

    public static <T> boolean has(Class<T> c, String id) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map != null && map.containsKey(id);
    }

    public static <T> List<T> all(Class<T> c) {
        return allInternal(c).collect(Collectors.toList());
    }

    public static <T> List<T> all(Class<T> c, String domain) {
        return allInternal(c, domain).collect(Collectors.toList());
    }

    private static <T> Stream<T> allInternal(Class<T> c) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map == null ? Stream.empty() : map.values().stream().map(c::cast);
    }

    private static <T> Stream<T> allInternal(Class<T> c, @Nonnull String domain) {
        return all(c).stream().filter(o -> o instanceof IAntimatterObject && ((IAntimatterObject) o).getDomain().equals(domain) ||
                o instanceof IForgeRegistryEntry && ((IForgeRegistryEntry<?>) o).getRegistryName() != null && ((IForgeRegistryEntry<?>) o).getRegistryName().getNamespace().equals(domain));
    }

    public static <T> void all(Class<T> c, Consumer<T> consumer) {
        allInternal(c).forEach(consumer);
    }

    public static <T> void all(Class<T> c, String domain, Consumer<T> consumer) {
        allInternal(c, domain).forEach(consumer);
    }

    public static <T> void all(Class<T> c, String[] domains, Consumer<T> consumer) {
        for (String domain : domains) {
            allInternal(c, domain).forEach(consumer);
        }
    }

    private static void runProvider(IAntimatterProvider provider) {
        LogManager.getLogger().debug("Running " + provider.getName());
        provider.run();
    }

    /** DeferredWorkQueue Section **/

    public static Optional<Deque<Runnable>> getCommonDeferredQueue() {
        return Optional.ofNullable(DEFERRED_QUEUE.get(0));
    }

    public static Optional<Deque<Runnable>> getClientDeferredQueue() {
        return Optional.ofNullable(DEFERRED_QUEUE.get(1));
    }

    public static Optional<Deque<Runnable>> getServerDeferredQueue() {
        return Optional.ofNullable(DEFERRED_QUEUE.get(2));
    }

    public static void runLaterCommon(Runnable... r) {
        DEFERRED_QUEUE.computeIfAbsent(0, q -> new LinkedList<>(Arrays.asList(r))).addAll(Arrays.asList(r));
    }

    public static void runLaterClient(Runnable... r) {
        DEFERRED_QUEUE.computeIfAbsent(1, q -> new LinkedList<>(Arrays.asList(r))).addAll(Arrays.asList(r));
    }

    public static void runLaterServer(Runnable... r) {
        DEFERRED_QUEUE.computeIfAbsent(2, q -> new LinkedList<>(Arrays.asList(r))).addAll(Arrays.asList(r));
    }

    /** Registrar Section **/

    public static void onRegistration(RegistrationEvent event) {
        Dist side = (FMLEnvironment.dist.isDedicatedServer() || EffectiveSide.get().isServer()) ? Dist.DEDICATED_SERVER : Dist.CLIENT;
        if (!REGISTRATION_EVENTS_HANDLED.add(event)) {
            if (ModLoadingContext.get().getActiveNamespace().equals(Ref.ID)) return;
            throw new IllegalStateException("The RegistrationEvent " + event.name() + " has already been handled");
        }
        INTERNAL_REGISTRAR.onRegistrationEvent(event, side);
        List<IAntimatterRegistrar> list = all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).collect(Collectors.toList());
        list.forEach(r -> r.onRegistrationEvent(event, side));
        if (CALLBACKS.containsKey(event)) CALLBACKS.get(event).forEach(Runnable::run);
    }

    public static boolean isModLoaded(String mod) {
        return ModList.get().isLoaded(mod);
    }

    public static void runOnEvent(RegistrationEvent event, Runnable runnable) {
        CALLBACKS.computeIfAbsent(event, k -> new ObjectArrayList<>()).add(runnable);
    }

    public static void addRegistrar(IAntimatterRegistrar registrar) {
        if (INTERNAL_REGISTRAR == null && registrar instanceof Antimatter) INTERNAL_REGISTRAR = registrar;
        else if (registrar.isEnabled() || AntimatterConfig.MOD_COMPAT.ENABLE_ALL_REGISTRARS) registerInternal(IAntimatterRegistrar.class, registrar.getId(), registrar);
        FMLJavaModLoadingContext.get().getModEventBus().register(AntimatterRegistration.class);
    }

    public static Optional<IAntimatterRegistrar> getRegistrar(String id) {
        return Optional.ofNullable(get(IAntimatterRegistrar.class, id));
    }

    public static boolean isRegistrarEnabled(String id) {
        return getRegistrar(id).map(IAntimatterRegistrar::isEnabled).orElse(false);
    }

    /** JEI Registry Section **/

    public static void registerJEICategory(RecipeMap<?> map, GuiData gui, Tier tier, String model, boolean override) {
        if (ModList.get().isLoaded(Ref.MOD_JEI) || ModList.get().isLoaded(Ref.MOD_REI)) {
            AntimatterJEIPlugin.registerCategory(map, gui,tier, model, override);
        }
    }

    public static void registerJEICategory(RecipeMap<?> map, GuiData gui, Machine<?> machine, boolean override) {
        if (ModList.get().isLoaded(Ref.MOD_JEI) || ModList.get().isLoaded(Ref.MOD_REI)) {
            AntimatterJEIPlugin.registerCategory(map, gui, machine.getFirstTier(), machine.getId(), override);
        }
    }

    public static void registerJEICategory(RecipeMap<?> map, GuiData gui) {
       registerJEICategory(map,gui,Tier.LV, null, true);
    }

    public static IRecipeRegistrate getRecipeRegistrate() {
        return recipe -> AntimatterAPI.register(IRecipeRegistrate.IRecipeLoader.class, recipe.getClass().getName(), recipe);
    }

    //TODO: Allow other than item.
    public static Item getReplacement(MaterialType<?> type, Material material, String... namespaces) {
        if (type.getId().contains("liquid")) return null;
        ITag.INamedTag<Item> tag = TagUtils.getForgeItemTag(String.join("", getConventionalMaterialType(type), "/", material.getId()));
        return getReplacement(null, tag, namespaces);
    }

    /**
     * This must run after DataGenerators have ran OR when the tag jsons are acknowledged. Otherwise this is useless!
     *
     * @param originalItem  Item that wants a replacement, may be null if only the tag should be the only query
     * @param tag           Tag that wants a replacement (as the originalItem may have multiple tags to search from)
     * @param namespaces    Namespaces of the tags to check against, by default this only checks against 'minecraft' if no namespaces are defined
     * @return originalItem if there's nothing found, null if there is no originalItem, or an replacement
     */
    public static <T> T getReplacement(@Nullable T originalItem, ITag.INamedTag<T> tag, String... namespaces) {
        if (tag == null) throw new IllegalArgumentException("AntimatterAPI#getReplacement received a null tag!");
        if (REPLACEMENTS.containsKey(tag.getName()))  return (T) REPLACEMENTS.get(tag.getName());//return RecipeIngredient.of(REPLACEMENTS.get(tag.getName().getPath().hashCode()),1);
        return originalItem;
        //if (replacementsFound) return originalItem;
        //Set<String> checks = Sets.newHashSet(namespaces);
        //if (checks.isEmpty()) checks.add("minecraft");
        /*Objects.requireNonNull(TagUtils.nc(tag).getAllElements().stream().filter(i -> checks.contains(Objects.requireNonNull(i.getRegistryName()).getNamespace()))
                .findAny().map(i -> {
                    REPLACEMENTS.put(tag.getName(), i);
                    return i;
                }).orElse(originalItem));*/
    }


    public static <T> void addReplacement(ResourceLocation tag, T obj) {
        REPLACEMENTS.put(tag, obj);
    }

    public static <T> void addReplacement(ITag.INamedTag<Item> tag, T obj) {
        REPLACEMENTS.put(tag.getName(), obj);
    }

    public static void registerBlockUpdateHandler(IBlockUpdateEvent handler) {
        BLOCK_UPDATE_HANDLERS.add(handler);
    }

    /**
     * COREMOD METHOD INSERTION: Runs every time when this is called:
     * @see ServerWorld#notifyBlockUpdate(BlockPos, BlockState, BlockState, int)
     */
    @SuppressWarnings("unused")
    public static void onNotifyBlockUpdate(World world, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        BLOCK_UPDATE_HANDLERS.forEach(h -> h.onNotifyBlockUpdate(world, pos, oldState, newState, flags));
    }

    public interface IBlockUpdateEvent {

        void onNotifyBlockUpdate(World world, BlockPos pos, BlockState oldState, BlockState newState, int flags);
    }
}
