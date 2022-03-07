package muramasa.antimatter.integration.kubejs;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

public class KubeJSBindings {

  public Item materialItem(String type, String material) {
    MaterialType t = AntimatterAPI.get(MaterialType.class, type);
    if (t == null)
      return Items.AIR;
    Material mat = AntimatterAPI.get(Material.class, material);
    if (mat == null)
      return Items.AIR;
    if (t instanceof MaterialTypeItem) {
      if (t.allowGen(mat)) {
        Item it = ((MaterialTypeItem) t).get(mat);
        // TODO: Needed?
        /*
         * TagCollectionManager.getManager().getItemTags().getOwningTags(it).forEach(tt
         * -> {
         * if (tt.equals(t.getMaterialTag(mat).getName()) &&
         * !DynamicResourcePack.hasTag(AntimatterItemTagProvider.getTagLoc("items",tt)))
         * {
         * DynamicResourcePack.forceAddTag(AntimatterItemTagProvider.getTagLoc("items",
         * tt), ITag.Builder.create().addItemEntry(it.getRegistryName(),
         * "Antimatter - Dynamic Data").serialize(), true);
         * }
         * });
         */
        return it;
      }
    }
    return Items.AIR;
  }

  public MaterialType type(String type) {
    return AntimatterAPI.get(MaterialType.class, type);
  }

  public ItemStackJS materialItemStack(String type, Material material, int count) {
    Item i = materialItem(type, material.getId());
    // Return null to break.
    if (i == Items.AIR)
      return null;
    return ItemStackJS.of(new ItemStack(i, count));
  }

  public ItemStackJS materialItemStackString(String type, String material, int count) {
    Item i = materialItem(type, material);
    // Return null to break.
    if (i == Items.AIR)
      return null;
    return ItemStackJS.of(new ItemStack(i, count));
  }

  public boolean allow(MaterialType type, Material mat) {
    return type.allowGen(mat);
  }

  public List<Item> all(String type) {
    MaterialType t = AntimatterAPI.get(MaterialType.class, type);
    if (t == null)
      Collections.emptyList();
    if (t instanceof MaterialTypeItem) {
      Set<Material> mat = t.all();
      MaterialTypeItem item = (MaterialTypeItem) t;
      return mat.stream().map(item::get).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public List<StoneType> allStoneTypes(String domain) {
    return AntimatterAPI.all(StoneType.class, domain);
  }

  public List<StoneType> allStoneTypes() {
    return AntimatterAPI.all(StoneType.class);
  }

  public Material getMat(ItemStackJS stack) {
    if (stack.getItem() instanceof MaterialItem) {
      return ((MaterialItem) stack.getItem()).getMaterial();
    }
    return null;
  }

  public Material getMat(Item item) {
    if (item instanceof MaterialItem) {
      return ((MaterialItem) item).getMaterial();
    }
    return null;
  }

  public List<ItemStackJS> allStack(String type, int count) {
    MaterialType t = AntimatterAPI.get(MaterialType.class, type);
    if (t == null)
      Collections.emptyList();
    if (t instanceof MaterialTypeItem) {
      Set<Material> mat = t.all();
      MaterialTypeItem item = (MaterialTypeItem) t;
      return mat.stream().map(tt -> ItemStackJS.of(new ItemStack(item.get(tt), count))).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public String materialTag(String type, String material) {
    MaterialType t = AntimatterAPI.get(MaterialType.class, type);
    if (t == null)
      return "";
    Material mat = AntimatterAPI.get(Material.class, material);
    if (mat == null)
      return "";
    return t.getMaterialTag(mat).getName().toString();
  }
}
