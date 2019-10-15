package muramasa.gtu.client.render.bakedmodels;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static muramasa.gtu.api.GregTechProperties.ROCK_MODEL;

public class BakedRock extends BakedBase {

    public static IBakedModel[] BAKED;

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        List<BakedQuad> quads = new LinkedList<>();
        if (state == null || !data.hasProperty(ROCK_MODEL)) return quads;
        return BAKED[data.getData(ROCK_MODEL)].getQuads(state, side, rand, data);
    }
}
