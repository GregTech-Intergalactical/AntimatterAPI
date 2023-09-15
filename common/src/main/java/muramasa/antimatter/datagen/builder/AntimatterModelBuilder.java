package muramasa.antimatter.datagen.builder;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.mojang.math.Vector3f;
import muramasa.antimatter.datagen.json.JAntimatterModel;
import muramasa.antimatter.datagen.json.JModel;
import net.devtech.arrp.json.models.JElement;
import net.devtech.arrp.json.models.JTextures;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AntimatterModelBuilder<T extends AntimatterModelBuilder<T>> implements IModelLocation {
    protected JAntimatterModel model = JAntimatterModel.modelKeepElements();
    protected JTextures textures = null;
    protected List<JElement> elements = new ArrayList<>();
    protected ResourceLocation customLoader = null;

    private final ResourceLocation location;

    public AntimatterModelBuilder(ResourceLocation location){
        this.location = location;
    }

    private T self() { return (T) this; }

    public T loader(ResourceLocation customLoader){
        this.customLoader = customLoader;
        return self();
    }

    /**
     * Set the parent model for the current model.
     *
     * @param parent the parent model
     * @return this builder
     * @throws NullPointerException  if {@code parent} is {@code null}
     */
    public T parent(ResourceLocation parent) {
        Preconditions.checkNotNull(parent, "Parent must not be null");
        model.parent(parent.toString());
        return self();
    }

    /**
     * Set the texture for a given dictionary key.
     *
     * @param key     the texture key
     * @param texture the texture, can be another key e.g. {@code "#all"}
     * @return this builder
     * @throws NullPointerException  if {@code key} is {@code null}
     * @throws NullPointerException  if {@code texture} is {@code null}
     * @throws IllegalStateException if {@code texture} is not a key (does not start
     *                               with {@code '#'}) and does not exist in any
     *                               known resource pack
     */
    public T texture(String key, String texture) {
        Preconditions.checkNotNull(key, "Key must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");
        if (textures == null){
            textures = new JTextures();
        }
        if (texture.charAt(0) == '#') {
            this.textures.var(key, texture);
            return self();
        } else {
            ResourceLocation asLoc;
            if (texture.contains(":")) {
                asLoc = new ResourceLocation(texture);
            } else {
                asLoc = new ResourceLocation(getLocation().getNamespace(), texture);
            }
            return texture(key, asLoc);
        }
    }

    /**
     * Set the texture for a given dictionary key.
     *
     * @param key     the texture key
     * @param texture the texture
     * @return this builder
     * @throws NullPointerException  if {@code key} is {@code null}
     * @throws NullPointerException  if {@code texture} is {@code null}
     * @throws IllegalStateException if {@code texture} is not a key (does not start
     *                               with {@code '#'}) and does not exist in any
     *                               known resource pack
     */
    public T texture(String key, ResourceLocation texture) {
        Preconditions.checkNotNull(key, "Key must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");
        if (textures == null){
            textures = new JTextures();
        }
        this.textures.var(key, texture.toString());
        return self();
    }

    public T ao(boolean ao) {
        model.ambientOcclusion(ao);
        return self();
    }

    public JElement element() {
        Preconditions.checkState(customLoader == null, "Cannot use elements and custom loaders at the same time");
        JElement element = new JElement();
        elements.add(element);
        return element;
    }

    /**
     * Get an existing element builder
     *
     * @param index the index of the existing element builder
     * @return the element builder
     * @throws IndexOutOfBoundsException if {@code} index is out of bounds
     */
    public JElement element(int index) {
        Preconditions.checkState(customLoader == null, "Cannot use elements and custom loaders at the same time");
        Preconditions.checkElementIndex(index, elements.size(), "Element index");
        return elements.get(index);
    }

    /**
     * Gets the number of elements in this model builder
     * @return the number of elements in this model builder
     */
    public int getElementCount()
    {
        return elements.size();
    }

    public ResourceLocation getLocation() {
        return location;
    }

    private String serializeLocOrKey(String tex) {
        if (tex.charAt(0) == '#') {
            return tex;
        }
        return new ResourceLocation(tex).toString();
    }

    private JsonArray serializeVector3f(Vector3f vec) {
        JsonArray ret = new JsonArray();
        ret.add(serializeFloat(vec.x()));
        ret.add(serializeFloat(vec.y()));
        ret.add(serializeFloat(vec.z()));
        return ret;
    }

    private Number serializeFloat(float f) {
        if ((int) f == f) {
            return (int) f;
        }
        return f;
    }

    public JModel build(){
        if (textures != null) {
            model.textures(textures);
        }
        if (!elements.isEmpty()) {
            model.element(elements.toArray(new JElement[0]));
        }
        if (customLoader != null){
            model.loader(customLoader.toString());
        }
        return model;
    }
}
