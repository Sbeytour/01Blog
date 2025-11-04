export interface User {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  bio?: string;
  profileImgUrl: string;
  role: UserRole;
  followersCount?: number;
  followingCount?: number;
  isFollowedByCurrentUser?: boolean;
  banned: boolean;
  reportCount?: number;
  joinedDate: string;
}

export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN'
}

export interface UpdateProfileRequest {
  username?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
  bio?: string;
  profileImgUrl?: string;
}