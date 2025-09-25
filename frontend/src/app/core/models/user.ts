export interface User {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    bio?: string;
    profileImgUrl?:string;
    role: UserRole;
}

export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN'
}
