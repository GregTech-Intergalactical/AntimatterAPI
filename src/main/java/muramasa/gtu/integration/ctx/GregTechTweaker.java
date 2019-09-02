package muramasa.gtu.integration.ctx;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockStorage;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.materials.TextureSet;
import muramasa.gtu.api.registration.IGregTechRegistrar;
import muramasa.gtu.api.registration.RegistrationEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;

@ZenClass("mods.gtu")
public class GregTechTweaker implements IGregTechRegistrar {

    public GregTechTweaker() {
        CraftTweakerAPI.registerClass(GregTechTweaker.class);
        CraftTweakerAPI.registerClass(CTMaterial.class);
        CraftTweakerAPI.registerClass(CTRecipeBuilder.class);
        CraftTweakerAPI.registerClass(CTStructureBuilder.class);

        GregTechAPI.addRegistrar(this);
    }

    @ZenMethod
    public static void addMaterialType(String id, boolean visible) {
        new MaterialType(id, visible, -1);
    }

    @ZenMethod
    public static void addMaterialType(String id, boolean visible, int unitValue) {
        new MaterialType(id, visible, unitValue);
    }

    @ZenMethod
    public static void addStorage(String id, String type, String... ids) {
        if (GregTechAPI.has(BlockStorage.class, id)) throw new IllegalArgumentException("a storage block with the id " + id + " already exists");
        Material[] materials = Arrays.stream(ids).filter(s -> Materials.get(s) != null).map(Materials::get).toArray(Material[]::new);
        if (materials.length == 0) throw new IllegalArgumentException("could not find any valid materials for passed names");
        if (materials.length > 16) throw new IllegalArgumentException("materials for " + id + "cannot be more than 16");
        MaterialType materialType = GregTechAPI.get(MaterialType.class, type);
        if (materialType == null) throw new IllegalArgumentException("materialType for id " + type + " does not exist");
        new BlockStorage(id, GregTechAPI.get(MaterialType.class, type), materials);
    }

    @ZenMethod
    public static CTMaterial getMaterial(String id) {
        Material material = Materials.get(id);
        if (material == null) throw new IllegalArgumentException("material for id " + id + " does not exist");
        return new CTMaterial(material);
    }

    @ZenMethod
    public static CTMaterial addMaterial(String id, int rgb, String textureSet) {
        if (Materials.get(id) != null) throw new IllegalArgumentException("material for id " + id + " already exists");
        return new CTMaterial(id, rgb, textureSet);
    }

    @ZenMethod
    public static void addTextureSet(String id) {
        new TextureSet(id);
    }

    @ZenMethod
    public static CTRecipeBuilder getBuilder(String id) {
        Machine machine = Machines.get(id);
        if (machine == null) throw new IllegalArgumentException("machine for id " + id + " could not be found");
        return new CTRecipeBuilder(machine.getRecipeBuilder());
    }

    @ZenMethod
    public static CTStructureBuilder addStructure(String id) {
        Machine machine = Machines.get(id);
        if (machine == null) throw new IllegalArgumentException("machine for id " + id + " could not be found");
        return new CTStructureBuilder(machine);
    }

    public static ItemStack[] getItems(IIngredient... items) {
        return Arrays.stream(items).map(CraftTweakerMC::getItemStack).toArray(ItemStack[]::new);
    }

    public static FluidStack[] getFluids(ILiquidStack... fluids) {
        return Arrays.stream(fluids).map(ILiquidStack::getInternal).toArray(FluidStack[]::new);
    }

    @Override
    public String getId() {
        return Ref.MOD_CT;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        if (event == RegistrationEvent.DATA) {
            CraftTweakerAPI.tweaker.loadScript(false, Ref.MODID + "_data");
        }
    }
}
