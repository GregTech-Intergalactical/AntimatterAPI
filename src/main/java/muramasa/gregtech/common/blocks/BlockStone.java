package muramasa.gregtech.common.blocks;

import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.client.render.StateMapperRedirect;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.LinkedHashMap;

public class BlockStone extends Block {

    private static LinkedHashMap<String, BlockStone> BLOCK_LOOKUP = new LinkedHashMap<>();

    private StoneType type;

    public BlockStone(StoneType type) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName("stone_" + type.getName());
        setRegistryName("stone_" + type.getName());
        setCreativeTab(Ref.TAB_MACHINES);
        this.type = type;
        BLOCK_LOOKUP.put(type.getName(), this);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_stone", "stone_type=" + type.getName()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":block_stone", "stone_type=" + type.getName())));
    }

    public StoneType getType() {
        return type;
    }

    public static BlockStone get(String type) {
        return BLOCK_LOOKUP.get(type);
    }

    public static Collection<BlockStone> getAll() {
        return BLOCK_LOOKUP.values();
    }
}
