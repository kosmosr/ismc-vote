package top.mollysu.ismcvote.core;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import top.mollysu.ismcvote.constant.VoteConstant;

import java.util.*;

/**
 * @author zengminghao
 * @date 2020/4/28 16:17
 */
public class VoteCustomCookieJar implements CookieJar {

    private final Map<String, Set<Cookie>> cookieStore;

    public VoteCustomCookieJar() {
        cookieStore = new HashMap<>(1);
        initCookie();
    }

    private void initCookie() {
        HashSet<Cookie> defaultCookies = new HashSet<>();
        Cookie srcurl = new Cookie.Builder()
                .domain(VoteConstant.DOMAIN)
                .name("srcurl")
                .value("68747470733a2f2f7777772e69736d632e63632f6e78732f3230323030342f353131312e68746d6c3f66726f6d3d73696e676c656d657373616765266973617070696e7374616c6c65643d30")
                .build();
        Cookie cnzzdata = new Cookie.Builder()
                .domain(VoteConstant.DOMAIN)
                .name("CNZZDATA1000285782")
                .value("1568122720-1588051111-https%253A%252F%252Fwww.ismc.cc%252F%7C1588063124")
                .build();
        defaultCookies.add(srcurl);
        defaultCookies.add(cnzzdata);
        cookieStore.put(VoteConstant.DOMAIN, defaultCookies);
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        return new ArrayList<>(cookieStore.getOrDefault(httpUrl.host(), new HashSet<>()));
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        String cookieKey = httpUrl.host();
        Set<Cookie> cookies = cookieStore.getOrDefault(httpUrl.host(), new HashSet<>());
        cookies.addAll(list);
        cookieStore.put(cookieKey, cookies);
    }
}
