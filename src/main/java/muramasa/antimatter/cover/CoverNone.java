package muramasa.antimatter.cover;

public class CoverNone extends BaseCover {

    public CoverNone() {
        super();
        register();
    }

    @Override
    public boolean ticks() {
        return false;
    }

    @Override
    public String getId() {
        return "none";
    }
}
