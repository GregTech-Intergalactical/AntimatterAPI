package muramasa.antimatter.datagen.builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.json.JConfigEntry;
import muramasa.antimatter.datagen.json.JLoaderModel;
import muramasa.antimatter.datagen.json.JModel;
import muramasa.antimatter.datagen.json.JRotationModel;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.devtech.arrp.json.models.JTextures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AntimatterBlockModelBuilder extends AntimatterModelBuilder<AntimatterBlockModelBuilder>{

    public static final String SIMPLE = Ref.ID.concat(":block/preset/simple");
    protected static final String LAYERED = Ref.ID.concat(":block/preset/layered");

    protected final List<Consumer<Object>> properties = new ObjectArrayList<>();

    public AntimatterBlockModelBuilder(ResourceLocation outputLocation) {
        super(outputLocation);
    }

    public AntimatterBlockModelBuilder property(String property, Object element) {
        model.property(property, element);
        return this;
    }

    public AntimatterBlockModelBuilder property(String property, String value) {
        model.property(property, value);
        return this;
    }

    public AntimatterBlockModelBuilder property(String property, String key, String value) {
        return property(property, new StringToString(key, value));
    }
    
    private record StringToString(String key, String value){}

    public AntimatterBlockModelBuilder particle(Texture tex) {
        model.property("particle", tex.toString());
        return this;
    }

    public AntimatterBlockModelBuilder model(String parent, String... textures) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("model", addModelObject(JLoaderModel.model(), parent, buildTextures(textures)));
    }

    public AntimatterBlockModelBuilder model(String parent, Texture... textures) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("model", addModelObject(JLoaderModel.model(), parent, buildTextures(textures)));
    }

    public AntimatterBlockModelBuilder model(String parent, Function<ImmutableMap.Builder<String, Texture>, ImmutableMap.Builder<String, Texture>> func) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("model", addModelObject(JLoaderModel.model(), parent, buildTextures(func.apply(new ImmutableMap.Builder<>()).build())));
    }

    public AntimatterBlockModelBuilder model(String parent, ImmutableMap<String, Texture> map) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("model", addModelObject(JLoaderModel.model(), parent, buildTextures(map)));
    }

    public AntimatterBlockModelBuilder rot(int... rotations) {
        loader(AntimatterModelManager.LOADER_MAIN);
        if (rotations.length != 3){
            throw new IllegalStateException("rotations must have no more or less then 3 elements");
        }
        model.rotation(rotations);
        return this;
    }

    public AntimatterBlockModelBuilder config(int id, String parent, Function<DynamicConfigBuilder, DynamicConfigBuilder> builderFunc) {
        return config(id, (b, l) -> l.add(builderFunc.apply(b.of(parent))));
    }

    public AntimatterBlockModelBuilder config(int id, IConfigFunction configFunc) {
        loader(AntimatterModelManager.LOADER_DYNAMIC);
        ImmutableList<DynamicConfigBuilder> builders = configFunc.apply(new DynamicConfigBuilder(), new ImmutableList.Builder<>()).build();
        JConfigEntry entry = JConfigEntry.configEntry();
        entry.setID(id);
        entry.addModels(getModelObjects(builders));
        model.configEntry(entry);
        return this;
    }

    public interface IConfigFunction {
        ImmutableList.Builder<DynamicConfigBuilder> apply(DynamicConfigBuilder b, ImmutableList.Builder<DynamicConfigBuilder> l);
    }

    public JModel[] getModelObjects(ImmutableList<DynamicConfigBuilder> builders) {
        List<JModel> models = new ArrayList<>();
        builders.forEach(b -> {
            JRotationModel model1 = JRotationModel.modelKeepElements();
            model1.parent(b.parent);
            JTextures textures1 = new JTextures();
            b.textures.forEach(textures1::var);
            model1.textures(textures1);
            if (b.hasRots()) model1.rotation(b.rotations);
            if (b.getLoader() != null) {
                model1.loader(b.loader.toString());
            }
            models.add(model1);
        });
        return models.toArray(new JModel[0]);
    }

    public JLoaderModel addModelObject(JLoaderModel o, String parent, ImmutableMap<String, String> textures) {
        if (!parent.contains(":"))
            parent = StringUtils.replace(StringUtils.replace(parent, "simple", SIMPLE), "layered", LAYERED);
        o.parent(parent);
        JTextures textures1 = new JTextures();
        textures.forEach((k, v) -> textures1.var(k, v.replaceAll("mc:", "minecraft:")));
        o.textures(textures1);
        return o;
    }

    public static String getSimple() {
        return SIMPLE;
    }

    public static String getLayered() {
        return LAYERED;
    }

    public AntimatterBlockModelBuilder staticConfigId(String mapId) {
        loader(AntimatterModelManager.LOADER_DYNAMIC);
        return property("staticConfigId", mapId);
    }

    public static ImmutableMap<String, String> buildTextures(ImmutableMap<String, Texture> map) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        map.forEach((k, v) -> builder.put(k, v.toString()));
        return builder.build();
    }

    public static ImmutableMap<String, String> buildTextures(Texture... textures) {
        return buildTextures(Arrays.stream(textures).map(ResourceLocation::toString).toArray(String[]::new));
    }

    public static ImmutableMap<String, String> buildTextures(String... textures) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        if (textures.length == 1) {
            builder.put("all", textures[0]);
        } else if (textures.length == 6) {
            for (int s = 0; s < 6; s++) {
                builder.put(Ref.DIRS[s].toString(), textures[s]);
            }
        }
        return builder.build();
    }

    public AntimatterBlockModelBuilder basicConfig(Block block, Texture[] tex) {
        if (!(block instanceof ITextureProvider) || tex.length < 13) return this;
        model(SIMPLE, ((ITextureProvider) block).getTextures());

        //Single (1)
        if (tex.length < 17) {
            config(1, SIMPLE, c -> c.tex(tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]));
            config(2, SIMPLE, c -> c.tex(tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]));
            config(4, SIMPLE, c -> c.tex(tex[1], tex[1], tex[0], tex[12], tex[0], tex[0]));
            config(8, SIMPLE, c -> c.tex(tex[1], tex[1], tex[12], tex[0], tex[0], tex[0]));
            config(16, SIMPLE, c -> c.tex(tex[0], tex[0], tex[0], tex[0], tex[0], tex[12]));
            config(32, SIMPLE, c -> c.tex(tex[0], tex[0], tex[0], tex[0], tex[12], tex[0]));
        } else {
            // 13 = facing right, 14 = facing down, 15 = facing left, 16 = facing up
            config(1, SIMPLE, c -> c.tex(tex[12], tex[12], tex[14], tex[14], tex[14], tex[14]));
            config(2, SIMPLE, c -> c.tex(tex[12], tex[12], tex[16], tex[16], tex[16], tex[16]));
            config(4, SIMPLE, c -> c.tex(tex[14], tex[16], tex[0], tex[12], tex[15], tex[13]));
            config(8, SIMPLE, c -> c.tex(tex[16], tex[14], tex[12], tex[0], tex[13], tex[15]));
            config(16, SIMPLE, c -> c.tex(tex[15], tex[15], tex[13], tex[15], tex[0], tex[12]));
            config(32, SIMPLE, c -> c.tex(tex[13], tex[13], tex[15], tex[13], tex[12], tex[0]));
        }


        //Lines (2)
        config(3, SIMPLE, c -> c.tex(tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]));
        config(12, SIMPLE, c -> c.tex(tex[1], tex[1], tex[12], tex[12], tex[0], tex[0]));
        config(48, SIMPLE, c -> c.tex(tex[0], tex[0], tex[0], tex[0], tex[12], tex[12]));

        //Elbows (2)
        config(6, SIMPLE, c -> c.tex(tex[1], tex[12], tex[0], tex[1], tex[11], tex[10]));
        config(5, SIMPLE, c -> c.tex(tex[12], tex[1], tex[12], tex[1], tex[9], tex[8]));
        config(9, SIMPLE, c -> c.tex(tex[12], tex[1], tex[1], tex[12], tex[8], tex[9]));
        config(10, SIMPLE, c -> c.tex(tex[1], tex[12], tex[1], tex[12], tex[10], tex[11]));
        config(17, SIMPLE, c -> c.tex(tex[12], tex[0], tex[8], tex[9], tex[12], tex[1]));
        config(18, SIMPLE, c -> c.tex(tex[0], tex[12], tex[10], tex[11], tex[12], tex[1]));
        config(33, SIMPLE, c -> c.tex(tex[12], tex[0], tex[9], tex[8], tex[1], tex[12]));
        config(34, SIMPLE, c -> c.tex(tex[0], tex[12], tex[11], tex[10], tex[1], tex[10]));
        config(20, SIMPLE, c -> c.tex(tex[9], tex[11], tex[0], tex[0], tex[0], tex[0]));
        config(24, SIMPLE, c -> c.tex(tex[11], tex[9], tex[0], tex[0], tex[0], tex[0]));
        config(36, SIMPLE, c -> c.tex(tex[8], tex[10], tex[0], tex[0], tex[0], tex[0]));
        config(40, SIMPLE, c -> c.tex(tex[10], tex[8], tex[0], tex[0], tex[0], tex[0]));

        //Side (3)
        config(7, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[1], tex[4], tex[2]));
        config(11, SIMPLE, c -> c.tex(tex[12], tex[12], tex[1], tex[12], tex[2], tex[4]));
        config(13, SIMPLE, c -> c.tex(tex[12], tex[1], tex[12], tex[12], tex[3], tex[3]));
        config(14, SIMPLE, c -> c.tex(tex[1], tex[12], tex[12], tex[12], tex[5], tex[5]));
        config(19, SIMPLE, c -> c.tex(tex[12], tex[12], tex[2], tex[4], tex[12], tex[1]));
        config(28, SIMPLE, c -> c.tex(tex[4], tex[4], tex[12], tex[12], tex[12], tex[0]));
        config(35, SIMPLE, c -> c.tex(tex[12], tex[12], tex[4], tex[2], tex[1], tex[12]));
        config(44, SIMPLE, c -> c.tex(tex[2], tex[2], tex[12], tex[12], tex[0], tex[12]));
        config(49, SIMPLE, c -> c.tex(tex[12], tex[0], tex[3], tex[3], tex[12], tex[12]));
        config(50, SIMPLE, c -> c.tex(tex[0], tex[12], tex[5], tex[5], tex[12], tex[12]));
        config(52, SIMPLE, c -> c.tex(tex[3], tex[5], tex[12], tex[0], tex[12], tex[12]));
        config(56, SIMPLE, c -> c.tex(tex[5], tex[3], tex[0], tex[12], tex[12], tex[12]));

        //Corner (3)
        config(21, SIMPLE, c -> c.tex(tex[11], tex[11], tex[0], tex[9], tex[0], tex[8]));
        config(22, SIMPLE, c -> c.tex(tex[9], tex[9], tex[0], tex[11], tex[0], tex[10]));
        config(25, SIMPLE, c -> c.tex(tex[9], tex[9], tex[8], tex[0], tex[0], tex[9]));
        config(26, SIMPLE, c -> c.tex(tex[11], tex[11], tex[10], tex[0], tex[0], tex[11]));
        config(37, SIMPLE, c -> c.tex(tex[10], tex[10], tex[0], tex[8], tex[9], tex[0]));
        config(38, SIMPLE, c -> c.tex(tex[8], tex[8], tex[0], tex[10], tex[11], tex[0]));
        config(41, SIMPLE, c -> c.tex(tex[8], tex[8], tex[9], tex[0], tex[8], tex[0]));
        config(42, SIMPLE, c -> c.tex(tex[10], tex[10], tex[11], tex[0], tex[10], tex[0]));

        //Arrow (4)
        config(23, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[4], tex[12], tex[2]));
        config(27, SIMPLE, c -> c.tex(tex[12], tex[12], tex[2], tex[12], tex[12], tex[4]));
        config(29, SIMPLE, c -> c.tex(tex[12], tex[4], tex[12], tex[12], tex[12], tex[3]));
        config(30, SIMPLE, c -> c.tex(tex[4], tex[12], tex[12], tex[12], tex[12], tex[5]));
        config(39, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[2], tex[4], tex[12]));
        config(43, SIMPLE, c -> c.tex(tex[12], tex[12], tex[4], tex[12], tex[2], tex[12]));
        config(45, SIMPLE, c -> c.tex(tex[12], tex[2], tex[12], tex[12], tex[3], tex[12]));
        config(46, SIMPLE, c -> c.tex(tex[2], tex[12], tex[12], tex[12], tex[5], tex[12]));
        config(53, SIMPLE, c -> c.tex(tex[12], tex[5], tex[12], tex[3], tex[12], tex[12]));
        config(54, SIMPLE, c -> c.tex(tex[3], tex[12], tex[12], tex[5], tex[12], tex[12]));
        config(57, SIMPLE, c -> c.tex(tex[12], tex[3], tex[3], tex[12], tex[12], tex[12]));
        config(58, SIMPLE, c -> c.tex(tex[5], tex[12], tex[5], tex[12], tex[12], tex[12]));

        //Cross (4)
        config(15, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[12], tex[6], tex[6]));
        config(51, SIMPLE, c -> c.tex(tex[12], tex[12], tex[6], tex[6], tex[12], tex[12]));
        config(60, SIMPLE, c -> c.tex(tex[6], tex[6], tex[12], tex[12], tex[12], tex[12]));

        //Five (5)
        config(31, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[12], tex[12], tex[6]));
        config(47, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[12], tex[6], tex[12]));
        config(55, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[6], tex[12], tex[12]));
        config(59, SIMPLE, c -> c.tex(tex[12], tex[12], tex[6], tex[12], tex[12], tex[12]));
        config(61, SIMPLE, c -> c.tex(tex[12], tex[6], tex[12], tex[12], tex[12], tex[12]));
        config(62, SIMPLE, c -> c.tex(tex[6], tex[12], tex[12], tex[12], tex[12], tex[12]));

        //All (6)
        config(63, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[12], tex[12], tex[12]));

        return this;
    }
}
