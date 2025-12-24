import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../../components/navbar/navbar';
import { PostService } from '../../core/services/postService';
import { Post } from '../../core/models/post';
import { PostCard } from '../../components/post-card/post-card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButton } from '@angular/material/button';
import { InfiniteScroll } from '../../components/infinite-scroll/infinite-scroll';

@Component({
  selector: 'app-home',
  imports: [
    Navbar,
    CommonModule,
    MatButton,
    PostCard,
    MatProgressSpinnerModule,
    MatIconModule,
    InfiniteScroll
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home implements OnInit {
  private postService = inject(PostService);

  posts = signal<Post[]>([]);
  isLoading = signal(true);
  isLoadingMore = signal(false);
  hasMore = signal(true);
  currentPage = signal(0);
  pageSize = 9;
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadPosts();
  }

  /**
   * Load first page of posts
   */
  loadPosts(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.currentPage.set(0);

    this.postService.getAllPosts(0, this.pageSize).subscribe({
      next: (response) => {
        this.posts.set(response.content);
        this.hasMore.set(response.hasMore);
        this.currentPage.set(response.currentPage);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading posts:', error);
        this.errorMessage.set('Failed to load posts. Please try again later.');
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Load more posts (triggered by infinite scroll)
   */
  loadMorePosts(): void {
    if (!this.hasMore() || this.isLoadingMore()) {
      return;
    }

    this.isLoadingMore.set(true);
    const nextPage = this.currentPage() + 1;

    this.postService.getAllPosts(nextPage, this.pageSize).subscribe({
      next: (response) => {
        // Append new posts to existing posts
        this.posts.update(current => [...current, ...response.content]);
        this.hasMore.set(response.hasMore);
        this.currentPage.set(response.currentPage);
        this.isLoadingMore.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading more posts:', error);
        this.isLoadingMore.set(false);
      }
    });
  }

  onPostEdited(editedPost: Post): void {
    this.posts.update(oldPosts => oldPosts.map(oldPost => oldPost.id === editedPost.id ? editedPost : oldPost))
  }

  onPostDeleted(postId: number): void {
    this.posts.update(currentPosts =>
      currentPosts.filter(post => post.id !== postId)
    );
  }

  onRetry(): void {
    this.loadPosts();
  }
}
