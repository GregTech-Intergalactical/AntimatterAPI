package muramasa.gregtech.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.common.blocks.BlockOre;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideOre extends ItemOverrideList {

    private IBakedModel[] bakedModels;

    public ItemOverrideOre(IBakedModel[] bakedModels) {
        super(ImmutableList.of());
        this.bakedModels = bakedModels;
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        Material material = ((BlockOre) Block.getBlockFromItem(stack.getItem())).getMaterial();
        if (material != null) {
            return bakedModels[material.getSet().ordinal()];
        }
        return bakedModels[0];
    }
}
