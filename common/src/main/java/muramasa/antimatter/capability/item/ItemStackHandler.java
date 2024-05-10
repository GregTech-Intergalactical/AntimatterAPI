package muramasa.antimatter.capability.item;

import muramasa.antimatter.util.Utils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.item.ContainerItemHandler;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.util.ItemHandlerUtils;

public class ItemStackHandler implements ExtendedItemContainer, ContainerItemHandler {
    protected NonNullList<ItemStack> stacks;

    public ItemStackHandler() {
        this(1);
    }

    public ItemStackHandler(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public ItemStackHandler(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public void setSize(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        this.onContentsChanged(slot);
    }

    @Override
    public int getContainerSize() {
        return this.stacks.size();
    }

    @NotNull
    @Override
    public ItemStack getItem(int slot) {
        this.validateSlotIndex(slot);
        return (ItemStack)this.stacks.get(slot);
    }

    @Override
    public ExtendedItemContainer getContainer() {
        return this;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.canPlaceItem(slot, stack)) {
            return stack;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = (ItemStack)this.stacks.get(slot);
            int limit = this.getStackLimit(slot, stack);
            if (!existing.isEmpty()) {
                if (!ItemHandlerUtils.canItemStacksStack(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    if (existing.isEmpty()) {
                        this.stacks.set(slot, reachedLimit ? Utils.ca(limit, stack) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }

                    this.onContentsChanged(slot);
                }

                return reachedLimit ? Utils.ca(stack.getCount() - limit, stack) : ItemStack.EMPTY;
            }
        }
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = (ItemStack)this.stacks.get(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    if (!simulate) {
                        this.stacks.set(slot, ItemStack.EMPTY);
                        this.onContentsChanged(slot);
                        return existing;
                    } else {
                        return existing.copy();
                    }
                } else {
                    if (!simulate) {
                        this.stacks.set(slot, Utils.ca(existing.getCount() - toExtract, existing));
                        this.onContentsChanged(slot);
                    }

                    return Utils.ca(toExtract, existing);
                }
            }
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return true;
    }


    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        ListTag nbtTagList = new ListTag();

        for(int i = 0; i < this.stacks.size(); ++i) {
            if (!this.stacks.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                this.stacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }

        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", this.stacks.size());
        return nbt;
    }

    public CompoundTag serializeWithEmpty(CompoundTag nbt) {
        ListTag nbtTagList = new ListTag();
        for(int i = 0; i < this.stacks.size(); ++i) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putInt("Slot", i);
            this.stacks.get(i).save(itemTag);
            nbtTagList.add(itemTag);
        }

        nbt.put("Items", nbtTagList);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.stacks.size());
        ListTag tagList = nbt.getList("Items", 10);

        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < this.stacks.size()) {
                this.stacks.set(slot, ItemStack.of(itemTags));
            }
        }

        this.onLoad();
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
        }
    }

    protected void onLoad() {
    }

    protected void onContentsChanged(int slot) {
    }
}
