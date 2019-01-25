package muramasa.itech.client.model.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.itech.client.model.models.ModelBase;
import muramasa.itech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideMachine extends ItemOverrideList {

    public ItemOverrideMachine() {
        super(ImmutableList.of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.TAG_MACHINE_STACK_DATA)) {
            NBTTagCompound tag = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
            if (tag.getString(Ref.KEY_MACHINE_STACK_TYPE).contains("hatch")) {
//                System.out.println("item" + tag.getString(Ref.KEY_MACHINE_STACK_TYPE));
//                System.out.println(ModelBase.getBaked("item", tag.getString(Ref.KEY_MACHINE_STACK_TYPE) + tag.getString(Ref.KEY_MACHINE_STACK_TIER))[0].getQuads(null, null, 0).size());
            }
            IBakedModel[] bakedModels = ModelBase.getBaked("item", tag.getString(Ref.KEY_MACHINE_STACK_TYPE) + tag.getString(Ref.KEY_MACHINE_STACK_TIER));
            if (bakedModels != null && bakedModels.length > 0) {
                return bakedModels[0];
            }
        }
        return ModelBase.missingModelBaked;
    }
}