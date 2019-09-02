package muramasa.gtu.client.render.bakedblockold;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideTextureData extends ItemOverrideList {

    protected IBakedModel baked;

    public ItemOverrideTextureData(IBakedModel baked) {
        super(ImmutableList.of());
        this.baked = baked;
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        return new BakedTextureDataItem(baked, ((BlockBakedOld) Block.getBlockFromItem(stack.getItem())).getDefaultData());
    }
}
