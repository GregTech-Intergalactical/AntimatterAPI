package muramasa.antimatter.datagen.builder;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.devtech.arrp.json.loot.JCondition;
import net.devtech.arrp.json.models.JOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import tesseract.FluidPlatformUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AntimatterItemModelBuilder extends AntimatterModelBuilder<AntimatterItemModelBuilder> {

    protected ResourceLocation loader;

    public AntimatterItemModelBuilder(ResourceLocation outputLocation) {
        super(outputLocation);
    }

    public AntimatterItemModelBuilder property(String property, Object element) {
        model.property(property, element);
        return this;
    }

    public AntimatterItemModelBuilder property(String property, String value) {
        model.property(property, value);
        return this;
    }

    public AntimatterItemModelBuilder property(String property, boolean value) {
        model.property(property, value);
        return this;
    }

    public AntimatterItemModelBuilder bucketLoader() {
        super.loader(new ResourceLocation("forge", "bucket"));
        return this;
    }

    public AntimatterItemModelBuilder cell() {
        super.loader(new ResourceLocation("antimatter", "cell"));
        return this;
    }

    public AntimatterItemModelBuilder bucketProperties(Fluid fluid) {
        return bucketProperties(fluid, true, FluidPlatformUtils.isFluidGaseous(fluid));
    }

    public AntimatterItemModelBuilder bucketProperties(Fluid fluid, boolean tint, boolean islighter) {
        property("fluid", AntimatterPlatformUtils.getIdFromFluid(fluid).toString());
        property("flipGas", islighter);
        property("applyTint", tint);
        // property("coverIsMask", false);
        return bucketLoader();
    }

    public AntimatterItemModelBuilder tex(Consumer<Map<String, String>> texer) {
        Map<String, String> textureMap = new Object2ObjectArrayMap<>();
        texer.accept(textureMap);
        textureMap.forEach(this::texture);
        return this;
    }

    public AntimatterItemModelBuilder tex(Map<String, Texture> textureMap) {
        textureMap.forEach(this::texture);
        return this;
    }

    public OverrideBuilder override(){
        return new OverrideBuilder();
    }

    public class OverrideBuilder {

        private ResourceLocation model = null;
        private final Map<ResourceLocation, Float> predicates = new LinkedHashMap<>();

        public OverrideBuilder model(IModelLocation model) {
            this.model = model.getLocation();
            return this;
        }

        public OverrideBuilder model(ResourceLocation model) {
            this.model = model;
            return this;
        }

        public OverrideBuilder predicate(ResourceLocation key, float value) {
            this.predicates.put(key, value);
            return this;
        }

        public AntimatterItemModelBuilder end() {
            JCondition condition = new JCondition();
            predicates.forEach((k, v) -> condition.parameter(k.toString(), v));
            AntimatterItemModelBuilder.this.model.addOverride(new JOverride(condition, model.toString()));
            return AntimatterItemModelBuilder.this;
        }
    }
}
