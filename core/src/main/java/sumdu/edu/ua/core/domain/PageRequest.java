package sumdu.edu.ua.core.domain;

public class PageRequest {
    private final int page;
    private final int size;

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }
    public int getPage() { return page; }
    public int getSize() { return size; }
}