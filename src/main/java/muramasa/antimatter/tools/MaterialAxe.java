package muramasa.antimatter.tools;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.materials.Material;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;

import javax.annotation.Nullable;

public class MaterialAxe extends AxeItem implements IAntimatterTool {

    protected String domain;
    protected IItemTier tier;
    protected AntimatterToolType type;
    protected Material primary;
    protected Material secondary;

    public MaterialAxe(String domain, AntimatterToolType type, IItemTier tier, Properties properties, Material primary, Material secondary) {
        super(tier, type.getBaseAttackDamage(), type.getBaseAttackSpeed(), properties);
        this.domain = domain;
        this.type = type;
        this.tier = tier;
        this.primary = primary;
        this.secondary = secondary;
        setRegistryName(domain, getId());
        AntimatterAPI.register(IAntimatterTool.class, this);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public AntimatterToolType getType() {
        return type;
    }

    @Override
    public Material getPrimaryMaterial() {
        return primary;
    }

    @Nullable
    @Override
    public Material getSecondaryMaterial() {
        return secondary;
    }

    @Override
    public Item asItem() { return this; }

}
