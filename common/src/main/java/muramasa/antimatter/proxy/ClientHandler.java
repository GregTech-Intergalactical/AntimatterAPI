package muramasa.antimatter.proxy;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.tesr.MachineTESR;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class ClientHandler implements IProxyHandler {

    @SuppressWarnings("ConstantConditions")
    public ClientHandler() {
        AntimatterTextureStitcher.addStitcher(event -> AntimatterAPI.all(CoverFactory.class).forEach(cover -> {
            if (cover == ICover.emptyFactory)
                return;
            for (ResourceLocation r : cover.getTextures()) {
                event.accept(r);
            }
        }));
    }

    public static boolean isLocal() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return true;
        ClientPacketListener listener =  mc.getConnection();
        if (listener == null) return true;
        return listener.getConnection().isMemoryConnection();
    }

    // Called before resource registration is performed.
    public static void preResourceRegistration() {

        AntimatterModelManager.init();
        AntimatterAPI.all(AntimatterModelLoader.class).forEach(l -> registerLoader(l.getLoc(), l));
        AntimatterDynamics.runAssetProvidersDynamically();
    }

    @ExpectPlatform
    private static void registerLoader(ResourceLocation location, AntimatterModelLoader<?> loader){
    }

    @ExpectPlatform
    public static<T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, BlockEntityRendererProvider<T> renderProvider){
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static void setup() {
        /* Register screens. */
        AntimatterAPI.runLaterClient(() -> {
            Set<ResourceLocation> registered = new ObjectOpenHashSet<>();
            AntimatterAPI.all(MenuHandler.class, h -> {
                if (!registered.contains(AntimatterPlatformUtils.getIdFromMenuType(h.getContainerType()))) {
                    registered.add(AntimatterPlatformUtils.getIdFromMenuType(h.getContainerType()));
                    MenuScreens.register(h.getContainerType(), (MenuScreens.ScreenConstructor) h.screen());
                }
            });
        });
        /* Set up render types. */
        AntimatterAPI.runLaterClient(() -> {
            ModelUtils.setRenderLayer(Data.PROXY_INSTANCE, RenderType.cutout());
            AntimatterAPI.all(BlockMachine.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockMultiMachine.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockOre.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockPipe.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockStorage.class).stream().filter(b -> b.getType() == Data.FRAME)
                    .forEach(b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(AntimatterFluid.class).forEach(f -> {
                ModelUtils.setRenderLayer(f.getFluid(), RenderType.translucent());
                ModelUtils.setRenderLayer(f.getFlowingFluid(), RenderType.translucent());
            });
        });
        AntimatterAPI.all(Machine.class).stream().filter(Machine::renderAsTesr).filter(Machine::renderContainerLiquids).map(Machine::getTileType).distinct().forEach(i -> registerBlockEntityRenderer(i, MachineTESR::new));
    }

    public static void onItemColorHandler(ItemColors colors) {
        for (Item item : AntimatterAPI.all(Item.class)) {
            if (item instanceof IColorHandler h && h.registerColorHandlers()) {
                colors.register((stack, i) -> h.getItemColor(stack, null, i), item);
            }
        }
        for (Block block : AntimatterAPI.all(Block.class)) {
            if (block instanceof IColorHandler h && h.registerColorHandlers()) {
                colors.register((stack, i) -> h.getItemColor(stack, null, i),
                        block.asItem());
            }
        }
    }

    public static void onBlockColorHandler(BlockColors colors) {
        for (Block block : AntimatterAPI.all(Block.class)) {
            if (block instanceof IColorHandler)
                colors.register(((IColorHandler) block)::getBlockColor, block);
        }
    }

    public static void onModelRegistry() {

    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
