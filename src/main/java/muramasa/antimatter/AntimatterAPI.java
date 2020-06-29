package muramasa.antimatter;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.providers.dummy.DummyTagProviders;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.recipe.RecipeMap;
import muramasa.antimatter.registration.*;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.util.Utils.getConventionalMaterialType;

public final class AntimatterAPI {

    private static final Object2ObjectMap<Class<?>, Object2ObjectMap<String, Object>> OBJECTS = new Object2ObjectOpenHashMap<>();
    private static final EnumMap<RegistrationEvent, List<Runnable>> CALLBACKS = new EnumMap<>(RegistrationEvent.class);
    private static final EnumSet<RegistrationEvent> REGISTRATION_EVENTS_HANDLED = EnumSet.noneOf(RegistrationEvent.class);
    private static final Object2ObjectOpenHashMap<String, List<Function<DataGenerator, IAntimatterProvider>>> PROVIDERS = new Object2ObjectOpenHashMap<>();
    private static final ObjectList<IBlockUpdateEvent> BLOCK_UPDATE_HANDLERS = new ObjectArrayList<>();
    private static final Int2ObjectMap<Deque<Runnable>> DEFERRED_QUEUE = new Int2ObjectOpenHashMap<>();

    private static final Int2ObjectMap<Item> REPLACEMENTS = new Int2ObjectOpenHashMap<>();
    private static boolean replacementsFound = false;

    private static IAntimatterRegistrar INTERNAL_REGISTRAR;

    /** Internal Registry Section **/

    private static void registerInternal(Class<?> c, String id, Object o) {
        OBJECTS.putIfAbsent(c, new Object2ObjectLinkedOpenHashMap<>());
        Object key = OBJECTS.get(c).get(id);
        if (key != null) throw new IllegalStateException(String.join("", "Class ", c.getName(), "'s object: ", id, " has already been registered by: ", key.toString()));
        OBJECTS.get(c).put(id, o);
    }

    public static void register(Class<?> c, String id, Object o) {
        registerInternal(c, id, o);
        if (o instanceof Block && notRegistered(Block.class, id)) registerInternal(Block.class, id, o);
        else if (o instanceof Item && notRegistered(Item.class, id)) registerInternal(Item.class, id, o);
        else if (o instanceof IRegistryEntryProvider && notRegistered(IRegistryEntryProvider.class, id)) registerInternal(IRegistryEntryProvider.class, id, o);
    }

    public static void register(Class<?> c, IAntimatterObject o) {
        register(c, o.getId(), o);
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

    public static <T> boolean has(Class<T> c, String id) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map != null && map.containsKey(id);
    }

