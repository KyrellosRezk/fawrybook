import { UserBasicData } from "../../auth/payloads/responses/user-basic-data";

export interface PostResponse {
    id: string,
    content: string,
    user: UserBasicData,
    commentsCount: nu,
    likeCount,
    disLikeCount,
    hasMedia
}