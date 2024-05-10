package muramasa.antimatter.cover;

import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoverStone extends BaseCover {
    public CoverStone(@NotNull ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    @Override
    public InteractionResult onInteract(Player player, InteractionHand hand, Direction side, @Nullable AntimatterToolType type) {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isNode() {
        return false;
    }

    @Override
    public <T> boolean blocksCapability(Class<T> cap, Direction side) {
        return side != null && !(source().getTile() instanceof BlockEntityPipe<?>);
    }

    @Override
    public Texture[] getTextures() {
        return super.getTextures();
    }

    @Override
    public ResourceLocation getModel(String type, Direction dir) {
        return new ResourceLocation(Ref.ID, "block/cover/cover_pipe_overlay_only");
    }
}
