package muramasa.antimatter.pipe;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.pipe.TileEntityCable;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.Tesseract;
import tesseract.api.ITickingController;
import tesseract.api.gt.GTController;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCable<T extends Cable<T>> extends BlockPipe<T> {

    public final boolean insulated;

    public BlockCable(T type, PipeSize size, boolean insulated) {
        super(type.getId(), type, size, 2);
        this.insulated = insulated;
        String prefix = insulated ? "cable" : "wire";
        this.side = new Texture(Ref.ID, "block/pipe/" + prefix + "_side");
        this.faces = new Texture[]{
                new Texture(Ref.ID, "block/pipe/" + prefix + "_vtiny"),
                new Texture(Ref.ID, "block/pipe/" + prefix + "_tiny"),
                new Texture(Ref.ID, "block/pipe/" + prefix + "_small"),
                new Texture(Ref.ID, "block/pipe/" + prefix + "_normal"),
                new Texture(Ref.ID, "block/pipe/" + prefix + "_large"),
                new Texture(Ref.ID, "block/pipe/" + prefix + "_huge")
        };
    }

    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 300;
    }

    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public AntimatterToolType getToolType() {
        return Data.WIRE_CUTTER;
    }

    public boolean isFireSource(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int i) {
        if (!(state.getBlock() instanceof BlockCable) && world == null || pos == null) return -1;
        return insulated ? i == 1 ? getRGB() : -1 : i == 0 || i == 1 ? getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return insulated ? i == 1 ? getRGB() : -1 : getRGB();
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        if (worldIn.isClientSide) return;
        if (this.insulated) return;
        if (entityIn instanceof LivingEntity entity) {
            if (worldIn.getBlockEntity(pos) instanceof TileEntityCable cable) {
                if (Tesseract.getGT_ENERGY().getController(worldIn, pos.asLong()) instanceof GTController c) {
                    if (c.cableIsActive.contains(pos.asLong())) {
                        entity.hurt(DamageSource.GENERIC, this.getType().getTier().getIntegerId());
                    }
                }
            }
        }
    }
/*
    @Nullable
    @Override
    public Tag<Block> getHarvestTool(BlockState state) {
        return Data.WIRE_CUTTER.getTag<Block>();
    }
*/
    @Override
    public List<String> getInfo(List<String> info, Level world, BlockState state, BlockPos pos) {
        if (world.isClientSide) return info;
        ITickingController<?, ?, ?> controller = Tesseract.getGT_ENERGY().getController(world, pos.asLong());
        if (controller != null) controller.getInfo(pos.asLong(), info);
        return info;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslatableComponent("generic.amp").append(": ").append(new TextComponent("" + this.type.getAmps(this.size)).withStyle(ChatFormatting.GREEN)));
        tooltip.add(new TranslatableComponent("generic.voltage").append(": ").append(new TextComponent("" + this.type.getTier().getVoltage()).withStyle(ChatFormatting.BLUE)));
        tooltip.add(new TranslatableComponent("generic.loss").append(": ").append(new TextComponent("" + this.type.getLoss()).withStyle(ChatFormatting.BLUE)));

        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslatableComponent("antimatter.tooltip.more").withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltip.add(new TextComponent("----------"));
            tooltip.add(new TranslatableComponent("antimatter.pipe.cable.info").withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add(new TextComponent("----------"));
        }
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