package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.InteractHandler;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.item.ItemCover;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static muramasa.antimatter.Data.*;

public class MachineInteractHandler extends InteractHandler<TileEntityMachine> {

    public MachineInteractHandler(TileEntityMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
        TileEntityMachine tile = getTile();
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() instanceof ItemCover) {
            return tile.getCapability(AntimatterCaps.COVERABLE).map(h -> h.placeCover(player, side, stack, ((ItemCover) stack.getItem()).getCover())).orElse(false);
        } else if (type == WRENCH || type == ELECTRIC_WRENCH) {
            return player.isCrouching() ? tile.setFacing(side) : tile.setOutputFacing(side);
        } else if (type == HAMMER) {
            tile.toggleMachine();
            player.sendMessage(new StringTextComponent("Machine was " + (tile.getMachineState() == MachineState.DISABLED ? "disabled" : "enabled")));
            return true;
        } else if (type == CROWBAR) {
            return tile.getCapability(AntimatterCaps.COVERABLE).map(h -> h.removeCover(player, side)).orElse(false);
        } else if (type == SCREWDRIVER) {
            CoverInstance<?> instance = tile.getCapability(AntimatterCaps.COVERABLE).map(h -> h.get(side)).orElse(COVER_EMPTY);
            return !player.getEntityWorld().isRemote() && !instance.isEmpty() && instance.getCover().hasGui() && instance.openGui(player, side);
        } else return tile.getCapability(AntimatterCaps.COVERABLE).map(h -> h.onInteract(player, hand, side, Utils.getToolType(player))).orElse(false);
    }
}
