package com.example.demo.dao;

import com.example.demo.query.ResultData;

public interface BaseRepository<T, Q> {

    ResultData<T> searchByQuery(Q query);

}
