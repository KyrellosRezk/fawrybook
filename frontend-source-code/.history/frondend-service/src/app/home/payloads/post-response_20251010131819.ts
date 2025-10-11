export interface PostResponse {
    id: string,
    content: string,
    user: User,
    commentsCount,
    likeCount,
    disLikeCount,
    hasMedia
}