package muramasa.antimatter.datagen.loaders;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.builder.AntimatterCookingRecipeBuilder;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.MaterialTag.GRINDABLE;
import static muramasa.antimatter.recipe.RecipeBuilders.*;
import static muramasa.antimatter.util.TagUtils.getForgeItemTag;
import static muramasa.antimatter.util.TagUtils.nc;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class MaterialRecipes {
    public static void init(Consumer<IFinishedRecipe> consumer, AntimatterRecipeProvider provider) {
        final ICriterionInstance in = provider.hasSafeItem(WRENCH.getTag());
        provider.addToolRecipe(DUST_BUILDER.get(DUST.getId()), consumer, Ref.ID, "dust_small", "antimatter_dusts",
                "has_wrench", in, DUST.all().stream().filter(t -> t.has(DUST_SMALL)).map(t -> DUST.get(t, 1)).collect(Collectors.toList()), of('D', PropertyIngredient.builder("primary").types(DUST_SMALL).tags(DUST).build()), "DD", "DD");
        provider.addToolRecipe(DUST_BUILDER.get(DUST.getId()), consumer, Ref.ID, "dust_tiny", "antimatter_dusts",
                "has_wrench", in, DUST.all().stream().filter(t -> t.has(DUST_TINY)).map(t -> DUST.get(t, 1)).collect(Collectors.toList()), of('D', PropertyIngredient.builder("primary").types(DUST_TINY).tags(DUST).build()), "DDD", "DDD", "DDD");
        provider.addToolRecipe(DUST_BUILDER.get(ROD.getId()), consumer, Ref.ID, "rod", "antimatter_material",
                "has_wrench", in, ROD.all().stream().filter(t -> t.has(INGOT)).map(t -> ROD.get(t, 1)).collect(Collectors.toList()), of('F', FILE.getTag(), 'I', PropertyIngredient.builder("primary").types(INGOT).tags(ROD).build()), "F", "I");
        provider.addToolRecipe(DUST_TWO_BUILDER.get(BOLT.getId()), consumer, Ref.ID, "bolt", "antimatter_material",
                "has_wrench", in, BOLT.all().stream().map(t -> BOLT.get(t, 2)).collect(Collectors.toList()), of('F', SAW.getTag(), 'I', PropertyIngredient.builder("primary").types(ROD).tags(BOLT).build()), "F", "I");
        provider.addToolRecipe(DUST_TWO_BUILDER.get(SCREW.getId()), consumer, Ref.ID, "screw", "antimatter_material",
                "has_wrench", in, SCREW.all().stream().map(t -> SCREW.get(t, 1)).collect(Collectors.toList()), of('F', FILE.getTag(), 'I', PropertyIngredient.builder("primary").types(BOLT).tags(SCREW).build()), "FI", "I ");

        provider.addToolRecipe(DUST_BUILDER.get(ROTOR.getId()), consumer, Ref.ID, "rotors", "antimatter_material", "has_screwdriver", provider.hasSafeItem(SCREWDRIVER.getTag()),
                ROTOR.all().stream().map(t -> ROTOR.get(t, 1)).collect(Collectors.toList()), ImmutableMap.<Character, Object>builder()
                        .put('S', SCREWDRIVER.getTag())
                        .put('F', FILE.getTag())
                        .put('H', HAMMER.getTag())
                        .put('P', PropertyIngredient.builder("primary").types(PLATE).tags(ROTOR).build())
                        .put('W', PropertyIngredient.builder("primary").types(SCREW).tags(ROTOR).build())
                        .put('R', PropertyIngredient.builder("primary").types(RING).tags(ROTOR).build())
                        .build(),
                "PHP", "WRF", "PSP");

        provider.addToolRecipe(DUST_BUILDER.get(RING.getId()), consumer, Ref.ID, "ring", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                RING.all().stream().map(t -> RING.get(t, 1)).collect(Collectors.toList()), ImmutableMap.<Character, Object>builder()
                        .put('F', FILE.getTag())
                        .put('H', HAMMER.getTag())
                        .put('P', PropertyIngredient.builder("primary").types(PLATE).tags(RING).build())
                        .put('W', PropertyIngredient.builder("primary").types(ROD).tags(RING).build())
                        .build(),
                " W ", "HPF");

        provider.addToolRecipe(DUST_BUILDER.get(PLATE.getId()), consumer, Ref.ID, "plate", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                PLATE.all().stream().filter(t -> t.has(INGOT)).map(t -> PLATE.get(t, 1)).collect(Collectors.toList()), ImmutableMap.<Character, Object>builder()
                        .put('H', HAMMER.getTag())
                        .put('P', PropertyIngredient.builder("primary").types(PLATE).tags(INGOT).build())
                        .build(),
                "HP", "P ");

        provider.addToolRecipe(DUST_BUILDER.get(DUST.getId()), consumer, Ref.ID, "grind_ingot", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                DUST.all().stream().filter(t -> t.has(INGOT, GRINDABLE)).map(t -> DUST.get(t, 1)).collect(Collectors.toList()), ImmutableMap.<Character, Object>builder()
                        .put('M', MORTAR.getTag())
                        .put('I', PropertyIngredient.builder("primary").types(INGOT).tags(DUST, GRINDABLE).build())
                        .build(),
                "MI");
        provider.addToolRecipe(DUST_BUILDER.get(DUST.getId()), consumer, Ref.ID, "grind_rock", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                DUST.all().stream().filter(t -> t.has(INGOT)).map(t -> DUST.get(t, 1)).collect(Collectors.toList()), ImmutableMap.<Character, Object>builder()
                        .put('M', MORTAR.getTag())
                        .put('I', PropertyIngredient.builder("primary").types(ROCK).tags(DUST).build())
                        .build(),
                "III", "III", "IIM");
        AntimatterAPI.all(BlockOre.class, o -> {
            if (o.getOreType() != ORE) return;
            if (!o.getMaterial().getSmeltInto().has(INGOT)) return;
            if (o.getMaterial().needsBlastFurnace()) return;
            Item ingot = INGOT.get(o.getMaterial().getSmeltInto());
            ITag.INamedTag<Item> oreTag = TagUtils.getForgeItemTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()));
            ITag.INamedTag<Item> ingotTag = TagUtils.getForgeItemTag("ingots/".concat(o.getMaterial().getSmeltInto().getId()));
            AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(oreTag, 1).get(), new ItemStack(ingot, o.getMaterial().getSmeltingMulti()), 2.0F, 100)
                    .addCriterion("has_material_" + o.getMaterial().getId(), provider.hasSafeItem(ingotTag))
                    .build(consumer, provider.fixLoc(Ref.ID, o.getId().concat("_to_ingot")));
            AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(oreTag, 1).get(), new ItemStack(ingot, o.getMaterial().getSmeltingMulti()), 2.0F, 200)
                    .addCriterion("has_material_" + o.getMaterial().getId(), provider.hasSafeItem(ingotTag))
                    .build(consumer, provider.fixLoc(Ref.ID, o.getId().concat("_to_ingot_smelting")));
        });
        AntimatterAPI.all(Material.class).stream().filter(m -> m.has(RAW_ORE) && m.getSmeltInto().has(INGOT) && !m.needsBlastFurnace()).forEach(m -> {
            AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(RAW_ORE.getMaterialTag(m), 1).get(), new ItemStack(INGOT.get(m.getSmeltInto()), m.getSmeltingMulti()), 2.0F, 100)
                    .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(INGOT.getMaterialTag(m.getSmeltInto())))
                    .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_raw_ore_to_ingot")));
            AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(RAW_ORE.getMaterialTag(m), 1).get(), new ItemStack(INGOT.get(m.getSmeltInto()), m.getSmeltingMulti()), 2.0F, 200)
                    .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(INGOT.getMaterialTag(m.getSmeltInto())))
                    .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_raw_ore_to_ingot_smelting")));
        });
        /*AntimatterAPI.all(Material.class).stream().filter(m -> m.has(DUST)).forEach(mat -> {
            Item dust = DUST.get(mat);
            if (mat.has(ROCK)) {
                ITag<Item> rockTag = nc(TagUtils.getForgeItemTag("rocks/".concat(mat.getId())));
                Item rock = ROCK.get(mat);
                Item smallDust = DUST_SMALL.get(mat);
                ShapelessRecipeBuilder.shapelessRecipe(dust)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(nc(MORTAR.getTag()))
                        .addCriterion("has_rock_" + mat.getId(), provider.hasSafeItem(rockTag))
                        .setGroup("rocks_grind_to_dust").build(consumer, provider.fixLoc(Ref.ID, rock.getRegistryName().getPath() + "_grind_to_" + dust.getRegistryName().getPath()));

                ShapelessRecipeBuilder.shapelessRecipe(smallDust)
                        .addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(nc(MORTAR.getTag()))
                        .addCriterion("has_rock_" + mat.getId(), provider.hasSafeItem(getForgeItemTag("rocks/".concat(mat.getId()))))
                        .setGroup("rocks_grind_to_small_dust").build(consumer, provider.fixLoc(Ref.ID, rock.getRegistryName().getPath() + "_grind_to_" + smallDust.getRegistryName().getPath()));
            }
        });*/
    }
}
