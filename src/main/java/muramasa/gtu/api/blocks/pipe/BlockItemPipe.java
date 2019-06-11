package muramasa.gtu.api.blocks.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.pipe.ItemPipeStack;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.tileentities.pipe.TileEntityItemPipe;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

//TODO 1.13+: Flatten and avoid CableStacks
public class BlockItemPipe extends BlockPipe<BlockItemPipe> {

    protected int[] slots, steps;

    public BlockItemPipe(Material material, int baseSlots) {
        super("item_pipe", material, Textures.PIPE_DATA[0]);
        slots = new int[] {
            baseSlots, baseSlots, baseSlots, baseSlots, baseSlots * 2, baseSlots * 4
        };
        steps = new int[] {
            32768 / baseSlots, 32768 / baseSlots, 32768 / baseSlots, 32768 / baseSlots, 16384 / baseSlots, 8192 / baseSlots
        };
        setSizes(PipeSize.NORMAL, PipeSize.LARGE, PipeSize.HUGE);
    }

    public int getSlotCount(PipeSize size) {
        return slots[size.ordinal()];
    }

    public int getStepSize(PipeSize size, boolean restrictive) {
        return restrictive ? steps[size.ordinal()] * 1000 : steps[size.ordinal()];
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (PipeSize size : getSizes()) {
            items.add(new ItemPipeStack(this, size, false).asItemStack());
        }
        for (PipeSize size : getSizes()) {
            items.add(new ItemPipeStack(this, size, true).asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityItemPipe();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasTagCompound()) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityItemPipe) {
                boolean restrictive = stack.getTagCompound().getBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE);
                ((TileEntityItemPipe) tile).init(restrictive);
            }
        }
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            boolean restrictive = stack.getTagCompound().getBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE);
            String name = (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + (restrictive ? "Restrictive " : "") + material.getDisplayName() + " Item Pipe";
            return name;
        }
        return stack.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            tooltip.add("Item Capacity: " + TextFormatting.BLUE + getSlotCount(size) + " Stacks/s");
            tooltip.add("Routing Value: " + TextFormatting.YELLOW + getStepSize(size, stack.getTagCompound().getBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE)));
        }
    }
}
