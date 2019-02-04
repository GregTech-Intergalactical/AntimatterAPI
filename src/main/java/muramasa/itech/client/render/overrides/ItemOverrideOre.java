package muramasa.itech.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.itech.api.materials.Material;
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
        return bakedModels[Material.get(stack.getMetadata()).getSet().ordinal()];
    }
}
