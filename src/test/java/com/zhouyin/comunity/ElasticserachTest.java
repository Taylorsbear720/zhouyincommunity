package com.zhouyin.comunity;


import com.alibaba.fastjson.JSONObject;
import com.zhouyin.comunity.dao.DiscussPostMapper;
import com.zhouyin.comunity.dao.DiscussRepository;
import com.zhouyin.comunity.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@SpringBootTest
@ContextConfiguration(classes = ComunityApplication.class)
public class ElasticserachTest {

    @Resource
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussRepository discussRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private RestHighLevelClient restHighLevelClient;


    @Test
    public void testInsert() {
        DiscussPost discussPost=discussMapper.selectDiscussPostById(265);
         System.out.println( discussRepository.save(discussPost));
        discussRepository.save(discussMapper.selectDiscussPostById(270));
        discussRepository.save(discussMapper.selectDiscussPostById(275));
    }


    @Test
    public void testInsertList() {
        discussRepository.saveAll(discussMapper.selectDiscussPost(101, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPost(102, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPost(103, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPost(111, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPost(112, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPost(131, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPost(132, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPost(133, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPost(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我是新人,使劲灌水.");
        discussRepository.save(post);
    }


    //放弃  7.X版本中去掉search等方法
  /*  @Test
    public void testSearchByRepository() {
        TermQueryBuilder termQueryBuilder = QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content")




        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // 底层获取得到了高亮显示的值, 但是没有返回.
        Page<DiscussPost> page = operations.search(nativeSearchQuery,DiscussPost.class);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }*/

        //不带高亮的查询
        @Test
        public void noHighlightQuery() throws IOException {
            SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名

            //构建搜索条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                    //在discusspost索引的title和content字段中都查询“互联网寒冬”
                    .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                    // matchQuery是模糊查询，会对key进行分词：searchSourceBuilder.query(QueryBuilders.matchQuery(key,value));
                    // termQuery是精准查询：searchSourceBuilder.query(QueryBuilders.termQuery(key,value));
                    .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                    .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                    .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                    //一个可选项，用于控制允许搜索的时间：searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
                    .from(0)// 指定从哪条开始查询
                    .size(10);// 需要查出的总记录条数

            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
           // System.out.println(JSONObject.toJSON(searchResponse));
          //  System.out.println(searchResponse.getTotalShards());
            List<DiscussPost> list = new LinkedList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
//            System.out.println(discussPost);
                list.add(discussPost);
            }
            System.out.println(list.size());
            for (DiscussPost post : list) {
                System.out.println(post);
            }
        }
        // 别人写的 大概看了下文档，没问题直接用了
        @Test
        public void highlightQuery() throws Exception{
            SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名
            Map<String,Object> res = new HashMap<>();

            //高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.field("content");
            highlightBuilder.requireFieldMatch(false);
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");

            //构建搜索条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                    .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                    .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                    .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                    .from(0)// 指定从哪条开始查询
                    .size(10)// 需要查出的总记录条数
                    .highlighter(highlightBuilder);//高亮
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<DiscussPost> list = new ArrayList<>();

            long total = searchResponse.getHits().getTotalHits().value;

            for (SearchHit hit : searchResponse.getHits().getHits()) {
                DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);

                // 处理高亮显示的结果
                HighlightField titleField = hit.getHighlightFields().get("title");
                if (titleField != null) {
                    discussPost.setTitle(titleField.getFragments()[0].toString());
                }
                HighlightField contentField = hit.getHighlightFields().get("content");
                if (contentField != null) {
                    discussPost.setContent(contentField.getFragments()[0].toString());
                }
//            System.out.println(discussPost);
                list.add(discussPost);
            }
            res.put("list",list);
            res.put("total",total);
            if(res.get("list")!= null){
                for (DiscussPost post : list = (List<DiscussPost>) res.get("list")) {
                    System.out.println(post);
                }
                System.out.println(res.get("total"));
            }
        }
    }


