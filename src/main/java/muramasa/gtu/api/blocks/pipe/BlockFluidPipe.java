//package muramasa.gtu.api.blocks.pipe;
//
//import muramasa.gtu.api.GregTechAPI;
//import muramasa.gtu.api.data.Textures;
//import muramasa.gtu.api.materials.Material;
//import muramasa.gtu.api.pipe.PipeSize;
//import muramasa.gtu.api.tileentities.pipe.TileEntityFluidPipe;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.item.ItemStack;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//import javax.annotation.Nullable;
//import java.util.List;
//
//public class BlockFluidPipe extends BlockPipe {
//
//    protected int heatResistance;
//    protected int[] capacities;
//    protected boolean gasProof;
//
//    public BlockFluidPipe(Material material, int baseCapacity, int heatResistance, boolean gasProof, PipeSize... sizes) {
//        super("fluid_pipe", material, Textures.PIPE_DATA[0], sizes.length > 0 ? sizes : new PipeSize[]{PipeSize.TINY, PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE, PipeSize.HUGE});
//        this.heatResistance = heatResistance;
//        this.gasProof = gasProof;
//        this.capacities = new int[] {
//            baseCapacity / 6, baseCapacity / 6, baseCapacity / 3, baseCapacity, baseCapacity * 2, baseCapacity * 4
//        };
//        GregTechAPI.register(BlockFluidPipe.class, this);
//    }
//
//    public BlockFluidPipe setCapacities(int... capacities) {
//        this.capacities = capacities;
//        return this;
//    }
//
//    @Nullable
//    @Override
//    public TileEntity createTileEntity(World world, BlockState state) {
//        return new TileEntityFluidPipe();
//    }
//
//    @Override
//    public String getDisplayName(ItemStack stack) {
//        //TODO add prefix and suffix for local
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + material.getDisplayName() + " Fluid Pipe";
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        //TODO localize
//        tooltip.add("Fluid Capacity: " + TextFormatting.BLUE + (capacities[size.ordinal()] * 20) + "L/s");
//        tooltip.add("Heat Limit: " + TextFormatting.RED + heatResistance + " K");
//    }
//}
