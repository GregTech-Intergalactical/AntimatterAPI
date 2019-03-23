package muramasa.gregtech.common.items;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.types.Cable;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.common.blocks.BlockCable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ItemBlockCable extends ItemBlock {

    public ItemBlockCable(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            PipeSize size = PipeSize.VALUES[compound.getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            boolean insulated = compound.getBoolean(Ref.KEY_CABLE_STACK_INSULATED);
            return (size.ordinal() * 2) + "x " + getType().getMaterial().getDisplayName() + (insulated ? " Cable" : " Wire");
        }
        return getUnlocalizedName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Cable type = getType();
        tooltip.add("Max Voltage: " + TextFormatting.GREEN + type.getVoltage() + " (" + type.getTier().getName().toUpperCase(Locale.ENGLISH) + ")");
        tooltip.add("Max Amperage: " + TextFormatting.YELLOW + type.getBaseAmps());
        boolean insulated = stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED);
        tooltip.add("Loss/Meter/Ampere: " + TextFormatting.RED + (insulated ? type.getLossInsulated() : type.getLoss()) + TextFormatting.GRAY + " EU-Volt");
    }

    public Cable getType() {
        return ((BlockCable) getBlock()).getType();
    }
}
