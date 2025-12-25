import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { AdminService } from '../../../core/services/adminService';
import { Post } from '../../../core/models/post';
import { Router } from '@angular/router';
import { TablePagination } from '../../../components/table-pagination/table-pagination';
import { StringHelpers } from '../../../core/utils/string-helpers';
import { UserHelpers } from '../../../core/utils/user-helpers';
import {
  DeleteConfirmationDialog,
  HidePostDialog
} from '../../../components/dialogs/admin-dialogs';

@Component({
  selector: 'app-posts-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    TablePagination
  ],
  templateUrl: './posts-list.html',
  styleUrl: './posts-list.scss'
})
export class PostsList implements OnInit {
  posts = signal<Post[]>([]);
  loading = signal<boolean>(false);

  displayedColumns: string[] = ['title', 'creator', 'status', 'date', 'reports', 'actions'];

  // Pagination
  totalElements = signal<number>(0);
  pageSize = signal<number>(20);
  pageIndex = signal<number>(0);

  constructor(
    private adminService: AdminService,
    private dialog: MatDialog,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.loading.set(true);
    this.adminService.getPosts(this.pageIndex(), this.pageSize()).subscribe({
      next: (response) => {
        this.posts.set(response.content);
        this.totalElements.set(response.totalElements);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Failed to load posts:', error);
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadPosts();
  }

  getCreatorName(post: Post): string {
    return UserHelpers.getFullName(post.creator);
  }

  getCreatorAvatar(post: Post): string | undefined {
    return post.creator.profileImgUrl;
  }

  getTruncatedTitle(title: string, maxLength: number = 30): string {
    return StringHelpers.truncate(title, maxLength);
  }

  navigateToPost(postId: number) {
    this.router.navigate(['/api/posts', postId]);
  }

  navigatToProfile(username: string) {
    this.router.navigate(['/profile', username]);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  openDeleteDialog(post: Post): void {
    const dialogRef = this.dialog.open(DeleteConfirmationDialog, {
      width: '400px',
      data: {
        title: 'Delete Post',
        message: 'Are you sure you want to permanently delete this post?',
        entityName: post.title
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.deletePost(post.id);
      }
    });
  }

  hiddePost(postId: number, reason: string): void {
    this.loading.set(true);
    this.adminService.hiddePost(postId, reason).subscribe({
      next: () => {
        this.loadPosts();
      },
      error: (error) => {
        console.error('Failed to hidde post:', error);
        this.loading.set(false);
      }
    });
  }

  openHiddeDialog(post: Post): void {
    const dialogRef = this.dialog.open(HidePostDialog, {
      width: '400px',
      data: { postTitle: post.title }
    });

    dialogRef.afterClosed().subscribe((reason: string) => {
      if (reason) {
        this.hiddePost(post.id, reason);
      }
    });
  }

  unhidePost(postId: number): void {
    this.loading.set(true);
    this.adminService.unhidePost(postId).subscribe({
      next: () => {
        this.loadPosts();
      },
      error: (error) => {
        console.error('Failed to unhide post:', error);
        this.loading.set(false);
      }
    });
  }

  deletePost(postId: number): void {
    this.loading.set(true);
    this.adminService.deletePost(postId).subscribe({
      next: () => {
        this.loadPosts();
      },
      error: (error) => {
        console.error('Failed to delete post:', error);
        this.loading.set(false);
      }
    });
  }
}