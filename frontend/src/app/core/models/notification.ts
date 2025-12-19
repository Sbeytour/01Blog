export interface Notification {
  id: number;
  type: 'NEW_POST';
  message: string;
  relatedPostId?: number;
  isRead: boolean;
  createdAt: string;
  creator: {
    id: number;
    username: string;
  };
}
