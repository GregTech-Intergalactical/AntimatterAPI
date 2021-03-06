package muramasa.antimatter.integration.jei.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.recipe.Recipe;
import net.minecraft.client.gui.FontRenderer;

import java.util.Objects;

public class FuelInfoRenderer implements IRecipeInfoRenderer{
    public static final IRecipeInfoRenderer INSTANCE = new FuelInfoRenderer();
    @Override
    public void render(MatrixStack stack, Recipe recipe, FontRenderer fontRenderer, int guiOffsetX, int guiOffsetY) {
        String fuelPerMb =  "Fuel content(mb): "+ ((double)recipe.getPower() / (double) Objects.requireNonNull(recipe.getInputFluids())[0].getAmount());
        String fuelPerB =  "Fuel content(bb): "+ ((double)recipe.getPower() / (double) Objects.requireNonNull(recipe.getInputFluids())[0].getAmount()) *1000;
        renderString(stack, fuelPerMb,fontRenderer, 5, 5,guiOffsetX,guiOffsetY);
        renderString(stack, fuelPerB,fontRenderer, 5, 15,guiOffsetX,guiOffsetY);
    }
}
