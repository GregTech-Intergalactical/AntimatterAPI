package muramasa.gregtech.client.render.overrides;

import com.google.common.collect.ImmutableList;
import muramasa.gregtech.api.items.ItemFluidCell;
import muramasa.gregtech.client.render.ModelUtils;
import muramasa.gregtech.client.render.bakedmodels.BakedFluidCell;
import muramasa.gregtech.client.render.models.ModelFluidCell;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.FluidStack;

public class ItemOverrideFluidCell extends ItemOverrideList {

    public ItemOverrideFluidCell() {
        super(ImmutableList.of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
        FluidStack fluidStack = ItemFluidCell.getContents(stack);
        if (fluidStack == null) return originalModel;
        String name = fluidStack.getFluid().getName();
        IBakedModel baked = ModelUtils.getCache(name);
        if (baked == null) {
            BakedFluidCell bakedCell = (BakedFluidCell) originalModel;
            ModelFluidCell model = new ModelFluidCell(fluidStack.getFluid());
            ModelUtils.putCache(name, (baked = model.bake(TRSRTransformation.identity(), bakedCell.format, ModelUtils.getTextureGetter())));
        }
        return baked;
    }
}
