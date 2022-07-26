package muramasa.antimatter.datagen.builder;

import com.google.common.base.Preconditions;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JMultipart;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JWhen;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class MultiPartBlockStateBuilder implements IStateBuilder{
    JState state = new JState();
    final Block owner;

    public MultiPartBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    @Override
    public JState toState() {
        return state;
    }

    public PartBuilder part() {
        PartBuilder builder = new PartBuilder(this);
        state.add(builder.multipart);
        return builder;
    }

    public static class PartBuilder{
        JMultipart multipart = new JMultipart();
        public MultiPartBlockStateBuilder owner;

        PartBuilder(MultiPartBlockStateBuilder owner) {
            this.owner = owner;
        }

        public ModelBuilder modelFile(IModelLocation model){
            ModelBuilder builder = new ModelBuilder(this);
            builder.modelFile(model);
            return builder;
        }

        public ModelBuilder modelFile(ResourceLocation model){
            ModelBuilder builder = new ModelBuilder(this);
            builder.modelFile(model);
            return builder;
        }

        public ModelBuilder model(){
            return new ModelBuilder(this);
        }

        public static class ModelBuilder {
            JBlockModel model = null;
            final PartBuilder owner;
            public ModelBuilder(PartBuilder owner){
                this.owner = owner;
            }
            public ModelBuilder modelFile(IModelLocation model) {
                Preconditions.checkNotNull(model, "Model must not be null");
                this.model = new JBlockModel(model.getLocation());
                return this;
            }

            public ModelBuilder modelFile(ResourceLocation location){
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
            public ModelBuilder rotationX(int value) {
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
            public ModelBuilder rotationY(int value) {
                Preconditions.checkNotNull(model, "modelFile must be called first");
                model.y(value);
                return this;
            }

            public ModelBuilder uvLock() {
                Preconditions.checkNotNull(model, "modelFile must be called first");
                model.uvlock();
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
            public ModelBuilder weight(int value) {
                Preconditions.checkNotNull(model, "modelFile must be called first");
                model.weight(value);
                return this;
            }

            public PartBuilder addModel(){
                Preconditions.checkNotNull(model, "modelFile must be called first");
                owner.multipart.addModel(model);
                return owner;
            }
        }

        public <T extends Comparable<T>> PartBuilder condition(Property<T> prop, T... values){
            JWhen when = new JWhen();
            List<String> valueList = new ArrayList<>();
            for (T value : values) {
                valueList.add(prop.getName(value));
            }
            when.add(prop.getName(), valueList.toArray(new String[0]));
            return this;
        }

        public MultiPartBlockStateBuilder end() { return owner; }
    }
}
