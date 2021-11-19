package muramasa.antimatter.mixin;

import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.IceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static muramasa.antimatter.Data.SAW;

@Mixin(IceBlock.class)
public class IceBlockMixin extends BreakableBlock {
    public IceBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "playerDestroy", at = @At(value = "HEAD"), cancellable = true)
    private void stopSpawnWater(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack, CallbackInfo info) {
        if (!stack.isEmpty() && stack.getItem() instanceof IAntimatterTool) {
            AntimatterToolType type = ((IAntimatterTool) stack.getItem()).getAntimatterToolType();
            if (type == SAW) {
                super.playerDestroy(worldIn, player, pos, state, te, stack);
                info.cancel();
            }
        }
    }
}
