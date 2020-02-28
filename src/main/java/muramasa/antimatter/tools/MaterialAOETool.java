package muramasa.antimatter.tools;

import muramasa.antimatter.materials.Material;
import muramasa.antimatter.tools.base.AntimatterToolType;
import muramasa.antimatter.tools.base.MaterialTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Iterator;

public class MaterialAOETool extends MaterialTool {

    public MaterialAOETool(String domain, AntimatterToolType type, IItemTier tier, Properties properties, Material primary, @Nullable Material secondary, int energyTier) {
        super(domain, type, tier, properties, primary, secondary, energyTier);
    }

    //Behaviour system
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity livingEntity) {
        if(!super.onBlockDestroyed(stack, world, state, pos, livingEntity)) return false;
        if (!(livingEntity instanceof PlayerEntity)) return true;
        PlayerEntity player = (PlayerEntity) livingEntity;
        Iterator<BlockPos> positions = Utils.getHarvestableBlocksToBreak(world, player, this, type.getMultiBlockBreakColumn(), type.getMultiBlockBreakRow(), type.getMultiBlockBreakDepth()).iterator();
        while (positions.hasNext()) {
            if (!enoughDurability(stack, type.getUseDurability(), type.isPowered())) return true;
            if (!Utils.breakBlock(world, player, stack, positions.next(), type.getUseDurability())) break;
        }
        return true;
    }
}
