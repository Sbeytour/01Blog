import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notification } from '../models/notification';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/notifications`;

  notifications = signal<Notification[]>([]);
  unreadCount = signal(0);
  isLoading = signal(false);

  getNotifications(unreadOnly: boolean = false): Observable<Notification[]> {
    this.isLoading.set(true);
    return this.http.get<Notification[]>(`${this.apiUrl}?unreadOnly=${unreadOnly}`).pipe(
      tap(notifications => {
        this.notifications.set(notifications);
        this.isLoading.set(false);
      })
    );
  }

  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread-count`).pipe(
      tap(count => this.unreadCount.set(count))
    );
  }

  markAsRead(notificationId: number): Observable<Notification> {
    return this.http.put<Notification>(`${this.apiUrl}/${notificationId}/read`, {}).pipe(
      tap(updatedNotification => {
        // Update the notification in the local array
        this.notifications.update(notifications =>
          notifications.map(n => n.id === notificationId ? updatedNotification : n)
        );
        // Decrease unread count
        this.unreadCount.update(count => Math.max(0, count - 1));
      })
    );
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/mark-all-read`, {}).pipe(
      tap(() => {
        // Mark all notifications as read in local state
        this.notifications.update(notifications =>
          notifications.map(n => ({ ...n, isRead: true }))
        );
        // Reset unread count
        this.unreadCount.set(0);
      })
    );
  }

  loadNotifications(): void {
    this.getNotifications(false).subscribe();
    this.getUnreadCount().subscribe();
  }
}
