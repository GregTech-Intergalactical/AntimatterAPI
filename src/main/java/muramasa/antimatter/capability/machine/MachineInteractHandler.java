package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.ints.Int2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMaps;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICapabilityHandler;
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
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.machine.MachineFlag.GUI;

public class MachineInteractHandler<T extends TileEntityMachine> extends InteractHandler<T> implements ICapabilityHandler {

    protected Int2BooleanMap buttonCache;

    public MachineInteractHandler(T tile, CompoundNBT tag) {
        super(tile);
        if (tile.getMachineType().hasGui()) {
            int size = tile.getMachineType().getGui().getButtons().size();
            if (size > 0) buttonCache = new Int2BooleanLinkedOpenHashMap(size);
        }
        if (tag != null) deserialize(tag);
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        TileEntityMachine tile = getTile();
        ItemStack stack = player.getHeldItem(hand);

        if (stack.getItem() instanceof ItemCover) {
            return tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.placeCover(player, side, stack, ((ItemCover) stack.getItem()).getCover())).orElse(false);
        } else if (hand == Hand.MAIN_HAND) {
            if (type == WRENCH || type == ELECTRIC_WRENCH) {
                return player.isCrouching() ? tile.setFacing(side) : tile.setOutputFacing(side);
            } else if (type == HAMMER) {
                tile.toggleMachine();
                // TODO: Replace by new TranslationTextComponent()
                player.sendMessage(new StringTextComponent("Machine was " + (tile.getMachineState() == MachineState.DISABLED ? "disabled" : "enabled")));
                return true;
            } else if (type == CROWBAR) {
                return tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.removeCover(player, side)).orElse(false);
            } else if (type == SCREWDRIVER) {
                CoverInstance<?> cover = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.get(side)).orElse(new CoverInstance<>(COVER_NONE, tile, side));
                return !player.getEntityWorld().isRemote() && !cover.isEmpty() && cover.getCover().hasGui() && cover.openGui(player, side);
            }
            return tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.onInteract(player, hand, side, Utils.getToolType(player))).orElse(false);
        }
        return true;
    }

    public Int2BooleanMap getButtonCache() {
        return buttonCache != null ? buttonCache : Int2BooleanMaps.EMPTY_MAP;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        if (getTile().getMachineState() != null) tag.putInt(Ref.TAG_MACHINE_STATE, getTile().getMachineState().ordinal());
        if (buttonCache != null) {
            ListNBT list = new ListNBT();
            for (Int2BooleanMap.Entry e : buttonCache.int2BooleanEntrySet()) {
                list.add(LongNBT.valueOf((long) e.getIntKey() << 32 | (e.getBooleanValue() ? 1 : 0)));
            }
            tag.put(Ref.TAG_MACHINE_BUTTON, list);
        }
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        getTile().setMachineState(MachineState.VALUES[tag.getInt(Ref.TAG_MACHINE_STATE)]);// TODO saving state needed? if recipe is saved, serverUpdate should handle it.
        if (buttonCache != null) {
            ListNBT list = tag.getList(Ref.TAG_MACHINE_BUTTON, Constants.NBT.TAG_LONG);
            for (INBT nbt : list) {
                long pack = ((LongNBT) nbt).getLong();
                buttonCache.put((int) (pack >> 32), (int) (pack) != 0);
            }
        }
    }

    @Override
    public Capability<?> getCapability() {
        return AntimatterCaps.INTERACTABLE_HANDLER_CAPABILITY;
    }
}
