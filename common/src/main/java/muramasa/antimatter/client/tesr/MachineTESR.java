package muramasa.antimatter.client.tesr;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.fluid.FluidTank;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.VertexTransformer;
import muramasa.antimatter.client.baked.BakedMachineSide;
import muramasa.antimatter.client.baked.ListBakedModel;
import muramasa.antimatter.client.baked.MachineBakedModel;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import tesseract.FluidPlatformUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MachineTESR implements BlockEntityRenderer<TileEntityMachine<?>> {

    protected final BlockEntityRendererProvider.Context context;
    public MachineTESR(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    //Renders this tile as a TESR.
    @Override
    public void render(@Nonnull TileEntityMachine<?> tile, float partialTicks, @Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (tile.getMachineType().renderContainerLiquids()) {
            renderLiquids(tile, partialTicks, stack, buffer, light, overlay);
        }
    }

    private void renderLiquids(@Nonnull TileEntityMachine<?> tile, float partialTicks, @Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        long t = tile.getBlockState().getSeed(tile.getBlockPos());
       // net.minecraftforge.client.ForgeHooksClient.setRenderLayer(RenderType.cutout());
        RenderType.cutout().setupRenderState();
        for (Caches.LiquidCache liquidCache : tile.liquidCache.get()) {
            stack.pushPose();
            stack.translate(0f, (1-liquidCache.percentage)*liquidCache.height, 0f);
            stack.translate(0.5D, 0.5D, 0.5D);
            stack.last().pose().multiply(RenderHelper.faceRotation(tile.getBlockState()).getMatrix());
            stack.translate(-0.5D, -0.5D, -0.5D);
            stack.scale(1.0f, liquidCache.percentage, 1.0f);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tile.getLevel(), liquidCache.model,tile.getBlockState(), tile.getBlockPos(), stack, builder, true, tile.getLevel().getRandom(), light ,overlay);
            stack.popPose();
        }

    }

    private static BakedModel renderInner(BlockState state, Random rand, int light, BakedModel inner, Fluid fluid, BlockAndTintGetter level, BlockPos pos) {
        List<BakedQuad> quads = ModelUtils.getQuadsFromBaked(inner, state, null, rand, level, pos);
        List<BakedQuad> out = VertexTransformer.processMany(quads, FluidPlatformUtils.getFluidColor(fluid), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(FluidPlatformUtils.getStillTexture(fluid)));
        boolean hot = FluidPlatformUtils.getFluidTemperature(fluid) >= FluidPlatformUtils.getFluidTemperature(Fluids.LAVA);
        for (BakedQuad bakedQuad : out) {
            ModelUtils.setLightData(bakedQuad, hot ? 1 << 7 : light);
        }
        return new ListBakedModel(out);
    }

    public static List<Caches.LiquidCache> buildLiquids(TileEntityMachine<?> tile) {
        List<Caches.LiquidCache> ret = new ObjectArrayList<>();
        MachineFluidHandler<?> handler = tile.fluidHandler.map(t -> t).orElse(null);
        if (handler == null) return Collections.emptyList();
        BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(tile.getBlockState());

        if (bakedModel instanceof MachineBakedModel model) {
            for (Direction dir : Ref.DIRS) {
                BakedModel ibm = model.getModel(tile.getBlockState(), dir, tile.getMachineState());
                if (!(ibm instanceof BakedMachineSide toRender)) continue;
                for (Map.Entry<String, BakedModel> customPart : toRender.customParts()) {
                    String[] parts = customPart.getKey().split(":");
                    if (parts.length != 3) continue;
                    boolean in = parts[0].equals("in");
                    int off;
                    int height;
                    try {
                        off = Integer.parseInt(parts[1]);
                        height = Integer.parseInt(parts[2]);
                    } catch (Exception ex) {
                        Antimatter.LOGGER.warn("Caught exception building model" + ex);
                        continue;
                    }
                    FluidHolder fluid = tile.fluidHandler.map(fh -> {
                        if (in) {
                            if (fh.getInputTanks() == null) return FluidHooks.emptyFluid();
                            FluidTank tank = fh.getInputTanks().getTank(off);
                            return tank == null ? FluidHooks.emptyFluid() : tank.getFluid();
                        }
                        if (fh.getOutputTanks() == null) return FluidHooks.emptyFluid();
                        FluidTank tank = fh.getOutputTanks().getTank(off);
                        return tank == null ? FluidHooks.emptyFluid() : tank.getFluid();
                    }).orElse(FluidHooks.emptyFluid());
                    BakedModel baked = renderInner(tile.getBlockState(), tile.getLevel().getRandom(), 16, customPart.getValue(), fluid.getFluid(), tile.getLevel(), tile.getBlockPos());

                    float fill = tile.fluidHandler.map(fh -> {
                        if (in) {
                            if (fh.getInputTanks() == null) return 0f;
                            FluidTank tank = fh.getInputTanks().getTank(off);
                            return tank == null ? 0f : (float)tank.getFluid().getFluidAmount() / (float)tank.getCapacity();
                        }
                        if (fh.getOutputTanks() == null) return 0f;
                        FluidTank tank = fh.getOutputTanks().getTank(off);
                        return tank == null ? 0f : (float)tank.getFluid().getFluidAmount() / (float)tank.getCapacity();
                    }).orElse(0f);

                    ret.add(new Caches.LiquidCache(fill, fluid.getFluid(), baked, height/16.0f, dir));
                }
            }
        }
        return ret;
    }
}
