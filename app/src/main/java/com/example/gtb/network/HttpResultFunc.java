package com.example.gtb.network;

import rx.functions.Func1;

public class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

    @Override
    public T call(HttpResult<T> httpResult) {
        if (httpResult.getCount() == 0) {
            throw new ApiException(ApiException.USER_NOT_EXIST);
        }
        return httpResult.getSubjects();
    }
}