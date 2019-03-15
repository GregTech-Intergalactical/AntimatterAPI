package muramasa.gregtech.common.blocks;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.MaterialSet;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.client.render.GTModelLoader;
import muramasa.gregtech.client.render.StateMapperRedirect;
import muramasa.gregtech.client.render.models.ModelTextureData;
import muramasa.gregtech.client.render.overrides.ItemOverrideOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockOre extends BlockBaked {

    private static StateMapperRedirect stateMapRedirect = new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_ore"));
    private static ModelTextureData model;

    private StoneType type;
    private Material material;

    public BlockOre(StoneType type, Material material) {
        super(TextureData.get().base(type.getTexture()).overlay(material.getSet().getBlockTexture(Prefix.Ore)));
        setUnlocalizedName("ore_" + type.getName() + "_" + material.getName());
        setRegistryName("ore_" + type.getName() + "_" + material.getName());
        setCreativeTab(Ref.TAB_BLOCKS);
        this.type = type;
        this.material = material;
    }

    public StoneType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (type == StoneType.STONE) {
            items.add(new ItemStack(this));
        }
    }

    //TODO
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return 1.0f + (getHarvestLevel(blockState) * 1.0f);
    }

    //TODO
    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(material.getOre(1));
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_ore", "inventory"));
        ModelLoader.setCustomStateMapper(this, stateMapRedirect);
        if (model == null) model = new ModelTextureData(this);
        GTModelLoader.register("block_ore", model);
    }

    @Override
    public ModelResourceLocation getModel() {
        return new ModelResourceLocation(Ref.MODID + ":ore");
    }

    @Override
    public List<Texture> getTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        textures.addAll(StoneType.getAllTextures());
        for (MaterialSet set : MaterialSet.values()) {
            textures.add(set.getBlockTexture(Prefix.Ore));
        }
        return textures;
    }

    @Override
    public ItemOverrideList getOverride(IBakedModel baked) {
        return new ItemOverrideOre(baked);
    }

    public static class ColorHandler implements IBlockColor {
        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
            if (tintIndex == 1) {
                BlockOre block = (BlockOre) state.getBlock();
                return block.getMaterial().getRGB();
            }
            return -1;
        }
    }
}
