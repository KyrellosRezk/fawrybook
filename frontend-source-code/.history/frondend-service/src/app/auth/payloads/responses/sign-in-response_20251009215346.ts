export interface SignInResponse {
    accessToken: string,
    refreshToken: string,
    OTPToken: string,
    user: UserBasicData
}