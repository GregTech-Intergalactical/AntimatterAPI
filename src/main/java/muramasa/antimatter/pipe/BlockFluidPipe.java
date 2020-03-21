package muramasa.antimatter.pipe;

import muramasa.antimatter.material.Material;

public class BlockFluidPipe extends BlockPipe {

    protected int heatResistance;
    protected int capacity;
    protected boolean gasProof;

    public BlockFluidPipe(String domain, Material material, PipeSize size, int capacity, int heatResistance, boolean gasProof) {
        super(domain, PipeType.FLUID, material, size);
        this.heatResistance = heatResistance;
        this.gasProof = gasProof;
        this.capacity = capacity;
        register(BlockFluidPipe.class);
    }

//
//    @Override
//    public String getDisplayName(ItemStack stack) {
//        //TODO add prefix and suffix for local
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + material.getDisplayName() + " Fluid Pipe";
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        //TODO localize
//        tooltip.add("Fluid Capacity: " + TextFormatting.BLUE + (capacities[size.ordinal()] * 20) + "L/s");
//        tooltip.add("Heat Limit: " + TextFormatting.RED + heatResistance + " K");
//    }

    public static class BlockFluidPipeBuilder extends BlockPipeBuilder {

        protected int heatResistance;
        protected boolean gasProof;
        protected int[] caps;

        public BlockFluidPipeBuilder(String domain, Material material, int heatResistance, boolean gasProof, PipeSize... sizes) {
            super(domain, material, sizes);
            this.heatResistance = heatResistance;
            this.gasProof = gasProof;
        }

        public BlockFluidPipeBuilder(String domain, Material material, int heatResistance, boolean gasProof) {
            this(domain, material, heatResistance, gasProof, PipeSize.VALUES);
        }

        public BlockFluidPipeBuilder caps(int baseCap) {
            this.caps = new int[]{baseCap / 6, baseCap / 6, baseCap / 3, baseCap, baseCap * 2, baseCap * 4};
            return this;
        }

        public BlockFluidPipeBuilder caps(int... caps) {
            this.caps = caps;
            return this;
        }

        @Override
        public void build() {
            for (int i = 0; i < sizes.length; i++) {
                new BlockFluidPipe(domain, material, sizes[i], caps[i], heatResistance, gasProof);
            }
        }
    }
}
