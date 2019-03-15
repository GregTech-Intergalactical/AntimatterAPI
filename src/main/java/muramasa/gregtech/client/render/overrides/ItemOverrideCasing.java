package muramasa.gregtech.client.render.overrides;

import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.client.render.bakedmodels.BakedBase;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideCasing extends ItemOverrideTextureData {

    private static IBakedModel[] BASE = new IBakedModel[Casing.getLastInternalId()];

    public ItemOverrideCasing(IBakedModel baked) {
        super(baked);
        for (Casing type : Casing.getAll()) {
            BASE[type.getInternalId()] = BakedBase.getBaked(baked, BakedBase.tex(baked.getQuads(null, null, -1), 0, type.getTexture()));
        }
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
//        int type = ((BlockCasing) Block.getBlockFromItem(stack.getItem())).getType().getInternalId();
//        return BASE[type];
        return super.handleItemState(originalModel, stack, world, entity);
    }
}
