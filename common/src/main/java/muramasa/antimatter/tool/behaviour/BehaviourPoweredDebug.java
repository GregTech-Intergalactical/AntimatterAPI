package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.IBasicAntimatterTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import tesseract.TesseractCapUtils;

public class BehaviourPoweredDebug implements IItemUse<IBasicAntimatterTool> {

    public static final BehaviourPoweredDebug INSTANCE = new BehaviourPoweredDebug();

    @Override
    public String getId() {
        return "powered_debug";
    }

    @Override
    public InteractionResult onItemUse(IBasicAntimatterTool instance, UseOnContext c) {
        if (instance.getAntimatterToolType().isPowered() && c.getLevel().getBlockState(c.getClickedPos()) == Blocks.REDSTONE_BLOCK.defaultBlockState() && c.getPlayer() != null && c.getPlayer().isCreative()) {
            ItemStack stack = c.getPlayer().getItemInHand(c.getHand());
            TesseractCapUtils.getEnergyHandlerItem(stack).ifPresent(i -> {
                if (i.getCapacity() - i.getEnergy() <= 50000)
                    i.setEnergy(i.getCapacity());
                else i.setEnergy(i.getEnergy() + 50000);
                c.getPlayer().setItemInHand(c.getHand(), i.getContainer().getItemStack());
            });
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
