package muramasa.gregtech.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.client.render.bakedmodels.BakedModelMachine;
import muramasa.gregtech.client.render.models.ModelBase;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideMachine extends ItemOverrideList {

//    private HashMap<String, IBakedModel> bakedItems;

    public ItemOverrideMachine(/*HashMap<String, IBakedModel> items*/) {
        super(ImmutableList.of());
//        bakedItems = items;
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
//        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.TAG_MACHINE_STACK_DATA)) {
//            NBTTagCompound tag = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
//            String type = ((BlockMachine) Block.getBlockFromItem(stack.getItem())).getType().getName();
//            IBakedModel bakedModel = bakedItems.get(type + tag.getString(Ref.KEY_MACHINE_STACK_TIER));
//            if (bakedModel != null) {
//                return bakedModel;
//            }
//        }
        IBakedModel baked = BakedModelMachine.OVERLAYS[Machines.PULVERIZER.getInternalId()][1];
        if (baked != null) return baked;
        return ModelBase.missingBaked;
    }
}