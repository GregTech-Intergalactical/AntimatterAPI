package muramasa.antimatter.item;

import net.minecraft.item.ItemStack;

public interface IChargeable {
    //TODO: comments
    /**
     * Adds energy to the node. Returns quantity of energy that was accepted.
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate If true, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    long insert(ItemStack stack, long maxReceive, boolean simulate);

    /**
     * Removes energy from the node. Returns quantity of energy that was removed.
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate If true, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    long extract(ItemStack stack,long maxExtract, boolean simulate);

    /**
     * @return Gets the amount of energy currently stored.
     */
    long getEnergy(ItemStack stack);

    /**
     * @return Gets the maximum amount of energy that can be stored.
     */
    long getCapacity(ItemStack stack);

    /**
     * @return Gets the maximum amount of voltage that can be output.
     */
    int getOutputVoltage();

    /**
     * @return Gets the maximum amount of voltage that can be input.
     */
    int getInputVoltage();

    /**
     * Gets if this storage can have energy extracted.
     * @return If this is false, then any calls to extractEnergy will return 0.
     */
    boolean canOutput();

    /**
     * Used to determine if this storage can receive energy.
     * @return If this is false, then any calls to receiveEnergy will return 0.
     */
    boolean canInput();
}
