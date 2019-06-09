package muramasa.gtu.api.blocks.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.pipe.CableStack;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.tileentities.pipe.TileEntityCable;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

import static muramasa.gtu.api.properties.GTProperties.CONNECTIONS;
import static muramasa.gtu.api.properties.GTProperties.TEXTURE;

//TODO 1.13+: Flatten and avoid CableStacks
public class BlockCable extends BlockPipe<BlockCable> implements IItemBlock, IColorHandler {

    protected int loss, lossInsulated;
    protected int[] amps;
    protected Tier tier;

    public BlockCable(Material material, int loss, int lossInsulated, int baseAmps, Tier tier) {
        super("cable", material, Textures.PIPE_DATA[1]);
        this.loss = loss;
        this.lossInsulated = lossInsulated;
        this.tier = tier;
        this.amps = new int[] {
            baseAmps, baseAmps * 2, baseAmps * 4, baseAmps * 8, baseAmps * 12, baseAmps * 16
        };
    }

    public long getVoltage() {
        return tier.getVoltage();
    }

    public int getLoss(boolean insulated) {
        return insulated ? lossInsulated : loss;
    }

    public int getAmps(PipeSize size) {
        return amps[size.ordinal()];
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityCable) {
            TileEntityCable cable = (TileEntityCable) tile;
            exState = exState.withProperty(CONNECTIONS, cable.getConnections());
            exState = exState.withProperty(TEXTURE, cable.isInsulated() ? Textures.PIPE_DATA[2] : getBlockData());
        }
        return exState;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (PipeSize size : getSizes()) {
            items.add(new CableStack(this, size, false).asItemStack());
        }
        for (PipeSize size : getSizes()) {
            items.add(new CableStack(this, size, true).asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCable();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasTagCompound()) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityCable) {
                boolean insulated = stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED);
                ((TileEntityCable) tile).init(insulated);
            }
        }
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "wire_cutter";
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            PipeSize size = PipeSize.VALUES[compound.getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            if (compound.getBoolean(Ref.KEY_CABLE_STACK_INSULATED)) {
                size = PipeSize.VALUES[Math.max(size.ordinal() - 1, 0)];
                return  size.getCableThickness() + "x " + material.getDisplayName() + " Cable";
            } else {
                return size.getCableThickness() + "x " + material.getDisplayName() + " Wire";
            }
        }
        return stack.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            tooltip.add("Max Voltage: " + TextFormatting.GREEN + getVoltage() + " (" + getTier().getId().toUpperCase(Locale.ENGLISH) + ")");
            tooltip.add("Max Amperage: " + TextFormatting.YELLOW + getAmps(size));
            boolean insulated = stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED);
            tooltip.add("Loss/Meter/Ampere: " + TextFormatting.RED + getLoss(insulated) + TextFormatting.GRAY + " EU-Volt");
        }
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityCable) {
            if (((TileEntityCable) tile).isInsulated()) {
                return i == 2 ? getRGB() : -1;
            } else {
                return i == 0 || i == 2 ? getRGB() : -1;
            }
        }
        return -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_CABLE_STACK_INSULATED) && stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED)) {
            return i == 2 ? ((BlockPipe) block).getRGB() : -1;
        } else {
            return i == 0 || i == 1 || i == 2 ? ((BlockPipe) block).getRGB() : -1;
        }
    }
}
