package muramasa.antimatter.recipe;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.pipe.PipeItemBlock;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.material.MaterialRecipe;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static muramasa.antimatter.material.Material.NULL;

public class RecipeBuilders {

    public static void init() {

    }

    /**
     * RECIPE BUILDERS
     **/

    public static final MaterialRecipe.Provider PROBE_BUILDER = MaterialRecipe.registerProvider("probe", Ref.ID, id -> new MaterialRecipe.ItemBuilder() {

        @Override
        public ItemStack build(CraftingContainer inv, MaterialRecipe.Result mats) {
            Object h = mats.mats.get("helmet");
            ItemStack helmet = ((ItemStack) h).copy();
            CompoundTag nbt = helmet.getOrCreateTag();
            if (nbt.contains("theoneprobe") && nbt.getBoolean("theoneprobe")) return ItemStack.EMPTY;
            nbt.putBoolean("theoneprobe", true);
            return helmet;
        }

        @Override
        public Map<String, Object> getFromResult(@Nonnull ItemStack stack) {
            return ImmutableMap.of();
        }
    });

    public static final MaterialRecipe.Provider CROWBAR_BUILDER = MaterialRecipe.registerProvider("crowbar", Ref.ID, id -> new MaterialRecipe.ItemBuilder() {
        @Override
        public ItemStack build(CraftingContainer inv, MaterialRecipe.Result mats) {
            int dye = ((DyeColor) mats.mats.get("secondary")).getMaterialColor().col;
            IAntimatterTool type = AntimatterAPI.get(IAntimatterTool.class, id);
            ItemStack stack = type.asItemStack(type.getAntimatterItemTier().getPrimary(), NULL);
            stack.getOrCreateTagElement(Ref.TAG_TOOL_DATA).putInt(Ref.KEY_TOOL_DATA_SECONDARY_COLOUR, dye);
            return stack;
        }

        @Override
        public Map<String, Object> getFromResult(@Nonnull ItemStack stack) {
            CompoundTag nbt = stack.getTag().getCompound(Ref.TAG_TOOL_DATA);
            int secondary = nbt.getInt(Ref.KEY_TOOL_DATA_SECONDARY_COLOUR);
            Optional<DyeColor> color = Arrays.stream(DyeColor.values()).filter(t -> t.getMaterialColor().col == secondary).findFirst();
            return ImmutableMap.of( "secondary", color.isEmpty() ? NULL : color.get());
        }
    });

    static {
        PropertyIngredient.addGetter(TagUtils.getForgelikeItemTag("dyes").location(), RecipeBuilders::getColor);
    }

    public static DyeColor getColor(ItemStack stack) {
        if (stack.getItem() instanceof DyeItem) {
            return ((DyeItem)stack.getItem()).getDyeColor();
        } else {
            for(int i = 0; i < 15; ++i) {
                DyeColor color = DyeColor.byId(i);
                String colorString = color.getName();
                if (stack.is(TagUtils.getForgelikeItemTag("dyes/" + colorString))) {
                    return color;
                }
                if (stack.is(TagUtils.getForgelikeItemTag(colorString + "_dyes"))){
                    return color;
                }
            }

            return null;
        }
    }
}
