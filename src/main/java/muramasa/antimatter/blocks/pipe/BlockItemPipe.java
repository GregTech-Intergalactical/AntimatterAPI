package muramasa.antimatter.blocks.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.gtu.data.Textures;

public class BlockItemPipe extends BlockPipe {

    protected boolean restrictive;
    protected int slots, steps;

    public BlockItemPipe(Material material, PipeSize size, boolean restrictive, int slots, int steps) {
        super("item_pipe", material, size, Textures.PIPE_DATA[0]);
        this.restrictive = restrictive;
        this.slots = slots;
        this.steps = steps;
        AntimatterAPI.register(BlockItemPipe.class, this);
    }

    public int getSlotCount() {
        return slots;
    }

    public int getStepSize() {
        return restrictive ? steps * 1000 : steps;
    }

//    @Nullable
//    @Override
//    public TileEntity createTileEntity(World world, BlockState state) {
//        return new TileEntityItemPipe();
//    }
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
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void onModelRegistration() {
//        for (int i = 0; i < sizes.length; i++) {
//            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), sizes[i].ordinal(), new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + sizes[i].getName() + ",restrictive=false"));
//            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), sizes[i].ordinal() + 8, new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + sizes[i].getName() + ",restrictive=true"));
//        }
//        //Redirect block model to custom baked model handling
//        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_pipe")));
//    }
//
//    @Override
//    public void onModelBuild(IRegistry<ModelResourceLocation, IBakedModel> registry) {
//        for (int i = 0; i < getSizes().length; i++) {
//            ModelResourceLocation loc = new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + getSizes()[i].getName() + ",restrictive=false");
//            IBakedModel baked = new BakedTextureDataItem(BakedPipe.BAKED[getSizes()[i].ordinal()][2], new TextureData().base(Textures.PIPE_DATA[0].getBase()).overlay(Textures.PIPE_DATA[0].getOverlay(getSizes()[i].ordinal())));
//            registry.putObject(loc, baked);
//
//            loc = new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + getSizes()[i].getName() + ",restrictive=true");
//            registry.putObject(loc, baked);
//        }
//    }


    //        slots = new int[] {
//            baseSlots, baseSlots, baseSlots, baseSlots, baseSlots * 2, baseSlots * 4
//        };
//        steps = new int[] {
//
//        };
    public static class BlockItemPipeBuilder extends BlockPipeBuilder {

        protected int[] slots, steps;
        protected boolean buildRestrictive = true, buildNonRestrictive = true;

        public BlockItemPipeBuilder(Material material, PipeSize[] sizes) {
            super(material, sizes);
        }

        public BlockItemPipeBuilder(Material material) {
            this(material, PipeSize.VALUES);
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
                if (buildRestrictive) new BlockItemPipe(material, sizes[i], true, slots[i], steps[i]);
                if (buildNonRestrictive) new BlockItemPipe(material, sizes[i], false, slots[i], steps[i]);
            }
        }
    }
}
