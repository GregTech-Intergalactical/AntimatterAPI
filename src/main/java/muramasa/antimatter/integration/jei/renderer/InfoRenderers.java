package muramasa.antimatter.integration.jei.renderer;

import net.minecraft.util.LazyValue;

/*
 Dist cleaning and that annoying stuff means that I have to lazily init info renderers.
 There is most likely a better way but I cba...
 */
public class InfoRenderers {
    public static final LazyValue<IRecipeInfoRenderer> DEFAULT_RENDERER = new LazyValue<>(() -> InternalInfoRenderers.DEFAULT_RENDERER);
    public static final LazyValue<IRecipeInfoRenderer> EMPTY_RENDERER = new LazyValue<>(() -> InternalInfoRenderers.EMPTY_RENDERER);
    public static final LazyValue<IRecipeInfoRenderer> STEAM_RENDERER = new LazyValue<>(() -> InternalInfoRenderers.STEAM_RENDERER);
    public static final LazyValue<IRecipeInfoRenderer> BLASTING_RENDERER = new LazyValue<>(() -> InternalInfoRenderers.BLASTING_RENDERER);
    public static final LazyValue<IRecipeInfoRenderer> FUEL_RENDERER = new LazyValue<>(() -> InternalInfoRenderers.FUEL_RENDERER);
}
