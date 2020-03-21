package muramasa.antimatter.pipe;

import muramasa.antimatter.material.Material;

public class BlockItemPipe extends BlockPipe {

    protected boolean restrictive;
    protected int slots, steps;

    public BlockItemPipe(String domain, Material material, PipeSize size, boolean restrictive, int slots, int steps) {
        super(domain, restrictive ? PipeType.ITEM_RESTRICTIVE : PipeType.ITEM, material, size);
        this.restrictive = restrictive;
        this.slots = slots;
        this.steps = steps;
        register(BlockItemPipe.class);
    }

    public int getSlotCount() {
        return slots;
    }

    public int getStepSize() {
        return restrictive ? steps * 1000 : steps;
    }

//
//    @Override
//    public String getDisplayName(ItemStack stack) {
//        boolean res = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[res ? stack.getMetadata() - 8 : stack.getMetadata()];
//        return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + (res ? "Restrictive " : "") + material.getDisplayName() + " Item Pipe";
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
//        boolean res = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[res ? stack.getMetadata() - 8 : stack.getMetadata()];
//        tooltip.add("Item Capacity: " + TextFormatting.BLUE + getSlotCount(size) + " Stacks/s");
//        tooltip.add("Routing Value: " + TextFormatting.YELLOW + getStepSize(size, res));
//    }

    public static class BlockItemPipeBuilder extends BlockPipeBuilder {

        protected int[] slots, steps;
        protected boolean buildRestrictive = true, buildNonRestrictive = true;

        public BlockItemPipeBuilder(String domain, Material material, PipeSize[] sizes) {
            super(domain, material, sizes);
        }

        public BlockItemPipeBuilder(String domain, Material material) {
            this(domain, material, PipeSize.VALUES);
        }

        public BlockItemPipeBuilder slots(int baseSlots) {
            this.slots = new int[]{baseSlots, baseSlots, baseSlots, baseSlots, baseSlots * 2, baseSlots * 4};
            return this;
        }

        public BlockItemPipeBuilder slots(int... slots) {
            this.slots = slots;
            return this;
        }

        public BlockItemPipeBuilder steps(int baseSteps) {
            this.steps = new int[]{32768 / baseSteps, 32768 / baseSteps, 32768 / baseSteps, 32768 / baseSteps, 16384 / baseSteps, 8192 / baseSteps};
            return this;
        }

        public BlockItemPipeBuilder steps(int... steps) {
            this.steps = steps;
            return this;
        }

        public BlockItemPipeBuilder restrict(boolean buildRestrictive, boolean buildNonRestrictive) {
            this.buildRestrictive = buildRestrictive;
            this.buildNonRestrictive = buildNonRestrictive;
            return this;
        }

        @Override
        public void build() {
            for (int i = 0; i < sizes.length; i++) {
                if (buildRestrictive) new BlockItemPipe(domain, material, sizes[i], true, slots[i], steps[i]);
                if (buildNonRestrictive) new BlockItemPipe(domain, material, sizes[i], false, slots[i], steps[i]);
            }
        }
    }
}
