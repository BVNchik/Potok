package ru.kodep.potok.models;

/**
 * Created by vlad on 26.02.18
 */

public class ResponseData<T> {

    private T data;
    private int perPage;
    private int pages;
    private int page;

    public T getData() {
        return data;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getPages() {
        return pages;
    }

    public int getPage() {
        return page;
    }
}
