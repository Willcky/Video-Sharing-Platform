package org.dromara.search.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.search.domain.dto.VideoSearchDTO;
import org.dromara.search.domain.vo.VideoSearchVO;
import org.dromara.search.domain.vo.VideoSearchPageVO;
import org.dromara.search.service.IVideoSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoSearchServiceImpl implements IVideoSearchService {

    private final RestHighLevelClient elasticsearchClient;
    private static final String VIDEO_INDEX = "videos";

    @Override
    public R<VideoSearchPageVO> searchVideos(VideoSearchDTO searchDTO) {
        try {
            SearchRequest searchRequest = new SearchRequest(VIDEO_INDEX);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // Build query
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
                boolQuery.should(QueryBuilders.matchQuery("title", searchDTO.getKeyword()))
                    .should(QueryBuilders.matchQuery("tags", searchDTO.getKeyword()))
                    .minimumShouldMatch(1);
            }

            // Add filter for non-deleted videos
            boolQuery.filter(QueryBuilders.termQuery("del_flag", false));
            sourceBuilder.query(boolQuery);

            // Add sorting based on sortType
            switch (searchDTO.getSortType()) {
                case 1: // Latest
                    sourceBuilder.sort("create_time", SortOrder.DESC);
                    break;
                case 2: // Most likes
                    sourceBuilder.sort("like_count", SortOrder.DESC);
                    break;
                case 3: // Most views
                    sourceBuilder.sort("view_count", SortOrder.DESC);
                    break;
                default: // Relevance (default)
                    if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
                        sourceBuilder.sort("_score", SortOrder.DESC);
                    } else {
                        sourceBuilder.sort("create_time", SortOrder.DESC);
                    }
            }

            // Pagination
            int from = (searchDTO.getPageNum() - 1) * searchDTO.getPageSize();
            sourceBuilder.from(from);
            sourceBuilder.size(searchDTO.getPageSize());

            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);

            // Process results
            List<VideoSearchVO> videos = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Map<String, Object> sourceMap = hit.getSourceAsMap();
                VideoSearchVO video = new VideoSearchVO();
                video.setId(Long.valueOf(sourceMap.get("id").toString()));
                video.setTitle((String) sourceMap.get("title"));
                video.setTags((String) sourceMap.get("tags"));
                video.setCategoryId(Long.valueOf(sourceMap.get("category_id").toString()));
                video.setThumbnailUrl((String) sourceMap.get("thumbnail_url"));
                video.setUserName((String) sourceMap.get("user_name"));
                video.setViewCount(Long.valueOf(sourceMap.get("view_count").toString()));
                video.setLikeCount(Long.valueOf(sourceMap.get("like_count").toString()));
                video.setCreateTime((Date) sourceMap.get("create_time"));
                videos.add(video);
            }

            VideoSearchPageVO pageVO = new VideoSearchPageVO(
                searchResponse.getHits().getTotalHits().value,
                searchDTO.getPageNum(),
                searchDTO.getPageSize(),
                videos
            );

            return R.ok(pageVO);
        } catch (IOException e) {
            return R.fail("Search failed");
        }
    }
}
