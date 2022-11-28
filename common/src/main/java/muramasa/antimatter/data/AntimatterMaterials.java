package muramasa.antimatter.data;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.TextureSet;

import static muramasa.antimatter.material.Element.*;
import static muramasa.antimatter.material.TextureSet.*;

public class AntimatterMaterials {
    //Vanilla Stone Materials
    public static Material Stone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "stone", 0xcdcdcd, NONE));
    public static Material Granite = AntimatterAPI.register(Material.class, new Material(Ref.ID, "granite", 0xa07882, NONE));
    public static Material Diorite = AntimatterAPI.register(Material.class, new Material(Ref.ID, "diorite", 0xf0f0f0, NONE));
    public static Material Andesite = AntimatterAPI.register(Material.class, new Material(Ref.ID, "andesite", 0xbfbfbf, NONE));
    public static Material Deepslate = AntimatterAPI.register(Material.class, new Material(Ref.ID, "deepslate", 0x1e1414, NONE));
    public static Material Tuff = AntimatterAPI.register(Material.class, new Material(Ref.ID, "tuff", 0x392923, NONE));
    public static Material Gravel = AntimatterAPI.register(Material.class, new Material(Ref.ID, "gravel", 0xcdcdcd, NONE));
    public static Material Dirt = AntimatterAPI.register(Material.class, new Material(Ref.ID, "dirt", 0x976d4d, NONE));
    public static Material Sand = AntimatterAPI.register(Material.class, new Material(Ref.ID, "sand", 0xfafac8, NONE));
    public static Material RedSand = AntimatterAPI.register(Material.class, new Material(Ref.ID, "red_sand", 0xff8438, NONE));
    public static Material Sandstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "sandstone", 0xfafac8, NONE));
    public static Material Blackstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "blackstone", 0x2c272d, NONE));
    public static Material Basalt = AntimatterAPI.register(Material.class, new Material(Ref.ID, "basalt", 0x1e1414, ROUGH));
    public static Material Endstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "endstone", 0xffffff, NONE));
    public static Material Netherrack = AntimatterAPI.register(Material.class, new Material(Ref.ID, "netherrack", 0xc80000, NONE));
    public static Material Prismarine = AntimatterAPI.register(Material.class, new Material(Ref.ID, "prismarine", 0x6eb2a5, NONE));
    public static Material DarkPrismarine = AntimatterAPI.register(Material.class, new Material(Ref.ID, "dark_prismarine", 0x587d6c, NONE));
    //Vanilla Metal/Gem Materials
    public static Material Iron = AntimatterAPI.register(Material.class, new Material(Ref.ID, "iron", 0xc8c8c8, METALLIC, Fe));
    public static Material Gold = AntimatterAPI.register(Material.class, new Material(Ref.ID, "gold", 0xffe650, SHINY, Au));
    //cause 1.18
    public static Material Copper = AntimatterAPI.register(Material.class, new Material(Ref.ID, "copper", 0xff6400, SHINY, Cu));
    public static Material Glowstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "glowstone", 0xffff00, SHINY));
    public static Material Sugar = AntimatterAPI.register(Material.class, new Material(Ref.ID, "sugar", 0xfafafa, DULL));
    public static Material Bone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "bone", 0xb3b3b3, DULL));
    public static Material Wood = AntimatterAPI.register(Material.class, new Material(Ref.ID, "wood", 0x643200, TextureSet.WOOD));
    public static Material Blaze = AntimatterAPI.register(Material.class, new Material(Ref.ID, "blaze", 0xffc800, NONE));
    public static Material Flint = AntimatterAPI.register(Material.class, new Material(Ref.ID, "flint", 0x002040, FLINT));
    public static Material Charcoal = AntimatterAPI.register(Material.class, new Material(Ref.ID, "charcoal", 0x644646, LIGNITE));
    public static Material Coal = AntimatterAPI.register(Material.class, new Material(Ref.ID, "coal", 0x464646, LIGNITE));
    public static Material Diamond = AntimatterAPI.register(Material.class, new Material(Ref.ID, "diamond", /*0x3de0e5*/0xc8ffff, DIAMOND));
    public static Material Emerald = AntimatterAPI.register(Material.class, new Material(Ref.ID, "emerald", 0x50ff50, GEM_V));
    public static Material EnderPearl = AntimatterAPI.register(Material.class, new Material(Ref.ID, "enderpearl", 0x6cdcc8, SHINY));
    public static Material EnderEye = AntimatterAPI.register(Material.class, new Material(Ref.ID, "endereye", 0xa0fae6, SHINY));
    public static Material Lapis = AntimatterAPI.register(Material.class, new Material(Ref.ID, "lapis", 0x4646dc, LAPIS));
    public static Material Redstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "redstone", 0xc80000, REDSTONE));
    public static Material Quartz = AntimatterAPI.register(Material.class, new Material(Ref.ID, "quartz", 0xe6d2d2, NONE));
    public static Material Netherite = AntimatterAPI.register(Material.class, new Material(Ref.ID, "netherite", 0x504650, DULL));
    public static Material NetherizedDiamond = AntimatterAPI.register(Material.class, new Material(Ref.ID, "netherized_diamond", 0x5a505a, DIAMOND));
    public static Material NetheriteScrap = AntimatterAPI.register(Material.class, new Material(Ref.ID, "netherite_scrap", 0x6e505a, ROUGH));
    public static Material Lava = AntimatterAPI.register(Material.class, new Material(Ref.ID, "lava", 0xff4000, NONE));
    public static Material Water = AntimatterAPI.register(Material.class, new Material(Ref.ID, "water", 0x0000ff, NONE));

    public static void init(){
        Material.init();
    }
}
