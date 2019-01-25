package com.example.demo.dao;

import com.example.demo.entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookDao extends ElasticsearchRepository<Book, String> {

}

