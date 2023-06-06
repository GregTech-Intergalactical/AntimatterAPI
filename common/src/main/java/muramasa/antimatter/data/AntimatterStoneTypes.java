package muramasa.antimatter.data;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.ore.VanillaStoneType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

public class AntimatterStoneTypes {
    public static StoneType STONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "stone", AntimatterMaterials.Stone, new Texture("minecraft", "block/stone"), SoundType.STONE, false).setState(Blocks.STONE));
    public static StoneType GRANITE = AntimatterAPI.register(StoneType.class, new VanillaStoneType(Ref.ID, "granite", AntimatterMaterials.Granite, "block/stone/", new Texture("minecraft", "block/granite"), SoundType.STONE, false).setState(Blocks.GRANITE));
    public static StoneType DIORITE = AntimatterAPI.register(StoneType.class, new VanillaStoneType(Ref.ID, "diorite", AntimatterMaterials.Diorite, "block/stone/", new Texture("minecraft", "block/diorite"), SoundType.STONE, false).setState(Blocks.DIORITE));
    public static StoneType ANDESITE = AntimatterAPI.register(StoneType.class, new VanillaStoneType(Ref.ID, "andesite", AntimatterMaterials.Andesite, "block/stone/", new Texture("minecraft", "block/andesite"), SoundType.STONE, false).setState(Blocks.ANDESITE));
    public static StoneType DEEPSLATE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "deepslate", AntimatterMaterials.Deepslate, new Texture("minecraft", "block/deepslate"), SoundType.STONE, false).setState(Blocks.DEEPSLATE));
    public static StoneType TUFF = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "tuff", AntimatterMaterials.Tuff, new Texture("minecraft", "block/tuff"), SoundType.STONE, false).setState(Blocks.TUFF));
    public static StoneType GRAVEL = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "gravel", AntimatterMaterials.Gravel, new Texture("minecraft", "block/gravel"), SoundType.GRAVEL, false).setState(Blocks.GRAVEL).setGravity(true).setBlockMaterial(net.minecraft.world.level.material.Material.SAND).setHardnessAndResistance(0.6F).setRequiresTool(false).setType(BlockTags.MINEABLE_WITH_SHOVEL)).setSandLike(true);
    public static StoneType DIRT = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "dirt", AntimatterMaterials.Dirt, new Texture("minecraft", "block/dirt"), SoundType.GRAVEL, false).setState(Blocks.DIRT).setBlockMaterial(net.minecraft.world.level.material.Material.DIRT).setHardnessAndResistance(0.5F).setRequiresTool(false).setType(BlockTags.MINEABLE_WITH_SHOVEL).setGenerateOre(false));
    public static StoneType SAND = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sand", AntimatterMaterials.Sand, new Texture("minecraft", "block/sand"), SoundType.SAND, false).setState(Blocks.SAND).setGravity(true).setBlockMaterial(net.minecraft.world.level.material.Material.SAND).setHardnessAndResistance(0.5F).setRequiresTool(false).setType(BlockTags.MINEABLE_WITH_SHOVEL)).setSandLike(true);
    public static StoneType SAND_RED = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sand_red", AntimatterMaterials.RedSand, new Texture("minecraft", "block/red_sand"), SoundType.SAND, false).setState(Blocks.RED_SAND).setGravity(true).setBlockMaterial(net.minecraft.world.level.material.Material.SAND).setHardnessAndResistance(0.5F).setRequiresTool(false).setType(BlockTags.MINEABLE_WITH_SHOVEL)).setSandLike(true);
    public static StoneType SANDSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sandstone", AntimatterMaterials.Sandstone, new Texture("minecraft", "block/sandstone"), SoundType.STONE, false).setState(Blocks.SANDSTONE)
            .setTextures(new Texture("minecraft", "block/sandstone_bottom"), new Texture("minecraft", "block/sandstone_top"), new Texture("minecraft", "block/sandstone"), new Texture("minecraft", "block/sandstone"), new Texture("minecraft", "block/sandstone"), new Texture("minecraft", "block/sandstone")));
    public static StoneType BASALT = AntimatterAPI.register(StoneType.class, new VanillaStoneType(Ref.ID, "basalt", AntimatterMaterials.Basalt, "block/stone/", new Texture("minecraft", "block/basalt_side"), SoundType.BASALT, false).setState(Blocks.BASALT).setHardnessAndResistance(1.25F, 4.2F)
            .setTextures(new Texture("minecraft", "block/basalt_top"), new Texture("minecraft", "block/basalt_top"), new Texture("minecraft", "block/basalt_side"), new Texture("minecraft", "block/basalt_side"), new Texture("minecraft", "block/basalt_side"), new Texture("minecraft", "block/basalt_side")));
    public static StoneType BLACKSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "blackstone", AntimatterMaterials.Blackstone, new Texture("minecraft", "block/blackstone"), SoundType.STONE, false).setState(Blocks.BLACKSTONE));
    public static StoneType NETHERRACK = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "netherrack", AntimatterMaterials.Netherrack, new Texture("minecraft", "block/netherrack"), SoundType.NETHERRACK, false).setState(Blocks.NETHERRACK).setHardnessAndResistance(0.4F));
    public static StoneType ENDSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "endstone", AntimatterMaterials.Endstone, new Texture("minecraft", "block/end_stone"), SoundType.STONE, false).setState(Blocks.END_STONE).setHardnessAndResistance(3.0F, 9.0F));

    public static void init(){}
}
