package net.minecraftforge.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.extensions.IForgeRawTagBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.stream.Stream;

@Mixin(Tag.Builder.class)
public class TagBuilderMixin implements IForgeRawTagBuilder {
    @Shadow @Final private List<Tag.BuilderEntry> entries;
    @Unique
    private boolean replace = false;
    @Unique
    private final List<Tag.BuilderEntry> removeEntries = new java.util.ArrayList<>(); // FORGE: internal field for tracking "remove" entries

    @Override
    public Stream<Tag.BuilderEntry> getRemoveEntries() {
        return removeEntries.stream();
    }

    @Override
    public Tag.Builder remove(final Tag.BuilderEntry proxy) { // internal forge method for adding remove entries
        this.removeEntries.add(proxy);
        return (Tag.Builder)(Object)this;
    }

    @Override
    public Tag.Builder replace(boolean value) {
        this.replace = value;
        return (Tag.Builder)(Object)this;
    }

    @Redirect(method = "serializeToJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Boolean;)V"))
    private void redirectAddProperty(JsonObject instance, String property, Boolean value){
        if (property.equals("replace")) {
            instance.addProperty(property, replace);
        } else {
            instance.addProperty(property, value);
        }
    }

    @Inject(method = "serializeToJson", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectSerializeTagAdditions(CallbackInfoReturnable<JsonObject> cir, JsonObject jsonObject, JsonArray jsonArray){
        this.serializeTagAdditions(jsonObject);
    }

    @Inject(method = "addFromJson", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectDeserializeTagAdditions(JsonObject json, String source, CallbackInfoReturnable<Tag.Builder> cir, JsonArray jsonArray, List<Tag.Entry> list){
        deserializeTagAdditions(list, json, this.entries);
    }

    private <T> void deserializeTagAdditions(List<Tag.Entry> list, JsonObject json, List<Tag.BuilderEntry> allList)
    {
        if (json.has("remove"))
        {
            for (JsonElement entry : GsonHelper.getAsJsonArray(json, "remove"))
            {
                String s = GsonHelper.convertToString(entry, "value");
                Tag.Entry dummy;
                if (!s.startsWith("#"))
                    dummy = new Tag.OptionalElementEntry(new ResourceLocation(s));
                else
                    dummy = new Tag.TagEntry(new ResourceLocation(s.substring(1)));
                allList.removeIf(e -> e.entry().equals(dummy));
            }
        }
    }
}
