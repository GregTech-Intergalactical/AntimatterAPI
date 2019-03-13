package muramasa.gregtech.common.items;

import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.Ref;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockMachines extends ItemBlock {

    public ItemBlockMachines(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.TAG_MACHINE_STACK_DATA)) {
            NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
            Tier tier = Tier.get(data.getString(Ref.KEY_MACHINE_STACK_TIER));
            return tier.getRarityColor() + I18n.format("machine." + getType().getName() + "." + tier.getName() + ".name");
        }
        return getUnlocalizedName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.TAG_MACHINE_STACK_DATA)) {
            NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
            if (getType().hasFlag(MachineFlag.BASIC)) {
                tooltip.add("Voltage IN: " + TextFormatting.GREEN + Tier.get(data.getString(Ref.KEY_MACHINE_STACK_TIER)).getVoltage() + " (" + data.getString(Ref.KEY_MACHINE_STACK_TIER).toUpperCase() + ")");
                tooltip.add("Capacity: " + TextFormatting.BLUE + (Tier.get(data.getString(Ref.KEY_MACHINE_STACK_TIER)).getVoltage() * 64));
            }
        }
    }

    public Machine getType() {
        return ((BlockMachine) getBlock()).getType();
    }
}
