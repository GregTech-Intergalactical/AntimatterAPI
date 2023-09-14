package muramasa.antimatter.gui;

import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.resources.ResourceLocation;

public class ButtonOverlay implements IAntimatterObject {

    public static ButtonOverlay STOP = new ButtonOverlay(Ref.ID,"stop",16, 16);
    public static ButtonOverlay TORCH_OFF = new ButtonOverlay(Ref.ID,"torch_off",16, 16)
            .setTextureOverride(new ResourceLocation("textures/block/redstone_torch_off.png"));
    public static ButtonOverlay TORCH_ON = new ButtonOverlay(Ref.ID,"torch_on",16, 16)
            .setTextureOverride(new ResourceLocation("textures/block/redstone_torch.png"));
    public static ButtonOverlay REDSTONE = new ButtonOverlay(Ref.ID, "redstone", 16, 16)
            .setTextureOverride(new ResourceLocation("textures/item/redstone.png"));
    public static ButtonOverlay EXPORT = new ButtonOverlay(Ref.ID,"export",16, 16);
    public static ButtonOverlay IMPORT = new ButtonOverlay(Ref.ID,"import",16, 16);
    public static ButtonOverlay EXPORT_IMPORT = new ButtonOverlay(Ref.ID,"export_import",16, 16);
    public static ButtonOverlay IMPORT_EXPORT = new ButtonOverlay(Ref.ID,"import_export",16, 16);
    public static ButtonOverlay INPUT_OFF = new ButtonOverlay(Ref.ID,"input_off",16, 16);

    public static ButtonOverlay GREY_OFF = new ButtonOverlay(Ref.ID,"grey_off", 16, 16);
    public static ButtonOverlay GREY_ON = new ButtonOverlay(Ref.ID,"grey_on", 16, 16);
    public static ButtonOverlay BLUE_OFF = new ButtonOverlay(Ref.ID,"blue_off", 16, 16);
    public static ButtonOverlay BLUE_ON = new ButtonOverlay(Ref.ID,"blue_on", 16, 16);
    public static ButtonOverlay LESS = new ButtonOverlay(Ref.ID,"less",16, 16);
    public static ButtonOverlay EQUAL = new ButtonOverlay(Ref.ID,"equal",16, 16);
    public static ButtonOverlay MORE = new ButtonOverlay(Ref.ID,"more",16, 16);
    public static ButtonOverlay WHITELIST = new ButtonOverlay(Ref.ID,"whitelist",16, 16);
    public static ButtonOverlay BLACKLIST = new ButtonOverlay(Ref.ID,"blacklist",16, 16);
    public static ButtonOverlay MINUS = new ButtonOverlay(Ref.ID,"minus", 16, 16);
    public static ButtonOverlay PLUS = new ButtonOverlay(Ref.ID,"plus", 16, 16);
    public static ButtonOverlay DIVISION = new ButtonOverlay(Ref.ID,"division", 16, 16);
    public static ButtonOverlay MULT = new ButtonOverlay(Ref.ID,"mult", 16, 16);
    public static ButtonOverlay PERCENT = new ButtonOverlay(Ref.ID,"percent", 16, 16);
    public static ButtonOverlay ARROW_LEFT = new ButtonOverlay(Ref.ID,"arrow_left",16, 16);
    public static ButtonOverlay A_LEFT = new ButtonOverlay(Ref.ID,"a_left", 16, 16);
    public static ButtonOverlay A_RIGHT = new ButtonOverlay(Ref.ID,"a_right", 16, 16);
    public static ButtonOverlay ARROW_RIGHT = new ButtonOverlay(Ref.ID,"arrow_right", 16, 16);
    public static ButtonOverlay INPUT_OUTPUT = new ButtonOverlay(Ref.ID,"in_out", 14, 14);
    public static ButtonOverlay NO_OVERLAY = new ButtonOverlay(Ref.ID, "no_overlay", 16, 16);
    public static ButtonOverlay APAD_LEFT = new ButtonOverlay(Ref.ID,"apad_left",14, 14);    // «
    public static ButtonOverlay PAD_LEFT = new ButtonOverlay(Ref.ID,"pad_left", 14, 14);     // ‹
    public static ButtonOverlay PAD_RIGHT = new ButtonOverlay(Ref.ID,"pad_right", 14, 14);   // »
    public static ButtonOverlay APAD_RIGHT = new ButtonOverlay(Ref.ID,"apad_right", 14, 14); // ›

    protected String id, domain;
    protected int w, h;
    protected boolean colored = false;
    protected boolean changedOnHovered = false;
    protected ResourceLocation textureOverride = null;

    public ButtonOverlay(String domain, String id, int w, int h) {
        this.domain = domain;
        this.id = id;
        this.w = w;
        this.h = h;
    }

    public ButtonOverlay colored(){
        colored = true;
        return this;
    }

    public ButtonOverlay changedOnHovered(){
        changedOnHovered = true;
        return this;
    }

    public ButtonOverlay setTextureOverride(ResourceLocation textureOverride){
        this.textureOverride = textureOverride;
        return this;
    }

    public ResourceLocation getTexture(){
        if (textureOverride != null) return textureOverride;
        return new ResourceLocation(domain, "textures/gui/button/" + id + ".png");
    }

    public String getId() {
        return id;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public boolean isChangedOnHovered() {
        return changedOnHovered;
    }

    public boolean isColored() {
        return colored;
    }
}

