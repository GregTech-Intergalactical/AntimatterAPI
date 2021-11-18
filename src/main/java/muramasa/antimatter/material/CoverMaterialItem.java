package muramasa.antimatter.material;

import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.IHaveCover;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CoverMaterialItem extends MaterialItem implements IHaveCover {

    protected final CoverFactory cover;

    public CoverMaterialItem(String domain, MaterialType<?> type, Material material, CoverFactory cover, Properties properties) {
        super(domain, type, material, properties);
        this.cover = cover;
    }

    public CoverMaterialItem(String domain, MaterialType<?> type, CoverFactory cover, Material material) {
        super(domain, type, material);
        this.cover = cover;
    }

    @Override
    public CoverFactory getCover() {
        return cover;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new StringTextComponent("Has cover."));
    }
}
