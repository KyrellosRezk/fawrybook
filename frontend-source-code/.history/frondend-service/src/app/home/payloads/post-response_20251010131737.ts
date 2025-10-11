export interface PostResponse 
String id,
    String content,
    RedisUserDto user,
    Integer commentsCount,
    Integer likeCount,
    Integer disLikeCount,
    Boolean hasMedia
) {}