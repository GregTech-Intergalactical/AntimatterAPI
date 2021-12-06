//package muramasa.gtu.integration.ctx;
//
//import crafttweaker.CraftTweakerAPI;
//import crafttweaker.api.item.IIngredient;
//import crafttweaker.api.liquid.ILiquidStack;
//import crafttweaker.api.minecraft.CraftTweakerMC;
//import crafttweaker.runtime.ScriptLoader;
//import muramasa.gtu.Ref;
//import muramasa.antimatter.GregTechAPI;
//import muramasa.antimatter.blocks.BlockStorage;
//import muramasa.gtu.data.Machines;
//import muramasa.gtu.data.Materials;
//import muramasa.antimatter.machines.types.Machine;
//import muramasa.antimatter.materials.Material;
//import muramasa.antimatter.materials.MaterialType;
//import muramasa.antimatter.materials.TextureSet;
//import muramasa.antimatter.ore.StoneType;
//import muramasa.antimatter.registration.IGregTechRegistrar;
//import muramasa.antimatter.registration.RegistrationEvent;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.fluids.FluidStack;
//import stanhebben.zenscript.annotations.ZenClass;
//import stanhebben.zenscript.annotations.ZenMethod;
//
//import java.util.Arrays;
//
//@ZenClass("mods.gtu")
//public class GregTechTweaker implements IGregTechRegistrar {
//
//    public GregTechTweaker() {
//        CraftTweakerAPI.registerClass(GregTechTweaker.class);
//        CraftTweakerAPI.registerClass(CTMaterial.class);
//        CraftTweakerAPI.registerClass(CTRecipeBuilder.class);
//        CraftTweakerAPI.registerClass(CTStructureBuilder.class);
//
//        GregTechAPI.addRegistrar(this);
//    }
//
//    @ZenMethod
//    public static void addMaterialType(String id, boolean visible) {
//        new MaterialType(id, visible, -1);
//    }
//
//    @ZenMethod
//    public static void addMaterialType(String id, boolean visible, int unitValue) {
//        new MaterialType(id, visible, unitValue);
//    }
//
//    @ZenMethod
//    public static void addStorage(String id, String type, String... ids) {
//        if (GregTechAPI.has(BlockStorage.class, id)) throw new IllegalArgumentException("A storage block with the id " + id + " already exists");
//        Material[] materials = Arrays.stream(ids).filter(s -> Materials.get(s) != null).map(Materials::get).toArray(Material[]::new);
//        int length = materials.length;
//        MaterialType materialType = GregTechAPI.get(MaterialType.class, type);
//        if (length == 0) throw new IllegalArgumentException("Could not find any valid materials for passed names");
//        else if (materialType == null) throw new IllegalArgumentException("MaterialType for id " + type + " does not exist");
//        else if (length <= 16) new BlockStorage(id, GregTechAPI.get(MaterialType.class, type), materials);
//        else if (length % 16 == 0) {
//            int sets = length / 16;
//            CraftTweakerAPI.logWarning("You have loaded a list of materials that have more than 16 materials, and is a multiple of 16, we have automatically split them into " + Integer.toString(sets) + " sets of blocks.");
//            for (int i = 0; i <= sets; i++) {
//                new BlockStorage(id + "_" + Integer.toString(i), GregTechAPI.get(MaterialType.class, type), Arrays.copyOfRange(materials, i * sets, (i + 1) * sets));
//            }
//        }
//        //Everything here *should* be more than 16 but can't be divided by 16
//        else {
//            int sets = length / 16;
//            int remainder = length % 16;
//            CraftTweakerAPI.logWarning("You have loaded a list of materials that have more than 16 materials, we have automatically split them into " + Integer.toString(sets) + " sets of blocks. With the last set of blocks having " + Integer.toString(remainder) + " metas.");
//            for (int i = 0; i <= sets; i++) {
//                if (i == sets) new BlockStorage(id, GregTechAPI.get(MaterialType.class, type), Arrays.copyOfRange(materials, i * 16, (i * 16) + remainder));
//                else new BlockStorage(id, GregTechAPI.get(MaterialType.class, type), Arrays.copyOfRange(materials, i * 16, (i + 1) * 16));
//            }
//        }
//        //new BlockStorage(id, GregTechAPI.get(MaterialType.class, type), materials);
//    }
//
//    @ZenMethod
//    public static CTMaterial getMaterial(String id) {
//        Material material = Materials.get(id);
//        if (material == null) throw new IllegalArgumentException("material for id " + id + " does not exist");
//        return new CTMaterial(material);
//    }
//
//    @ZenMethod
//    public static CTMaterial addMaterial(String id, int rgb, String textureSet) {
//        if (Materials.get(id) != null) throw new IllegalArgumentException("material for id " + id + " already exists");
//        return new CTMaterial(id, rgb, textureSet);
//    }
//
//    @ZenMethod
//    public static void addTextureSet(String id) {
//        new TextureSet(id);
//    }
//
//    @ZenMethod
//    public static CTRecipeBuilder getBuilder(String id) {
//        Machine machine = Machines.get(id);
//        if (machine == null) throw new IllegalArgumentException("machine for id " + id + " could not be found");
//        return new CTRecipeBuilder(machine.getRecipeBuilder());
//    }
//
//    @ZenMethod
//    public static CTStructureBuilder addStructure(String id) {
//        Machine machine = Machines.get(id);
//        if (machine == null) throw new IllegalArgumentException("machine for id " + id + " could not be found");
//        return new CTStructureBuilder(machine);
//    }
//
//    public static ItemStack[] getItems(IIngredient... items) {
//        return Arrays.stream(items).map(CraftTweakerMC::getItemStack).toArray(ItemStack[]::new);
//    }
//
//    public static FluidStack[] getFluids(ILiquidStack... fluids) {
//        return Arrays.stream(fluids).map(ILiquidStack::getInternal).toArray(FluidStack[]::new);
//    }
//
//    @Override
//    public String getId() {
//        return Ref.MOD_CT;
//    }
//
//    @Override
//    public void onRegistrationEvent(RegistrationEvent event) {
//        if (event == RegistrationEvent.DATA) {
//            ScriptLoader loader = CraftTweakerAPI.tweaker.getOrCreateLoader(Ref.MODID + "_data");
//            CraftTweakerAPI.tweaker.loadScript(false, loader);
//        }
//    }
//}
