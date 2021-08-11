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

    @Shadow
    private ItemStack copyEnchantments(ItemStack copyTo, ItemStack copyFrom){
        throw new AssertionError();
    }

    @Shadow
    private ItemStack removeEnchantments(ItemStack stack, int damage, int count){
        throw new AssertionError();
    }

    @Inject(/*remap = false,*/ method = "updateRecipeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void checkTools(CallbackInfo ci){
        ItemStack a = this.inputInventory.getStackInSlot(0);
        ItemStack b = this.inputInventory.getStackInSlot(1);
        boolean match = true;
        if (a.getItem() == b.getItem()){
            if (a.getItem() instanceof IAntimatterTool){
                IAntimatterTool tool = (IAntimatterTool) a.getItem();
                match = tool.getPrimaryMaterial(a) == tool.getPrimaryMaterial(b) && tool.getSecondaryMaterial(a) == tool.getSecondaryMaterial(b);
                if (match){
                    int k = a.getMaxDamage() - a.getDamage();
                    int l = a.getMaxDamage() - b.getDamage();
                    int i1 = k + l + a.getMaxDamage() * 5 / 100;
                    int i = Math.max(a.getMaxDamage() - i1, 0);
                    ItemStack copy = this.copyEnchantments(a, b);
                    if (tool.getAntimatterToolType().isPowered()) {
                        this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                        this.detectAndSendChanges();
                        ci.cancel();
                        return;
                    }
                    this.outputInventory.setInventorySlotContents(0, this.removeEnchantments(copy, i, 1));
                    this.detectAndSendChanges();
                    ci.cancel();
                    return;
                }
            } else if (a.getItem() instanceof IAntimatterArmor){
                IAntimatterArmor tool = (IAntimatterArmor) a.getItem();
                match = tool.getMaterial(a) == tool.getMaterial(b);
            }

        }
        if (!match){
            this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            this.detectAndSendChanges();
            ci.cancel();
        }
    }
}
