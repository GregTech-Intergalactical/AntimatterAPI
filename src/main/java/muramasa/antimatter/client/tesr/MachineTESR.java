package muramasa.antimatter.client.tesr;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.VertexTransformer;
import muramasa.antimatter.client.baked.BakedMachineSide;
import muramasa.antimatter.client.baked.GroupedBakedModel;
import muramasa.antimatter.client.baked.MachineBakedModel;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
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
        MachineFluidHandler<?> handler = tile.fluidHandler.map(t -> t).orElse(null);
        Function<String, Fluid> getter = str -> {
            if (!str.equals("")) {
                return Fluids.WATER;
            }
            return null;
            /*int index = Integer.parseInt(str.substring(2));
            if (str.startsWith("in")) {
                return Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(handler.getInputs()[index].getFluid().getAttributes().getFlowingTexture());
            } else {
                return Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(handler.getOutputs()[index].getFluid().getAttributes().getFlowingTexture());
            }*/
        };
        if (handler == null) return;
        IVertexBuilder builder = buffer.getBuffer(RenderType.cutout());
        IBakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(tile.getBlockState());
        stack.pushPose();
        float f = tile.getFacing().toYRot();
        //stack.translate(tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ());
        //stack.translate(0.5D, 0.5D, 0.5D);
        //stack.mulPose(Vector3f.YP.rotationDegrees(-f));
        //stack.translate(-0.5D, -0.5D, -0.5D);
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
                                IBakedModel temp = renderInner(stack, tile.getBlockState(), tile.getLevel().getRandom(),buffer, light, overlay, customPart.getValue(), getter.apply(customPart.getKey()));
                                long t = tile.getBlockState().getSeed(tile.getBlockPos());
                                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(RenderType.cutout());
                                Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModelSmooth(tile.getLevel(), temp, tile.getBlockState(), tile.getBlockPos(), stack, builder, false, tile.getLevel().getRandom(),t, overlay, data);
                            }
                        }
                    }
                }
            }
        }
        stack.popPose();
    }

    private IBakedModel renderInner(MatrixStack stack, BlockState state, Random rand, int color, int light, int overlay, IBakedModel inner, TextureAtlasSprite name) {
        List<BakedQuad> quads = inner.getQuads(state, null, rand, EmptyModelData.INSTANCE);
        List<BakedQuad> out = VertexTransformer.processMany(quads, name);
        IBakedModel temp = new IBakedModel() {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
                return p_200117_2_ == null ? out : Collections.emptyList();
            }

            @Override
            public boolean useAmbientOcclusion() {
                return true;
            }

            @Override
            public boolean isGui3d() {
                return true;
            }

            @Override
            public boolean usesBlockLight() {
                return true;
            }

            @Override
            public boolean isCustomRenderer() {
                return true;
            }

            @Override
            public TextureAtlasSprite getParticleIcon() {
                return null;
            }

            @Override
            public ItemOverrideList getOverrides() {
                return ItemOverrideList.EMPTY;
            }
        };
        if (true) return temp;
        for (BakedQuad quad : quads) {
            BakedQuadBuilder consumer = new BakedQuadBuilder(name);
            consumer.setContractUVs(true);
            consumer.setApplyDiffuseLighting(true);
            for(int e = 0; e < 4; e++)
            {
                float dx = RenderHelper.xFromQuad(quad, e) ;
                float dy = RenderHelper.yFromQuad(quad, e);
                float dz = RenderHelper.zFromQuad(quad, e);
                consumer.put(e, dx, dy, dz);
                consumer.put(e, 1,1,1,1);
                //builder.vertex(dx, dy, dz);
                //builder.color(1,1,1,1);
                switch (e) {
                    case 0:
                        consumer.put(e, 0.0f, 0.0f);
                        break;
                    case 1:
                        consumer.put(e, 1.0f, 1.0f);
                        break;
                    case 2:
                        consumer.put(e, 1.0f, 0.0f);
                        break;
                    case 3:
                        consumer.put(e, 0.0f, 0.0f);
                        break;
                }
                consumer.put(e, light);
                Vector3f norm = RenderHelper.normalFromQuad(quad, e);
                float offX = (float) quad.getDirection().getStepX();
                float offY = (float) quad.getDirection().getStepY();
                float offZ = (float) quad.getDirection().getStepZ();
                consumer.put(e, norm.x(), norm.y(), norm.z());
                consumer.put(e, overlay);
                //builder.normal(offX, offY, offZ);
                //builder.overlayCoords(overlay);
                out.add(consumer.build());
                //builder.endVertex();
            }
        }
        return temp;
    }

}
