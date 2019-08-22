package muramasa.gtu.loaders;

import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.recipe.RecipeHelper;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.common.Data;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CraftingRecipeLoader {

    public static void init() {
        
        RecipeHelper.addShaped("hopper", new ItemStack(Blocks.HOPPER), "IxI", "ICI", " I ", 'I', Materials.Vibranium.getIngot(1), 'C', new ItemStack(Blocks.CHEST));
        RecipeHelper.addShapeless("scanner", Data.DebugScanner.get(1), Materials.Vibranium.getIngot(1), Materials.Iridium.getIngot(1));
        
        RecipeHelper.addShaped("gear_wood", Materials.Wood.getGear(1), "SPS", "PwP", "SPS", 'P', "plankWood", 'S', "stickWood");   
        RecipeHelper.addShaped("gear_stone", Materials.Stone.getGear(1), "SPS", "PhP", "SPS", 'P', "cobblestone", 'S', "stone"); 
        
        //RecipeHelper.addShapeless("paper_ring", Materials.Paper.getRing(1), "k", "X", 'X', Materials.Paper.getRing(1));
        //RecipeHelper.addShapeless("silicone_rubber__ring", Materials.SiliconeRubber.getRing(1), "k", "X", 'X', Materials.SiliconeRubber.getRing(1));
        RecipeHelper.addShapeless("rubber_ring", Materials.Rubber.getRing(1), ToolType.KNIFE.getOreDict(), Materials.Rubber.getPlate(1));
        RecipeHelper.addShapeless("styrene_butadiene_rubber_ring", Materials.StyreneButadieneRubber.getRing(1), ToolType.KNIFE.getOreDict(), Materials.StyreneButadieneRubber.getPlate(1));

        RecipeHelper.addShaped("rubber_torch", new ItemStack(Blocks.TORCH, 4), " R ", " S ", "   ", 'R', "dropRubber", 'S', "stickWood");
        RecipeHelper.addShaped("sulfur_torch", new ItemStack(Blocks.TORCH, 6), " R ", " S ", "   ", 'R', Materials.Sulfur.getDust(1), 'S', "stickWood");
    
        RecipeHelper.addShapeless("resin_sticky_piston", new ItemStack(Blocks.STICKY_PISTON), "dropRubber", Blocks.PISTON);
    }
}
