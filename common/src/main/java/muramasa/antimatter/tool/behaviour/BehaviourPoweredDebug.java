package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import tesseract.TesseractCapUtils;

// TODO: REPLACE WITH CAPABILITY
public class BehaviourPoweredDebug implements IItemUse<IAntimatterTool> {

    public static final BehaviourPoweredDebug INSTANCE = new BehaviourPoweredDebug();

    @Override
    public String getId() {
        return "powered_debug";
    }

    @Override
    public InteractionResult onItemUse(IAntimatterTool instance, UseOnContext c) {
        if (instance.getAntimatterToolType().isPowered() && c.getLevel().getBlockState(c.getClickedPos()) == Blocks.REDSTONE_BLOCK.defaultBlockState() && c.getPlayer() != null) {
            ItemStack stack = c.getPlayer().getItemInHand(c.getHand());
            TesseractCapUtils.getEnergyHandlerItem(stack).ifPresent(i -> {
                if (i.getCapacity() - i.getEnergy() <= 50000)
                    i.setEnergy(i.getCapacity());
                else i.setEnergy(i.getEnergy() + 50000);
            });
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
