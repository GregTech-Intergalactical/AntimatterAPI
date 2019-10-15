//package muramasa.gtu.api.blocks.pipe;
//
//import muramasa.gtu.Ref;
//import muramasa.gtu.api.GregTechAPI;
//import muramasa.gtu.api.data.Textures;
//import muramasa.gtu.api.materials.Material;
//import muramasa.gtu.api.pipe.PipeSize;
//import muramasa.gtu.api.texture.TextureData;
//import muramasa.gtu.api.tileentities.pipe.TileEntityItemPipe;
//import muramasa.gtu.client.render.bakedblockold.BakedTextureDataItem;
//import muramasa.gtu.client.render.bakedmodels.BakedPipe;
//import net.minecraft.block.state.BlockStateContainer;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.block.model.ModelResourceLocation;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.creativetab.CreativeTabs;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Direction;
//import net.minecraft.util.Hand;
//import net.minecraft.util.NonNullList;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.registry.IRegistry;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraft.world.World;
//import net.minecraftforge.client.model.ModelLoader;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//import javax.annotation.Nullable;
//import java.util.List;
//
//import static muramasa.gtu.api.GregTechProperties.*;
//
//public class BlockItemPipe extends BlockPipe {
//
//    protected int[] slots, steps;
//
//    public BlockItemPipe(Material material, int baseSlots, PipeSize... sizes) {
//        super("item_pipe", material, Textures.PIPE_DATA[0], sizes.length > 0 ? sizes : new PipeSize[]{PipeSize.NORMAL, PipeSize.LARGE, PipeSize.HUGE});
//        slots = new int[] {
//            baseSlots, baseSlots, baseSlots, baseSlots, baseSlots * 2, baseSlots * 4
//        };
//        steps = new int[] {
//            32768 / baseSlots, 32768 / baseSlots, 32768 / baseSlots, 32768 / baseSlots, 16384 / baseSlots, 8192 / baseSlots
//        };
//
//        overrideState(this, new BlockStateContainer.Builder(this).add(PIPE_SIZE, PIPE_RESTRICTIVE).add(PIPE_CONNECTIONS, TEXTURE, COVER).build());
//
//        GregTechAPI.register(BlockItemPipe.class, this);
//    }
//
//    public int getSlotCount(PipeSize size) {
//        return slots[size.ordinal()];
//    }
//
//    public int getStepSize(PipeSize size, boolean restrictive) {
//        return restrictive ? steps[size.ordinal()] * 1000 : steps[size.ordinal()];
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        boolean res = meta > 7;
//        int size = res ? meta - 8 : meta;
//        return getDefaultState().withProperty(PIPE_SIZE, PipeSize.VALUES[size]).withProperty(PIPE_RESTRICTIVE, res);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        int meta = state.getValue(PIPE_SIZE).ordinal();
//        return state.getValue(PIPE_RESTRICTIVE) ? meta + 8 : meta;
//    }
//
//    @Override
//    public BlockState getStateForPlacement(World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, Hand hand) {
//        int stackMeta = placer.getHeldItem(hand).getMetadata();
//        int size = stackMeta > 7 ? stackMeta - 8 : stackMeta;
//        return getDefaultState().withProperty(PIPE_SIZE, PipeSize.VALUES[size]).withProperty(PIPE_RESTRICTIVE, stackMeta > 7);
//    }
//
//    @Override
//    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
//        for (int i = 0; i < sizes.length; i++) {
//            items.add(new ItemStack(this, 1, sizes[i].ordinal()));
//        }
//        for (int i = 0; i < sizes.length; i++) {
//            items.add(new ItemStack(this, 1, sizes[i].ordinal() + 8));
//        }
//    }
//
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
//    public void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry) {
//        for (int i = 0; i < getSizes().length; i++) {
//            ModelResourceLocation loc = new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + getSizes()[i].getName() + ",restrictive=false");
//            IBakedModel baked = new BakedTextureDataItem(BakedPipe.BAKED[getSizes()[i].ordinal()][2], new TextureData().base(Textures.PIPE_DATA[0].getBase()).overlay(Textures.PIPE_DATA[0].getOverlay(getSizes()[i].ordinal())));
//            registry.putObject(loc, baked);
//
//            loc = new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + getSizes()[i].getName() + ",restrictive=true");
//            registry.putObject(loc, baked);
//        }
//    }
//}
