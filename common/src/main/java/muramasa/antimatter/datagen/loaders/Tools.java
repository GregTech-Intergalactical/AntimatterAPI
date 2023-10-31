package muramasa.antimatter.datagen.loaders;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.function.Consumer;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.data.AntimatterDefaultTools.*;
import static muramasa.antimatter.data.AntimatterMaterialTypes.*;
import static muramasa.antimatter.data.AntimatterMaterials.Wood;
import static muramasa.antimatter.material.MaterialTags.*;
import static muramasa.antimatter.recipe.RecipeBuilders.CROWBAR_BUILDER;
import static muramasa.antimatter.recipe.RecipeBuilders.PROBE_BUILDER;

public class Tools {
    public static void init(Consumer<FinishedRecipe> consumer, AntimatterRecipeProvider provider) {
        final CriterionTriggerInstance in = provider.hasSafeItem(WRENCH.getTag());

        if (AntimatterAPI.isModLoaded(Ref.MOD_TOP)) {
            ARMOR.getAll().forEach((m, a) ->{
                provider.addToolRecipe(PROBE_BUILDER.get(m.getId() + "_" + AntimatterDefaultTools.HELMET.getId()), consumer, Ref.ID, m.getId() + "_helmet_with_probe", "antimatter_armor",
                        "has_helmet", provider.hasSafeItem(AntimatterDefaultTools.HELMET.getToolStack(m).getItem()), AntimatterDefaultTools.HELMET.getToolStack(m), of('H', PropertyIngredient.builder("helmet").itemStacks(AntimatterDefaultTools.HELMET.getToolStack(m).getItem()).build(), 'P', AntimatterPlatformUtils.getItemFromID(Ref.MOD_TOP, "probe")), "HP");
            });

        }

        /*provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.HAMMER.getId()), consumer, Ref.ID, AntimatterDefaultTools.HAMMER.getId() + "_" + "recipe", "antimatter_tools",
                "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.HAMMER.getToolStack(Material.NULL, Material.NULL)), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.INGOT, GEM).tool(AntimatterDefaultTools.HAMMER, true).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.SOFT_HAMMER.getId()), consumer, Ref.ID, AntimatterDefaultTools.SOFT_HAMMER.getId() + "_" + "recipe", "antimatter_tools",
                "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.SOFT_HAMMER.getToolStack(Material.NULL, Material.NULL)), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.INGOT, GEM).tool(AntimatterDefaultTools.SOFT_HAMMER, true).tags(RUBBERTOOLS).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        if (Material.get("wood") != Material.NULL && Material.get("wood").has(MaterialTags.TOOLS) && MaterialTags.TOOLS.get(Material.get("wood")).toolTypes().contains(AntimatterDefaultTools.SOFT_HAMMER)) {
            provider.addToolRecipe(WOOD_TOOL_BUILDER.get(AntimatterDefaultTools.SOFT_HAMMER.getId()), consumer, Ref.ID, AntimatterDefaultTools.SOFT_HAMMER.getId() + "_wood_" + "recipe", "antimatter_tools",
                    "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.SOFT_HAMMER.getToolStack(Material.get("wood"), Material.NULL)), of('I', ItemTags.PLANKS, 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        }*/

        ARMOR.all().forEach(m ->{
            TagKey<Item> input = m.has(GEM) ? GEM.getMaterialTag(m) : PLATE.getMaterialTag(m);
            ImmutableMap.Builder<Character, Object> builder = ImmutableMap.builder();
            builder.put('I', input);
            if (!m.has(GEM)) builder.put('H', HAMMER.getTag());
            String[] strings = !m.has(GEM) ? new String[]{"III", "IHI"} : new String[]{"III", "I I"};
            provider.addStackRecipe(consumer, Ref.ID, "", "antimatter_armor", "has_wench", in, AntimatterDefaultTools.HELMET.getToolStack(m),
                    builder.build(), strings);
            strings = !m.has(GEM) ? new String[]{"IHI", "III", "III"} : new String[]{"I I", "III", "III"};
            provider.addStackRecipe(consumer, Ref.ID, "", "antimatter_armor", "has_wench", in, AntimatterDefaultTools.CHESTPLATE.getToolStack(m),
                    builder.build(), strings);
            strings = !m.has(GEM) ? new String[]{"III", "IHI", "I I"} : new String[]{"III", "I I", "I I"};
            provider.addStackRecipe(consumer, Ref.ID, "", "antimatter_armor", "has_wench", in, AntimatterDefaultTools.LEGGINGS.getToolStack(m),
                    builder.build(), strings);
            strings = !m.has(GEM) ? new String[]{"I I", "IHI"} : new String[]{"I I", "I I"};
            provider.addStackRecipe(consumer, Ref.ID, "", "antimatter_armor", "has_wench", in, AntimatterDefaultTools.BOOTS.getToolStack(m),
                    builder.build(), strings);
        });
       TOOLS.getAll().forEach((m, t) -> {
           if (m.has(INGOT) || m.has(GEM) || m == Wood){
               TagKey<Item> plateGem = m.has(GEM) ? GEM.getMaterialTag(m) : m.has(PLATE) ? PLATE.getMaterialTag(m) : INGOT.getMaterialTag(m);
               TagKey<Item> ingotGem = m.has(GEM) ? GEM.getMaterialTag(m) : INGOT.getMaterialTag(m);
               TagKey<Item> rod = t.handleMaterial().has(ROD) ? ROD.getMaterialTag(t.handleMaterial()) : ROD.getMaterialTag(Wood);
               if (t.toolTypes().contains(WRENCH)){
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, WRENCH.getToolStack(m),
                           of('H', HAMMER.getTag(), 'P', plateGem), "PHP", "PPP", " P ");
               }
               if (t.toolTypes().contains(HAMMER)){
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, HAMMER.getToolStack(m),
                           of('R', rod, 'P', ingotGem), "PP ", "PPR", "PP ");
               }
               if (t.toolTypes().contains(SOFT_HAMMER) && m.has(RUBBERTOOLS)){
                   TagKey<Item> ingotGem1 = m == Wood ? ItemTags.PLANKS : m.has(GEM) ? GEM.getMaterialTag(m) : INGOT.getMaterialTag(m);
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, SOFT_HAMMER.getToolStack(m),
                           of('R', rod, 'P', ingotGem1), "PP ", "PPR", "PP ");
               }
               if (t.toolTypes().contains(MORTAR)){
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, MORTAR.getToolStack(m),
                           of('S', TagUtils.getForgelikeItemTag("stone"), 'P', ingotGem), " P ", "SPS", "SSS");
               }
               if (t.toolTypes().contains(FILE)){
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, FILE.getToolStack(m),
                           of('R', rod, 'P', plateGem), "P", "P", "R");
               }
               if (t.toolTypes().contains(SCREWDRIVER) && m.has(ROD)){
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, SCREWDRIVER.getToolStack(m),
                           of('R', rod, 'P', ROD.getMaterialTag(m),'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), " FP", " PH", "R  ");
               }
               if (t.toolTypes().contains(PLUNGER) && m.has(ROD)){
                   RUBBERTOOLS.all().stream().filter(r -> r.has(PLATE) && r != Wood).forEach(r -> {
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, m.getId() + "_plunger_with_" + r.getId(), "", "has_wrench", in, PLUNGER.getToolStack(m),
                               of('P', rod, 'R', PLATE.getMaterialTag(r),'F', AntimatterDefaultTools.FILE.getTag(), 'W', WIRE_CUTTER.getTag()), "WRR", " PR", "P F");
                   });

               }
               if (t.toolTypes().contains(SAW)){
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, SAW.getToolStack(m),
                           of('R', rod, 'P', plateGem,'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "PPR", "FH ");
               }
               if (t.toolTypes().contains(WIRE_CUTTER)){
                   ImmutableMap.Builder<Character, Object> builder = ImmutableMap.builder();
                   builder.put('R', rod).put('P', plateGem).put('F', AntimatterDefaultTools.FILE.getTag()).put('H', AntimatterDefaultTools.HAMMER.getTag()).put('S', SCREWDRIVER.getTag());
                   if (m.has(SCREW)) builder.put('W', SCREW.getMaterialTag(m));
                   String last = m.has(SCREW) ? "RWR" : "R R";
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, WIRE_CUTTER.getToolStack(m),
                           builder.build(), "PFP", "HPS", last);
               }
               if (t.toolTypes().contains(BRANCH_CUTTER)){
                   ImmutableMap.Builder<Character, Object> builder = ImmutableMap.builder();
                   builder.put('R', rod).put('P', plateGem).put('F', AntimatterDefaultTools.FILE.getTag()).put('S', SCREWDRIVER.getTag());
                   if (m.has(SCREW)) builder.put('W', SCREW.getMaterialTag(m));
                   String last = m.has(SCREW) ? "RWR" : "R R";
                   provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, BRANCH_CUTTER.getToolStack(m),
                           builder.build(), "PFP", "PSP", last);
               }
               if (t.toolTypes().contains(CROWBAR) && m.has(ROD)){
                   provider.addToolRecipe(CROWBAR_BUILDER.get(m.getId() + "_" + CROWBAR.getId()), consumer, Ref.SHARED_ID, "", "antimatter_crowbars",
                           "has_wrench", in, CROWBAR.getToolStack(m), of('H', AntimatterDefaultTools.HAMMER.getTag(), 'C', PropertyIngredient.builder("secondary").itemTags(TagUtils.getForgelikeItemTag("dyes")).build(), 'R', ROD.getMaterialTag(m), 'F', AntimatterDefaultTools.FILE.getTag()), "HCR", "CRC", "RCF");
               }

               if (t.toolTypes().contains(PICKAXE)){
                   if (m.has(FLINT)){
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, PICKAXE.getToolStack(m),
                               of('R', rod, 'P', ingotGem), "PPP", " R ");
                   } else {
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, PICKAXE.getToolStack(m),
                               of('R', rod, 'P', plateGem, 'I', ingotGem,'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "PII", "FRH", " R ");
                   }
               }

               if (t.toolTypes().contains(AXE)){
                   if (m.has(FLINT)){
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, AXE.getToolStack(m),
                               of('R', rod, 'P', ingotGem), "PP", "PR");
                   } else {
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, AXE.getToolStack(m),
                               of('R', rod, 'P', plateGem, 'I', ingotGem,'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "PIH", "PR ", "FR ");
                   }
               }

               if (t.toolTypes().contains(SHOVEL)){
                   if (m.has(FLINT)){
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, SHOVEL.getToolStack(m),
                               of('R', rod, 'P', ingotGem), "P", "R");
                   } else {
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, SHOVEL.getToolStack(m),
                               of('R', rod, 'P', plateGem,'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "FPH", " R ", " R ");
                   }
               }

               if (t.toolTypes().contains(SWORD)){
                   if (m.has(FLINT)){
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, SWORD.getToolStack(m),
                               of('R', rod, 'P', ingotGem), "P", "P", "R");
                   } else {
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, SWORD.getToolStack(m),
                               of('R', rod, 'P', plateGem,'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "FPH", " P ", " R ");
                   }
               }

               if (t.toolTypes().contains(HOE)){
                   if (m.has(FLINT)){
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, HOE.getToolStack(m),
                               of('R', rod, 'P', ingotGem), "PP", " R");
                   } else {
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, HOE.getToolStack(m),
                               of('R', rod, 'P', plateGem, 'I', ingotGem,'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "PIH", "FR ", " R ");
                   }
               }

               if (t.toolTypes().contains(KNIFE)){
                   if (m.has(FLINT)){
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, KNIFE.getToolStack(m),
                               of('R', rod, 'P', ingotGem), "R", "P");
                   } else {
                       provider.addStackRecipe(consumer, Ref.SHARED_ID, "", "", "has_wrench", in, KNIFE.getToolStack(m),
                               of('R', rod, 'P', plateGem,'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "FP", "HR");
                   }
               }

           }

       });
    }
}
