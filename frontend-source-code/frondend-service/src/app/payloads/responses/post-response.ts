import { UserBasicData } from "./user-basic-data";

export interface PostResponse {
    id: string,
    content: string,
    user: UserBasicData,
    commentsCount: number,
    likeCount: number,
    disLikeCount: number,
    hasMedia: boolean,
    mediaPaths: string[],
    expanded?: boolean,
    liked: boolean,
    disliked: boolean
}