package muramasa.antimatter.proxy;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockFakeTile;
import muramasa.antimatter.block.BlockFrame;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.block.BlockSurfaceRock;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.tesr.MachineTESR;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    @ExpectPlatform
    public static<T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, BlockEntityRendererProvider<T> renderProvider){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static<T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> renderProvider){
        throw new AssertionError();
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static void setup() {
        MaterialType.buildTooltips();
        AntimatterAPI.all(Material.class, Material::setChemicalFormula);
        /* Register screens. */
        AntimatterAPI.runLaterClient(() -> {
            Set<ResourceLocation> registered = new ObjectOpenHashSet<>();
            AntimatterAPI.all(MenuHandler.class, h -> {
                if (!registered.contains(AntimatterPlatformUtils.INSTANCE.getIdFromMenuType(h.getContainerType()))) {
                    registered.add(AntimatterPlatformUtils.INSTANCE.getIdFromMenuType(h.getContainerType()));
                    MenuScreens.register(h.getContainerType(), AntimatterAPI.get(MenuScreens.ScreenConstructor.class, h.screenID(), h.screenDomain()));
                }
            });
        });
        /* Set up render types. */
        AntimatterAPI.runLaterClient(() -> {
            AntimatterAPI.all(BlockMachine.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockFakeTile.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockMultiMachine.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockOre.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockPipe.class, b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockStorage.class).stream().filter(b -> b.getType() == AntimatterMaterialTypes.RAW_ORE_BLOCK)
                    .forEach(b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockFrame.class).stream().filter(b -> b.getType() == AntimatterMaterialTypes.FRAME)
                    .forEach(b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
            AntimatterAPI.all(BlockSurfaceRock.class).stream().forEach(b -> ModelUtils.setRenderLayer(b, RenderType.cutout()));
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
            if (block instanceof IColorHandler h && h.registerColorHandlers())
                colors.register(h::getBlockColor, block);
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
