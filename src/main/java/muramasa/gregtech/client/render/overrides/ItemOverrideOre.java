package muramasa.gregtech.client.render.overrides;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideOre extends ItemOverrideTextureData {

    public ItemOverrideOre(IBakedModel baked) {
        super(baked);
//        BakedBase.getBaked(baked, BakedBase.tex(baked.getQuads(null, null, -1), 0, StoneType.STONE.getTexture()));
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
//        TextureData data = ((BlockOre) Block.getBlockFromItem(stack.getItem())).getBlockData();
////        data.setBase(null);
//        return new BakedTextureData(baked, data);
        return super.handleItemState(originalModel, stack, world, entity);
    }
}
