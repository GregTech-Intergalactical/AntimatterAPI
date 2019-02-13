package muramasa.gregtech.api.cover;

public class CoverStack {

    private Cover cover;

    public CoverStack(Cover cover) {
        this.cover = cover;
    }

    public Cover getCover() {
        return cover;
    }

    public boolean isEqual(Cover cover) {
        return this.cover.getName().equals(cover.getName());
    }
}
