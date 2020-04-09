
package muramasa.antimatter.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
import tesseract.api.electric.IElectricCable;
import tesseract.util.Dir;

import javax.annotation.Nullable;

public class BlockCable extends BlockPipe<Cable<?>> implements IItemBlockProvider, IColorHandler, IElectricCable {

    protected boolean insulated;

    public BlockCable(PipeType<?> type, PipeSize size, boolean insulated) {
        super(insulated ? "cable" : "wire", type, size);
        this.insulated = insulated;

        String prefix = insulated ? "cable" : "wire";
        this.modelId = 2;
        this.side = new Texture(Ref.ID, "block/pipe/" + prefix + "_side");
        this.faces = new Texture[] {
            new Texture(Ref.ID, "block/pipe/" + prefix + "_vtiny"),
            new Texture(Ref.ID, "block/pipe/" + prefix + "_tiny"),
            new Texture(Ref.ID, "block/pipe/" + prefix + "_small"),
            new Texture(Ref.ID, "block/pipe/" + prefix + "_normal"),
            new Texture(Ref.ID, "block/pipe/" + prefix + "_large"),
            new Texture(Ref.ID, "block/pipe/" + prefix + "_huge")
        };
    }

    @Override
    public boolean canConnect(IBlockReader world, BlockState state, BlockPos pos) {
        return state.getBlock() instanceof BlockCable;
    }

    @Override
    public long getVoltage() {
        return getType().getTier().getVoltage();
    }

    @Override
    public int getLoss() {
        return getType().getLoss();
    }

    @Override
    public int getAmps() {
        return getType().getAmps(getSize());
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (world.isRemote()) return; // Avoid client-side
        TesseractAPI.addElectricCable(world.getDimension().getType().getId(), pos.toLong(), this);
    }
    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TesseractAPI.removeNode(world.getDimension().getType().getId(), pos.toLong());
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        TesseractAPI.removeNode(worldIn.getDimension().getType().getId(), pos.toLong());
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        TesseractAPI.removeNode(worldIn.getDimension().getType().getId(), pos.toLong());
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return true;
    }

//    @Nullable
//    @Override
//    public String getHarvestTool(BlockState state) {
//        return "wire_cutter";
//    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        if (!(state.getBlock() instanceof BlockCable) && world == null || pos == null) return -1;
        return insulated ? i == 1 ? getRGB() : -1 : i == 0 || i == 1 ? getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return insulated ? i == 1 ? getRGB() : -1 : getRGB();
    }

//    @Override
//    public String getDisplayName(ItemStack stack) {
//        boolean ins = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[ins ? stack.getMetadata() - 8 : stack.getMetadata()];
//        return size.getCableThickness() + "x " + material.getDisplayName() + (ins ? " Cable" : " Wire");
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
//        boolean ins = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[ins ? stack.getMetadata() - 8 : stack.getMetadata()];
//        tooltip.add("Max Voltage: " + TextFormatting.GREEN + getVoltage() + " (" + getTier().getId().toUpperCase(Locale.ENGLISH) + ")");
//        tooltip.add("Max Amperage: " + TextFormatting.YELLOW + getAmps(size));
//        tooltip.add("Loss/Meter/Ampere: " + TextFormatting.RED + getLoss(ins) + TextFormatting.GRAY + " EU-Volt");
//    }
}