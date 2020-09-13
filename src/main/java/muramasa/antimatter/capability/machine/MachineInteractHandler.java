package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.InteractHandler;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.item.ItemCover;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

import static muramasa.antimatter.Data.*;

public class MachineInteractHandler<T extends TileEntityMachine> extends InteractHandler<T> {

    public MachineInteractHandler(T tile) {
        super(tile);
        // if (tag != null) deserialize(tag);
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        TileEntityMachine tile = getTile();
        ItemStack stack = player.getHeldItem(hand);

        if (stack.getItem() instanceof ItemCover) {
            return tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.placeCover(player, side, stack, ((ItemCover) stack.getItem()).getCover())).orElse(false);
        } else if (hand == Hand.MAIN_HAND) {
            if (type == WRENCH || type == ELECTRIC_WRENCH) {
                return true;
                // return player.isCrouching() ? tile.setFacing(side) : tile.setOutputFacing(side);
            } else if (type == HAMMER) {
                tile.toggleMachine();
                // TODO: Replace by new TranslationTextComponent()
                player.sendMessage(new StringTextComponent("Machine was " + (tile.getMachineState() == MachineState.DISABLED ? "disabled" : "enabled")));
                return true;
            } else if (type == CROWBAR) {
                return tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.removeCover(player, side)).orElse(false);
            } else if (type == SCREWDRIVER) {
                CoverInstance<?> instance = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.get(side)).orElse(COVER_EMPTY);
                return !player.getEntityWorld().isRemote() && !instance.isEmpty() && instance.getCover().hasGui() && instance.openGui(player, side);
            }
            return tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.onInteract(player, hand, side, Utils.getToolType(player))).orElse(false);
        }
        return true;
    }

    /*
    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        if (getTile().getMachineState() != null) tag.putInt(Ref.TAG_MACHINE_STATE, getTile().getMachineState().ordinal());
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        getTile().setMachineState(MachineState.VALUES[tag.getInt(Ref.TAG_MACHINE_STATE)]);// TODO saving state needed? if recipe is saved, serverUpdate should handle it.
    }

    @Override
    public Capability<?> getCapability() {
        return AntimatterCaps.INTERACTABLE_HANDLER_CAPABILITY;
    }
     */
}
