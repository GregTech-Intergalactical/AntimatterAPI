package muramasa.antimatter.mixin;

import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GrindstoneContainer.class)
public abstract class GrindstoneContainerMixin extends Container {
    @Shadow
    private IInventory inputInventory;
    @Shadow
    private IInventory outputInventory;
    protected GrindstoneContainerMixin(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }
    @Inject(method = "updateRecipeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void checkTools(CallbackInfo ci){
        ItemStack itemstack = this.inputInventory.getStackInSlot(0);
        ItemStack itemstack1 = this.inputInventory.getStackInSlot(1);
        boolean match = true;
        if (itemstack.getItem() == itemstack1.getItem()){
            if (itemstack.getItem() instanceof IAntimatterTool){
                IAntimatterTool tool = (IAntimatterTool) itemstack.getItem();
                IAntimatterTool tool1 = (IAntimatterTool) itemstack1.getItem();
                match = tool.getPrimaryMaterial(itemstack) == tool1.getPrimaryMaterial(itemstack1) && tool.getSecondaryMaterial(itemstack) == tool1.getSecondaryMaterial(itemstack1);
            } else if (itemstack.getItem() instanceof IAntimatterArmor){
                IAntimatterArmor tool = (IAntimatterArmor) itemstack.getItem();
                IAntimatterArmor tool1 = (IAntimatterArmor) itemstack1.getItem();
                match = tool.getMaterial(itemstack) == tool1.getMaterial(itemstack1);
            }
        }
        if (!match){
            this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            this.detectAndSendChanges();
            ci.cancel();
        }
    }
}
