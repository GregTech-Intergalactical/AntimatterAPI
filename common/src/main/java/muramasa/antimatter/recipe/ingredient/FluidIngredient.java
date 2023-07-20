package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonObject;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
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
import tesseract.FluidPlatformUtils;
import tesseract.TesseractGraphWrappers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FluidIngredient {
    private FluidHolder[] stacks = new FluidHolder[0];
    private TagKey<Fluid> tag;
    private long amount = 0;
    private boolean evaluated = false;

    public static final FluidIngredient EMPTY = new FluidIngredient();

    private FluidIngredient() {

    }


    public FluidHolder[] getStacks() {
        if (evaluated) return stacks;
        evaluated = true;
        if (tag != null) {
            List<FluidHolder> list = new ObjectArrayList<>();
            Registry.FLUID.getTagOrEmpty(tag).iterator().forEachRemaining(t -> {
                FluidHolder stack = FluidHooks.newFluidHolder(t.value(), getAmount(), null);
                list.add(stack);
            });
            this.stacks = list.toArray(new FluidHolder[0]);
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
            FluidHolder stack = t.copyHolder();
            stack.setAmount(droplets);
            return stack;
        }).toArray(FluidHolder[]::new);
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
        for (FluidHolder stack : this.stacks) {
            FluidPlatformUtils.writeToPacket(buffer, stack);
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
        FluidHolder[] stacks = new FluidHolder[count];
        for (int i = 0; i < count; i++) {
            stacks[i] = FluidPlatformUtils.readFromPacket(buf);
        }
        FluidIngredient ing = new FluidIngredient();
        long amount = 0;
        for (FluidHolder stack : stacks) {
            if (stack.getFluidAmount() > amount){
                amount = stack.getFluidAmount();
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

    public static FluidIngredient of(FluidHolder stack) {
        Objects.requireNonNull(stack);
        FluidIngredient ing = new FluidIngredient();
        ing.stacks = new FluidHolder[]{stack};
        ing.amount = stack.getFluidAmount();
        return ing;
    }

    public List<FluidHolder> drain(MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        return drain(amount, handler, input, simulate);
    }

    public List<FluidHolder> drain(long amount, MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        long drained = amount;
        List<FluidHolder> ret = new ObjectArrayList<>(1);
        for (FluidHolder stack : stacks) {
            stack = stack.copyHolder();
            stack.setAmount(drained);
            FluidHolder drain = input ? handler.drainInput(stack, simulate) : handler.extractFluid(stack, simulate);
            drained -= drain.getFluidAmount();
            if (!drain.isEmpty()) {
                ret.add(drain);
            }
            if (drained == 0) break;
        }
        return ret;
    }

    public long drainedAmount(long amount, MachineFluidHandler<?> handler, boolean input, boolean simulate) {
        return drain(amount, handler, input, simulate).stream().mapToLong(FluidHolder::getFluidAmount).sum();
    }
}
