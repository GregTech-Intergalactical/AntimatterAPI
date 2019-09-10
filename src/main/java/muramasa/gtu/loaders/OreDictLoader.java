package muramasa.gtu.loaders;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockStorage;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.items.MaterialTool;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.ore.BlockOre;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.Data;
import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictLoader {

    public static void init() {
        //Register materialItem entries (materialTypeMaterialName)
        GregTechAPI.all(MaterialItem.class).forEach(i -> {
            Material material = i.getMaterial();
            OreDictionary.registerOre(i.getType().oreName(material), i);
            if (i.getType() == MaterialType.ROD) {
                OreDictionary.registerOre("stick".concat(Utils.underscoreToUpperCamel(material.getId())), i);
            }
            if (i.getType() == MaterialType.LENS) { // Can apply to other MaterialItems too
                EnumDyeColor colour = Utils.determineColour(material.getRGB());
                OreDictionary.registerOre(i.getType().getId().concat(Utils.underscoreToUpperCamel(colour.getName())), i);
            }
        });

        GregTechAPI.all(BlockOre.class).forEach(o -> {
            StoneType[] stoneTypes = o.getStoneTypesFromSet(o.getSetId());
            for (int i = 0; i < stoneTypes.length; i++) {
                StoneType currentType = stoneTypes[i];
                String oreName = o.getType().getType().getId() + "_" + currentType.getOreId() + "_" + o.getMaterial().getId();
                if (currentType == StoneType.ENDSTONE || currentType == StoneType.NETHERRACK) {
                    OreDictionary.registerOre(Utils.underscoreToLowerCamel(oreName), new ItemStack(o, 1, i));
                    oreName = o.getType().getType().getId() + "_" + currentType.getId() + "_" + o.getMaterial().getId();
                    OreDictionary.registerOre(Utils.underscoreToLowerCamel(oreName), new ItemStack(o, 1, i));
                }
                else OreDictionary.registerOre(Utils.underscoreToLowerCamel(oreName), new ItemStack(o, 1, i));
            }
        });
        
        GregTechAPI.all(BlockStorage.class).forEach(b -> {
            Material[] materials = b.getMaterials();
            for (int i = 0; i < materials.length; i++) {
                String matId = Utils.underscoreToUpperCamel(StringUtils.capitalize(materials[i].getId()));
                if (b.getType() == MaterialType.BLOCK) OreDictionary.registerOre("block".concat(matId), new ItemStack(b, 1, i));
                else OreDictionary.registerOre("frame".concat(matId), new ItemStack(b, 1, i));
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
