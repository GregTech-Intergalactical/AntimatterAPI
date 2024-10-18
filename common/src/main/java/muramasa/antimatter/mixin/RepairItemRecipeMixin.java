package muramasa.antimatter.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin extends CustomRecipe {

    public RepairItemRecipeMixin(ResourceLocation idIn) {
        super(idIn);
    }

    @Inject(method = "assemble", at = @At("HEAD"), cancellable = true)
    private void getCraftingResultInject(CraftingContainer inv, CallbackInfoReturnable<ItemStack> ci) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);
                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !AntimatterPlatformUtils.INSTANCE.isRepairable(itemstack1)) {
                        return;
                    }
                }
            }
        }
        if (list.size() == 2) {
            ItemStack a = list.get(0);
            ItemStack b = list.get(1);
            if (a.getItem() == b.getItem() && a.getCount() == 1 && b.getCount() == 1 && AntimatterPlatformUtils.INSTANCE.isRepairable(a) && a.getItem() instanceof IAntimatterTool tool) {
                boolean match = tool.getPrimaryMaterial(a) == tool.getPrimaryMaterial(b) && tool.getSecondaryMaterial(a) == tool.getSecondaryMaterial(b);
                if (!match) {
                    ci.setReturnValue(ItemStack.EMPTY);
                    return;
                }
                Item item = a.getItem();
                int j = a.getMaxDamage() - a.getDamageValue();
                int k = a.getMaxDamage() - b.getDamageValue();
                int l = j + k + a.getMaxDamage() * 5 / 100;
                int i1 = a.getMaxDamage() - l;
                if (i1 < 0) {
                    i1 = 0;
                }

                ItemStack output = tool.asItemStack(tool.getPrimaryMaterial(a), tool.getSecondaryMaterial(a));
                output.setDamageValue(i1);

                Map<Enchantment, Integer> map = Maps.newHashMap();
                Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(a);
                Map<Enchantment, Integer> map2 = EnchantmentHelper.getEnchantments(b);
                Registry.ENCHANTMENT.stream().filter(Enchantment::isCurse).forEach((curse) -> {
                    int j1 = Math.max(map1.getOrDefault(curse, 0), map2.getOrDefault(curse, 0));
                    if (j1 > 0) {
                        map.put(curse, j1);
                    }

                });
                if (!map.isEmpty()) {
                    EnchantmentHelper.setEnchantments(map, output);
                }

                ci.setReturnValue(output);
            }
        }
    }

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private void matchesInject(CraftingContainer inv, Level world, CallbackInfoReturnable<Boolean> ci) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);
                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !AntimatterPlatformUtils.INSTANCE.isRepairable(itemstack1)) {
                        return;
                    }
                }
            }
        }
        if (list.size() == 2) {
            ItemStack a = list.get(0);
            ItemStack b = list.get(1);
            boolean match = true;
            if (a.getItem() instanceof IAntimatterTool tool) {
                match = tool.getPrimaryMaterial(a) == tool.getPrimaryMaterial(b) && tool.getSecondaryMaterial(a) == tool.getSecondaryMaterial(b);
            }
            if (!match) ci.setReturnValue(false);
        }
    }
}
