package muramasa.antimatter.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RepairItemRecipe;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin extends SpecialRecipe {

    public RepairItemRecipeMixin(ResourceLocation idIn) {
        super(idIn);
    }

    @Inject(method = "assemble", at = @At("HEAD"), cancellable = true)
    private void getCraftingResultInject(CraftingInventory inv, CallbackInfoReturnable<ItemStack> ci) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);
                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.isRepairable()) {
                        return;
                    }
                }
            }
        }
        if (list.size() == 2) {
            ItemStack a = list.get(0);
            ItemStack b = list.get(1);
            if (a.getItem() == b.getItem() && a.getCount() == 1 && b.getCount() == 1 && a.isRepairable() && (a.getItem() instanceof IAntimatterTool || a.getItem() instanceof IAntimatterArmor)) {
                boolean match = true;
                if (a.getItem() instanceof IAntimatterTool) {
                    IAntimatterTool tool = (IAntimatterTool) a.getItem();
                    match = tool.getPrimaryMaterial(a) == tool.getPrimaryMaterial(b) && tool.getSecondaryMaterial(a) == tool.getSecondaryMaterial(b);
                } else if (a.getItem() instanceof IAntimatterArmor) {
                    IAntimatterArmor armor = (IAntimatterArmor) a.getItem();
                    match = armor.getMaterial(a) == armor.getMaterial(b);
                }
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

                ItemStack output = item instanceof IAntimatterTool ? ((IAntimatterTool) item).asItemStack(((IAntimatterTool) item).getPrimaryMaterial(a), ((IAntimatterTool) item).getSecondaryMaterial(a)) : ((IAntimatterArmor) item).asItemStack(((IAntimatterArmor) item).getMaterial(a));
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
    private void matchesInject(CraftingInventory inv, World world, CallbackInfoReturnable<Boolean> ci) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);
                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.isRepairable()) {
                        return;
                    }
                }
            }
        }
        if (list.size() == 2) {
            ItemStack a = list.get(0);
            ItemStack b = list.get(1);
            boolean match = true;
            if (a.getItem() instanceof IAntimatterTool) {
                IAntimatterTool tool = (IAntimatterTool) a.getItem();
                match = tool.getPrimaryMaterial(a) == tool.getPrimaryMaterial(b) && tool.getSecondaryMaterial(a) == tool.getSecondaryMaterial(b);
            } else if (a.getItem() instanceof IAntimatterArmor) {
                IAntimatterArmor armor = (IAntimatterArmor) a.getItem();
                match = armor.getMaterial(a) == armor.getMaterial(b);
            }
            if (!match) ci.setReturnValue(false);
        }
    }
}
