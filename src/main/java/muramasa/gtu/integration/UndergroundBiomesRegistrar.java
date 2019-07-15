package muramasa.gtu.integration;

import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.api.enums.IgneousVariant;
import exterminatorjeff.undergroundbiomes.api.enums.MetamorphicVariant;
import exterminatorjeff.undergroundbiomes.api.enums.SedimentaryVariant;
import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.ore.BlockOre;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.api.registration.IGregTechRegistrar;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.worldgen.WorldGenHelper;
import net.minecraft.block.SoundType;

public class UndergroundBiomesRegistrar implements IGregTechRegistrar {

    //UB Igneous
    public static StoneType UB_RED_GRANIITE = new StoneType("ub_red_granite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/red_granite"), SoundType.STONE);
    public static StoneType UB_BLACK_GRANIITE = new StoneType("ub_black_granite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/black_granite"), SoundType.STONE);
    public static StoneType UB_RHYOLITE = new StoneType("ub_rhyolite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/rhyolite"), SoundType.STONE);
    public static StoneType UB_ANDESITE = new StoneType("ub_andesite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/andesite"), SoundType.STONE);
    public static StoneType UB_GABBRO = new StoneType("ub_gabbro", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/gabbro"), SoundType.STONE);
    public static StoneType UB_BASALT = new StoneType("ub_basalt", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/basalt"), SoundType.STONE);
    public static StoneType UB_KOMATIITE = new StoneType("ub_komatiite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/komatiite"), SoundType.STONE);
    public static StoneType UB_DACITE = new StoneType("ub_dacite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/dacite"), SoundType.STONE);

    //UB Metamorphic
    public static StoneType UB_GNEISS = new StoneType("ub_gneiss", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/gneiss"), SoundType.STONE);
    public static StoneType UB_ECLOGITE = new StoneType("ub_eclogite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/eclogite"), SoundType.STONE);
    public static StoneType UB_MARBLE = new StoneType("ub_marble", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/marble"), SoundType.STONE);
    public static StoneType UB_QUARTZITE = new StoneType("ub_quartzite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/quartzite"), SoundType.STONE);
    public static StoneType UB_BLUE_SCHIST = new StoneType("ub_blue_schist", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/blueschist"), SoundType.STONE);
    public static StoneType UB_GREEN_SCHIST = new StoneType("ub_green_schist", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/greenschist"), SoundType.STONE);
    public static StoneType UB_SOAPSTONE = new StoneType("ub_soapstone", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/soapstone"), SoundType.STONE);
    public static StoneType UB_MIGMATITE = new StoneType("ub_migmatite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/migmatite"), SoundType.STONE);

    //UB Sedimentary
    public static StoneType UB_LIMESTONE = new StoneType("ub_limestone", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/limestone"), SoundType.STONE);
    public static StoneType UB_CHALK = new StoneType("ub_chalk", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/chalk"), SoundType.STONE);
    public static StoneType UB_SHALE = new StoneType("ub_shale", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/shale"), SoundType.STONE);
    public static StoneType UB_SILTSTONE = new StoneType("ub_siltstone", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/siltstone"), SoundType.STONE);
    public static StoneType UB_LIGNITE = new StoneType("ub_lignite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/lignite"), SoundType.STONE);
    public static StoneType UB_DOLOMITE = new StoneType("ub_dolomite", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/dolomite"), SoundType.STONE);
    public static StoneType UB_GREYWACKE = new StoneType("ub_greywacke", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/greywacke"), SoundType.STONE);
    public static StoneType UB_CHERT = new StoneType("ub_chert", Ref.MOD_UB, Materials.Stone, false, new Texture(Ref.MOD_UB, "blocks/chert"), SoundType.STONE);


    @Override
    public String getId() {
        return Ref.MOD_UB;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case INIT:

                break;
            case WORLDGEN:
                WorldGenHelper.ORE_MAP.put(API.IGNEOUS_STONE.getBlock().getDefaultState().withProperty(IgneousVariant.IGNEOUS_VARIANT_PROPERTY, IgneousVariant.RED_GRANITE), BlockOre.get(UB_RED_GRANIITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.IGNEOUS_STONE.getBlock().getDefaultState().withProperty(IgneousVariant.IGNEOUS_VARIANT_PROPERTY, IgneousVariant.BLACK_GRANITE), BlockOre.get(UB_BLACK_GRANIITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.IGNEOUS_STONE.getBlock().getDefaultState().withProperty(IgneousVariant.IGNEOUS_VARIANT_PROPERTY, IgneousVariant.RHYOLITE), BlockOre.get(UB_RHYOLITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.IGNEOUS_STONE.getBlock().getDefaultState().withProperty(IgneousVariant.IGNEOUS_VARIANT_PROPERTY, IgneousVariant.ANDESITE), BlockOre.get(UB_ANDESITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.IGNEOUS_STONE.getBlock().getDefaultState().withProperty(IgneousVariant.IGNEOUS_VARIANT_PROPERTY, IgneousVariant.GABBRO), BlockOre.get(UB_GABBRO).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.IGNEOUS_STONE.getBlock().getDefaultState().withProperty(IgneousVariant.IGNEOUS_VARIANT_PROPERTY, IgneousVariant.BASALT), BlockOre.get(UB_BASALT).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.IGNEOUS_STONE.getBlock().getDefaultState().withProperty(IgneousVariant.IGNEOUS_VARIANT_PROPERTY, IgneousVariant.KOMATIITE), BlockOre.get(UB_KOMATIITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.IGNEOUS_STONE.getBlock().getDefaultState().withProperty(IgneousVariant.IGNEOUS_VARIANT_PROPERTY, IgneousVariant.DACITE), BlockOre.get(UB_DACITE).getDefaultState());

                WorldGenHelper.ORE_MAP.put(API.METAMORPHIC_STONE.getBlock().getDefaultState().withProperty(MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY, MetamorphicVariant.GNEISS), BlockOre.get(UB_GNEISS).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.METAMORPHIC_STONE.getBlock().getDefaultState().withProperty(MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY, MetamorphicVariant.ECLOGITE), BlockOre.get(UB_ECLOGITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.METAMORPHIC_STONE.getBlock().getDefaultState().withProperty(MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY, MetamorphicVariant.MARBLE), BlockOre.get(UB_MARBLE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.METAMORPHIC_STONE.getBlock().getDefaultState().withProperty(MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY, MetamorphicVariant.QUARTZITE), BlockOre.get(UB_QUARTZITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.METAMORPHIC_STONE.getBlock().getDefaultState().withProperty(MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY, MetamorphicVariant.BLUESCHIST), BlockOre.get(UB_BLUE_SCHIST).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.METAMORPHIC_STONE.getBlock().getDefaultState().withProperty(MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY, MetamorphicVariant.GREENSCHIST), BlockOre.get(UB_GREEN_SCHIST).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.METAMORPHIC_STONE.getBlock().getDefaultState().withProperty(MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY, MetamorphicVariant.SOAPSTONE), BlockOre.get(UB_SOAPSTONE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.METAMORPHIC_STONE.getBlock().getDefaultState().withProperty(MetamorphicVariant.METAMORPHIC_VARIANT_PROPERTY, MetamorphicVariant.MIGMATITE), BlockOre.get(UB_MIGMATITE).getDefaultState());

                WorldGenHelper.ORE_MAP.put(API.SEDIMENTARY_STONE.getBlock().getDefaultState().withProperty(SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY, SedimentaryVariant.LIMESTONE), BlockOre.get(UB_LIMESTONE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.SEDIMENTARY_STONE.getBlock().getDefaultState().withProperty(SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY, SedimentaryVariant.CHALK), BlockOre.get(UB_CHALK).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.SEDIMENTARY_STONE.getBlock().getDefaultState().withProperty(SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY, SedimentaryVariant.SHALE), BlockOre.get(UB_SHALE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.SEDIMENTARY_STONE.getBlock().getDefaultState().withProperty(SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY, SedimentaryVariant.SILTSTONE), BlockOre.get(UB_SILTSTONE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.SEDIMENTARY_STONE.getBlock().getDefaultState().withProperty(SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY, SedimentaryVariant.LIGNITE), BlockOre.get(UB_LIGNITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.SEDIMENTARY_STONE.getBlock().getDefaultState().withProperty(SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY, SedimentaryVariant.DOLOMITE), BlockOre.get(UB_DOLOMITE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.SEDIMENTARY_STONE.getBlock().getDefaultState().withProperty(SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY, SedimentaryVariant.GREYWACKE), BlockOre.get(UB_GREYWACKE).getDefaultState());
                WorldGenHelper.ORE_MAP.put(API.SEDIMENTARY_STONE.getBlock().getDefaultState().withProperty(SedimentaryVariant.SEDIMENTARY_VARIANT_PROPERTY, SedimentaryVariant.CHERT), BlockOre.get(UB_CHERT).getDefaultState());
                break;
        }
    }
}
