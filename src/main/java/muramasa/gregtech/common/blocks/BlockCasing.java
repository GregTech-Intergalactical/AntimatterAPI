package muramasa.gregtech.common.blocks;

import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.client.render.StateMapperRedirect;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityCasing;
import muramasa.gregtech.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;

public class BlockCasing extends Block {

    private static LinkedHashMap<String, BlockCasing> BLOCK_LOOKUP = new LinkedHashMap<>();

    private Casing type;

    public BlockCasing(Casing type) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName("casing_" + type.getName());
        setRegistryName("casing_" + type.getName());
        setCreativeTab(Ref.TAB_MACHINES);
        setSoundType(SoundType.METAL);
        this.type = type;
        BLOCK_LOOKUP.put(type.getName(), this);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCasing();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_casing", "casing_type=" + type.getName()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":block_casing", "casing_type=" + type.getName())));
    }

    public Casing getType() {
        return type;
    }

    public static BlockCasing get(String type) {
        return BLOCK_LOOKUP.get(type);
    }

    public static Collection<BlockCasing> getAll() {
        return BLOCK_LOOKUP.values();
    }
}
