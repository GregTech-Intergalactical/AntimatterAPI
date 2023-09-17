package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.types.BasicMachine;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.machine.types.MultiMachine;
import muramasa.antimatter.material.*;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.map.RecipeBuilder;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;

import java.util.Map;
import java.util.Objects;

public class AMCreationEvent extends EventJS {
    public static final Map<String, SoundType> SOUND_TYPE_REGISTRY = new Object2ObjectOpenHashMap<>();
    public StoneType createStoneType(String id, String material, String texture, SoundType soundType, boolean generateBlock) {
        return AntimatterAPI.register(StoneType.class, new StoneType(Ref.MOD_KJS, id, Material.get(material), new Texture(texture), soundType, generateBlock));
    }

    public StoneType createStoneType(String id, String material, String texture, SoundType soundType, String stoneState) {
        return AntimatterAPI.register(StoneType.class, new StoneType(Ref.MOD_KJS, id, Material.get(material), new Texture(texture), soundType, false).setStateSupplier(() -> AntimatterPlatformUtils.getBlockFromId(new ResourceLocation(stoneState)).defaultBlockState()));
    }

    public Material createMaterial(String id, int rgb, String textureSet, String textureSetDomain) {
        TextureSet set = Objects.requireNonNull(AntimatterAPI.get(TextureSet.class, textureSet, textureSetDomain), "Specified texture set in Material created via kubejs event is null");
        return AntimatterAPI.register(Material.class, new Material(Ref.MOD_KJS, id, rgb, set));
    }

    public Material createMaterial(String id, int rgb, String textureSet, String textureSetDomain, String element) {
        TextureSet set = Objects.requireNonNull(AntimatterAPI.get(TextureSet.class, textureSet, textureSetDomain), "Specified texture set in Material created via kubejs event is null");
        return AntimatterAPI.register(Material.class, new Material(Ref.MOD_KJS, id, rgb, set, Element.getFromElementId(element)));
    }

    public BasicMachine createBasicMachine(String id){
        return new BasicMachine(Ref.MOD_KJS, id);
    }

    public MultiMachine createMultiMachine(String id){
        return new MultiMachine(Ref.MOD_KJS, id);
    }

    public BasicMultiMachine createBasicMultiMachine(String id){
        return new BasicMultiMachine(Ref.MOD_KJS, id);
    }

    public Machine createMachine(String id){
        return new Machine(Ref.MOD_KJS, id);
    }

    public RecipeMap createRecipeMap(String id){
        return AntimatterAPI.register(RecipeMap.class, new RecipeMap<>(Ref.MOD_KJS, id, new RecipeBuilder()));
    }



    public void addFlagsToMaterial(String materialId, String... flags) {
        if (Material.get(materialId) != Material.NULL) {
            for (String flag : flags) {
                IMaterialTag tag = AntimatterAPI.get(IMaterialTag.class, flag);
                if (tag != null) {
                    Material.get(materialId).flags(type(flag));
                }
            }
        }
    }

    public MachineFlag machineFlag(String id){
        return MachineFlag.valueOf(id);
    }

    public MaterialType type(String type) {
        return AntimatterAPI.get(MaterialType.class, type);
    }

    public RecipeMap recipeMap(String id){
        return AntimatterAPI.get(RecipeMap.class, id);
    }

    public SlotType slotType(String id){
        return AntimatterAPI.get(SlotType.class, id, Ref.ID);
    }

