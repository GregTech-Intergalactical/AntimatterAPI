package muramasa.gregtech.common.blocks;

import muramasa.gregtech.api.enums.Coil;
import muramasa.gregtech.client.render.StateMapperRedirect;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityCoil;
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

public class BlockCoil extends Block {

    private static LinkedHashMap<String, BlockCoil> BLOCK_LOOKUP = new LinkedHashMap<>();

    private Coil type;

    public BlockCoil(Coil type) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName("coil_" + type.getName());
        setRegistryName("coil_" + type.getName());
        setCreativeTab(Ref.TAB_BLOCKS);
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
        return new TileEntityCoil();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_coil", "coil_type=" + type.getName()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":block_coil", "coil_type=" + type.getName())));
    }

    public Coil getType() {
        return type;
    }

    public static BlockCoil get(String type) {
        return BLOCK_LOOKUP.get(type);
    }

    public static Collection<BlockCoil> getAll() {
        return BLOCK_LOOKUP.values();
    }
}
