export interface RequestActionRequest {
    senderId: string,
    action: 'APPROVED' | 'DECLINED'
}