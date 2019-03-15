package muramasa.gregtech.client.render.overrides;

import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.client.render.bakedmodels.BakedBase;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideOre extends ItemOverrideTextureData {

    private static IBakedModel[] BASE = new IBakedModel[StoneType.getLastInternalId()];

    public ItemOverrideOre(IBakedModel baked) {
        super(baked);
        for (StoneType type : StoneType.getAll()) {
            BASE[type.getInternalId()] = BakedBase.getBaked(baked, BakedBase.tex(baked.getQuads(null, null, -1), 0, type.getTexture()));
        }
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
//        Block block = Block.getBlockFromItem(stack.getItem());
//        if (!(block instanceof BlockOre)) return originalModel;
//        BlockOre ore = (BlockOre) block;
//        return new BakedTextureData(BASE[ore.getType().getInternalId()], ore.getBlockData());
        return super.handleItemState(originalModel, stack, world, entity);
    }
}
