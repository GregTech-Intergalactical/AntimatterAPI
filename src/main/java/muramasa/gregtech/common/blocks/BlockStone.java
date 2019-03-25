package muramasa.gregtech.common.blocks;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.api.interfaces.IHasModelOverride;
import muramasa.gregtech.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStone extends Block implements IHasModelOverride {

    private StoneType type;

    public BlockStone(StoneType type) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName("stone_" + type.getName());
        setRegistryName("stone_" + type.getName());
        setCreativeTab(Ref.TAB_BLOCKS);
        this.type = type;
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_stone", "stone_type=" + type.getName()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":block_stone", "stone_type=" + type.getName())));
    }
}
