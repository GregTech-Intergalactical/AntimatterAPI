package muramasa.antimatter.cover;

public class CoverNone extends BaseCover {

    public CoverNone() {
        super();
        register();
    }

    @Override
    public String getId() {
        return "none";
    }
}
