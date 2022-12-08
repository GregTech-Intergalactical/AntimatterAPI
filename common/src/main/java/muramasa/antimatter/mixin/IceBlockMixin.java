package muramasa.antimatter.mixin;

import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static muramasa.antimatter.data.AntimatterDefaultTools.SAW;

@Mixin(IceBlock.class)
public class IceBlockMixin extends HalfTransparentBlock {
    public IceBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "playerDestroy", at = @At(value = "HEAD"), cancellable = true)
    private void stopSpawnWater(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack, CallbackInfo info) {
        if (!stack.isEmpty() && stack.getItem() instanceof IAntimatterTool) {
            AntimatterToolType type = ((IAntimatterTool) stack.getItem()).getAntimatterToolType();
            if (type == SAW) {
                super.playerDestroy(worldIn, player, pos, state, te, stack);
                info.cancel();
            }
        }
    }
}
