package muramasa.gtu.api.blocks.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.pipe.PipeStack;
import muramasa.gtu.api.tileentities.pipe.TileEntityFluidPipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluidPipe extends BlockPipe<BlockFluidPipe> {

    protected int heatResistance;
    protected int[] capacities;
    protected boolean gasProof;

    public BlockFluidPipe(Material material, int baseCapacity, int heatResistance, boolean gasProof) {
        super("fluid_pipe", material, Textures.PIPE_DATA[0]);
        this.heatResistance = heatResistance;
        this.gasProof = gasProof;
        this.capacities = new int[] {
            baseCapacity / 6, baseCapacity / 6, baseCapacity / 3, baseCapacity, baseCapacity * 2, baseCapacity * 4
        };
        setSizes(PipeSize.TINY, PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE, PipeSize.HUGE);
    }

    public BlockFluidPipe setCapacities(int... capacities) {
        this.capacities = capacities;
        return this;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (PipeSize size : getSizes()) {
            items.add(new PipeStack(this, size).asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityFluidPipe();
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + material.getDisplayName() + " Fluid Pipe";
        }
        return stack.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            tooltip.add("Fluid Capacity: " + TextFormatting.BLUE + (capacities[size.ordinal()] * 20) + "L/s");
            tooltip.add("Heat Limit: " + TextFormatting.RED + heatResistance + " K");
        }
    }
}
