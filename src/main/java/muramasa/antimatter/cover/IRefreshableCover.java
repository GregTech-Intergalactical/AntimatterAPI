package muramasa.antimatter.cover;

//Interface used for covers that can refresh, such as for tesseract.
public interface IRefreshableCover {
    void refresh(CoverStack<?> instance);
}
