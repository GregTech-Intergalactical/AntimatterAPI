package muramasa.antimatter.recipe.ingredient;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FluidIngredient {
    private FluidStack[] stacks = new FluidStack[0];
    private Tag.Named<Fluid> tag;
    private int amount = 0;
    private boolean evaluated = false;

    public static final FluidIngredient EMPTY = new FluidIngredient();

    private FluidIngredient() {

    }


    public FluidStack[] getStacks() {
        if (evaluated) return stacks;
        evaluated = true;
        if (tag != null) {
            stacks = SerializationTags.getInstance().getTagOrThrow(Registry.FLUID_REGISTRY, tag.getName(), ta -> new RuntimeException("failed to get tag " + ta))
                    .getValues().stream().map(t -> new FluidStack(t, amount)).toArray(FluidStack[]::new);
        }
        return stacks;
    }

    public int getAmount() {
        return amount;
    }

    public FluidIngredient copy(int amount) {
        FluidIngredient ing = new FluidIngredient();
        ing.stacks = Arrays.stream(stacks).map(t -> {
            FluidStack stack = t.copy();
            stack.setAmount(amount);
            return stack;
        }).toArray(FluidStack[]::new);
        ing.evaluated = this.evaluated;
        ing.amount = amount;
        ing.tag = this.tag;
        return ing;
    }

    public void write(FriendlyByteBuf buffer) {
        getStacks();
        buffer.writeVarInt(stacks.length);
        for (FluidStack stack : this.stacks) {
            buffer.writeFluidStack(stack);
        }
    }

    public static FluidIngredient of(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        FluidStack[] stacks = new FluidStack[count];
        for (int i = 0; i < count; i++) {
            stacks[i] = buf.readFluidStack();
        }
        FluidIngredient ing = new FluidIngredient();
        ing.stacks = stacks;
        ing.evaluated = true;
        return ing;
    }

    public static FluidIngredient of(ResourceLocation loc, int amount) {
        Objects.requireNonNull(loc);
        FluidIngredient ing = new FluidIngredient();
        ing.tag = TagUtils.getFluidTag(loc);
        ing.amount = amount;
        return ing;
    }

    public static FluidIngredient of(Material mat, int amount) {
        return of(new ResourceLocation("forge", mat.getId()), amount);
    }

    public static FluidIngredient of(FluidStack stack) {
        Objects.requireNonNull(stack);
        FluidIngredient ing = new FluidIngredient();
        ing.stacks = new FluidStack[]{stack};
        ing.amount = stack.getAmount();
        return ing;
    }

    public List<FluidStack> drain(MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        return drain(amount, handler, input, simulate);
    }

    public List<FluidStack> drain(int amount, MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        int drained = amount;
        List<FluidStack> ret = new ObjectArrayList<>(1);
        IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
        for (FluidStack stack : stacks) {
            stack = stack.copy();
            stack.setAmount(drained);
            FluidStack drain = input ? handler.drainInput(stack, action) : handler.drain(stack, action);
            drained -= drain.getAmount();
            if (!drain.isEmpty()) {
                ret.add(drain);
            }
            if (drained == 0) break;
        }
        return ret;
    }

    public int drainedAmount(int amount, MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        return drain(amount, handler, input, simulate).stream().mapToInt(FluidStack::getAmount).sum();
    }
}
