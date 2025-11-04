import { Component, OnInit, signal, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AdminService } from '../../../core/services/adminService';
import { Post } from '../../../core/models/post';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';

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
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatDialogModule
  ],
  templateUrl: './posts-list.html',
  styleUrl: './posts-list.scss'
})
export class PostsList implements OnInit {
  posts = signal<Post[]>([]);
  loading = signal<boolean>(false);

  displayedColumns: string[] = ['title', 'creator', 'status', 'date'];

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
    this.adminService.getPosts().subscribe({
      next: (posts: Post[]) => {
        this.posts.set(posts);
        this.totalElements.set(posts.length);
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
    return `${post.creator.firstName} ${post.creator.lastName}`.trim();
  }

  getCreatorAvatar(post: Post): string | undefined {
    return post.creator.profileImgUrl;
  }

  getTruncatedTitle(title: string, maxLength: number = 30): string {
    return title.length > maxLength ? title.substring(0, maxLength) + '...' : title;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  viewPost(postId: number): void {
    this.router.navigate(['/posts', postId]);
  }

  openDeleteDialog(post: Post): void {
    const dialogRef = this.dialog.open(DeletePostDialog, {
      width: '400px',
      data: { post }
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
    const dialogRef = this.dialog.open(BanUserDialog, {
      width: '400px',
      data: { post }
    });

    dialogRef.afterClosed().subscribe((reason: string) => {
      if (reason) {
        this.hiddePost(post.id, reason);
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

// Delete Post Dialog Component
@Component({
  selector: 'delete-post-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Delete Post</h2>
    <mat-dialog-content>
      <p>Are you sure you want to permanently delete this post?</p>
      <p><strong>{{ data.post.title }}</strong></p>
      <p style="color: #f44336;">This action cannot be undone!</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Cancel</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="true">
        Delete
      </button>
    </mat-dialog-actions>
  `
})
export class DeletePostDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { post: Post }) { }
}


// Ban User Dialog Component
@Component({
  selector: 'hidde-post-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule],
  template: `
    <h2 mat-dialog-title>Ban User</h2>
    <mat-dialog-content>
      <p>You are about to ban <strong>{{ data.post.title }}</strong></p>
      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Reason for ban</mat-label>
        <textarea
          matInput
          [(ngModel)]="reason"
          placeholder="Enter reason (min 10 characters)"
          rows="4"
          required
        ></textarea>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="null">Cancel</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="reason" [disabled]="!isValid()">
        Ban User
      </button>
    </mat-dialog-actions>
  `
})
export class BanUserDialog {
  reason: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data: { post: Post }) { }

  isValid(): boolean {
    return this.reason.trim().length >= 10;
  }
}

