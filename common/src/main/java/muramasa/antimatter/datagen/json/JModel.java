package muramasa.antimatter.datagen.json;

import net.devtech.arrp.json.loot.JCondition;
import net.devtech.arrp.json.models.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JModel implements Cloneable {
    String parent;
    // true is default
    Boolean ambientocclusion;
    // some thingy idk
    JDisplay display;
    // texture variables
    JTextures textures;
    // make serializer
    List<JElement> elements = new ArrayList<>();
    List<JOverride> overrides;

    /**
     * @see #model(String)
     * @see #model()
     */
    public JModel() {}

    /**
     * @return a new jmodel that does not override it's parent's elements
     */
    public static JModel modelKeepElements() {
        JModel model = new JModel();
        model.elements = null;
        return model;
    }

    /**
     * @return a new jmodel that does not override it's parent's elements
     */
    public static JModel modelKeepElements(String parent) {
        JModel model = new JModel();
        model.parent = parent;
        model.elements = null;
        return model;
    }

    public static JModel modelKeepElements(ResourceLocation identifier) {
        return modelKeepElements(identifier.toString());
    }

    public static JModel model() {
        return new JModel();
    }

    public static JModel model(String parent) {
        JModel model = new JModel();
        model.parent = parent;
        return model;
    }

    public static JModel model(ResourceLocation identifier) {
        return model(identifier.toString());
    }

    public static JOverride override(JCondition predicate, ResourceLocation model) {
        return new JOverride(predicate, model.toString());
    }

    public static JCondition condition() {
        return new JCondition(null);
    }

    public static JDisplay display() {
        return new JDisplay();
    }

    public static JElement element() {
        return new JElement();
    }

    public static JFace face(String texture) {
        return new JFace(texture);
    }

    public static JFaces faces() {
        return new JFaces();
    }

    public static JPosition position() {
        return new JPosition();
    }

    public static JRotation rotation(Direction.Axis axis) {
        return new JRotation(axis);
    }

    public static JTextures textures() {
        return new JTextures();
    }

    public JModel parent(String parent) {
        this.parent = parent;
        return this;
    }

    public JModel ambientOcclusion(boolean occlusion) {
        this.ambientocclusion = occlusion;
        return this;
    }

    public JModel display(JDisplay display) {
        this.display = display;
        return this;
    }

    public JModel textures(JTextures textures) {
        this.textures = textures;
        return this;
    }

    public JModel element(JElement... elements) {
        if(this.elements == null) {
            this.elements = new ArrayList<>();
        }
        this.elements.addAll(Arrays.asList(elements));
        return this;
    }

    public JModel addOverride(JOverride override) {
        if(this.overrides == null) this.overrides = new ArrayList<>();
        this.overrides.add(override);
        return this;
    }

    @Override
    public JModel clone() {
        try {
            return (JModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
