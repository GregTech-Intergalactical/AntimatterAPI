package muramasa.antimatter.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.pipe.BlockEntityItemPipe;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.TesseractGraphWrappers;
import tesseract.api.ITickingController;
import tesseract.graph.Connectivity;

import java.util.List;
import java.util.Map;

public class BlockItemPipe<T extends ItemPipe<T>> extends BlockPipe<T> {
    final boolean restricted;

    public BlockItemPipe(T type, PipeSize size, boolean restricted) {
        super((restricted ? "restrictive_" : "") + type.getId(), type, size, 0);
        this.restricted = restricted;
        if (restricted){
            this.side = new Texture(Ref.ID, "block/pipe/pipe_restrictor_side");
        }
    }

    @Override
    public List<String> getInfo(List<String> info, Level world, BlockState state, BlockPos pos, boolean simple) {
        if (world.isClientSide || simple) return info;
        info.add("Capacity: " + getType().getCapacity(getSize()));
        return info;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter p_49817_, List<Component> tooltip, TooltipFlag p_49819_) {
        tooltip.add(Utils.translatable("antimatter.tooltip.stepsize", (type.getStepsize(getSize()) * (restricted ? 100 : 1))).withStyle(ChatFormatting.AQUA));
        tooltip.add(Utils.translatable("antimatter.tooltip.bandwidth", Utils.literal(type.getCapacity(getSize()) + "/s").withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.AQUA));
        if (Screen.hasShiftDown()) {
            tooltip.add(Utils.literal("----------"));
            tooltip.add(Utils.translatable("antimatter.pipe.item.info").withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add(Utils.literal("----------"));
        } else if (stack.getTag() == null || !stack.getTag().contains("covers")){
            tooltip.add(Utils.translatable("antimatter.tooltip.more"));
        }
        super.appendHoverText(stack, p_49817_, tooltip, p_49819_);
    }

    public boolean isRestricted() {
        return restricted;
    }

    //    @Override
//    public ITextComponent getDisplayName(ItemStack stack) {
//        boolean res = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[res ? stack.getMetadata() - 8 : stack.getMetadata()];
//        return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + (res ? "Restrictive " : "") + material.getDisplayName() + " Item Pipe";
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        boolean res = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[res ? stack.getMetadata() - 8 : stack.getMetadata()];
//        tooltip.add("Item Capacity: " + TextFormatting.BLUE + getSlotCount(size) + " Stacks/s");
//        tooltip.add("Routing Value: " + TextFormatting.YELLOW + getStepSize(size, res));
//    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext cont && cont.getEntity() instanceof Player player){
            if (Utils.getToolType(player) == AntimatterDefaultTools.WRENCH_ALT){
                return Shapes.block();
            }
        }
        return super.getShape(state, world, pos, context);
    }
}
