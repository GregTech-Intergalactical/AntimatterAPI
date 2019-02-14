package muramasa.gregtech.api.cover.behaviour;

import muramasa.gregtech.api.cover.CoverBehaviour;

public abstract class CoverBehaviourTintable extends CoverBehaviour {

    public static int coverTintIndex = 5;

    public abstract int getRGB();
}
