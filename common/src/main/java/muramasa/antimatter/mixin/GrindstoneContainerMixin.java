package muramasa.antimatter.mixin;

import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneContainerMixin extends AbstractContainerMenu {
    @Final
    @Shadow
    private Container repairSlots;
    @Final
    @Shadow
    private Container resultSlots;

    protected GrindstoneContainerMixin(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    @Shadow
    private ItemStack mergeEnchants(ItemStack copyTo, ItemStack copyFrom) {
        throw new AssertionError();
    }

    @Shadow
    private ItemStack removeNonCurses(ItemStack stack, int damage, int count) {
        throw new AssertionError();
    }

    @Inject(method = "createResult", at = @At(value = "HEAD"), cancellable = true)
    private void checkTools(CallbackInfo ci) {
        ItemStack a = this.repairSlots.getItem(0);
        ItemStack b = this.repairSlots.getItem(1);
        boolean match = true;
        if (a.getItem() == b.getItem()) {
            if (a.getItem() instanceof IAntimatterTool) {
                IAntimatterTool tool = (IAntimatterTool) a.getItem();
                match = tool.getPrimaryMaterial(a) == tool.getPrimaryMaterial(b) && tool.getSecondaryMaterial(a) == tool.getSecondaryMaterial(b);
                if (match) {
                    int k = a.getMaxDamage() - a.getDamageValue();
                    int l = a.getMaxDamage() - b.getDamageValue();
                    int i1 = k + l + a.getMaxDamage() * 5 / 100;
                    int i = Math.max(a.getMaxDamage() - i1, 0);
                    ItemStack copy = this.mergeEnchants(a, b);
                    if (tool.getAntimatterToolType().isPowered()) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.broadcastChanges();
                        ci.cancel();
                        return;
                    }
                    this.resultSlots.setItem(0, this.removeNonCurses(copy, i, 1));
                    this.broadcastChanges();
                    ci.cancel();
                    return;
                }
            }

        }
        if (!match) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
            ci.cancel();
        }
    }
}
