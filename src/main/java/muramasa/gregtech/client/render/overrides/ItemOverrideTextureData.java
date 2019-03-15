package muramasa.gregtech.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.gregtech.client.render.bakedmodels.BakedTextureData;
import muramasa.gregtech.common.blocks.BlockBaked;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideTextureData extends ItemOverrideList {

    private IBakedModel baked;

    public ItemOverrideTextureData(IBakedModel baked) {
        super(ImmutableList.of());
        this.baked = baked;
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof BlockBaked)) return originalModel;
        return new BakedTextureData(baked, ((BlockBaked) block).getBlockData());
    }
}
