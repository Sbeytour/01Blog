import { Comment } from './comment';

export interface Post {
    id: number;
    title: string;
    content: string;
    creator: {
        id: number;
        username: string;
        email?: string;
        firstName: string;
        lastName: string;
        profileImgUrl?: string;
        role?: string;
    };
    media: Media[];
    comments?: Comment[];
    likesCount: number;
    commentsCount: number;
    isHidden: boolean;
    isLikedByCurrentUser?: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface Media {
    id: number;
    url: string;
    type: 'IMAGE' | 'VIDEO';
}

export interface CreatePostRequest {
    title: string;
    content: string;
}

export interface FilePreview {
    mediaId?: number;
    file: File | null;
    url: string;
    type: 'IMAGE' | 'VIDEO';
}