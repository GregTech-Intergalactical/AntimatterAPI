package muramasa.gregtech.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.client.render.bakedmodels.BakedPipe;
import muramasa.gregtech.client.render.models.ModelBase;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverridePipe extends ItemOverrideList {

    public ItemOverridePipe() {
        super(ImmutableList.of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            return BakedPipe.BAKED[size.ordinal()][2];
        }
        return ModelBase.MISSING;
    }
}
