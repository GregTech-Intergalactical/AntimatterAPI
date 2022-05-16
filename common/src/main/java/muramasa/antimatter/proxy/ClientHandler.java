package muramasa.antimatter.proxy;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.AntimatterTextureStitcher;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Set;

public class ClientHandler implements IProxyHandler {

    @SuppressWarnings("ConstantConditions")
    public ClientHandler() {
        /* Client event listeners. */
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ClientHandler::onItemColorHandler);
        eventBus.addListener(ClientHandler::onBlockColorHandler);
        eventBus.addListener(ClientHandler::onModelRegistry);
        eventBus.addListener(AntimatterTextureStitcher::onTextureStitch);
        AntimatterTextureStitcher.addStitcher(event -> AntimatterAPI.all(CoverFactory.class).forEach(cover -> {
            if (cover == ICover.emptyFactory)
                return;
            for (ResourceLocation r : cover.getTextures()) {
                event.accept(r);
            }
        }));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::preResourceRegistration);

    }

    public static boolean isLocal() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return true;
        ClientPacketListener listener =  mc.getConnection();
        if (listener == null) return true;
        return listener.getConnection().isMemoryConnection();
    }

    // Called before resource registration is performed.
    public static void preResourceRegistration(ParticleFactoryRegisterEvent ev) {

        AntimatterModelManager.init();
        AntimatterAPI.all(AntimatterModelLoader.class).forEach(l -> ModelLoaderRegistry.registerLoader(l.getLoc(), l));
        AntimatterDynamics.runAssetProvidersDynamically();
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static void setup(FMLClientSetupEvent e) {
        /* Register screens. */
        AntimatterAPI.runLaterClient(() -> {
            Set<ResourceLocation> registered = new ObjectOpenHashSet<>();
            AntimatterAPI.all(MenuHandler.class, h -> {
                if (!registered.contains(h.getContainerType().getRegistryName())) {
                    registered.add(h.getContainerType().getRegistryName());
                    MenuScreens.register(h.getContainerType(), (MenuScreens.ScreenConstructor) h.screen());
                }
            });
        });
        /* Set up render types. */
        AntimatterAPI.runLaterClient(() -> {
            ItemBlockRenderTypes.setRenderLayer(Data.PROXY_INSTANCE, RenderType.cutout());
            AntimatterAPI.all(BlockMachine.class, b -> ItemBlockRenderTypes.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockMultiMachine.class, b -> ItemBlockRenderTypes.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockOre.class, b -> ItemBlockRenderTypes.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockPipe.class, b -> ItemBlockRenderTypes.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockStorage.class).stream().filter(b -> b.getType() == Data.FRAME)
                    .forEach(b -> ItemBlockRenderTypes.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(AntimatterFluid.class).forEach(f -> {
                ItemBlockRenderTypes.setRenderLayer(f.getFluid(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(f.getFlowingFluid(), RenderType.translucent());
            });
        });
        AntimatterAPI.all(Machine.class).stream().filter(Machine::renderAsTesr).filter(Machine::renderContainerLiquids).map(Machine::getTileType).distinct().forEach(i -> BlockEntityRenderers.register(i, MachineTESR::new));
    }

    public static void onItemColorHandler(ColorHandlerEvent.Item e) {
        for (Item item : AntimatterAPI.all(Item.class)) {
            if (item instanceof IColorHandler && ((IColorHandler) item).registerColorHandlers()) {
                e.getItemColors().register((stack, i) -> ((IColorHandler) item).getItemColor(stack, null, i), item);
            }
        }
        for (Block block : AntimatterAPI.all(Block.class)) {
            if (block instanceof IColorHandler && ((IColorHandler) block).registerColorHandlers()) {
                e.getItemColors().register((stack, i) -> ((IColorHandler) block).getItemColor(stack, null, i),
                        block.asItem());
            }
        }
    }

    public static void onBlockColorHandler(ColorHandlerEvent.Block e) {
        for (Block block : AntimatterAPI.all(Block.class)) {
            if (block instanceof IColorHandler)
                e.getBlockColors().register(((IColorHandler) block)::getBlockColor, block);
        }
    }

    public static void onModelRegistry(ModelRegistryEvent e) {

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
