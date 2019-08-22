package muramasa.gtu.loaders;

import java.awt.Color;

import com.google.common.base.CaseFormat;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.items.MaterialTool;
import muramasa.gtu.api.items.StandardItem;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.Data;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictLoader {

    public static void init() {
        //Register materialItem entries (materialTypeMaterialName)
        GregTechAPI.all(MaterialItem.class).forEach(i -> {
            Material material = i.getMaterial();
            OreDictionary.registerOre(i.getType().oreName(material), i);
            if (i.getType() == MaterialType.LENS) { // Can apply to other MaterialItems too
                EnumDyeColor colour = Utils.determineColour(material.getRGB());
                OreDictionary.registerOre(i.getType().getId() + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, colour.getName()), i);
            }
        });

        //Register craftingTool entries (craftingToolType)
        GregTechAPI.all(MaterialTool.class).forEach(t -> OreDictionary.registerOre(t.getType().getOreDict(), t));
    
        //OreDictionary.registerOre("resinSticky", Data.StickyResin);
        OreDictionary.registerOre("dropRubber", Data.StickyResin);
        OreDictionary.registerOre("craftingSawDiamond", Data.DiamondSawBlade);
        OreDictionary.registerOre("craftingGrinderDiamond", Data.DiamondGrindHead);
        OreDictionary.registerOre("craftingGrinderTungsten", Data.TungstenGrindHead);
        OreDictionary.registerOre("ingotAlloyIridium", Data.IridiumAlloyIngot);
        OreDictionary.registerOre("plateReinforcedIridium", Data.IridiumReinforcedPlate);
    }
}
