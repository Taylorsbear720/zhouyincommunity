package com.zhouyin.comunity.dao;

import com.zhouyin.comunity.entity.Comment;

import java.util.List;

public interface CommentMapper {
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

}
