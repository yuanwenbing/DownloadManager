package com.yuan.library.db;

public interface IUrl {
    /**
     * @return http method
     */
    int getMethod();

    /**
     * return the full url
     *
     * @return
     */
    String getUrl();

    /**
     * set query string to url.
     *
     * @param query query string, it a request parameter of string format usually
     */
    void setQuery(String query);
}