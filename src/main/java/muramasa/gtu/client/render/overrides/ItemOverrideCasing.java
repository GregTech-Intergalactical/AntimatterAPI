package muramasa.gtu.client.render.overrides;

import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedTextureDataItem;
import muramasa.gtu.api.blocks.BlockBaked;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideCasing extends ItemOverrideTextureData {

    public ItemOverrideCasing() {
        super(ModelUtils.BAKED_BASIC);
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        return new BakedTextureDataItem(ModelUtils.BAKED_BASIC, ((BlockBaked) Block.getBlockFromItem(stack.getItem())).getDefaultData());
    }
}
