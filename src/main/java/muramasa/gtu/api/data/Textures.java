package muramasa.gtu.api.data;

import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;

public class Textures {

    public static final Texture ERROR = new Texture("blocks/machine/overlay/invalid/front");

    public static final Texture PIPE = new Texture("blocks/pipe/pipe_side");
    public static final Texture WIRE = new Texture("blocks/pipe/wire_side");
    public static final Texture CABLE = new Texture("blocks/pipe/cable_side");

    public static final Texture[] PIPE_FACE = new Texture[] {
        new Texture("blocks/pipe/pipe_vtiny"),
        new Texture("blocks/pipe/pipe_tiny"),
        new Texture("blocks/pipe/pipe_small"),
        new Texture("blocks/pipe/pipe_normal"),
        new Texture("blocks/pipe/pipe_large"),
        new Texture("blocks/pipe/pipe_huge")
    };

    public static final Texture[] CABLE_FACE = new Texture[] {
        new Texture("blocks/pipe/cable_vtiny"),
        new Texture("blocks/pipe/cable_tiny"),
        new Texture("blocks/pipe/cable_small"),
        new Texture("blocks/pipe/cable_normal"),
        new Texture("blocks/pipe/cable_large"),
        new Texture("blocks/pipe/cable_huge")
    };

    public static final Texture[] WIRE_FACE = new Texture[] {
        WIRE, WIRE, WIRE, WIRE, WIRE, WIRE
    };

    public static final TextureData[] PIPE_DATA = new TextureData[] {
        new TextureData().base(PIPE).overlay(PIPE_FACE),
        new TextureData().base(WIRE).overlay(WIRE_FACE),
        new TextureData().base(CABLE).overlay(CABLE_FACE)
    };

    public static final Texture[] LARGE_TURBINE = new Texture[] {
        new Texture("blocks/ct/turbine/large_turbine_0"),
        new Texture("blocks/ct/turbine/large_turbine_1"),
        new Texture("blocks/ct/turbine/large_turbine_2"),
        new Texture("blocks/ct/turbine/large_turbine_3"),
        new Texture("blocks/ct/turbine/large_turbine_4"),
        new Texture("blocks/ct/turbine/large_turbine_5"),
        new Texture("blocks/ct/turbine/large_turbine_6"),
        new Texture("blocks/ct/turbine/large_turbine_7"),
        new Texture("blocks/ct/turbine/large_turbine_8")
    };

    public static final Texture[] LARGE_TURBINE_ACTIVE = new Texture[] {
        new Texture("blocks/ct/turbine/large_turbine_active_0"),
        new Texture("blocks/ct/turbine/large_turbine_active_1"),
        new Texture("blocks/ct/turbine/large_turbine_active_2"),
        new Texture("blocks/ct/turbine/large_turbine_active_3"),
        new Texture("blocks/ct/turbine/large_turbine_active_4"),
        new Texture("blocks/ct/turbine/large_turbine_active_5"),
        new Texture("blocks/ct/turbine/large_turbine_active_6"),
        new Texture("blocks/ct/turbine/large_turbine_active_7"),
        new Texture("blocks/ct/turbine/large_turbine_active_8")
    };

    public static final Texture[] FUSION_1_CT = new Texture[] {
        new Texture("blocks/ct/fusion/fusion_1_0"),
        new Texture("blocks/ct/fusion/fusion_1_1"),
        new Texture("blocks/ct/fusion/fusion_1_2"),
        new Texture("blocks/ct/fusion/fusion_1_3"),
        new Texture("blocks/ct/fusion/fusion_1_4"),
        new Texture("blocks/ct/fusion/fusion_1_5"),
        new Texture("blocks/ct/fusion/fusion_1_6"),
        new Texture("blocks/ct/fusion/fusion_1_7"),
        new Texture("blocks/ct/fusion/fusion_1_8"),
        new Texture("blocks/ct/fusion/fusion_1_9"),
        new Texture("blocks/ct/fusion/fusion_1_10"),
        new Texture("blocks/ct/fusion/fusion_1_11"),
        new Texture("blocks/ct/fusion/fusion_1_12")
    };

    public static final Texture[] FUSION_2_CT = new Texture[] {
        new Texture("blocks/ct/fusion/fusion_2_0"),
        new Texture("blocks/ct/fusion/fusion_2_1"),
        new Texture("blocks/ct/fusion/fusion_2_2"),
        new Texture("blocks/ct/fusion/fusion_2_3"),
        new Texture("blocks/ct/fusion/fusion_2_4"),
        new Texture("blocks/ct/fusion/fusion_2_5"),
        new Texture("blocks/ct/fusion/fusion_2_6"),
        new Texture("blocks/ct/fusion/fusion_2_7"),
        new Texture("blocks/ct/fusion/fusion_2_8"),
        new Texture("blocks/ct/fusion/fusion_2_9"),
        new Texture("blocks/ct/fusion/fusion_2_10"),
        new Texture("blocks/ct/fusion/fusion_2_11"),
        new Texture("blocks/ct/fusion/fusion_2_12")
    };

    public static final Texture[] FUSION_3_CT = new Texture[] {
        new Texture("blocks/ct/fusion/fusion_3_0"),
        new Texture("blocks/ct/fusion/fusion_3_1"),
        new Texture("blocks/ct/fusion/fusion_3_2"),
        new Texture("blocks/ct/fusion/fusion_3_3"),
        new Texture("blocks/ct/fusion/fusion_3_4"),
        new Texture("blocks/ct/fusion/fusion_3_5"),
        new Texture("blocks/ct/fusion/fusion_3_6"),
        new Texture("blocks/ct/fusion/fusion_3_7"),
        new Texture("blocks/ct/fusion/fusion_3_8"),
        new Texture("blocks/ct/fusion/fusion_3_9"),
        new Texture("blocks/ct/fusion/fusion_3_10"),
        new Texture("blocks/ct/fusion/fusion_3_11"),
        new Texture("blocks/ct/fusion/fusion_3_12")
    };
}
