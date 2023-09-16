package muramasa.antimatter.registration;

import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public interface IItemBlockProvider {

    default BlockItem getItemBlock() {
        return new AntimatterItemBlock((Block) this);
    }

    default CreativeModeTab getItemGroup() {
        return Ref.TAB_BLOCKS;
    }

    default Component getDisplayName(ItemStack stack) {
        return Utils.translatable(stack.getDescriptionId());
    }

    default boolean generateItemBlock() {
        return true;
    }
}
