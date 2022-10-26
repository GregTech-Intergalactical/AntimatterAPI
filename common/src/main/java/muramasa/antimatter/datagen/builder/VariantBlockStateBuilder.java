package muramasa.antimatter.datagen.builder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JVariant;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.function.Function;

public class VariantBlockStateBuilder implements IStateBuilder {
    private final Block owner;
    JState state = JState.state();

    public VariantBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    @Override
    public JState toState() {
        return state;
    }

    public VariantBlockStateBuilder wildcard(VariantBuilder builder){
        state.add(new JVariant().put("", builder.model));
        return this;
    }

    public VariantBlockStateBuilder forAllStates(Function<BlockState, VariantBuilder> mapper) {
        return forAllStatesExcept(mapper);
    }

    public VariantBlockStateBuilder forAllStatesExcept(Function<BlockState, VariantBuilder> mapper, Property<?>... ignored) {
        JVariant variant = new JVariant();
        for (BlockState fullState : owner.getStateDefinition().getPossibleStates()) {
            Map<Property<?>, Comparable<?>> propertyValues = Maps.newLinkedHashMap(fullState.getValues());
            for (Property<?> p : ignored) {
                propertyValues.remove(p);
            }
            JBlockModel model = mapper.apply(fullState).model;
            StringBuilder ret = new StringBuilder();
            propertyValues.forEach((p, c) -> {
                if (ret.length() > 0) {
                    ret.append(',');
                }
                ret.append(p.getName())
                        .append('=')
                        .append(((Property) p).getName(c));
            });
            variant.put(ret.toString(), model);
        }
        state.add(variant);
        return this;
    }

    public static class VariantBuilder {
        JBlockModel model = null;
        public VariantBuilder modelFile(IModelLocation model) {
            Preconditions.checkNotNull(model, "Model must not be null");
            this.model = new JBlockModel(model.getLocation());
            return this;
        }

        public VariantBuilder modelFile(ResourceLocation location){
            Preconditions.checkNotNull(location, "Model must not be null");
            this.model = new JBlockModel(location);
            return this;
        }

        /**
         * Set the x-rotation for this model.
         *
         * @param value the x-rotation value
         * @return this builder
         * @throws IllegalArgumentException if {@code value} is not a valid x-rotation
         *                                  (see {@link BlockModelRotation})
         */
        public VariantBuilder rotationX(int value) {
            Preconditions.checkNotNull(model, "modelFile must be called first");
            model.x(value);
            return this;
        }

        /**
         * Set the y-rotation for this model.
         *
         * @param value the y-rotation value
         * @return this builder
         * @throws IllegalArgumentException if {@code value} is not a valid y-rotation
         *                                  (see {@link BlockModelRotation})
         */
        public VariantBuilder rotationY(int value) {
            Preconditions.checkNotNull(model, "modelFile must be called first");
            model.y(value);
            return this;
        }

        public VariantBuilder uvLock() {
            Preconditions.checkNotNull(model, "modelFile must be called first");
            model.uvlock();
            return this;
        }

        public VariantBuilder uvLock(boolean uv) {
            Preconditions.checkNotNull(model, "modelFile must be called first");
            if (uv) model.uvlock();
            return this;
        }

        /**
         * Set the random weight for this model.
         *
         * @param value the weight value
         * @return this builder
         * @throws IllegalArgumentException if {@code value} is less than or equal to
         *                                  zero
         */
        public VariantBuilder weight(int value) {
            Preconditions.checkNotNull(model, "modelFile must be called first");
            model.weight(value);
            return this;
        }
    }
}
