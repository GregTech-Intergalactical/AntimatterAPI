package muramasa.antimatter.datagen.loaders;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.MaterialTag.*;
import static muramasa.antimatter.recipe.RecipeBuilders.*;
import static muramasa.antimatter.recipe.RecipeBuilders.TOOL_BUILDER;

public class Tools {
    public static void init(Consumer<IFinishedRecipe> consumer, AntimatterRecipeProvider provider) {
        final ICriterionInstance in = provider.hasSafeItem(WRENCH.getTag());

        if (AntimatterAPI.isModLoaded(Ref.MOD_TOP)) {
            provider.addToolRecipe(PROBE_BUILDER.get(HELMET.getId()), consumer, Ref.ID, "helmet_with_probe", "antimatter_armor",
                    "has_helmet", provider.hasSafeItem(HELMET.getToolStack(NULL).getItem()), Collections.singletonList(HELMET.getToolStack(NULL)), of('H', PropertyIngredient.builder("helmet").itemStacks(HELMET.getToolStack(NULL).getItem()).build(), 'P', ForgeRegistries.ITEMS.getValue(new ResourceLocation(Ref.MOD_TOP, "probe"))), "HP");
        }

        provider.addToolRecipe(TOOL_BUILDER.get(HAMMER.getId()), consumer, Ref.ID, HAMMER.getId() + "_" + "recipe", "antimatter_tools",
                "has_wrench", in, Collections.singletonList(HAMMER.getToolStack(NULL, NULL)), of('I', PropertyIngredient.builder("primary").types(INGOT, GEM).tool(HAMMER, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        provider.addToolRecipe(TOOL_BUILDER.get(SOFT_HAMMER.getId()), consumer, Ref.ID, SOFT_HAMMER.getId() + "_" + "recipe", "antimatter_tools",
                "has_wrench", in, Collections.singletonList(SOFT_HAMMER.getToolStack(NULL, NULL)), of('I', PropertyIngredient.builder("primary").types(INGOT, GEM).tool(SOFT_HAMMER, true).tags(RUBBERTOOLS).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        if (Material.get("wood") != NULL && Material.get("wood").getToolTypes().contains(SOFT_HAMMER)) {
            provider.addToolRecipe(WOOD_TOOL_BUILDER.get(SOFT_HAMMER.getId()), consumer, Ref.ID, SOFT_HAMMER.getId() + "_wood_" + "recipe", "antimatter_tools",
                    "has_wrench", in, Collections.singletonList(SOFT_HAMMER.getToolStack(Material.get("wood"), NULL)), of('I', ItemTags.PLANKS, 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        }

        provider.addToolRecipe(ARMOR_BUILDER.get(HELMET.getId()), consumer, Ref.ID, HELMET.getId() + "_recipe", "antimatter_helmets",
                "has_wrench", in, Collections.singletonList(HELMET.getToolStack(NULL)), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(ARMOR).build(), 'H', HAMMER.getTag()), "III", "IHI");
        provider.addToolRecipe(ARMOR_BUILDER.get(CHESTPLATE.getId()), consumer, Ref.ID, CHESTPLATE.getId() + "_recipe", "antimatter_chestplates",
                "has_wrench", in, Collections.singletonList(CHESTPLATE.getToolStack(NULL)), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(ARMOR).build(), 'H', HAMMER.getTag()), "IHI", "III", "III");
        provider.addToolRecipe(ARMOR_BUILDER.get(LEGGINGS.getId()), consumer, Ref.ID, LEGGINGS.getId() + "_recipe", "antimatter_leggings",
                "has_wrench", in, Collections.singletonList(LEGGINGS.getToolStack(NULL)), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(ARMOR).build(), 'H', HAMMER.getTag()), "III", "IHI", "I I");
        provider.addToolRecipe(ARMOR_BUILDER.get(BOOTS.getId()), consumer, Ref.ID, BOOTS.getId() + "_recipe", "antimatter_boots",
                "has_wrench", in, Collections.singletonList(BOOTS.getToolStack(NULL)), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(ARMOR).build(), 'H', HAMMER.getTag()), "I I", "IHI");

            /*addToolRecipe(TOOL_BUILDER.get(PLUNGER.getId()), consumer, muramasa.antimatter.Ref.ID, PLUNGER.getId() + "_recipe", "antimatter_plungers",
                    "has_wrench", in, Collections.singletonList(PLUNGER.getToolStack(NULL, NULL)),
                    of('W', WIRE_CUTTER.getTag(), 'I',  PropertyIngredient.of(INGOT, "primary"), 'S', Tags.Items.SLIMEBALLS, 'R', PropertyIngredient.builder("secondary").types(ROD).tags(RUBBERTOOLS).build(), 'F', FILE.getTag()), "WIS", " RI", "R F");*/

        provider.addToolRecipe(TOOL_BUILDER.get(WRENCH.getId()), consumer, Ref.ID, WRENCH.getId() + "_recipe", "antimatter_wrenches",
                "has_wrench", in, WRENCH.getToolStack(NULL, NULL), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(WRENCH, true).build(), 'H', HAMMER.getTag()), "IHI", "III", " I ");

        provider.addToolRecipe(TOOL_BUILDER.get(MORTAR.getId()), consumer, Ref.ID, MORTAR.getId() + "_recipe", "antimatter_mortars",
                "has_wrench", in, MORTAR.getToolStack(NULL, NULL), of('I', PropertyIngredient.builder("primary").types(INGOT, GEM).tool(MORTAR, true).build(), 'S', Tags.Items.STONE), " I ", "SIS", "SSS");

        provider.addToolRecipe(TOOL_BUILDER.get(FILE.getId()), consumer, Ref.ID, FILE.getId() + "_recipe", "antimatter_files",
                "has_wrench", in, FILE.getToolStack(NULL, NULL), of('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(FILE, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "P", "P", "R");

        provider.addToolRecipe(TOOL_BUILDER.get(SCREWDRIVER.getId()), consumer, Ref.ID, SCREWDRIVER.getId() + "_recipe", "antimatter_screwdrivers",
                "has_wrench", in, SCREWDRIVER.getToolStack(NULL, NULL),
                of('M', PropertyIngredient.builder("primary").types(ROD).tool(SCREWDRIVER, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag()), " FM", " MH", "R  ");

        provider.addToolRecipe(TOOL_BUILDER.get(SAW.getId()), consumer, Ref.ID, SAW.getId() + "_recipe", "antimatter_saws",
                "has_wrench", in, SAW.getToolStack(NULL, NULL), of('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(SAW, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag()), "PPR", "FH ");

        provider.addToolRecipe(TOOL_BUILDER.get(WIRE_CUTTER.getId()), consumer, Ref.ID, WIRE_CUTTER.getId() + "_recipe_noscrew", "antimatter_files",
                "has_wrench", in, WIRE_CUTTER.getToolStack(NULL, NULL), b ->
                        b.put('P', PropertyIngredient.builder("primary").inverse().tool(SCREWDRIVER, true).types(PLATE, GEM).tags(SCREW).build()).put('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()).put('F', FILE.getTag()).put('H', HAMMER.getTag())
                                .put('S', SCREWDRIVER.getTag())
                , "PFP", "HPS", "R R");
        provider.addToolRecipe(TOOL_BUILDER.get(WIRE_CUTTER.getId()), consumer, Ref.ID, WIRE_CUTTER.getId() + "_recipe_screw", "antimatter_files",
                "has_wrench", in, WIRE_CUTTER.getToolStack(NULL, NULL), b ->
                        b.put('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(SCREW).tool(SCREWDRIVER, true).build()).put('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()).put('F', FILE.getTag()).put('H', HAMMER.getTag())
                                .put('S', SCREWDRIVER.getTag()).put('W', PropertyIngredient.of(SCREW, "primary"))
                , "PFP", "HPS", "RWR");

        provider.addToolRecipe(TOOL_BUILDER.get(BRANCH_CUTTER.getId()), consumer, Ref.ID, BRANCH_CUTTER.getId() + "_recipe_noscrew", "antimatter_files",
                "has_wrench", in, BRANCH_CUTTER.getToolStack(NULL, NULL), b ->
                        b.put('P', PropertyIngredient.builder("primary").inverse().tool(SCREWDRIVER, true).types(PLATE, GEM).tags(SCREW).build()).put('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()).put('F', FILE.getTag())
                                .put('S', SCREWDRIVER.getTag())
                , "PFP", "PSP", "R R");
        provider.addToolRecipe(TOOL_BUILDER.get(BRANCH_CUTTER.getId()), consumer, Ref.ID, BRANCH_CUTTER.getId() + "_recipe_screw", "antimatter_files",
                "has_wrench", in, BRANCH_CUTTER.getToolStack(NULL, NULL), b ->
                        b.put('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(SCREW).tool(SCREWDRIVER, true).build()).put('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()).put('F', FILE.getTag())
                                .put('S', SCREWDRIVER.getTag()).put('W', PropertyIngredient.of(SCREW, "primary"))
                , "PFP", "PSP", "RWR");

        Function<AntimatterToolType, ImmutableMap<Character, Object>> map1 = type -> of('I', PropertyIngredient.builder("primary").inverse().tags(FLINT).types(INGOT, GEM).tool(type, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'P', PropertyIngredient.builder("primary").inverse().tags(FLINT).types(PLATE, GEM).tool(type, true).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag());

        Function<AntimatterToolType, ImmutableMap<Character, Object>> map2 = type -> of('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'P', PropertyIngredient.builder("primary").inverse().tags(FLINT).types(PLATE, GEM).tool(type, true).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag());

        String[] strings1 = new String[]{"PII", "FRH", " R "};
        String[] strings3 = new String[]{" P ", "FPH", " R "};

        String[] strings2 = new String[]{"FPH", " R ", " R "};
        String[] strings2Gem = new String[]{"FGH", " R ", " R "};

        provider.addToolRecipe(TOOL_BUILDER.get(PICKAXE.getId()), consumer, Ref.ID, PICKAXE.getId() + "_with", "antimatter_pickaxes",
                "has_wrench", in, PICKAXE.getToolStack(NULL, NULL), map1.apply(PICKAXE), strings1);

        provider.addToolRecipe(TOOL_BUILDER.get(SHOVEL.getId()), consumer, Ref.ID, SHOVEL.getId() + "_with", "antimatter_shovels",
                "has_wrench", in, SHOVEL.getToolStack(NULL, NULL), map2.apply(SHOVEL), strings2);

        provider.addToolRecipe(TOOL_BUILDER.get(AXE.getId()), consumer, Ref.ID, AXE.getId() + "_with", "antimatter_axes",
                "has_wrench", in, AXE.getToolStack(NULL, NULL), map1.apply(AXE), "PIH", "PR ", "FR ");


        provider.addToolRecipe(TOOL_BUILDER.get(SWORD.getId()), consumer, Ref.ID, SWORD.getId() + "_with", "antimatter_swords",
                "has_wrench", in, SWORD.getToolStack(NULL, NULL), map2.apply(SWORD), " P ", "FPH", " R ");

        provider.addToolRecipe(TOOL_BUILDER.get(HOE.getId()), consumer, Ref.ID, HOE.getId() + "_with", "antimatter_swords",
                "has_wrench", in, HOE.getToolStack(NULL, NULL), map1.apply(HOE), "PIH", "FR ", " R ");

        provider.addToolRecipe(CROWBAR_BUILDER.get(CROWBAR.getId()), consumer, Ref.ID, CROWBAR.getId() + "_recipe", "antimatter_crowbars",
                "has_wrench", in, CROWBAR.getToolStack(NULL, NULL), of('H', HAMMER.getTag(), 'C', PropertyIngredient.builder("secondary").itemTags(Tags.Items.DYES).build(), 'R', PropertyIngredient.builder("primary").types(ROD).tool(CROWBAR, true).build(), 'F', FILE.getTag()), "HCR", "CRC", "RCF");

        provider.addToolRecipe(TOOL_BUILDER.get(KNIFE.getId()), consumer, Ref.ID, KNIFE.getId() + "_with", "antimatter_knives",
                "has_wrench", in, KNIFE.getToolStack(NULL, NULL), of('P', PropertyIngredient.builder("primary").inverse().tags(FLINT).types(PLATE, GEM).tool(KNIFE, true).build(), 'S', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag()), "FP", "HS");

        // List<Material> handleMats = AntimatterAPI.all(Material.class).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.has(HANDLE))).collect(Collectors.toList());

        provider.addToolRecipe(TOOL_BUILDER.get(PLUNGER.getId()), consumer, Ref.ID, PLUNGER.getId() + "_", "antimatter_plungers",
                "has_wrench", in, PLUNGER.getToolStack(NULL, NULL),
                of('W', WIRE_CUTTER.getTag(), 'R', PropertyIngredient.builder("primary").tool(PLUNGER, true).types(ROD).build(), 'I', PropertyIngredient.builder("secondary").types(PLATE).tags(HANDLE, RUBBERTOOLS).build(), 'F', FILE.getTag()), "WII", " RI", "R F");

        provider.addToolRecipe(TOOL_BUILDER.get(PICKAXE.getId()), consumer, Ref.ID,  "flint_" + PICKAXE.getId() + "_" +"recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(GEM.getMaterialTag(Flint)), PICKAXE.getToolStack(Flint, NULL), of('I', PropertyIngredient.builder("primary").types(GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "III", " R ", " R ");

        provider.addToolRecipe(TOOL_BUILDER.get(AXE.getId()), consumer, Ref.ID,  "flint_" + AXE.getId() + "_" +"recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(GEM.getMaterialTag(Flint)), AXE.getToolStack(Flint, NULL), of('I', PropertyIngredient.builder("primary").types(GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "II", "IR", " R");

        provider.addToolRecipe(TOOL_BUILDER.get(SWORD.getId()), consumer, Ref.ID,  "flint_" + SWORD.getId() + "_" +"recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(GEM.getMaterialTag(Flint)), SWORD.getToolStack(Flint, NULL), of('I', PropertyIngredient.builder("primary").types(GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "I", "I", "R");

        provider.addToolRecipe(TOOL_BUILDER.get(SHOVEL.getId()), consumer, Ref.ID,  "flint_" + SHOVEL.getId() + "_" +"recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(GEM.getMaterialTag(Flint)), SHOVEL.getToolStack(Flint, NULL), of('I', PropertyIngredient.builder("primary").types(GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "I", "R", "R");

        provider.addToolRecipe(TOOL_BUILDER.get(HOE.getId()), consumer, Ref.ID,  "flint_" + HOE.getId() + "_" +"recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(GEM.getMaterialTag(Flint)), HOE.getToolStack(Flint, NULL), of('I', PropertyIngredient.builder("primary").types(GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "II", " R", " R");

        provider.addToolRecipe(TOOL_BUILDER.get(KNIFE.getId()), consumer, Ref.ID,  "flint_" + KNIFE.getId() + "_" +"recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(GEM.getMaterialTag(Flint)), KNIFE.getToolStack(Flint, NULL), of('I', PropertyIngredient.builder("primary").types(GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "RI");
    }
}
