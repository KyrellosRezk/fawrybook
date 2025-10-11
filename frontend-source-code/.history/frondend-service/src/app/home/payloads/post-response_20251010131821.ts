export interface PostResponse {
    id: string,
    content: string,
    user: UserB,
    commentsCount,
    likeCount,
    disLikeCount,
    hasMedia
}