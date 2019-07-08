package muramasa.gtu.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.gtu.Ref;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedOreItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideOre extends ItemOverrideList {

    public ItemOverrideOre() {
        super(ImmutableList.of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        if (!stack.hasTagCompound()) return ModelUtils.BAKED_MISSING;
        return new BakedOreItem(stack.getTagCompound().getInteger(Ref.KEY_ORE_STACK_MATERIAL), stack.getTagCompound().getString(Ref.KEY_ORE_STACK_STONE), stack.getTagCompound().getInteger(Ref.KEY_ORE_STACK_TYPE));
    }
}
