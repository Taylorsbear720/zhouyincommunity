package com.zhouyin.comunity.dao;



//elasticsearch 接口
import com.zhouyin.comunity.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussRepository extends ElasticsearchRepository<DiscussPost,Integer> {
}
