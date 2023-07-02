package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.recipe.RecipeUtil;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.TesseractGraphWrappers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FluidIngredient {
    private FluidStack[] stacks = new FluidStack[0];
    private TagKey<Fluid> tag;
    private long amount = 0;
    private boolean evaluated = false;

    public static final FluidIngredient EMPTY = new FluidIngredient();

    private FluidIngredient() {

    }


    public FluidStack[] getStacks() {
        if (evaluated) return stacks;
        evaluated = true;
        if (tag != null) {
            List<FluidStack> list = new ObjectArrayList<>();
            Registry.FLUID.getTagOrEmpty(tag).iterator().forEachRemaining(t -> {
                FluidStack stack = new FluidStack(t.value(), this.getAmountInMB());
                list.add(stack);
            });
            this.stacks = list.toArray(new FluidStack[0]);
        }
        return stacks;
    }

    public TagKey<Fluid> getTag() {
        return tag;
    }

    public long getAmount() {
        return amount;
    }

    public int getAmountInMB(){
        return (int) (getAmount() / TesseractGraphWrappers.dropletMultiplier);
    }

    public FluidIngredient copy(long droplets) {
        FluidIngredient ing = new FluidIngredient();
        ing.stacks = Arrays.stream(stacks).map(t -> {
            FluidStack stack = t.copy();
            stack.setAmount(droplets);
            return stack;
        }).toArray(FluidStack[]::new);
        ing.evaluated = this.evaluated;
        ing.amount = droplets;
        ing.tag = this.tag;
        return ing;
    }

    public FluidIngredient copyMB(int amount) {
        return copy(amount * TesseractGraphWrappers.dropletMultiplier);
    }

    public void write(FriendlyByteBuf buffer) {
        getStacks();
        buffer.writeVarInt(stacks.length);
        for (FluidStack stack : this.stacks) {
            AntimatterPlatformUtils.writeFluidStack(stack, buffer);
        }
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        if (tag != null){
            json.addProperty("fluidTag", true);
            json.addProperty("tag", tag.location().toString());
            json.addProperty("amount", amount);
        } else {
            json = RecipeUtil.fluidstackToJson(stacks[0]);
        }
        return json;
    }

    public static FluidIngredient of(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        FluidStack[] stacks = new FluidStack[count];
        for (int i = 0; i < count; i++) {
            stacks[i] = AntimatterPlatformUtils.readFluidStack(buf);
        }
        FluidIngredient ing = new FluidIngredient();
        long amount = 0;
        for (FluidStack stack : stacks) {
            if (stack.getRealAmount() > amount){
                amount = stack.getRealAmount();
            }
        }
        ing.stacks = stacks;
        ing.evaluated = true;
        ing.amount = amount;
        return ing;
    }

    public static FluidIngredient of(ResourceLocation loc, long droplets) {
        Objects.requireNonNull(loc);
        FluidIngredient ing = new FluidIngredient();
        ing.tag = TagUtils.getFluidTag(loc);
        ing.amount = droplets;
        return ing;
    }

    public static FluidIngredient of(Material mat, long droplets) {
        return of(new ResourceLocation(AntimatterPlatformUtils.isForge() ? "forge" : "c", mat.getId()), droplets);
    }

    public static FluidIngredient ofMB(ResourceLocation loc, int amount) {
        return of(loc, amount * TesseractGraphWrappers.dropletMultiplier);
    }

    public static FluidIngredient ofMB(Material mat, int amount) {
        return of(mat, amount * TesseractGraphWrappers.dropletMultiplier);
    }

    public static FluidIngredient of(FluidStack stack) {
        Objects.requireNonNull(stack);
        FluidIngredient ing = new FluidIngredient();
        ing.stacks = new FluidStack[]{stack};
        ing.amount = stack.getRealAmount();
        return ing;
    }

    public List<FluidStack> drain(MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        return drain(amount, handler, input, simulate);
    }

    public List<FluidStack> drain(long amount, MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        long drained = amount;
        List<FluidStack> ret = new ObjectArrayList<>(1);
        IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
        for (FluidStack stack : stacks) {
            stack = stack.copy();
            stack.setAmount(drained);
            FluidStack drain = input ? handler.drainInput(stack, action) : handler.drain(stack, action);
            drained -= drain.getRealAmount();
            if (!drain.isEmpty()) {
                ret.add(drain);
            }
            if (drained == 0) break;
        }
        return ret;
    }

    public long drainedAmount(long amount, MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        return drain(amount, handler, input, simulate).stream().mapToLong(FluidStack::getRealAmount).sum();
    }
}
