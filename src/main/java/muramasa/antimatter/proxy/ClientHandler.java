package muramasa.antimatter.proxy;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.registration.IColorHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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

        IEventBus forge = MinecraftForge.EVENT_BUS;

        forge.register(MaterialType.class);
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
                    ScreenManager.registerFactory(h.getContainerType(), (ScreenManager.IScreenFactory) h.screen());
                }
            });
        });
        /* Set up render types. */
        AntimatterAPI.runLaterClient(() -> {
            RenderTypeLookup.setRenderLayer(Data.PROXY_INSTANCE, RenderType.getCutout());
            AntimatterAPI.all(BlockMachine.class, b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(BlockMultiMachine.class, b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(BlockOre.class, b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(BlockPipe.class, b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(BlockStorage.class).stream().filter(b -> b.getType() == Data.FRAME)
                    .forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(AntimatterFluid.class).forEach(f -> {
                RenderTypeLookup.setRenderLayer(f.getFluid(), RenderType.getTranslucent());
                RenderTypeLookup.setRenderLayer(f.getFlowingFluid(), RenderType.getTranslucent());
            });
        });
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
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
