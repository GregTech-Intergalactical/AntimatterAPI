package muramasa.antimatter.datagen.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AntimatterItemModelBuilder extends ItemModelBuilder {

    protected ResourceLocation loader;
    protected final List<Consumer<JsonObject>> properties = new ObjectArrayList<>();

    public AntimatterItemModelBuilder(ResourceLocation outputLocation, ExistingFileHelper exFileHelper) {
        super(outputLocation, exFileHelper);
    }

    public AntimatterItemModelBuilder property(String property, JsonElement element) {
        properties.add(o -> o.add(property, element));
        return this;
    }

    public AntimatterItemModelBuilder property(String property, String value) {
        properties.add(o -> o.addProperty(property, value));
        return this;
    }

    public AntimatterItemModelBuilder property(String property, boolean value) {
        properties.add(o -> o.addProperty(property, value));
        return this;
    }

    public AntimatterItemModelBuilder bucketLoader() {
        this.loader = new ResourceLocation("forge", "bucket");
        return this;
    }

    public AntimatterItemModelBuilder cell() {
        this.loader = new ResourceLocation("antimatter", "cell");
        return this;
    }

    public AntimatterItemModelBuilder bucketProperties(Fluid fluid) {
        return bucketProperties(fluid, true, fluid.getAttributes().isLighterThanAir());
    }

    public AntimatterItemModelBuilder bucketProperties(Fluid fluid, boolean tint, boolean islighter) {
        property("fluid", fluid.getRegistryName().toString());
        property("flipGas", islighter);
        property("applyTint", tint);
        // property("coverIsMask", false);
        return bucketLoader();
    }

    public AntimatterItemModelBuilder tex(Consumer<Map<String,String>> texer) {
        texer.accept(this.textures);
        return this;
    }


    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();
        if (loader != null) root.addProperty("loader", loader.toString());
        if (!properties.isEmpty()) properties.forEach(c -> c.accept(root));
        return root;
    }

}
