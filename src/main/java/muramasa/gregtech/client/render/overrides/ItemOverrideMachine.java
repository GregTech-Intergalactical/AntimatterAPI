package muramasa.gregtech.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.client.render.bakedmodels.BakedMachineItem;
import muramasa.gregtech.client.render.models.ModelBase;
import muramasa.gregtech.common.blocks.BlockMachine;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemOverrideMachine extends ItemOverrideList {

    public ItemOverrideMachine() {
        super(ImmutableList.of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        if (!stack.hasTagCompound()) return ModelBase.MISSING;
        return new BakedMachineItem(
            ((BlockMachine) Block.getBlockFromItem(stack.getItem())).getType(),
            Tier.get(stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER))
        );
    }
}