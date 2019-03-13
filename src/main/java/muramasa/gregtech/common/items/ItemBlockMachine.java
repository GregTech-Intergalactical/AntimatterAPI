package muramasa.gregtech.common.items;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.blocks.BlockMachine;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockMachine extends ItemBlock {

    public ItemBlockMachine(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    //TODO add a ItemBlockMachine per type * tier to avoid NBT

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_MACHINE_STACK_TIER)) {
            Tier tier = Tier.get(stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER));
            return tier.getRarityColor() + I18n.format("machine." + getType().getName() + "." + tier.getName() + ".name");
        }
        return getUnlocalizedName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_MACHINE_STACK_TIER)) {
            Tier tier = Tier.get(stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER));
            if (getType().hasFlag(MachineFlag.BASIC)) {
                tooltip.add("Voltage IN: " + TextFormatting.GREEN + tier.getVoltage() + " (" + tier.getName().toUpperCase() + ")");
                tooltip.add("Capacity: " + TextFormatting.BLUE + (tier.getVoltage() * 64));
            }
        }
    }

    public Machine getType() {
        return ((BlockMachine) getBlock()).getType();
    }
}
