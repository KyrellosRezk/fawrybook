export interface UserProfileDataResponse {
  username: string;
  firstName: string;
  middleName: string;
  lastName: string;
  email: string;
  governorate: {
    id: number;
    name: string;
  };
  position: {
    id: number;
    name: string;
  };
  isFollower: boolean;
  logoPath: string;
}
