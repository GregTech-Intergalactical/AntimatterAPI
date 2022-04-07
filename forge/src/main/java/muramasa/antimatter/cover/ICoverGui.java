package muramasa.antimatter.cover;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;

public interface ICoverGui extends ICover, MenuProvider, IGuiHandler {

    default Component getDisplayName() {
        return new TextComponent(Utils.underscoreToUpperCamel(this.getId()));
    }

    default String getDomain() {
        return Ref.ID;
    }
}
