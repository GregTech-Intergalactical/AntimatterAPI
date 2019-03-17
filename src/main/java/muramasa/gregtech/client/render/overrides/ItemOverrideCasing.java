package muramasa.gregtech.client.render.overrides;

import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.client.render.bakedmodels.BakedTextureDataItem;
import muramasa.gregtech.client.render.models.ModelBase;
import muramasa.gregtech.common.blocks.BlockBaked;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideCasing extends ItemOverrideTextureData {

    private static IBakedModel[] BASE = new IBakedModel[Casing.getLastInternalId()];

    public ItemOverrideCasing() {
        super(ModelBase.BAKED_BASIC);
//        for (Casing type : Casing.getAll()) {
//            BASE[type.getInternalId()] = BakedBase.getBaked(baked, BakedBase.tex(baked.getQuads(null, null, -1), 0, type.getTexture()));
//        }
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        return new BakedTextureDataItem(baked, ((BlockBaked) Block.getBlockFromItem(stack.getItem())).getBlockData());
    }
}
