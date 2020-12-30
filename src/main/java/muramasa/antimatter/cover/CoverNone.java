package muramasa.antimatter.cover;

public class CoverNone extends Cover {

    public CoverNone() {
        super();
        register();
    }

    @Override
    public String getId() {
        return "none";
    }
}
