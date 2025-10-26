export interface Comment {
    id: number;
    content: string;
    user: {
        id: number;
        username: string;
        firstName: string;
        lastName: string;
        profileImgUrl?: string;
    };
    createdAt: string;
    updatedAt: string;
}

export interface CreateCommentRequest {
    content: string;
}

export interface PagedCommentResponse {
    comments: Comment[];
    totalComments: number;
    currentPage: number;
    totalPages: number;
    hasMore: boolean;
}
