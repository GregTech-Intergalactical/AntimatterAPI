package muramasa.antimatter.recipe.map;

import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.capability.Holder;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jeirei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.integration.jeirei.renderer.InfoRenderers;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.tile.TileEntityMachine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public interface IRecipeMap extends ISharedAntimatterObject {
    ItemStack[] EMPTY_ITEM = new ItemStack[0];
    FluidStack[] EMPTY_FLUID = new FluidStack[0];

    IRecipe find(@Nonnull ItemStack[] items, @Nonnull FluidStack[] fluids, Tier tier, @Nonnull Predicate<IRecipe> canHandle);
    void add(IRecipe recipe);
    void compileRecipe(IRecipe recipe);
    void compile(RecipeManager manager);
    void resetCompiled();
    Collection<IRecipe> getRecipes(boolean filterHidden);
    boolean acceptsItem(ItemStack item);
    boolean acceptsFluid(FluidStack fluid);

    @Nullable
    default Tier getGuiTier() {
        return null;
    }

    default Object getIcon() {
        return null;
    }

    default <T extends TileEntityMachine<T>> IRecipe find(Holder<IItemHandler, MachineItemHandler<T>> itemHandler, Holder<IFluidHandler, MachineFluidHandler<T>> fluidHandler, Tier tier, Predicate<IRecipe> validateRecipe) {
        return find(itemHandler.map(MachineItemHandler::getInputs).orElse(EMPTY_ITEM),
                fluidHandler.map(FluidHandler::getInputs).orElse(EMPTY_FLUID), tier, validateRecipe);
    }

    default IRecipe find(@Nonnull LazyOptional<MachineItemHandler<?>> itemHandler,
                        @Nonnull LazyOptional<MachineFluidHandler<?>> fluidHandler, Tier tier, Predicate<IRecipe> validator) {
        return find(itemHandler.map(MachineItemHandler::getInputs).orElse(EMPTY_ITEM),
                fluidHandler.map(MachineFluidHandler::getInputs).orElse(EMPTY_FLUID), tier, validator);
    }
    @Nullable
    default GuiData getGui() {
        return null;
    }

    default Proxy getProxy(){
        return null;
    }

    @Nonnull
    @Environment(EnvType.CLIENT)
    default IRecipeInfoRenderer getInfoRenderer() {
        return InfoRenderers.DEFAULT_RENDERER;
    }

    default Component getDisplayName() {
        return new TranslatableComponent("jei.category." + getLoc().getPath());
    }
}
