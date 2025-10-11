export interface PostResponse {
    id,
    String content,
    RedisUserDto user,
    Integer commentsCount,
    Integer likeCount,
    Integer disLikeCount,
    Boolean hasMedia
}