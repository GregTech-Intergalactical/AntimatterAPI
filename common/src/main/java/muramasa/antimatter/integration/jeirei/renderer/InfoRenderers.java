package muramasa.antimatter.integration.jeirei.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.IRecipe;
import net.minecraft.client.gui.Font;

import java.util.Objects;

/*
 Dist cleaning and that annoying stuff means that I have to lazily init info renderers.
 There is most likely a better way but I cba...
 */
public class InfoRenderers {
    public static final IRecipeInfoRenderer BLASTING_RENDERER = new IRecipeInfoRenderer() {
        @Override
        public void render(PoseStack stack, IRecipe recipe, Font fontRenderer, int guiOffsetX, int guiOffsetY) {
            if (recipe.getDuration() == 0 && recipe.getPower() == 0) return;
            String power = "Duration: " + recipe.getDuration() + " ticks";
            String euT = "EU/t: " + recipe.getPower();
            String total = "Total: " + recipe.getPower() * recipe.getDuration() + " EU";
            String temperature = "Temperature: " + recipe.getSpecialValue() + " K";
            Tier tier = Tier.getTier((int) (recipe.getPower() / recipe.getAmps()));
            String formattedText = " (" + tier.getId().toUpperCase() + ")";
            renderString(stack, power, fontRenderer, 5, 0, guiOffsetX, guiOffsetY);
            renderString(stack, euT, fontRenderer, 5, 10, guiOffsetX, guiOffsetY);
            renderString(stack, formattedText, fontRenderer, 5 + stringWidth(euT, fontRenderer), 10, Tier.EV.getRarityFormatting().getColor(), guiOffsetX, guiOffsetY);
            renderString(stack, temperature, fontRenderer, 5, 20, guiOffsetX, guiOffsetY);
            renderString(stack, total, fontRenderer, 5, 30, guiOffsetX, guiOffsetY);
        }

        @Override
        public int getRows() {
            return 4;
        }
    };

    public static final IRecipeInfoRenderer BASIC_RENDERER = new IRecipeInfoRenderer() {
        @Override
        public void render(PoseStack stack, IRecipe recipe, Font fontRenderer, int guiOffsetX, int guiOffsetY) {
            renderString(stack, "Duration: " + recipe.getDuration() + " ticks", fontRenderer, 5, 0, guiOffsetX, guiOffsetY);
        }

        @Override
        public int getRows() {
            return 1;
        }
    };
    public static final IRecipeInfoRenderer EMPTY_RENDERER = (stack, recipe, fontRenderer, guiOffsetX, guiOffsetY) -> {

    };

    public static final IRecipeInfoRenderer DEFAULT_RENDERER = new IRecipeInfoRenderer() {
        public void render(PoseStack stack, IRecipe recipe, Font fontRenderer, int guiOffsetX, int guiOffsetY) {
            if (recipe.getDuration() == 0 && recipe.getPower() == 0) return;
            String power = "Duration: " + recipe.getDuration() + " ticks";
            String euT = "EU/t: " + recipe.getPower();
            String amps = "Amps: " + recipe.getAmps();
            String total = "Total: " + recipe.getPower() * recipe.getDuration() + " EU";
            Tier tier = Tier.getTier((int) (recipe.getPower() / recipe.getAmps()));
            String formattedText = " (" + tier.getId().toUpperCase() + ")";
            renderString(stack, power, fontRenderer, 5, 0, guiOffsetX, guiOffsetY);
            renderString(stack, euT, fontRenderer, 5, 10, guiOffsetX, guiOffsetY);
            renderString(stack, formattedText, fontRenderer, 5 + stringWidth(euT, fontRenderer), 10, Tier.EV.getRarityFormatting().getColor(), guiOffsetX, guiOffsetY);
            renderString(stack, amps, fontRenderer, 5, 20, guiOffsetX, guiOffsetY);
            renderString(stack, total, fontRenderer, 5, 30, guiOffsetX, guiOffsetY);
        }

        @Override
        public int getRows() {
            return 4;
        }
    };

    public static final IRecipeInfoRenderer RF_RENDERER = new IRecipeInfoRenderer() {
        public void render(PoseStack stack, IRecipe recipe, Font fontRenderer, int guiOffsetX, int guiOffsetY) {
            if (recipe.getDuration() == 0 && recipe.getPower() == 0) return;
            String power = "Duration: " + recipe.getDuration() + " ticks";
            String euT = "RF/t: " + recipe.getPower();
            String total = "Total: " + recipe.getPower() * recipe.getDuration() + " RF";
            renderString(stack, power, fontRenderer, 5, 0, guiOffsetX, guiOffsetY);
            renderString(stack, euT, fontRenderer, 5, 10, guiOffsetX, guiOffsetY);
            renderString(stack, total, fontRenderer, 5, 20, guiOffsetX, guiOffsetY);
        }

        @Override
        public int getRows() {
            return 3;
        }
    };

    public static final IRecipeInfoRenderer FUEL_RENDERER = new IRecipeInfoRenderer() {
        @Override
        public void render(PoseStack stack, IRecipe recipe, Font fontRenderer, int guiOffsetX, int guiOffsetY) {
            String fuelPerMb = "Fuel content(mb): " + ((double) recipe.getPower() / (double) Objects.requireNonNull(recipe.getInputFluids()).get(0).getAmount());
            String fuelPerB = "Fuel content(bb): " + ((double) recipe.getPower() / (double) Objects.requireNonNull(recipe.getInputFluids()).get(0).getAmount()) * 1000;
            renderString(stack, fuelPerMb, fontRenderer, 5, 0, guiOffsetX, guiOffsetY);
            renderString(stack, fuelPerB, fontRenderer, 5, 10, guiOffsetX, guiOffsetY);
        }

        @Override
        public int getRows() {
            return 2;
        }
    };

    public static final IRecipeInfoRenderer STEAM_RENDERER = new IRecipeInfoRenderer() {
        @Override
        public void render(PoseStack stack, IRecipe recipe, Font fontRenderer, int guiOffsetX, int guiOffsetY) {
            String power = "Duration: " + recipe.getDuration() + " ticks";
            String euT = "Steam: ";
            String total = "Total steam: " + recipe.getDuration() * recipe.getPower() + " mb";
            renderString(stack, power, fontRenderer, 5, 0, guiOffsetX, guiOffsetY);
            renderString(stack, euT + recipe.getPower() + "mb/t", fontRenderer, 5, 10, guiOffsetX, guiOffsetY);
            renderString(stack, total, fontRenderer, 5, 20, guiOffsetX, guiOffsetY);
        }

        @Override
        public int getRows() {
            return 3;
        }
    };
}
