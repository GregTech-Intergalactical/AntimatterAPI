package muramasa.gregtech.api.items;

import com.google.common.collect.Sets;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.util.ToolHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

public class MaterialTool extends ItemTool {

    protected MaterialTool(ToolType type) {
        super(0, 0, ToolMaterial.WOOD, Sets.newHashSet());
        attackDamage = type.getDamageEntity();

    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ToolHelper.getMaxDurability(stack);
    }
}
