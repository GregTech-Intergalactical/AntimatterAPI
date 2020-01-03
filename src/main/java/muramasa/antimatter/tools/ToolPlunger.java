package muramasa.antimatter.tools;

import muramasa.antimatter.items.MaterialTool;
import muramasa.antimatter.materials.Material;
import net.minecraft.item.ItemStack;

public class ToolPlunger extends MaterialTool {

    public ToolPlunger() {
        super(GregTechToolType.PLUNGER);
    }

    @Override
    public int getRGB(ItemStack stack, int i) {
        Material mat = getSecondary(stack);
        return i == 0 ? -1 : mat != null ? mat.getRGB() : -1;
    }
}
