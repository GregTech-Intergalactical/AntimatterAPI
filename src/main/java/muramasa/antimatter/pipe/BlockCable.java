
package muramasa.antimatter.pipe;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import tesseract.Tesseract;
import tesseract.api.ITickingController;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BlockCable extends BlockPipe<Cable<?>> {

    protected boolean insulated;

    public BlockCable(Cable<?> type, PipeSize size, boolean insulated) {
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
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 300;
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return true;
    }


    @Override
    public boolean isFireSource(BlockState state, IWorldReader world, BlockPos pos, Direction side)
    {
        return true;
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        if (!(state.getBlock() instanceof BlockCable) && world == null || pos == null) return -1;
        return insulated ? i == 1 ? getRGB() : -1 : i == 0 || i == 1 ? getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return insulated ? i == 1 ? getRGB() : -1 : getRGB();
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return Data.WIRE_CUTTER.getToolType();
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        ITickingController controller = Tesseract.GT_ENERGY.getController(world.getDimensionKey(), pos.toLong());
        if (controller != null) info.addAll(Arrays.asList(controller.getInfo(pos.toLong())));
        return info;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslationTextComponent("generic.amp").appendString(": ").append(new StringTextComponent(""+this.type.getAmps(this.size)).mergeStyle(TextFormatting.GREEN)));
        tooltip.add(new TranslationTextComponent("generic.voltage").appendString(": ").append(new StringTextComponent(""+this.type.getTier().getVoltage()).mergeStyle(TextFormatting.BLUE)));
        tooltip.add(new TranslationTextComponent("generic.loss").appendString(": ").append(new StringTextComponent(""+this.type.getLoss()).mergeStyle(TextFormatting.BLUE)));
    }

    //    @Override
//    public ITextComponent getDisplayName(ItemStack stack) {
//        boolean ins = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[ins ? stack.getMetadata() - 8 : stack.getMetadata()];
//         return size.getCableThickness() + "x " + material.getDisplayName() + (ins ? " Cable" : " Wire");
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        boolean ins = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[ins ? stack.getMetadata() - 8 : stack.getMetadata()];
//        tooltip.add("Max Voltage: " + TextFormatting.GREEN + getVoltage() + " (" + getTier().getId().toUpperCase(Locale.ENGLISH) + ")");
//        tooltip.add("Max Amperage: " + TextFormatting.YELLOW + getAmps(size));
//        tooltip.add("Loss/Meter/Ampere: " + TextFormatting.RED + getLoss(ins) + TextFormatting.GRAY + " EU-Volt");
//    }
}