export interface Post {
    id: number;
    title: string;
    content: string;
    creator: {
        id: number;
        username: string;
        firstName: string;
        lastName: string;
        profileImgUrl?: string;
    };
    media: Media[];
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
    file: File;
    url: string;
    type: 'image' | 'video';
}