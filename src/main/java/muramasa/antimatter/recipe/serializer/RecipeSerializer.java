package muramasa.antimatter.recipe.serializer;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<Recipe> {

    public static final RecipeSerializer INSTANCE = new RecipeSerializer();

    static {
        INSTANCE.setRegistryName(new ResourceLocation(Ref.ID, "machine"));
    }
    private final static ResourceLocation ING_ID = new ResourceLocation("antimatter", "ingredient");
    @Override
    public Recipe read(ResourceLocation recipeId, JsonObject json) {
        try {
            ItemStack[] outputs = null;
            if (json.has("item_out")) {
                outputs = Streams.stream(json.getAsJsonArray("item_out")).map(t -> CraftingHelper.getItemStack(t.getAsJsonObject(), true)).toArray(ItemStack[]::new);
            }
            FluidStack[] fluidInputs = null;
            if (json.has("fluid_in")) {
                fluidInputs = Streams.stream(json.getAsJsonArray("fluid_in")).map(this::getStack).toArray(FluidStack[]::new);
            }
            FluidStack[] fluidOutputs = null;
            if (json.has("fluid_out")) {
                fluidOutputs = Streams.stream(json.getAsJsonArray("fluid_out")).map(this::getStack).toArray(FluidStack[]::new);
            }
            List<RecipeIngredient> list = new ObjectArrayList<>();
            if (json.has("item_in")) {
                JsonArray array = json.getAsJsonArray("item_in");
                for (JsonElement element : array) {
                    Ingredient i = CraftingHelper.getIngredient(element);
                    if (i instanceof AntimatterIngredient) {
                        list.add(new RecipeIngredient(() -> (AntimatterIngredient) i));
                    } else {{
                        if (element.isJsonObject()) {
                            JsonObject obj = element.getAsJsonObject();
                            if (obj.has("count")) {
                                list.add(AntimatterIngredient.of(obj.get("count").getAsInt(),i.getMatchingStacks()));
                            } else {
                                list.add(AntimatterIngredient.of(i.getMatchingStacks()[0].getCount(),i.getMatchingStacks()));
                            }
                        }
                    }}
                }
            }
            long eut = json.get("euT").getAsLong();
            int duration = json.get("duration").getAsInt();
            int amps = json.has("amps") ? json.get("amps").getAsInt() : 1;
            Recipe r = new Recipe(list, outputs, fluidInputs, fluidOutputs, duration, eut, 0, amps);
            ResourceLocation map = new ResourceLocation(json.get("map").getAsString());
            r.setIds(recipeId, map);
            return r;
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return null;
    }

    private FluidStack getStack(JsonElement element) {
        try {
            return FluidStack.loadFluidStackFromNBT(JsonToNBT.getTagFromJson(element.getAsString()));
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return FluidStack.EMPTY;
    }

    @Nullable
    @Override
    public Recipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        int size = buffer.readInt();
        List<RecipeIngredient> ings = new ObjectArrayList<>(size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                AntimatterIngredient a = (AntimatterIngredient) CraftingHelper.getIngredient(ING_ID, buffer);
                ings.add(new RecipeIngredient(() -> a));
            }
        }
        size = buffer.readInt();
        ItemStack[] out = new ItemStack[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                out[i] = buffer.readItemStack();
            }
        }
        size = buffer.readInt();
        FluidStack[] in = new FluidStack[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                in[i] = FluidStack.readFromPacket(buffer);
            }
        }
        size = buffer.readInt();
        FluidStack[] outf = new FluidStack[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                outf[i] = FluidStack.readFromPacket(buffer);
            }
        }
        size = buffer.readInt();
        int[] chances = new int[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                chances[i] = buffer.readInt();
            }
        }
        long power = buffer.readLong();
        int dur = buffer.readInt();
        int special = buffer.readInt();
        int amps = buffer.readInt();
        ResourceLocation map = buffer.readResourceLocation();
        ResourceLocation id = buffer.readResourceLocation();

        Recipe r = new Recipe(
                ings,
                out.length == 0 ? null : out,
                in.length == 0 ? null : in,
                outf.length == 0 ? null : outf,
                dur,
                power,
                special,
                amps
        );
        if (chances.length > 0)
            r.addChances(chances);
        r.setIds(id, map);
        return r;
    }

    @Override
    public void write(PacketBuffer buffer, Recipe recipe) {
        buffer.writeInt(!recipe.hasInputItems() ? 0 : recipe.getInputItems().size());
        if (recipe.hasInputItems()) {
            recipe.getInputItems().forEach(t -> CraftingHelper.write(buffer, t.get()));
        }
        buffer.writeInt(!recipe.hasOutputItems() ? 0 : recipe.getOutputItems().length);
        if (recipe.hasOutputItems()) {
            Arrays.stream(recipe.getOutputItems()).forEach(buffer::writeItemStack);
        }
        buffer.writeInt(!recipe.hasInputFluids() ? 0 : recipe.getInputFluids().length);
        if (recipe.hasInputFluids()) {
            Arrays.stream(recipe.getInputFluids()).forEach(buffer::writeFluidStack);
        }
        buffer.writeInt(!recipe.hasOutputFluids() ? 0 : recipe.getOutputFluids().length);
        if (recipe.hasOutputFluids()) {
            Arrays.stream(recipe.getOutputFluids()).forEach(buffer::writeFluidStack);
        }
        buffer.writeInt(recipe.hasChances() ? recipe.getChances().length : 0);
        if (recipe.hasChances()) {
            Arrays.stream(recipe.getChances()).forEach(buffer::writeInt);
        }
        buffer.writeLong(recipe.getPower());
        buffer.writeInt(recipe.getDuration());
        buffer.writeInt(recipe.getSpecialValue());
        buffer.writeInt(recipe.getAmps());
        buffer.writeResourceLocation(recipe.mapId);
        buffer.writeResourceLocation(recipe.id);
    }
}