    public static void init(){
        SOUND_TYPE_REGISTRY.put("wood", SoundType.WOOD);
        SOUND_TYPE_REGISTRY.put("gravel", SoundType.GRAVEL);
        SOUND_TYPE_REGISTRY.put("grass", SoundType.GRASS);
        SOUND_TYPE_REGISTRY.put("lily_pad", SoundType.LILY_PAD);
        SOUND_TYPE_REGISTRY.put("stone", SoundType.STONE);
        SOUND_TYPE_REGISTRY.put("metal", SoundType.METAL);
        SOUND_TYPE_REGISTRY.put("glass", SoundType.GLASS);
        SOUND_TYPE_REGISTRY.put("wool", SoundType.WOOL);
        SOUND_TYPE_REGISTRY.put("sand", SoundType.SAND);
        SOUND_TYPE_REGISTRY.put("snow", SoundType.SNOW);
        SOUND_TYPE_REGISTRY.put("powder_snow", SoundType.POWDER_SNOW);
        SOUND_TYPE_REGISTRY.put("ladder", SoundType.LADDER);
        SOUND_TYPE_REGISTRY.put("anvil", SoundType.ANVIL);
        SOUND_TYPE_REGISTRY.put("slime_block", SoundType.SLIME_BLOCK);
        SOUND_TYPE_REGISTRY.put("honey_block", SoundType.HONEY_BLOCK);
        SOUND_TYPE_REGISTRY.put("wet_grass", SoundType.WET_GRASS);
        SOUND_TYPE_REGISTRY.put("coral_block", SoundType.CORAL_BLOCK);
        SOUND_TYPE_REGISTRY.put("bamboo", SoundType.BAMBOO);
        SOUND_TYPE_REGISTRY.put("bamboo_sapling", SoundType.BAMBOO_SAPLING);
        SOUND_TYPE_REGISTRY.put("scaffolding", SoundType.SCAFFOLDING);
        SOUND_TYPE_REGISTRY.put("sweet_berry_bush", SoundType.SWEET_BERRY_BUSH);
        SOUND_TYPE_REGISTRY.put("crop", SoundType.CROP);
        SOUND_TYPE_REGISTRY.put("hard_crop", SoundType.HARD_CROP);
        SOUND_TYPE_REGISTRY.put("vine", SoundType.VINE);
        SOUND_TYPE_REGISTRY.put("nether_wart", SoundType.NETHER_WART);
        SOUND_TYPE_REGISTRY.put("lantern", SoundType.LANTERN);
        SOUND_TYPE_REGISTRY.put("stem", SoundType.STEM);
        SOUND_TYPE_REGISTRY.put("nylium", SoundType.NYLIUM);
        SOUND_TYPE_REGISTRY.put("fungus", SoundType.FUNGUS);
        SOUND_TYPE_REGISTRY.put("roots", SoundType.ROOTS);
        SOUND_TYPE_REGISTRY.put("shroomlight", SoundType.SHROOMLIGHT);
        SOUND_TYPE_REGISTRY.put("weeping_vines", SoundType.WEEPING_VINES);
        SOUND_TYPE_REGISTRY.put("twisting_vines", SoundType.TWISTING_VINES);
        SOUND_TYPE_REGISTRY.put("soul_sand", SoundType.SOUL_SAND);
        SOUND_TYPE_REGISTRY.put("soul_soil", SoundType.SOUL_SOIL);
        SOUND_TYPE_REGISTRY.put("basalt", SoundType.BASALT);
        SOUND_TYPE_REGISTRY.put("wart_block", SoundType.WART_BLOCK);
        SOUND_TYPE_REGISTRY.put("netherrack", SoundType.NETHERRACK);
        SOUND_TYPE_REGISTRY.put("nether_bricks", SoundType.NETHER_BRICKS);
        SOUND_TYPE_REGISTRY.put("nether_sprouts", SoundType.NETHER_SPROUTS);
        SOUND_TYPE_REGISTRY.put("nether_ore", SoundType.NETHER_ORE);
        SOUND_TYPE_REGISTRY.put("bone_block", SoundType.BONE_BLOCK);
        SOUND_TYPE_REGISTRY.put("netherite_block", SoundType.NETHERITE_BLOCK);
        SOUND_TYPE_REGISTRY.put("ancient_debris", SoundType.ANCIENT_DEBRIS);
        SOUND_TYPE_REGISTRY.put("lodestone", SoundType.LODESTONE);
        SOUND_TYPE_REGISTRY.put("chain", SoundType.CHAIN);
        SOUND_TYPE_REGISTRY.put("nether_gold_ore", SoundType.NETHER_GOLD_ORE);
        SOUND_TYPE_REGISTRY.put("gilded_blackstone", SoundType.GILDED_BLACKSTONE);
        SOUND_TYPE_REGISTRY.put("candle", SoundType.CANDLE);
        SOUND_TYPE_REGISTRY.put("amethyst", SoundType.AMETHYST);
        SOUND_TYPE_REGISTRY.put("amethyst_cluster", SoundType.AMETHYST_CLUSTER);
        SOUND_TYPE_REGISTRY.put("small_amethyst_bud", SoundType.SMALL_AMETHYST_BUD);
        SOUND_TYPE_REGISTRY.put("medium_amethyst_bud", SoundType.MEDIUM_AMETHYST_BUD);
        SOUND_TYPE_REGISTRY.put("large_amethyst_bud", SoundType.LARGE_AMETHYST_BUD);
        SOUND_TYPE_REGISTRY.put("tuff", SoundType.TUFF);
        SOUND_TYPE_REGISTRY.put("calcite", SoundType.CALCITE);
        SOUND_TYPE_REGISTRY.put("dripstone_block", SoundType.DRIPSTONE_BLOCK);
        SOUND_TYPE_REGISTRY.put("pointed_dripstone", SoundType.POINTED_DRIPSTONE);
        SOUND_TYPE_REGISTRY.put("copper", SoundType.COPPER);
        SOUND_TYPE_REGISTRY.put("cave_vines", SoundType.CAVE_VINES);
        SOUND_TYPE_REGISTRY.put("spore_blossom", SoundType.SPORE_BLOSSOM);
        SOUND_TYPE_REGISTRY.put("azalea", SoundType.AZALEA);
        SOUND_TYPE_REGISTRY.put("flowering_azalea", SoundType.FLOWERING_AZALEA);
        SOUND_TYPE_REGISTRY.put("moss_carpet", SoundType.MOSS_CARPET);
        SOUND_TYPE_REGISTRY.put("moss", SoundType.MOSS);
        SOUND_TYPE_REGISTRY.put("big_dripleaf", SoundType.BIG_DRIPLEAF);
        SOUND_TYPE_REGISTRY.put("small_dripleaf", SoundType.SMALL_DRIPLEAF);
        SOUND_TYPE_REGISTRY.put("rooted_dirt", SoundType.ROOTED_DIRT);
        SOUND_TYPE_REGISTRY.put("hanging_roots", SoundType.HANGING_ROOTS);
        SOUND_TYPE_REGISTRY.put("azalea_leaves", SoundType.AZALEA_LEAVES);
        SOUND_TYPE_REGISTRY.put("sculk_sensor", SoundType.SCULK_SENSOR);
        SOUND_TYPE_REGISTRY.put("glow_lichen", SoundType.GLOW_LICHEN);
        SOUND_TYPE_REGISTRY.put("deepslate", SoundType.DEEPSLATE);
        SOUND_TYPE_REGISTRY.put("deepslate_bricks", SoundType.DEEPSLATE_BRICKS);
        SOUND_TYPE_REGISTRY.put("deepslate_tiles", SoundType.DEEPSLATE_TILES);
        SOUND_TYPE_REGISTRY.put("polished_deepslate", SoundType.POLISHED_DEEPSLATE);
    }

    public SoundType soundType(String id){
        return SOUND_TYPE_REGISTRY.getOrDefault(id, SoundType.STONE);
    }
}
