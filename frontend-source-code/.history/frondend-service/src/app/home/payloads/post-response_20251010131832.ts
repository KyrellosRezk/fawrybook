import { UserBasicData } from "../../auth/payloads/responses/user-basic-data";

export interface PostResponse {
    id: string,
    content: string,
    user: UserBasicData,
    commentsCount: number,
    likeCount: n,
    disLikeCount,
    hasMedia
}