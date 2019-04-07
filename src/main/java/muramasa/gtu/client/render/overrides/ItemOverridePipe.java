package muramasa.gtu.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedPipe;
import muramasa.gtu.client.render.bakedmodels.BakedTextureDataItem;
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
            if (stack.getTagCompound().hasKey(Ref.KEY_CABLE_STACK_INSULATED)) {
                int index = stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED) ? 2 : 1;
                return new BakedTextureDataItem(BakedPipe.BAKED[size.ordinal()][2], new TextureData().base(Textures.PIPE_DATA[index].getBase()).overlay(Textures.PIPE_DATA[index].getOverlay()[size.ordinal()]));
            }
            return new BakedTextureDataItem(BakedPipe.BAKED[size.ordinal()][2], new TextureData().base(Textures.PIPE_DATA[0].getBase()).overlay(Textures.PIPE_DATA[0].getOverlay()[size.ordinal()]));
        }
        return ModelUtils.BAKED_MISSING;
    }
}