    public static <T> List<T> all(Class<T> c) {
        Object2ObjectMap<String, Object> map = OBJECTS.get(c);
        return map != null ? map.values().stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static <T> List<T> all(Class<T> c, String domain) {
        return all(c).stream().filter(o -> o instanceof IAntimatterObject && ((IAntimatterObject) o).getDomain().equals(domain) ||
            o instanceof IForgeRegistryEntry && ((IForgeRegistryEntry<?>) o).getRegistryName() != null && ((IForgeRegistryEntry<?>) o).getRegistryName().getNamespace().equals(domain)).collect(Collectors.toList());
    }

    public static <T> void all(Class<T> c, Consumer<T> consumer) {
        all(c).forEach(consumer);
    }

    public static <T> void all(Class<T> c, String domain, Consumer<T> consumer) {
        all(c, domain).forEach(consumer);
    }

    /** Providers and Dynamic Resource Pack Section **/
    public static void addProvider(String domain, Function<DataGenerator, IAntimatterProvider> providerFunc) {
        PROVIDERS.computeIfAbsent(domain, k -> new ObjectArrayList<>()).add(providerFunc);
    }

    public static void onProviderInit(String domain, DataGenerator gen, Dist side) {
        PROVIDERS.getOrDefault(domain, Collections.emptyList()).stream().map(f -> f.apply(gen)).filter(p -> p.getSide().equals(side)).forEach(gen::addProvider);
    }

    public static void runBackgroundProviders() {
        Antimatter.LOGGER.info("Running DummyTagProviders...");
        Ref.BACKGROUND_GEN.addProviders(DummyTagProviders.DUMMY_PROVIDERS);
        try {
            Ref.BACKGROUND_GEN.run();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Can't run this in parallel since ItemTagsProviders need BlockTagsProviders to run first
    public static void runDataProvidersDynamically() {
        replacementsFound = true;
        PROVIDERS.forEach((k, v) -> v.stream().map(f -> f.apply(Ref.BACKGROUND_GEN)).filter(p -> p.getSide().equals(Dist.DEDICATED_SERVER)).forEach(AntimatterAPI::runProvider));
    }

    public static void runAssetProvidersDynamically() {
        PROVIDERS.forEach((k, v) -> v.parallelStream().map(f -> f.apply(Ref.BACKGROUND_GEN)).filter(p -> p.getSide().equals(Dist.CLIENT)).forEach(AntimatterAPI::runProvider));
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
        if (!REGISTRATION_EVENTS_HANDLED.add(event)) {
            if (ModLoadingContext.get().getActiveNamespace().equals(Ref.ID)) return;
            throw new IllegalStateException("The RegistrationEvent " + event.name() + " has already been handled");
        }
        INTERNAL_REGISTRAR.onRegistrationEvent(event);
        all(IAntimatterRegistrar.class, r -> r.onRegistrationEvent(event));
        if (CALLBACKS.containsKey(event)) CALLBACKS.get(event).forEach(Runnable::run);
    }

    public static void runOnEvent(RegistrationEvent event, Runnable runnable) {
        CALLBACKS.computeIfAbsent(event, k -> new ObjectArrayList<>()).add(runnable);
    }

    public static void addRegistrar(IAntimatterRegistrar registrar) {
        if (INTERNAL_REGISTRAR == null && registrar instanceof Antimatter) INTERNAL_REGISTRAR = registrar;
        else if (registrar.isEnabled() || AntimatterConfig.MOD_COMPAT.ENABLE_ALL_REGISTRARS) registerInternal(IAntimatterRegistrar.class, registrar.getId(), registrar);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AntimatterRegistration::onRegister);
    }

    public static Optional<IAntimatterRegistrar> getRegistrar(String id) {
        return Optional.ofNullable(get(IAntimatterRegistrar.class, id));
    }

    public static boolean isRegistrarEnabled(String id) {
        return getRegistrar(id).map(IAntimatterRegistrar::isEnabled).orElse(false);
    }

    public static Item getReplacement(MaterialType<?> type, Material material, String... namespaces) {
        Tag<Item> tag = Utils.getForgeItemTag(String.join("", getConventionalMaterialType(type), "/", material.getId()));
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
    public static Item getReplacement(@Nullable Item originalItem, Tag<Item> tag, String... namespaces) {
        if (tag == null) throw new IllegalArgumentException("AntimatterAPI#getReplacement received a null tag!");
        if (REPLACEMENTS.containsKey(tag.getId().getPath().hashCode())) return REPLACEMENTS.get(tag.getId().getPath().hashCode());
        if (replacementsFound) return originalItem;
        Set<String> checks = Sets.newHashSet(namespaces);
        if (checks.isEmpty()) checks.add("minecraft");
        return tag.getAllElements().stream().filter(i -> checks.contains(Objects.requireNonNull(i.getRegistryName()).getNamespace()))
            .findAny().map(i -> {
                REPLACEMENTS.put(tag.getId().getPath().hashCode(), i);
                return i;
            }).orElse(originalItem);
    }

    /** JEI Registry Section **/
    public static void registerJEICategory(RecipeMap<?> map, GuiData gui) {
        if (ModList.get().isLoaded(Ref.MOD_JEI)) {
            //AntimatterJEIPlugin.registerCategory(map, gui);
        }
    }

    /** Attempts to do smart interaction with a compatible Tile/Block **/
    public static boolean onInteract(TileEntity tile, PlayerEntity player, Hand hand, Direction side) {
        boolean result = tile.getCapability(AntimatterCaps.COVERABLE, side).map(h -> h.onInteract(player, hand, side, Utils.getToolType(player))).orElse(false);
        result = tile.getCapability(AntimatterCaps.INTERACTABLE, side).map(h -> h.onInteract(player, hand, side, Utils.getToolType(player))).orElse(false);
        return result;
    }

    public static void registerBlockUpdateHandler(IBlockUpdateEvent handler) {
        BLOCK_UPDATE_HANDLERS.add(handler);
    }

    /**
     * COREMOD METHOD INSERTION: Runs every time when this is called:
     * @see ServerWorld#notifyBlockUpdate(BlockPos, BlockState, BlockState, int)
     */
    @SuppressWarnings("unused")
    public static void onNotifyBlockUpdate(ServerWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
        BLOCK_UPDATE_HANDLERS.forEach(h -> h.onNotifyBlockUpdate(world, pos, oldState, newState));
    }

    public interface IBlockUpdateEvent {

        void onNotifyBlockUpdate(ServerWorld world, BlockPos pos, BlockState oldState, BlockState newState);
    }
}
