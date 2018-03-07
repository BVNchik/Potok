package ru.kodep.vlad.potok.models;

/**
 * Created by vlad on 26.02.18
 */

public class ResponseData<T> {

    private T data;
    private int per_page;
    private int pages;
    private int page;

    public T getData() {
        return data;
    }

    public int getPerPage() {
        return per_page;
    }

    public int getPages() {
        return pages;
    }

    public int getPage() {
        return page;
    }
}
