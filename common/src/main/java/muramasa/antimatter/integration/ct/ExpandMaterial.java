package muramasa.antimatter.integration.ct;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.Material;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@NativeTypeRegistration(value = Material.class, zenCodeName = "mods.antimatter.Material")
public class ExpandMaterial {
    @ZenCodeType.Method
    @ZenCodeType.Getter("domain")
    public static String getDomain(Material internal){
        return internal.getDomain();
    }
    @ZenCodeType.Method
    @ZenCodeType.Getter("id")
    public static String getId(Material internal){
        return internal.getId();
    }
    @ZenCodeType.Method
    @ZenCodeType.Getter("materialDomain")
    public static String getMaterialDomain(Material internal){
        return internal.materialDomain();
    }

    @ZenCodeType.Method
    public static boolean has(Material internal, IMaterialTag... tags) {
        return internal.has(tags);
    }
}
