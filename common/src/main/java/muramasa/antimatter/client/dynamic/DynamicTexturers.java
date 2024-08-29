package muramasa.antimatter.client.dynamic;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.ICoverHandlerProvider;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.SimpleModelState;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.mixin.client.BlockModelAccessor;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DynamicTexturers {

    /**
     * Dynamic texture implementations.
     **/

    public static final DynamicTextureProvider<ICover, ICover.DynamicKey> COVER_DYNAMIC_TEXTURER = new DynamicTextureProvider<ICover, ICover.DynamicKey>(
            t -> {
                if (t.currentDir == t.source.side()) {
                    UnbakedModel m = null;
                    try {
                        //for some reason first load can cause circular exception
                        m = ModelUtils.getModel(t.source.getModel(t.type, Direction.SOUTH));
                    } catch (Exception ignored) {

                    }
                    if (m == null) {
                        return Collections.emptyList();
                    }
                    BlockModel model = (BlockModel) m;
                    ((BlockModelAccessor)model).getTextureMap().put("base", Either.left(ModelUtils.getBlockMaterial(t.key.machineTexture)));
                    t.source.setTextures(
                            (name, texture) -> ((BlockModelAccessor)model).getTextureMap().put(name, Either.left(ModelUtils.getBlockMaterial(texture))));
                    Transformation base = RenderHelper.faceRotation(t.source.side());
                    BakedModel b = model.bake(ModelUtils.getModelBakery(), model, ModelUtils.getDefaultTextureGetter(),
                            new SimpleModelState(base), t.source.getModel(t.type, Direction.SOUTH), true);

                    Predicate<Map.Entry<String, BakedModel>> predicate = getEntryPredicate(t);

                    List<BakedQuad> ret = new ObjectArrayList<>();
                    for (Direction dir : Ref.DIRS) {
                        ret.addAll(ModelUtils.getQuadsFromBakedCover(b, t.state, dir, t.rand, t.level, t.pos, predicate));
                    }
                    ret.addAll(ModelUtils.getQuadsFromBakedCover(b, t.state, null, t.rand, t.level, t.pos, predicate));
                    return t.source.transformQuads(t.state, ret);
                }
                return Collections.emptyList();
            });

    @Nullable
    private static Predicate<Map.Entry<String, BakedModel>> getEntryPredicate(DynamicTextureProvider<ICover, ICover.DynamicKey>.BuilderData t) {
        ICoverHandler<?> coverHandler = t.getBlockEntity() instanceof ICoverHandlerProvider<?> provider ? provider.getCoverHandler().orElse(null) : null;
        Predicate<Map.Entry<String, BakedModel>> predicate = null;

        if (coverHandler != null){
            predicate = e -> {
                String key = e.getKey();
                if (key.isEmpty()) return true;
                Direction dir = Utils.rotate(t.currentDir, Direction.byName(key));
                if (dir == null) throw new NullPointerException("Dir null in getBlockQuads");
                boolean ok =  coverHandler.get(dir).isEmpty();//(filter & (1 << dir.get3DDataValue())) > 0;
                return ok;
            };
        }
        return predicate;
    }

    public static final DynamicTextureProvider<Machine<?>, BlockEntityMachine.DynamicKey> TILE_DYNAMIC_TEXTURER = new DynamicTextureProvider<Machine<?>, BlockEntityMachine.DynamicKey>(
            t -> {
                Vec3i vector3i = t.currentDir.getNormal();
                Vector4f vector4f = new Vector4f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ(), 0.0F);
                vector4f.transform(RenderHelper.faceRotation(t.state).inverse().getMatrix());
                Direction side = Direction.getNearest(vector4f.x(), vector4f.y(), vector4f.z());
                UnbakedModel model = ModelUtils.getModel(t.source.getModel(t.type, side));
                BlockEntity blockEntity = t.getBlockEntity();
                if (!(blockEntity instanceof BlockEntityMachine<?> machine) ||!(model instanceof BlockModel m)) return Collections.emptyList();
                ((BlockModelAccessor)m).getTextureMap().put("base", Either.left(
                    ModelUtils.getBlockMaterial(machine.getMultiTexture().apply(side))));
                   AntimatterProperties.MachineProperties prop = t.key.properties;
                for (int i = 0; i < prop.type.getOverlayLayers(); i++) {
                    String suffix = i == 0 ? "" : String.valueOf(i);
                    ((BlockModelAccessor)m).getTextureMap().put("overlay" + suffix,
                            Either
                                    .left(ModelUtils.getBlockMaterial(prop.type.getOverlayTextures(
                                            prop.state, prop.tier, i)[side.get3DDataValue()])));
                }

                BakedModel b = model.bake(ModelUtils.getModelBakery(), ModelUtils.getDefaultTextureGetter(),
                        new SimpleModelState(RenderHelper.faceRotation(t.state)),
                        new ResourceLocation(t.source.getId()));
                List<BakedQuad> list = new ObjectArrayList<>(10);
                for (Direction dir : Ref.DIRS) {
                    list.addAll(ModelUtils.getQuadsFromBaked(b, t.state, dir, t.rand, t.level, t.pos));
                }
                list.addAll(ModelUtils.getQuadsFromBaked(b, t.state, null, t.rand, t.level, t.pos));
                return list;
            });
}
