package muramasa.antimatter.integration.ct;

import com.blamejared.crafttweaker.api.annotation.BracketResolver;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Element;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.material.TextureSet;
import net.minecraft.world.item.crafting.Ingredient;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Objects;
import java.util.function.Consumer;

@ZenRegister
@ZenCodeType.Name("mods.antimatter.Api")
public class AntimatterCraftTweaker {

    @ZenCodeType.Method
    public static Object get(String clazz, String domain, String name) {
        return AntimatterAPI.get(clazz, domain, name);
    }
    @ZenCodeType.Method
    public static Object get(String clazz, String name) {
        return AntimatterAPI.get(clazz, name);
    }

    @ZenCodeType.Method
    public static <T> void all(String clazz, @ZenCodeType.OptionalString String domain, Consumer<T> consumer) {
        AntimatterAPI.all(clazz, domain, consumer);
    }

    @ZenCodeType.Method
    public static IIngredient ingredient(Material mat, MaterialType type, int count) {
        if (mat == null || type == null || count == 0) return null;
        if (type instanceof MaterialTypeItem<?> i) {
            return IIngredient.fromIngredient(Ingredient.of(i.getMaterialTag(mat)));
        }
        if (type instanceof MaterialTypeBlock<?> b) {
            return IIngredient.fromIngredient(Ingredient.of(b.getMaterialTag(mat)));
        }
        return null;
    }


    @ZenCodeType.Method
    public static Material getMat(String name) {
        return AntimatterAPI.get(Material.class, name);
    }

    @ZenCodeType.Method
    public static Material createMaterial(String id, int rgb, String textureSet, String textureSetDomain) {
        TextureSet set = Objects.requireNonNull(AntimatterAPI.get(TextureSet.class, textureSet, textureSetDomain), "Specified texture set in Material created via CT event is null");
        return AntimatterAPI.register(Material.class, new Material(Ref.MOD_CT, id, rgb, set));
    }

    @ZenCodeType.Method
    public static Material createMaterial(String id, int rgb, String textureSet, String textureSetDomain, String element) {
        TextureSet set = Objects.requireNonNull(AntimatterAPI.get(TextureSet.class, textureSet, textureSetDomain), "Specified texture set in Material created via CT event is null");
        return AntimatterAPI.register(Material.class, new Material(Ref.MOD_KJS, id, rgb, set, Element.getFromElementId(element)));
    }

    @ZenCodeType.StaticExpansionMethod
    @BracketResolver("antimatterapi")
    public static Object get(String tokens) {
        String[] toks = tokens.split(":");
        if (toks.length == 3) {
            return AntimatterAPI.get(toks[0], toks[1], toks[2]);
        } else if (toks.length == 2) {
            return AntimatterAPI.get(toks[0], toks[1]);
        }
        return null;
    }

    @ZenCodeType.StaticExpansionMethod
    @BracketResolver("ammaterial")
    public static Material mat(String tokens) {
        return getMat(tokens);
    }
}
