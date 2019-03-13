package muramasa.gregtech.client.render.overrides;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideBasic extends ItemOverrideList {

    private IBakedModel bakedModel;

    public ItemOverrideBasic(IBakedModel bakedModel) {
        super(ImmutableList.of());
        this.bakedModel = bakedModel;
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        return bakedModel;
    }
}
