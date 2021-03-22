package muramasa.antimatter.material;

import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.cover.IHaveCover;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CoverMaterialItem extends MaterialItem implements IHaveCover {

    protected final ICover cover;

    public CoverMaterialItem(String domain, MaterialType<?> type, Material material, ICover cover, Properties properties) {
        super(domain, type, material, properties);
        this.cover = cover;
    }

    public CoverMaterialItem(String domain, MaterialType<?> type,ICover cover, Material material) {
        super(domain, type, material);
        this.cover = cover;
    }

    @Override
    public ICover getCover() {
        return cover;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(new StringTextComponent("Has cover."));
    }
}
