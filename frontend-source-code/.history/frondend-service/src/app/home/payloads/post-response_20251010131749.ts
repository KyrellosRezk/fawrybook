export interface PostResponse {
    id,
    content,
    RedisUserDto user,
    Integer commentsCount,
    Integer likeCount,
    Integer disLikeCount,
    Boolean hasMedia
}