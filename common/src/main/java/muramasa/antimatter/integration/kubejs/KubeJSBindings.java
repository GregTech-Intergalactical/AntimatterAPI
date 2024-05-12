package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KubeJSBindings {

  public Item material_item(String type, String material) {
    MaterialType t = AntimatterAPI.get(MaterialType.class, type);
    if (t == null)
      return Items.AIR;
    Material mat = AntimatterAPI.get(Material.class, material);
    if (mat == null)
      return Items.AIR;
    if (t instanceof MaterialTypeItem) {
      if (t.allowGen(mat)) {
        return ((MaterialTypeItem) t).get(mat);
      }
    }
    return Items.AIR;
  }

  public MaterialType type(String type) {
    return AntimatterAPI.get(MaterialType.class, type);
  }

  public ItemStack material_stack(String type, Material material, int count) {
    Item i = material_item(type, material.getId());
    // Return null to break.
    if (i == Items.AIR)
      return null;
    return new ItemStack(i, count);
  }

  public ItemStack material_stack(String type, Object m, int count) {
      String material = null;
      if (m instanceof Material mat) {
          material = mat.getId();
      }
      if (m instanceof String name) {
          material = name;
      }
    Item i = material_item(type, material);
    // Return null to break.
    if (i == Items.AIR)
      return null;
    return new ItemStack(i, count);
  }

  public boolean allow(MaterialType<?> type, Material mat) {
    return type.allowGen(mat);
  }

  public List<Item> all(String type) {
    MaterialType t = AntimatterAPI.get(MaterialType.class, type);
    if (t == null)
      Collections.emptyList();
    if (t instanceof MaterialTypeItem<?> item) {
      Set<Material> mat = t.all();
        return mat.stream().map(item::get).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public List<StoneType> all_stone_types(String domain) {
    return AntimatterAPI.all(StoneType.class, domain);
  }

  public List<StoneType> all_stone_types() {
    return AntimatterAPI.all(StoneType.class);
  }

  public Material get_material(Object stack) {
    if (stack instanceof Item item) {
        if (item instanceof MaterialItem it) {
            return it.getMaterial();
        }
    }
    if (stack instanceof ItemStack istack) {
        if (istack.getItem() instanceof MaterialItem m) {
            return m.getMaterial();
        }
    }
    return null;
  }

  public List<ItemStack> all_stack(String type, int count) {
    MaterialType t = AntimatterAPI.get(MaterialType.class, type);
    if (t == null)
      Collections.emptyList();
    if (t instanceof MaterialTypeItem) {
      Set<Material> mat = t.all();
      MaterialTypeItem item = (MaterialTypeItem) t;
      return mat.stream().map(tt -> new ItemStack(item.get(tt), count)).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public String material_tag(String type, String material) {
    MaterialType t = AntimatterAPI.get(MaterialType.class, type);
    if (t == null)
      return "";
    Material mat = AntimatterAPI.get(Material.class, material);
    if (mat == null)
      return "";
    return t.getMaterialTag(mat).location().toString();
  }
}
