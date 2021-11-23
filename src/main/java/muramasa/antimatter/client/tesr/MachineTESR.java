package muramasa.antimatter.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.VertexTransformer;
import muramasa.antimatter.client.baked.BakedMachineSide;
import muramasa.antimatter.client.baked.ListBakedModel;
import muramasa.antimatter.client.baked.MachineBakedModel;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class MachineTESR extends TileEntityRenderer<TileEntityMachine<?>> {

    public MachineTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    //Renders this tile as a TESR.
    @Override
    public void render(@Nonnull TileEntityMachine<?> tile, float partialTicks, @Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (tile.getMachineType().renderContainerLiquids()) {
            renderLiquids(tile, partialTicks, stack, buffer, light, overlay);
        }
    }

    private void renderLiquids(@Nonnull TileEntityMachine<?> tile, float partialTicks, @Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.cutout());
        long t = tile.getBlockState().getSeed(tile.getBlockPos());
        net.minecraftforge.client.ForgeHooksClient.setRenderLayer(RenderType.cutout());
        //Vector4f f = new Vector4f(0,0,0,1);
        //f.transform(stack.last().pose());
        for (Caches.LiquidCache liquidCache : tile.liquidCache.get()) {
            stack.pushPose();
            //stack.translate(-f.x(), -f.y() - 0.5f, -f.z());
            stack.scale(1.0f, liquidCache.percentage, 1.0f);
            //stack.translate(f.x(), f.y() + 0.5f, f.z());
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModelSmooth(tile.getLevel(), liquidCache.model, tile.getBlockState(), tile.getBlockPos(), stack, builder, false, tile.getLevel().getRandom(), t, light, EmptyModelData.INSTANCE);
            stack.popPose();
        }

    }

    private static IBakedModel renderInner(BlockState state, Random rand, int light, IBakedModel inner, Fluid fluid) {
        List<BakedQuad> quads = inner.getQuads(state, null, rand, EmptyModelData.INSTANCE);
        List<BakedQuad> out = VertexTransformer.processMany(quads, fluid.getAttributes().getColor(), Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(fluid.getAttributes().getStillTexture()));
        boolean hot = fluid.getAttributes().getTemperature() >= Fluids.LAVA.getAttributes().getTemperature();
        for (BakedQuad bakedQuad : out) {
            LightUtil.setLightData(bakedQuad, hot ? 1 << 7 : light);
        }
        return new ListBakedModel(out);
    }

    public static List<Caches.LiquidCache> buildLiquids(TileEntityMachine<?> tile) {
        List<Caches.LiquidCache> ret = new ObjectArrayList<>();
        MachineFluidHandler<?> handler = tile.fluidHandler.map(t -> t).orElse(null);
        if (handler == null) return Collections.emptyList();
        IBakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(tile.getBlockState());

        if (bakedModel instanceof MachineBakedModel) {
            MachineBakedModel model = (MachineBakedModel) bakedModel;
            IModelData data = model.getModelData(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), new ModelDataMap.Builder().build());
            ModelConfig config = data.getData(AntimatterProperties.DYNAMIC_CONFIG);
            if (config == null) throw new IllegalStateException("Missing dynamic config in MachineTESR!");
            for (int i : config.getConfig()) {
                IBakedModel[] arr = model.getConfigs().get(i);
                if (arr != null) {
                    for (IBakedModel iBakedModel : arr) {
                        if (iBakedModel instanceof BakedMachineSide) {
                            BakedMachineSide toRender = (BakedMachineSide) iBakedModel;
                            for (Map.Entry<String, IBakedModel> customPart : toRender.customParts()) {
                                boolean in = customPart.getKey().startsWith("in");
                                int off = Character.getNumericValue(in ? customPart.getKey().charAt(2) : customPart.getKey().charAt(3));
                                FluidStack fluid = tile.fluidHandler.map(fh -> {
                                    if (in) {
                                        if (fh.getInputTanks() == null) return FluidStack.EMPTY;
                                        FluidTank tank = fh.getInputTanks().getTank(off);
                                        return tank == null ? FluidStack.EMPTY : tank.getFluid();
                                    }
                                    if (fh.getOutputTanks() == null) return FluidStack.EMPTY;
                                    FluidTank tank = fh.getOutputTanks().getTank(off);
                                    return tank == null ? FluidStack.EMPTY : tank.getFluid();
                                }).orElse(FluidStack.EMPTY);
                                IBakedModel baked = renderInner(tile.getBlockState(), tile.getLevel().getRandom(), 16, customPart.getValue(), fluid.getFluid());

                                float fill = tile.fluidHandler.map(fh -> {
                                    if (in) {
                                        if (fh.getInputTanks() == null) return 0f;
                                        FluidTank tank = fh.getInputTanks().getTank(off);
                                        return tank == null ? 0f : (float)tank.getFluidAmount() / (float)tank.getCapacity();
                                    }
                                    if (fh.getOutputTanks() == null) return 0f;
                                    FluidTank tank = fh.getOutputTanks().getTank(off);
                                    return tank == null ? 0f : (float)tank.getFluidAmount() / (float)tank.getCapacity();
                                }).orElse(0f);

                                ret.add(new Caches.LiquidCache(fill, fluid.getFluid(), baked));
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }
}
